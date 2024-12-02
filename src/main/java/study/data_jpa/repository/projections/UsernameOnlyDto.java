package study.data_jpa.repository.projections;

import lombok.Getter;

public class UsernameOnlyDto {

    private final String username;

    // 생성자의 파라미터로 매칭해서 가져온다.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
