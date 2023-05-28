package org.acme.shop.adapter.out.persistence.adapter;

import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.OrderItemEntity;
import org.acme.shop.adapter.out.persistence.mapper.DomainEntityMapper;
import org.acme.shop.adapter.out.persistence.repository.OrderItemRepository;
import org.acme.shop.application.port.out.OrderItemPersistencePort;
import org.acme.shop.domain.OrderItem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderItemPersistenceAdapter implements OrderItemPersistencePort{

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    DomainEntityMapper<OrderItem, OrderItemEntity> orderItemMapper;

    public OrderItem save(OrderItem orderItem) {
        OrderItemEntity orderItemEntity = domainToEntity(orderItem);
        orderItemEntity = orderItemRepository.persist(orderItemEntity)
                                                .subscribe()
                                                .asCompletionStage()
                                                .join();
        return entityToDomain(orderItemEntity);
    }

    public List<OrderItem> findByOrderId(Long orderId) {

        List<OrderItemEntity> orderItemEntities = orderItemRepository.findByOrderId(orderId)
                                                                .subscribe()
                                                                .asCompletionStage()
                                                                .join();
        return entityToDomain(orderItemEntities);
    }

    public void delete(OrderItem orderItem) {
        OrderItemEntity orderItemEntity = domainToEntity(orderItem);
        orderItemRepository.delete(orderItemEntity)
                                .subscribe()
                                .asCompletionStage()
                                .join();
    }

    private OrderItem entityToDomain(OrderItemEntity orderItemEntity) {
        return orderItemMapper.entityToDomain(orderItemEntity);
    }

    private List<OrderItem> entityToDomain(List<OrderItemEntity> orderItemEntities) {
        return orderItemMapper.entityToDomain(orderItemEntities);
    }

    private OrderItemEntity domainToEntity(OrderItem orderItem) {
        return orderItemMapper.domainToEntity(orderItem);
    }
}
