package study.data_jpa.repository;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import study.data_jpa.dto.MemberMybatisDto;
import study.data_jpa.entity.Member;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
public class MemberRepositoryMybatisImpl implements MemberRepositoryMybatis {

    private final SqlSession sqlSession;

    private String namespace = "study.data_jpa.repository.MemberRepositoryMybatisImpl.";

    @Override
    public List<Member> findMemberMybatisCustom() {
        return sqlSession.selectList(namespace + "findMemberMybatisCustom");
    }

    @Override
    public MemberMybatisDto findMemberByIdCustom(Long id) {
        return sqlSession.selectOne(namespace + "findMemberByIdCustom", id);
    }


}
