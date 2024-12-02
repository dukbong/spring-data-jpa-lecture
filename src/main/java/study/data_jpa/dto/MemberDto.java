package study.data_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import study.data_jpa.entity.Member;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    // dto는 엔티티를 매개변수로 받아도 괜찮지만 가급적 엔티티는 dto를 받지 않는것이 좋다.
    public MemberDto(Member member){
        this.id = member.getId();
        this.username = member.getUsername();
        if(member.getTeam() != null){
            this.teamName = member.getTeam().getName();
        }
    }
}
