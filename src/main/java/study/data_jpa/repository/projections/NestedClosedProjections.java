package study.data_jpa.repository.projections;

// 중첩 프로젝션
public interface NestedClosedProjections {
    // 첫번째는 정확히 가져오는 것을 확인 함
    String getUsername();
    // 두번째는 부터는 그냥 엔티티 자체를 가져오게 된다. ( 최적화가 안된다는 말이다. )
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
