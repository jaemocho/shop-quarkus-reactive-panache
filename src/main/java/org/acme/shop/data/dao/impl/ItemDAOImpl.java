package org.acme.shop.data.dao.impl;

import java.util.List;

import org.acme.shop.data.dao.ItemDAO;
import org.acme.shop.data.entity.Item;
import org.acme.shop.data.repository.ItemRepository;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ItemDAOImpl implements ItemDAO{
    
    @Inject
    ItemRepository itemRepository;
    
    public Uni<Item> save(Item item){
        return itemRepository.persist(item);
    }

    public Uni<Void> delete(Item item) {
        return itemRepository.delete(item);                    
    }

    public Uni<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Uni<List<Item>> findAll() {
        return itemRepository.findAll().list();
    }

    public Uni<List<Item>> findByCategoryId(Long id){
        return itemRepository.findByCategoryId(id);
    }

    public Uni<Item> findByIdForUpdate(Long id){
        return itemRepository.findByIdForUpdate(id);
    }
}
