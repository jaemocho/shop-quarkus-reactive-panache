package org.acme.shop.application.port.out;

import java.util.Date;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.OrderEntity;
import io.smallrye.mutiny.Uni;


public interface OrderPersistencePort {
    public Uni<OrderEntity> save(OrderEntity orderEntity);

    public Uni<List<OrderEntity>> findAll();

    public Uni<OrderEntity> findById(Long id);

    public Uni<OrderEntity> findOrderInfoByOrderId(Long id);

    public Uni<List<OrderEntity>> findOrderInfoByMemberId(String id);

    public Uni<List<OrderEntity>> findByOrderDateLessThan(Date orderDate);

    public Uni<List<OrderEntity>> findByOrderDateGreaterThan(Date orderDate);

    public Uni<List<OrderEntity>> findByOrderDateBetween(Date start, Date end);
}
