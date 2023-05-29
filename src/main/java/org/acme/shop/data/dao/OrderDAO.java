package org.acme.shop.data.dao;

import java.util.Date;
import java.util.List;

import org.acme.shop.data.entity.Order;

import io.smallrye.mutiny.Uni;


public interface OrderDAO {
    public Uni<Order> save(Order order);

    public Uni<List<Order>> findAll();

    public Uni<Order> findById(Long id);

    public Uni<Order> findOrderInfoByOrderId(Long id);

    public Uni<List<Order>> findOrderInfoByMemberId(String id);

    public Uni<List<Order>> findByOrderDateLessThan(Date orderDate);

    public Uni<List<Order>> findByOrderDateGreaterThan(Date orderDate);

    public Uni<List<Order>> findByOrderDateBetween(Date start, Date end);
}
