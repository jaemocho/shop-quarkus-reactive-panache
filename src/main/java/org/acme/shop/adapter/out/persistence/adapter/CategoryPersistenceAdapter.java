package org.acme.shop.adapter.out.persistence.adapter;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.adapter.out.persistence.repository.CategoryRepository;
import org.acme.shop.application.port.out.CategoryPersistencePort;
import org.hibernate.reactive.mutiny.Mutiny;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CategoryPersistenceAdapter implements CategoryPersistencePort{

    @Inject
    CategoryRepository categoryRepository;

    public Uni<CategoryEntity> save(CategoryEntity categoryEntity){
        return categoryRepository.persist(categoryEntity);
    }

    public Uni<List<CategoryEntity>> findAll() {
        return categoryRepository.findAll().list();
    }

    public Uni<List<CategoryEntity>> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Uni<CategoryEntity> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Uni<Void> delete(CategoryEntity categoryEntity) {
        return categoryRepository.delete(categoryEntity);
    }
}
