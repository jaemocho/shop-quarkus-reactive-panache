package org.acme.shop.data.dao;

import java.util.List;

import org.acme.shop.data.entity.Category;

import io.smallrye.mutiny.Uni;

public interface CategoryDAO {
    public Uni<Category> save(Category category);

    public Uni<List<Category>> findAll();

    public Uni<List<Category>> findByName(String name);

    public Uni<Category> findById(Long id);

    public Uni<Void> delete(Category category);
}
