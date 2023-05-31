package org.acme.shop.data.repository;

import java.util.Date;
import java.util.List;

import org.acme.shop.data.entity.Order;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@WithSession
public class OrderRepository implements PanacheRepositoryBase<Order, Long>{
    
    public Uni<List<Order>> findByOrderDateLessThan(Date orderDate) {
        return list("orderDate < ?1", orderDate);
    }

    public Uni<List<Order>> findByOrderDateGreaterThan(Date orderDate){
        return list("orderDate > ?1",orderDate);
    }

    public Uni<List<Order>>  findByOrderDateBetween(Date start, Date end) {
        return list("orderDate >= ?1 and orderDate <= ?2 ",start, end);
    }

    public Uni<Order> findOrderInfoByOrderId(Long orderId){
        return find("SELECT o from Order o join fetch o.member m join fetch o.orderItems oi join fetch oi.item i  join fetch i.category WHERE o.id = ?1" ,orderId).firstResult();
    }

    public Uni<List<Order>>  findOrderInfoByMemberId(String memberId){
        return list("SELECT o from Order o join fetch o.member m join fetch o.orderItems oi join fetch oi.item i  join fetch i.category WHERE m.id = ?1", memberId);
    }

}
