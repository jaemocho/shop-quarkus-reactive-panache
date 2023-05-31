package org.acme.shop.data.repository;

import org.acme.shop.data.entity.Category;
import org.acme.shop.data.entity.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.UniAsserter;
import jakarta.inject.Inject;

@QuarkusTest
public class ItemRepositoryTest {
    
    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ItemRepository itemRepository;

    @Test
    @TestReactiveTransaction
    public void item_test(UniAsserter asserter) {

                // category 초기 값 insert
        Category category1 = Category.builder()
                                .name("WOMEN")
                                .build();

        
        Category category2 = Category.builder()
                                .name("MEN")
                                .build();     

        asserter 
        .execute(() -> categoryRepository.persistAndFlush(category1))
        .execute(() -> categoryRepository.persistAndFlush(category2));
        
                // item insert
        Item item1 = Item.builder()
                        .name("women's T shirt")
                        .price(5000)
                        .remainQty(0)
                        .build();

        Item item2 = Item.builder()
                        .name("women's T dress")
                        .price(50000)
                        .remainQty(50)
                        .build();  

        Item item3 = Item.builder()
                        .name("men's T dress")
                        .price(55000)
                        .remainQty(50)
                        .build();  

        // item의 category 지정
        item1.setCategory(category1);
        item2.setCategory(category1);
        item3.setCategory(category2);
        
        asserter 
        .execute(() -> itemRepository.persistAndFlush(item1))
        .execute(() -> itemRepository.persistAndFlush(item2))
        .execute(() -> itemRepository.persistAndFlush(item3));

        asserter
        // women's T shirt item 조회
        .assertThat(() -> itemRepository.findByName("women's T shirt"), 
            items -> {
                Assertions.assertEquals(1, items.size());
                Assertions.assertEquals("WOMEN", items.get(0).getCategory().getName());
            })
        // WOMEN category id로 item 조회             
        .assertThat(() -> itemRepository.findByCategoryId(category1.getId()),
            items -> {
                Assertions.assertEquals(2, items.size());
            })
        // 45000 원 보다 비싼 item  조회            
        .assertThat(() -> itemRepository.findByPriceGreaterThan(45000),
        items -> {
            Assertions.assertEquals(2, items.size());
        })
        // 45000 원 보다 비싼 item  조회            
        .assertThat(() -> itemRepository.findByPriceGreaterThan(4500),
        items -> {
            Assertions.assertEquals(3, items.size());
        })
        // 재고가 0개 보다 많은 item 조회 
        .assertThat(() -> itemRepository.findByRemainQtyGreaterThan(0),
        items -> {
            Assertions.assertEquals(2, items.size());
        });
        
        // qty update test 
        item2.updateItem(item2.getName(), item2.getPrice(), 10000);

        asserter
        .assertThat(() -> itemRepository.findById(item2.getId()),
        item -> {
            Assertions.assertEquals(10000, item.getRemainQty());
        });
    }

    @Test
    @TestReactiveTransaction
    public void itemUpdate_test(UniAsserter asserter) {
        
        // 초기 item data insert
        Item item = Item.builder()
                        .name("T shirt")
                        .price(5000)
                        .remainQty(0)
                        .build();

        asserter 
        .execute(() -> itemRepository.persistAndFlush(item));
        
        asserter
        .assertThat(() -> itemRepository.findByIdForUpdate(item.getId()), 
        updateItem -> {
                updateItem.updateItem(updateItem.getName(), 500, updateItem.getRemainQty());
                asserter.assertThat(()-> itemRepository.findById(item.getId()), 
                i -> {
                    Assertions.assertEquals(500, i.getPrice());
                });
            });
    }
}
