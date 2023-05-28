package org.acme.shop.adapter.out.persistence.repository;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.ItemEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

@ApplicationScoped
@WithSession
public class ItemRepository implements PanacheRepositoryBase<ItemEntity, Long>{
    
    public Uni<List<ItemEntity>> findByName(String name){
        return list("name", name);
    }

    public Uni<List<ItemEntity>> findByCategoryId(Long categoryId) {
        return list("SELECT i from ItemEntity i join fetch i.categoryEntity c WHERE c.id = :categoryId", Parameters.with("categoryId", categoryId));
    }

    public Uni<List<ItemEntity>> findByPriceGreaterThan(int price){
        return list("price > ?1", price);
    }

    public Uni<List<ItemEntity>> findByRemainQtyGreaterThan(int remainQty){
        return list("remainQty > ?1" , remainQty);
    }

    public Uni<ItemEntity> findByIdForUpdate(Long id) {
        return find("id", id).withLock(LockModeType.PESSIMISTIC_WRITE).firstResult();
    }

}
