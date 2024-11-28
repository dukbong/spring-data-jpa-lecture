package study.data_jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberJpaRepository;
import study.data_jpa.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberJpaRepository memberJpaRepository;

    @GetMapping("/all")
    public List<Member> all () {
        return memberJpaRepository.findAll();
    }
}
