package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void saveTest() {
        // given
        Member member = new Member("memberA", 10, null);
        // when
        Member saveMember = memberJpaRepository.save(member);

        // then
        Member findMember = memberJpaRepository.find(saveMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        assertThat(findMember1.getUsername()).isEqualTo(member1.getUsername());
        assertThat(findMember1.getId()).isEqualTo(member1.getId());
        assertThat(findMember1).isEqualTo(member1);
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember2.getUsername()).isEqualTo(member2.getUsername());
        assertThat(findMember2.getId()).isEqualTo(member2.getId());
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all).hasSize(2);

        // count 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        // 삭제 후 count 검증
        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    void dirtyCheckingTest() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        // when
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        findMember1.setUsername("memberAA");
    }
    
    @Test
    void findByUsernameAndAgeGreaterThen() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberA", 20, null);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        // then
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUsername()).isEqualTo("memberA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
    }
    
    @Test
    void findByUsernameWithNamedQuery() {
        // given
        Member member1 = new Member("memberA", 10, null);
        Member member2 = new Member("memberB", 20, null);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> users = memberJpaRepository.findByUsername("memberA");
        
        // then
        assertThat(users).hasSize(1);
        assertThat(users.get(0)).isEqualTo(member1);
    }

    @Test
    public void paging() {
        // given
        memberJpaRepository.save(new Member("member2", 10, null));
        memberJpaRepository.save(new Member("member1", 10, null));
        memberJpaRepository.save(new Member("member4", 10, null));
        memberJpaRepository.save(new Member("member3", 10, null));
        memberJpaRepository.save(new Member("member5", 30, null));

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<Member> result = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);
        // then
        assertThat(result).hasSize(3)
                .extracting("username", "age")
                .containsExactly(
                        Tuple.tuple("member4", 10),
                        Tuple.tuple("member3", 10),
                        Tuple.tuple("member2", 10)
                );

        assertThat(totalCount).isEqualTo(4L);
    }

    @Test
    public void paging2() {
        // given
        memberJpaRepository.save(new Member("member2", 10, null));
        memberJpaRepository.save(new Member("member1", 10, null));
        memberJpaRepository.save(new Member("member4", 10, null));
        memberJpaRepository.save(new Member("member3", 10, null));
        memberJpaRepository.save(new Member("member5", 30, null));

        int age = 10;
        int offset = 1;
        int limit = 3;

        // when
        List<Member> result = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);
        // then
        assertThat(result).hasSize(3)
                .extracting("username", "age")
                .containsExactly(
                        Tuple.tuple("member3", 10),
                        Tuple.tuple("member2", 10),
                        Tuple.tuple("member1", 10)
                );

        assertThat(totalCount).isEqualTo(4L);
    }

    @Test
    public void pagingAsync() throws ExecutionException, InterruptedException {
        // given
        memberJpaRepository.save(new Member("member2", 10, null));
        memberJpaRepository.save(new Member("member1", 10, null));
        memberJpaRepository.save(new Member("member4", 10, null));
        memberJpaRepository.save(new Member("member3", 10, null));
        memberJpaRepository.save(new Member("member5", 30, null));

        entityManager.flush();

        Thread.sleep(2000);

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        CompletableFuture<List<Member>> resultFuture = memberJpaRepository.findByPageAsync(age, offset, limit);
        CompletableFuture<Long> totalCountFuture = memberJpaRepository.totalCountAsync(age);

        CompletableFuture.allOf(resultFuture, totalCountFuture).join();

        List<Member> result = resultFuture.get();
        long totalCount = totalCountFuture.get();

        // then
        assertThat(result).hasSize(3)
                        .extracting("username", "age")
                                .containsExactly(
                                        Tuple.tuple("member4", 10),
                                        Tuple.tuple("member3", 10),
                                        Tuple.tuple("member2", 10)
                                );
        assertThat(totalCount).isEqualTo(4L);
    }

    @Test
    void bulkUpdate() {
        // given
        Member member2 = memberJpaRepository.save(new Member("member2", 10, null));
        Member member1 = memberJpaRepository.save(new Member("member1", 15, null));
        Member member4 = memberJpaRepository.save(new Member("member4", 20, null));
        Member member3 = memberJpaRepository.save(new Member("member3", 25, null));
        Member member5 = memberJpaRepository.save(new Member("member5", 30, null));

        // when ( 1차 캐시 clear X ) - log 확인
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        entityManager.clear();

        // then
        assertThat(resultCount).isEqualTo(3);
        List<Member> members = memberJpaRepository.findAll();
        assertThat(members).hasSize(5)
                .extracting("age")
                .containsExactlyInAnyOrder(10,15,21,26,31);
    }


}
