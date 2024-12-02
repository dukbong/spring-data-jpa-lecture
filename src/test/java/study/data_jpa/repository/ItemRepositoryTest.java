package study.data_jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Item;
import study.data_jpa.entity.ItemCreateId;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    private ItemCreateIdRepository itemCreateIdRepository;

    @Test
    public void save () {
        Item item = new Item("A");
        // SimpleJpaRepository를 보면 이런 식으로 하게 되면 persist가 아닌 merage가 발생한다.
        // merage는 select 후 업데이트 혹은 인서트를 하므로 비효율적이다.
        // 기본저으로 merage는 준영속 상태를 영속 상태로 변경하거나 할 때 사용하게 되기 때문에 이는 좋지 않은 설계이다.
        itemRepository.save(item);
    }

    @Test
    void saveCreateId() {
        // given
        ItemCreateId item = new ItemCreateId("A");
        // when
        itemCreateIdRepository.save(item);
        // then
    }


}