package study.data_jpa.repository.projections;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}") // 이런식으로 꾸밀 수 도 있다.
    // @Value 로 하면 오픈 프로젝션 ( 엔티티를 모두 조회 후 조합하여 한가지로 만든다. )
    // @Value가 없다면 클로즈 프로젝션 ( 정확히 어떤 값만 가져온다. )
    String getUsername();
}
