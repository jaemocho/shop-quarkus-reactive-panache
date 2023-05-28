package org.acme.shop.adapter.out.persistence.adapter;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.ItemEntity;
import org.acme.shop.adapter.out.persistence.repository.ItemRepository;
import org.acme.shop.application.port.out.ItemPersistencePort;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ItemPersistenceAdapter implements ItemPersistencePort{
    
    @Inject
    ItemRepository itemRepository;
    
    public Uni<ItemEntity> save(ItemEntity itemEntity){
        return itemRepository.persist(itemEntity);
    }

    public Uni<Void> delete(ItemEntity itemEntity) {
        return itemRepository.delete(itemEntity);                    
    }

    public Uni<ItemEntity> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Uni<List<ItemEntity>> findAll() {
        return itemRepository.findAll().list();
    }

    public Uni<List<ItemEntity>> findByCategoryId(Long id){
        return itemRepository.findByCategoryId(id);
    }

    public Uni<ItemEntity> findByIdForUpdate(Long id){
        return itemRepository.findByIdForUpdate(id);
    }
}
