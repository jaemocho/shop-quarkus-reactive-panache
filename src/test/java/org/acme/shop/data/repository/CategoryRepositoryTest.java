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
public class CategoryRepositoryTest {
    
    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ItemRepository itemRepository;

    @Test
    @TestReactiveTransaction
    public void category_test(UniAsserter asserter) {
        
        // category 초기 date insert
        Category category1 = Category.builder()
                                .name("WOMEN")
                                .build();

        
        Category category2 = Category.builder()
                                .name("MEN")
                                .build();        

        Category category3 = Category.builder()
                                .name("KIDS")
                                .build();

        Category category4 = Category.builder()
                                .name("BABY")
                                .build();                                

        asserter 
        .execute(() -> categoryRepository.persistAndFlush(category1))
        .execute(() -> categoryRepository.persistAndFlush(category2))
        .execute(() -> categoryRepository.persistAndFlush(category3))
        .execute(() -> categoryRepository.persistAndFlush(category4))

        // 전체 조회 테스트
        .assertThat(() -> categoryRepository.findAll().list(), categorys -> {
                    Assertions.assertEquals(4, categorys.size());
        })

        // 삭제 테스트
        .execute(() -> categoryRepository.deleteById(1L))
        .assertThat(() -> categoryRepository.findAll().list(), categorys -> {
            Assertions.assertEquals(3, categorys.size());
        })

        // findById 테스트 
        .assertThat(() -> categoryRepository.findById(2L), category -> {
            Assertions.assertEquals("MEN", category.getName());
        });


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

        asserter 
        .execute(() -> itemRepository.persistAndFlush(item1))
        .execute(() -> itemRepository.persistAndFlush(item2));
        
        // MEN category에 item 1.2 추가 
        category2.addItem(item1);
        category2.addItem(item2);

        // categorys 조회 테스트 
        asserter
        .assertThat(() -> categoryRepository.findByName("MEN"), categorys -> { 
            Assertions.assertEquals(2, categorys.get(0).getItems().size());
        });
        

    }
}
