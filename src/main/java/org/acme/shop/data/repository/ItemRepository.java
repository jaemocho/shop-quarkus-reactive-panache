package org.acme.shop.data.repository;

import java.util.List;

import org.acme.shop.data.entity.Item;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

@ApplicationScoped
@WithSession
public class ItemRepository implements PanacheRepositoryBase<Item, Long>{
    
    public Uni<List<Item>> findByName(String name){
        return list("name", name);
    }

    public Uni<List<Item>> findByCategoryId(Long categoryId) {
        return list("SELECT i from Item i join fetch i.category c WHERE c.id = :categoryId", Parameters.with("categoryId", categoryId));
    }

    public Uni<List<Item>> findByPriceGreaterThan(int price){
        return list("price > ?1", price);
    }

    public Uni<List<Item>> findByRemainQtyGreaterThan(int remainQty){
        return list("remainQty > ?1" , remainQty);
    }

    public Uni<Item> findByIdForUpdate(Long id) {
        return find("id", id).withLock(LockModeType.PESSIMISTIC_WRITE).firstResult();
    }

}
