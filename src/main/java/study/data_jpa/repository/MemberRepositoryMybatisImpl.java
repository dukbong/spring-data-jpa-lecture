package study.data_jpa.repository;

import lombok.RequiredArgsConstructor;
import study.data_jpa.entity.Member;
import org.apache.ibatis.session.SqlSession;

import java.util.List;


@RequiredArgsConstructor
public class MemberRepositoryMybatisImpl implements MemberRepositoryMybatis {

    private final SqlSession sqlSession;

    private String namespace = "study.data_jpa.repository.MemberRepositoryMybatisImpl.";

    @Override
    public List<Member> findMemberMybatisCustom() {
        return sqlSession.selectList(namespace + "findMemberMybatisCustom");
    }
}
