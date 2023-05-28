package org.acme.shop.application.port.out;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.ItemEntity;

import io.smallrye.mutiny.Uni;

public interface ItemPersistencePort {
    public Uni<ItemEntity> save(ItemEntity itemEntity);

    public Uni<ItemEntity> findById(Long id);

    public Uni<Void> delete(ItemEntity itemEntity);

    public Uni<List<ItemEntity>> findAll();

    public Uni<List<ItemEntity>> findByCategoryId(Long id);

    public Uni<ItemEntity> findByIdForUpdate(Long id);
}
