package org.acme.shop.application.service;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.application.port.in.CategoryUsecase;
import org.acme.shop.application.port.out.CategoryPersistencePort;
import org.acme.shop.application.service.dto.ReqCategoryDto;
import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.exception.ShopException;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CategoryService implements CategoryUsecase{
    
    @Inject
    CategoryPersistencePort categoryPersistencePort;

    @WithTransaction
    public Uni<CategoryEntity> addCategory(ReqCategoryDto reqCategoryDto) {
        CategoryEntity categoryEntity = createNewCategoryEntity(reqCategoryDto);
        return categoryPersistencePort.save(categoryEntity);
    }

    private CategoryEntity createNewCategoryEntity(ReqCategoryDto reqCategoryDto){
        CategoryEntity categoryEntity = CategoryEntity.builder()
                                            .name(reqCategoryDto.getName())
                                            .build();
        return categoryEntity;                                
    }

    @WithTransaction
    public Uni<Void> removeCategory(Long id) throws ShopException{
        Uni<CategoryEntity> category = getCategory(id);
        category = nullCheck(category);
        return category
                .onItem().ifNotNull()
                .transformToUni(
                        c -> categoryPersistencePort.delete(c)); 
    }

    private Uni<CategoryEntity> nullCheck(Uni<CategoryEntity> category) {
        return category.onItem().ifNull().failWith(
            CommonUtils.throwShopException(CategoryEntity.class.getSimpleName()));
    }
    

    @WithTransaction
    public Uni<List<CategoryEntity>> getAllCategory() {
        return categoryPersistencePort.findAll();
    }

    public Uni<List<CategoryEntity>> getCategoryByName(String name) {
        return categoryPersistencePort.findByName(name);
    }

    public Uni<CategoryEntity> getCategoryById(Long id) {
        Uni<CategoryEntity> category = getCategory(id);
        return nullCheck(category);
    }

    private Uni<CategoryEntity> getCategory(Long id) {
        return categoryPersistencePort.findById(id);
    }


}
