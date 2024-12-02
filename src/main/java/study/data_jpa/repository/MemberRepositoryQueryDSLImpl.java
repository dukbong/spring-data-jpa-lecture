package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.data_jpa.entity.Member;

import java.util.List;

@RequiredArgsConstructor
// 사용자 정의 레포지토리 만들때 규칙은 구현체의 이름을 메인 레포지토리 + Impl 이렇게 맞춰야한다.
public class MemberRepositoryQueryDSLImpl implements MemberRepositoryQueryDSL {

    private final EntityManager em;

    @Override
    public List<Member> findMemberQueryDSLCustom() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
