package org.acme.shop.data.dao;

import java.util.List;

import org.acme.shop.data.entity.Item;

import io.smallrye.mutiny.Uni;

public interface ItemDAO {
    
    public Uni<Item> save(Item item);

    public Uni<Item> findById(Long id);

    public Uni<Void> delete(Item item);

    public Uni<List<Item>> findAll();

    public Uni<List<Item>> findByCategoryId(Long id);

    public Uni<Item> findByIdForUpdate(Long id);
}
