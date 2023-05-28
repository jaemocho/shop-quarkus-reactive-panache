package org.acme.shop.application.port.in;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.application.service.dto.ReqCategoryDto;
import org.acme.shop.common.exception.ShopException;

import io.smallrye.mutiny.Uni;

public interface CategoryUsecase {
    public Uni<CategoryEntity> addCategory(ReqCategoryDto reqCategoryDto);
    
    public Uni<Void> removeCategory(Long id) throws ShopException;

    public Uni<List<CategoryEntity>> getAllCategory();

    public Uni<List<CategoryEntity>> getCategoryByName(String name);

    public Uni<CategoryEntity> getCategoryById(Long id) throws ShopException;
}
