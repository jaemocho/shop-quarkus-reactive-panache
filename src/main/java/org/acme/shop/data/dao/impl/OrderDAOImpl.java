package org.acme.shop.data.dao.impl;

import java.util.Date;
import java.util.List;

import org.acme.shop.data.dao.OrderDAO;
import org.acme.shop.data.entity.Order;
import org.acme.shop.data.repository.OrderRepository;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderDAOImpl implements OrderDAO {
    
    @Inject
    OrderRepository orderRepository;

    @WithTransaction
    public Uni<Order> save(Order order) {
        return orderRepository.persist(order);
    }

    @WithTransaction
    public Uni<List<Order>> findAll() {
        return orderRepository.findAll().list();
    }

    public Uni<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Uni<Order> findOrderInfoByOrderId(Long id) {
        return orderRepository.findOrderInfoByOrderId(id);
    }

    public Uni<List<Order>> findOrderInfoByMemberId(String id) {
        return orderRepository.findOrderInfoByMemberId(id);
    }

    public Uni<List<Order>> findByOrderDateLessThan(Date orderDate) {
        return orderRepository.findByOrderDateLessThan(orderDate);
    }


    public Uni<List<Order>> findByOrderDateGreaterThan(Date orderDate) {
        return orderRepository.findByOrderDateGreaterThan(orderDate);
    }

    public Uni<List<Order>> findByOrderDateBetween(Date start, Date end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }

    public Uni<Void> delete(Order order) {
        return orderRepository.delete(order);
    }

}
