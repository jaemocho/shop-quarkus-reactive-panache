package org.acme.shop.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.CategoryDAO;
import org.acme.shop.data.dao.ItemDAO;
import org.acme.shop.data.dto.ReqItemDto;
import org.acme.shop.data.entity.Category;
import org.acme.shop.data.entity.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

@QuarkusTest
public class ItemServiceTest {
    
    @Inject
    private ItemService itemService;

    @InjectMock
    private CategoryDAO categoryDAO;

    @InjectMock
    private ItemDAO itemDAO;

    @Test
    @RunOnVertxContext
    public void addItem_test(UniAsserter asserter) {

        //given
        ReqItemDto reqItemDto = ReqItemDto.builder()
                                        .name("T-shirt")
                                        .price(5000)
                                        .remainQty(100)
                                        .categoryId(1L)
                                        .build();

        Category category = Category.builder()
                                .id(1L)
                                .name("WOMEN")
                                .build();

        Item item = Item.builder()
                    .id(1L)
                    .name("T-shirt")
                    .price(5000)
                    .remainQty(100)
                    .build();
        item.setCategory(category);

        when(categoryDAO.findById(reqItemDto.getCategoryId())).
            thenReturn(Uni.createFrom().item(category));
        when(itemDAO.save(any(Item.class)))
            .thenReturn(Uni.createFrom().item(item));

        //when
        asserter.assertThat(() -> itemService.addItem(reqItemDto)
        , i -> {
            //then 
            Assertions.assertEquals("WOMEN", i.getCategory().getName());
        });
    }

    @Test
    @RunOnVertxContext
    public void getAllItem_test(UniAsserter asserter) {
        
        //given
        Category category = Category.builder()
                                .id(1L)
                                .name("WOMEN")
                                .build();

        Item item1 = Item.builder()
                        .id(1L)
                        .name("T-shirt")
                        .price(5000)
                        .remainQty(100)
                        .build();

        Item item2 = Item.builder()
                        .id(2L)
                        .name("T-shirt")
                        .price(5000)
                        .remainQty(100)
                        .build();

        Item item3 = Item.builder()
                        .id(3L)
                        .name("T-shirt")
                        .price(5000)
                        .remainQty(100)
                        .build();

        item1.setCategory(category);
        item3.setCategory(category);
                                
        List<Item> items = new ArrayList<Item>();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        when(itemDAO.findAll())
            .thenReturn(Uni.createFrom().item(items));

        // when
        asserter.assertThat(() -> itemService.getAllItem()
            , getItems -> { 
                // then
                Assertions.assertEquals(3,getItems.size());
                
                long cnt = getItems.stream()
                            .filter( i-> 
                                (i.getCategory() != null ) &&
                                    ("WOMEN".equals(i.getCategory().getName()))
                            )
                            .count();
                Assertions.assertEquals(2,cnt);
            });
    }

    @Test
    @RunOnVertxContext
    public void getItemByCategoryId_test(UniAsserter asserter) {
        
        //given
        Category category = Category.builder()
                                .id(1L)
                                .name("WOMEN")
                                .build();

        Item item1 = Item.builder()
                        .id(1L)
                        .name("T-shirt")
                        .price(5000)
                        .remainQty(100)
                        .category(category)
                        .build();
        
        Item item2 = Item.builder()
                        .id(2L)
                        .name("T-shirt")
                        .price(5000)
                        .remainQty(100)
                        .category(category)
                        .build();
        
        Item item3 = Item.builder()
                        .id(3L)
                        .name("T-shirt")
                        .price(5000)
                        .remainQty(100)
                        .category(category)
                        .build();

        List<Item> items = new ArrayList<Item>();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        when(itemDAO.findByCategoryId(1L))
            .thenReturn(Uni.createFrom().item(items));

        // when
        asserter.assertThat(()-> itemService.getItemByCategoryId(1L), 
            getItems -> {
                // then
                Assertions.assertEquals(3, getItems.size());
                Long cnt = getItems.stream()
                                    .filter(i -> (i.getCategory() != null) &&
                                                    ("WOMEN".equals(i.getCategory().getName()))
                                    )
                                    .count();
                Assertions.assertEquals(3, cnt);                                    
            });
    }

    @Test
    @RunOnVertxContext
    public void updateItem_test(UniAsserter asserter) {
        
        // given
        ReqItemDto reqItemDto = ReqItemDto.builder()
                                    .name("T-shirt")
                                    .price(400)
                                    .remainQty(200)
                                    .build();

        Item item1 = Item.builder()
                        .id(1L)
                        .name("T-shirt")
                        .price(5000)
                        .remainQty(100)
                        .build();
        

        when(itemDAO.findByIdForUpdate(item1.getId()))
            .thenReturn(Uni.createFrom().item(item1));

        // when 
        asserter.assertThat(() -> itemService.updateItem(item1.getId(), reqItemDto)
            ,i -> {
                // then 
                Assertions.assertEquals(400, i.getPrice());
                Assertions.assertEquals(200, i.getRemainQty());
            });
    }

    @Test // update시도 시 item id가 없을 때
    @RunOnVertxContext
    public void updateItemException_test(UniAsserter asserter) {
        
        // given
        Long itemId = 1L;

        ReqItemDto reqItemDto = ReqItemDto.builder()
                            .name("T-shirt")
                            .price(400)
                            .remainQty(200)
                            .build();


        when(itemDAO.findByIdForUpdate(itemId))
            .thenReturn(Uni.createFrom().nullItem());

        // when then
        asserter.assertFailedWith(()-> itemService.updateItem(itemId, reqItemDto)
                , ShopException.class);
    }

    @Test
    @RunOnVertxContext
    public void getItemById_test(UniAsserter asserter) {

        // given
        Item item1 = Item.builder()
                .id(1L)
                .name("T-shirt")
                .price(5000)
                .remainQty(100)
                .build();

        when(itemDAO.findById(item1.getId()))
            .thenReturn(Uni.createFrom().item(item1));


        // when 
        asserter.assertThat(() -> itemService.getItemById(item1.getId())
            ,i -> {
                // then
                Assertions.assertEquals("T-shirt", i.getName());
                Assertions.assertEquals(5000, i.getPrice());
                Assertions.assertEquals(100, i.getRemainQty());
            });
    }

    @Test // 찾는 item id가 없을 때 
    @RunOnVertxContext
    public void getItemByIdException_test(UniAsserter asserter) throws ShopException {

        // given
        Long itemId = 1L;
        
        when(itemDAO.findById(itemId)).thenReturn(Uni.createFrom().nullItem());

                // when then
        asserter.assertFailedWith(()->itemService.getItemById(itemId)
                , ShopException.class);
    }
}
