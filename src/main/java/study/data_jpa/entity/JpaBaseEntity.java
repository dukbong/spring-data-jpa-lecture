package study.data_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 속성 상속 관계이기 때문에 MappedSuperclass를 사용해야한다.
@MappedSuperclass
@Getter @Setter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    // 저장 전 세팅
    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createDate = now;
        this.updateDate = now;
    }

    // 업데이트 전 세팅
    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }
}
