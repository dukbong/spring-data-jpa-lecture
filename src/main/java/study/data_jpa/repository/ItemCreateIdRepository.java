package study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.data_jpa.entity.ItemCreateId;

public interface ItemCreateIdRepository extends JpaRepository<ItemCreateId, Long> {
}
