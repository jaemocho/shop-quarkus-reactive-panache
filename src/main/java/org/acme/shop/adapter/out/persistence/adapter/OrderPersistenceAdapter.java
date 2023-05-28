package org.acme.shop.adapter.out.persistence.adapter;

import java.util.Date;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.OrderEntity;
import org.acme.shop.adapter.out.persistence.repository.OrderRepository;
import org.acme.shop.application.port.out.OrderPersistencePort;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderPersistenceAdapter implements OrderPersistencePort {
    
    @Inject
    OrderRepository orderRepository;

    @WithTransaction
    public Uni<OrderEntity> save(OrderEntity orderEntity) {
        return orderRepository.persist(orderEntity);
    }

    @WithTransaction
    public Uni<List<OrderEntity>> findAll() {
        return orderRepository.findAll().list();
    }

    public Uni<OrderEntity> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Uni<OrderEntity> findOrderInfoByOrderId(Long id) {
        return orderRepository.findOrderInfoByOrderId(id);
    }

    public Uni<List<OrderEntity>> findOrderInfoByMemberId(String id) {
        return orderRepository.findOrderInfoByMemberId(id);
    }

    public Uni<List<OrderEntity>> findByOrderDateLessThan(Date orderDate) {
        return orderRepository.findByOrderDateLessThan(orderDate);
    }


    public Uni<List<OrderEntity>> findByOrderDateGreaterThan(Date orderDate) {
        return orderRepository.findByOrderDateGreaterThan(orderDate);
    }

    public Uni<List<OrderEntity>> findByOrderDateBetween(Date start, Date end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }

    public Uni<Void> delete(OrderEntity orderEntity) {
        return orderRepository.delete(orderEntity);
    }

}
