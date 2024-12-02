package study.data_jpa.repository;

import org.apache.ibatis.annotations.Options;
import study.data_jpa.dto.MemberMybatisDto;
import study.data_jpa.entity.Member;

import java.util.List;

public interface MemberRepositoryMybatis {
    List<Member> findMemberMybatisCustom();
    MemberMybatisDto findMemberByIdCustom(Long id);
}
