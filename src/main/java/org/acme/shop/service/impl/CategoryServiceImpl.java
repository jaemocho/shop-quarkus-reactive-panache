package org.acme.shop.service.impl;

import java.util.List;

import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.CategoryDAO;
import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.data.entity.Category;
import org.acme.shop.service.CategoryService;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CategoryServiceImpl implements CategoryService{
    
    @Inject
    CategoryDAO categoryDAO;

    @WithTransaction
    public Uni<Category> addCategory(ReqCategoryDto reqCategoryDto) {
        Category category = createNewCategoryEntity(reqCategoryDto);
        return categoryDAO.save(category);
    }

    private Category createNewCategoryEntity(ReqCategoryDto reqCategoryDto){
        Category category = Category.builder()
                                            .name(reqCategoryDto.getName())
                                            .build();
        return category;                                
    }

    @WithTransaction
    public Uni<Void> removeCategory(Long id) throws ShopException{
        Uni<Category> categoryUni = getCategory(id);
        categoryUni = nullCheck(categoryUni);
        return categoryUni
                .onItem().ifNotNull()
                .transformToUni(c -> categoryDAO.delete(c));
    }

    private Uni<Category> getCategory(Long id) {
        return categoryDAO.findById(id);
    }

    private Uni<Category> nullCheck(Uni<Category> categoryUni) {
        return categoryUni.onItem().ifNull().failWith(
            CommonUtils.throwShopException(Category.class.getSimpleName()));
    }
    

    @WithTransaction
    public Uni<List<Category>> getAllCategory() {
        return categoryDAO.findAll();
    }

    public Uni<List<Category>> getCategoryByName(String name) {
        return categoryDAO.findByName(name);
    }

    public Uni<Category> getCategoryById(Long id) {
        Uni<Category> category = getCategory(id);
        return nullCheck(category);
    }

}
