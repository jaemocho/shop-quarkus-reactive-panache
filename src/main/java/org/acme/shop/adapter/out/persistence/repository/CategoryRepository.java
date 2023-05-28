package org.acme.shop.adapter.out.persistence.repository;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WithSession
public class CategoryRepository implements PanacheRepositoryBase<CategoryEntity, Long>{

    public Uni<List<CategoryEntity>> findByName(String name) {
        return list("name", name);
    }
}