package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void testMember() {
        // given
        Member member = new Member("memberA", 10, null);
        // when
        Member saveMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(saveMember.getId()).get();
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        assertThat(findMember1.getUsername()).isEqualTo(member1.getUsername());
        assertThat(findMember1.getId()).isEqualTo(member1.getId());
        assertThat(findMember1).isEqualTo(member1);
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember2.getUsername()).isEqualTo(member2.getUsername());
        assertThat(findMember2.getId()).isEqualTo(member2.getId());
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all).hasSize(2);

        // count 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        // 삭제 후 count 검증
        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberA", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        // then
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUsername()).isEqualTo(member2.getUsername());
        assertThat(members.get(0).getAge()).isEqualTo(member2.getAge());
    }

    @Test
    void findByUsernameWithNamedQuery() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> users = memberRepository.findByUsernameWithNamedQuery("memberA");

        // then
        assertThat(users).hasSize(1);
        assertThat(users.get(0)).isEqualTo(member1);
    }

    @Test
    void findByUsernameWithDirectly() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> users = memberRepository.findByUsernameWithDirectly("memberA", 10);

        // then
        assertThat(users).hasSize(1);
        assertThat(users.get(0)).isEqualTo(member1);
    }
    @Test
    void findUsernameList() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<String> usernames = memberRepository.findUsernameList();

        // then
        assertThat(usernames).hasSize(2).containsExactly(member1.getUsername(), member2.getUsername());
    }
    
    @Test
    void findMemberDto() {
        // given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("memberA", 10, team);
        Member member2 = new Member("memberB", 20, team);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<MemberDto> dtos = memberRepository.findMemberDto();

        // then
        assertThat(dtos).hasSize(2)
                .extracting("id", "username", "teamName")
                .containsExactly(
                        tuple(member1.getId(), member1.getUsername(), member1.getTeam().getName()),
                        tuple(member2.getId(), member2.getUsername(), member2.getTeam().getName())
                );
    }
    
    @Test
    void findByNames() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);
        // when
        List<Member> members = memberRepository.findByNames(List.of("memberA", "memberB"));
        // then
        assertThat(members).hasSize(2)
                .extracting("id", "username", "age")
                .containsExactly(
                        tuple(member1.getId(), member1.getUsername(), member1.getAge()),
                        tuple(member2.getId(), member2.getUsername(), member2.getAge())
                );
    }

    @Test
    void returnTypeTest() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);

        // when
        memberRepository.save(member1);
        memberRepository.save(member2);

        // then
        List<Member> members = memberRepository.findListByUsername("memberA");
        assertThat(members).hasSize(1)
                .extracting("id", "username", "age")
                .containsExactly(tuple(member1.getId(), member1.getUsername(), member1.getAge()));

        Member member = memberRepository.findByUsername("memberA");
        assertThat(member).isEqualTo(member1);

        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("memberA");
        assertThat(optionalMember.isPresent()).isTrue();
        assertThat(optionalMember.get().getUsername()).isEqualTo(member1.getUsername());
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member5", 30, null));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // Page를 반환타입으로 하는 경우
        // 페이징 처리 하는 조회 쿼리를 던진 후 totalCount를 조회하는 쿼리(최적화 된)를 자동으로 날린다.
        // 알아둘 점 : totalCount는 데이터의 양이 많으면 성능에 영향을 주기 때문에 문제가 발생할 수 있다.
        //            성능에 영향을 줄이기 위해서는 최적화가 필요하다. ( 직접 짜야 한다. )
        //            이 문제를 위해 쿼리를 분리하는게 좋다. (value, countQuery)
        Page<Member> result = memberRepository.findByAgeWithPage(age, pageRequest);

        Page<MemberDto> resultDto = result.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> content = result.getContent();
        long totalCount = result.getTotalElements();
        assertThat(content).hasSize(3)
                .extracting("username", "age")
                .containsExactly(
                        Tuple.tuple("member4", 10),
                        Tuple.tuple("member3", 10),
                        Tuple.tuple("member2", 10)
                );

        assertThat(totalCount).isEqualTo(4L); // 총 검색 개수
        assertThat(result.getNumber()).isEqualTo(0); // 현재 페이지 번호
        assertThat(result.getTotalPages()).isEqualTo(2); // 총 몇페이지
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    public void slice() {
        // given
        memberRepository.save(new Member("member2", 10, null));
        memberRepository.save(new Member("member1", 10, null));
        memberRepository.save(new Member("member4", 10, null));
        memberRepository.save(new Member("member3", 10, null));
        memberRepository.save(new Member("member5", 30, null));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // Slice는 총 개수는 가져오지 않는다.
        Slice<Member> result = memberRepository.findByAge(age, pageRequest);

        // then
        List<Member> content = result.getContent();
        assertThat(content).hasSize(3)
                .extracting("username", "age")
                .containsExactly(
                        Tuple.tuple("member4", 10),
                        Tuple.tuple("member3", 10),
                        Tuple.tuple("member2", 10)
                );

        assertThat(result.getNumber()).isEqualTo(0); // 현재 페이지 번호
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void bulkUpdate() {
        // given
        Member member2 = memberRepository.save(new Member("member2", 10, null));
        Member member1 = memberRepository.save(new Member("member1", 15, null));
        Member member4 = memberRepository.save(new Member("member4", 20, null));
        Member member3 = memberRepository.save(new Member("member3", 25, null));
        Member member5 = memberRepository.save(new Member("member5", 30, null));

        member4.setUsername("new-member4");
        // when
        int resultCount = memberRepository.bulkAgePlus(20);
        // 영속성 컨테이너가 확실히 초기화 되었는지 검증
        assertThat(entityManager.contains(member2)).isFalse();
        assertThat(entityManager.contains(member1)).isFalse();
        assertThat(entityManager.contains(member4)).isFalse();
        assertThat(entityManager.contains(member3)).isFalse();
        assertThat(entityManager.contains(member5)).isFalse();

        // then
        assertThat(resultCount).isEqualTo(3);

        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(5)
                .extracting("username","age")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("member2", 10),
                        Tuple.tuple("member1", 15),
                        Tuple.tuple("member4", 21),
                        Tuple.tuple("member3", 26),
                        Tuple.tuple("member5", 31)
                );
    }


    @Test
    void findMemberLazyWithFetchJoin() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findMemberFetchJoin();

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void findMemberLazyWithOverride_EntityGraph() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findAll();

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void findMemberLazyWithMethodNaming_EntityGraph() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void findMemberLazyWithMemberQuery_EntityGraph() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findMemberEntityGraph();

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void findMemberLazyWithMemberQuery_NamedEntityGraph() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findNamedEntityGraphByUsername("member1");

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void findMemberLazy_joinTest() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findMemberFetchJoin();

        assertThat(members).hasSize(1);

        // then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.team.class = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }


    @Test
    @Transactional(readOnly = true)
    void queryHint() {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10, null));
        entityManager.flush();
        entityManager.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
        findMember.setUsername("member2");
        entityManager.flush();

        // then
        Member result = memberRepository.findById(member1.getId()).get();
        assertThat(result.getUsername()).isEqualTo("member2");
    }

}