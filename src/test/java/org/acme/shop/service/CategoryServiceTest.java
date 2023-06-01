package org.acme.shop.service;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.CategoryDAO;
import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.data.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;


@QuarkusTest
public class CategoryServiceTest {

    @Inject
    private CategoryService categoryService;

    @InjectMock
    private CategoryDAO categoryDAO;
    
    @Test
    @RunOnVertxContext
    public void addCategory_test(UniAsserter asserter) throws Exception {

        // given
        ReqCategoryDto reqCategoryDto = ReqCategoryDto.builder()
                                            .name("WOMEN")
                                            .build();
        Category category = Category.builder()
                                .id(1L)
                                .name("WOMEN")
                                .build();

        when(categoryDAO.save(any(Category.class)))
            .thenReturn(Uni.createFrom().item(category));
        
        // when then 
        asserter.assertThat(() -> categoryService.addCategory(reqCategoryDto)
        , c -> {
            Assertions.assertEquals("WOMEN", c.getName());
        });
    }

    @Test
    @RunOnVertxContext
    public void getAllCategory_test(UniAsserter asserter) {
        when(categoryDAO.findAll())
            .thenReturn(Uni.createFrom().item(initCategoryData()));

        asserter.assertThat(() -> categoryService.getAllCategory(),
            categorys -> {
                Assertions.assertEquals(3, categorys.size());
            }
         );
    }

    @Test
    @RunOnVertxContext
    public void getCategoryById_test(UniAsserter asserter) {
        when(categoryDAO.findById(1L))
            .thenReturn(Uni.createFrom().item(initCategoryData().get(0)));

        asserter.assertThat(() -> categoryService.getCategoryById(1L),
            c -> {
                Assertions.assertEquals("WOMEN", c.getName());
            }
         );
    }

    @Test
    @RunOnVertxContext
    public void getCategoryByName_test(UniAsserter asserter) {
        when(categoryDAO.findByName("WOMEN"))
            .thenReturn(Uni.createFrom().item(initCategoryData()));

        asserter.assertThat(() -> categoryService.getCategoryByName("WOMEN"),
            categorys -> {
                Assertions.assertEquals(3, categorys.size());
            }
         );
    }

    @Test // 찾는 category id 가 없을 때 
    @RunOnVertxContext
    public void getCategoryByIdException_test(UniAsserter asserter) {

        //given
        //List<Category> categoryList = initCategoryData();

        when(categoryDAO.findById(1L))
            .thenReturn(Uni.createFrom().nullItem());
        
        // when then
        asserter.assertFailedWith(()-> categoryService.getCategoryById(1L)
        , ShopException.class);
    }

    @Test // category 삭제 시 조회가 안될 때 
    @RunOnVertxContext
    public void removeCategoryException_test(UniAsserter asserter) throws ShopException {

        //given
        // List<Category> categoryList = initCategoryData();

        when(categoryDAO.findById(1L))
            .thenReturn(Uni.createFrom().nullItem());

        // when then
        asserter.assertFailedWith(()-> categoryService.removeCategory(1L)
        , ShopException.class);
    }

    private List<Category> initCategoryData() {
        
        Category category1 = Category.builder().id(1L).name("WOMEN").build();
        Category category2 = Category.builder().id(2L).name("MEN").build();
        Category category3 = Category.builder().id(3L).name("KIDS").build();

        List<Category> categoryList = new ArrayList<Category>();
        categoryList.add(category1);
        categoryList.add(category2);
        categoryList.add(category3);

        return categoryList;

    }
}
