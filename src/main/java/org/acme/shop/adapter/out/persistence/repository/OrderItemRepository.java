package org.acme.shop.adapter.out.persistence.repository;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.OrderItemEntity;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepositoryBase<OrderItemEntity, Long>{
    
    public Uni<List<OrderItemEntity>> findByOrderId(Long orderId){
        return list("SELECT oi from OrderItemEntity oi join fetch oi.orderEntity o join fetch oi.itemEntity i join fetch i.categoryEntity WHERE o.id = ? ", orderId);
    }
}
