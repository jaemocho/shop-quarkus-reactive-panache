package org.acme.shop.adapter.out.persistence.repository;

import java.util.Date;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.OrderEntity;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<OrderEntity, Long>{
    
    public Uni<List<OrderEntity>> findByOrderDateLessThan(Date orderDate) {
        return list("OrderDate", orderDate);
    }

    public Uni<List<OrderEntity>> findByOrderDateGreaterThan(Date orderDate){
        return list("orderDate > ?1",orderDate);
    }

    public Uni<List<OrderEntity>>  findByOrderDateBetween(Date start, Date end) {
        return list("orderDate >= ?1 and orderDate <= ?2 ",start, end);
    }

    public Uni<OrderEntity> findOrderInfoByOrderId(Long orderId){
        return find("SELECT o from OrderEntity o join fetch o.memberEntity m join fetch o.orderItemEntities oi join fetch oi.itemEntity i  join fetch i.categoryEntity WHERE o.id = ?1" ,orderId).firstResult();
    }

    public Uni<List<OrderEntity>>  findOrderInfoByMemberId(String memberId){
        return list("SELECT o from OrderEntity o join fetch o.memberEntity m join fetch o.orderItemEntities oi join fetch oi.itemEntity i  join fetch i.categoryEntity WHERE m.id = ?1", memberId);
    }

}
