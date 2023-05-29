package org.acme.shop.service;

import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.data.entity.Category;

import io.smallrye.mutiny.Uni;

public interface CategoryService {
    public Uni<Category> addCategory(ReqCategoryDto reqCategoryDto);
    
    public Uni<Void> removeCategory(Long id) throws ShopException;

    public Uni<List<Category>> getAllCategory();

    public Uni<List<Category>> getCategoryByName(String name);

    public Uni<Category> getCategoryById(Long id) throws ShopException;
}
