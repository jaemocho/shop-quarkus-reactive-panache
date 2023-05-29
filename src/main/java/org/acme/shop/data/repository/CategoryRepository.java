package org.acme.shop.data.repository;

import java.util.List;

import org.acme.shop.data.entity.Category;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WithSession
public class CategoryRepository implements PanacheRepositoryBase<Category, Long>{

    public Uni<List<Category>> findByName(String name) {
        return list("name", name);
    }
}