package org.acme.shop.adapter.out.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.domain.Category;


import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryMapper implements DomainEntityMapper<Category,CategoryEntity> {
    
    public List<CategoryEntity> domainToEntity(List<Category> categories) {
        List<CategoryEntity> categoryEntities = new ArrayList<CategoryEntity>();
        for(Category c : categories) {
            categoryEntities.add(domainToEntity(c));
        }
        return categoryEntities;   
    }

    public CategoryEntity domainToEntity(Category category) {
        if ( category == null ) {
            return null;
        }
        return CategoryEntity.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .build();
    }

    public List<Category> entityToDomain(List<CategoryEntity> categoryEntities)  {
        List<Category> categories = new ArrayList<Category>();
        for(CategoryEntity c : categoryEntities) {
            categories.add(entityToDomain(c));
        }
        return categories;
    }

    public Category entityToDomain(CategoryEntity categoryEntity) {
        if( categoryEntity == null ) {
            return null;
        }
        return Category.builder()
                    .id(categoryEntity.getId())
                    .name(categoryEntity.getName())
                    .build();
    }
}
