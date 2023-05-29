package org.acme.shop.data.dao.impl;

import java.util.List;


import org.acme.shop.data.dao.CategoryDAO;
import org.acme.shop.data.entity.Category;
import org.acme.shop.data.repository.CategoryRepository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CategoryDAOImpl implements CategoryDAO{

    @Inject
    CategoryRepository categoryRepository;

    public Uni<Category> save(Category category){
        return categoryRepository.persist(category);
    }

    public Uni<List<Category>> findAll() {
        return categoryRepository.findAll().list();
    }

    public Uni<List<Category>> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Uni<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Uni<Void> delete(Category category) {
        return categoryRepository.delete(category);
    }
}
