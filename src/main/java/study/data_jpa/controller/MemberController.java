package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // 도메인 클래스 컨버터 ( 실무팁: 조회용으로만 써야 한다. )
    // id(PK) 값이면서 엔티티로 파라미터로 받으면 도메인 클래스 컨버터가 알아서 조회를 해온다.
    // 하지만 이는 트랜잭션이 없는 범위에서 이뤄지기 떄문에 영속성 컨텍스트의 관리를 받지 못한다.
    // 그리고 도메인 클래스 컨버터의 경우 별도의 의존성 주입을 하지 않아도 동작한다.
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    // default : /members?page=0&size=20&sort=id,asc (asc는 생략 가능)
    // 만약 default 값을 변경하고 싶다면 application.yml 참조 >> 글로벌 설정
    // 별도 메소드에 설정하고 싶다면? members2를 참조 :: 당연히 개별 설정이 우선권을 갖는다.
    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable) {
        Page<Member> member = memberRepository.findAll(pageable);
        Page<MemberDto> result = member.map(MemberDto::new);
        return result;
    }

    @GetMapping("/members2")
    public Page<MemberDto> list2(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        Page<Member> member = memberRepository.findAll(pageable);
        Page<MemberDto> result = member.map(MemberDto::new);
        return result;
    }

    // @PostConstruct
    public void init() {
        for(int i = 0; i < 100; i++) {
            memberRepository.save(new Member("member" + i, i, null));
        }
    }
}
