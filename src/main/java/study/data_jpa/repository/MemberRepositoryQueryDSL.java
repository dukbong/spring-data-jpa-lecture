package study.data_jpa.repository;

import study.data_jpa.entity.Member;

import java.util.List;

public interface MemberRepositoryQueryDSL {
    List<Member> findMemberQueryDSLCustom();
}
