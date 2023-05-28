package org.acme.shop.application.port.out;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import io.smallrye.mutiny.Uni;

public interface CategoryPersistencePort {
    public Uni<CategoryEntity> save(CategoryEntity categoryEntity);

    public Uni<List<CategoryEntity>> findAll();

    public Uni<List<CategoryEntity>> findByName(String name);

    public Uni<CategoryEntity> findById(Long id);

    public Uni<Void> delete(CategoryEntity categoryEntity);
}
