package study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.projections.MemberProjection;
import study.data_jpa.repository.projections.UsernameOnly;
import study.data_jpa.repository.projections.UsernameOnlyDto;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryQueryDSL, MemberRepositoryMybatis {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // Spring Data JPA에서 NamedQuery 사용 방법
    // 실무 X -> Repository에 직접 Query를 작성하면서 NamedQuery의 장점까지 가져갈 수 있는 방법이 있기 떄문이다.
    // 유일한 장점 : 어플리케이션 로딩 시점에 파싱하기 때문에 오타 났을때 확인이 빠르다.
    //             일반적인 Query는 문자열이기 때문에 사용자가 해당 기능을 사용하기 전까지는 오타인지 알 수 없다.
    @Query(name = "Member.findByUsernameWithNamedQuery")
    List<Member> findByUsernameWithNamedQuery(@Param("username") String username);

    // 실무 O
    // 어플리케이션 로딩 시점에서 파싱하기 때문에 오타 났을때 확인 할 수 있다.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findByUsernameWithDirectly(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);


    // 메소드 이름 find ~ By 안에 있는 건 아무거나 적어도 되며
    // JPA에서 반환타입은 어떤거든 쓸 수 있다.
    List<Member> findListByUsername(String username); // collections
    Member findByUsername(String username); // single
    Optional<Member> findOptionalByUsername(String username); // optional single

    // 페이징
    @Query(
            value = "select m from Member m left join m.team where m.age = :age",
            countQuery = "select count(m) from Member m where m.age = :age"
    )
    Page<Member> findByAgeWithPage(@Param("age") int age, Pageable pageable);
    Slice<Member> findByAge(int age, Pageable pageable);

    // 벌크 연산
    // 주의 사항 : 벌크 연산 시 영속성 컨테이너를 무시하고 바로 쿼리로 던지고 DB에 반영해버린다.

    /***
     * JPA 동작 순서 : 만약 JPQL을 작성했다면 앞단에 있는 걸 우선 다 flush() 후 JPQL로 작성한걸 flush() 한다.
     */

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // fetchJoin >> N + 1 문제 해결 방법 중 1개
    // 단 fetchJoin의 경우 xxToOne일때만 사용하는것을 권장
    // xxToMany가 "주"를 다루면 fetchJoin 시 데이터가 뻥튀기 되기 때문에 페이징 처리 시 그냥 지연 로딩과 batch size를 권장
    @Query("select m from Member m join fetch m.team t")
    List<Member> findMemberFetchJoin();
    // EntityGraph
    // EntityGraph는 JPQL 작성 없이 fetch Join 할 수 있다.
    // default로 left join fetch로 만들어준다. >> 아마 예상하는 이유는 "주"가 되는 데이터를 모두 가져와야 하기 때문이다.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @EntityGraph("Member.all")
    List<Member> findNamedEntityGraphByUsername(@Param("username") String username);

    // JPA HINT
    // 스냅샵을 만들지 않겠다.
    // 아무리 최적화를 했다한들 이런건 다 비용이기 때문에 읽는거에 스냅샵을 만들 필요ㅇ가 없다.
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(@Param("username") String username);

    // lock
    // 실무에서 실시간 트래픽이 많은 곳에서 사용시 성능 저하가 있기 떄문에 낙관적 락을 사용해서 로직으로 풀어내는게 좋다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);


    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
    List<UsernameOnlyDto> findProjections2ByUsername(@Param("username") String username);
    // 쿼리는 같지만 가져오게 싶은게 다를때 사용하기 좋다. (동적 프로젝션)
    <T> List<T> findProjections3ByUsername(@Param("username") String username, Class<T> clazz);


    // 네이티브 쿼리 ( 제약이 너무 많다. )
    // 1. sort 정상 동작 하지 않을 수 있다.
    // 2. jpql처럼 애플리케이션 로딩 시점에서 문법 확인 불가
    // 3. 동적 쿼리 불가
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName from member m left join team t",
    countQuery = "select count(*) from member",
    nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
