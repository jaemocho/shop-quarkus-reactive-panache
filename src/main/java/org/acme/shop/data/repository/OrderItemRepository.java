package org.acme.shop.data.repository;
import java.util.List;

import org.acme.shop.data.entity.OrderItem;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderItemRepository implements PanacheRepositoryBase<OrderItem, Long>{
    
    public Uni<List<OrderItem>> findByOrderId(Long orderId){
        return list("SELECT oi from OrderItem oi join fetch oi.order o join fetch oi.item i join fetch i.category WHERE o.id = ? ", orderId);
    }
}
