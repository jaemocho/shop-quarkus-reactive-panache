package org.acme.shop.adapter.out.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;
import org.acme.shop.adapter.out.persistence.entity.OrderEntity;
import org.acme.shop.adapter.out.persistence.entity.OrderItemEntity;
import org.acme.shop.domain.Member;
import org.acme.shop.domain.Order;
import org.acme.shop.domain.OrderItem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderMapper implements DomainEntityMapper<Order, OrderEntity> {

    @Inject
    DomainEntityMapper<Member, MemberEntity> memberMapper;
    
    @Inject
    DomainEntityMapper<OrderItem, OrderItemEntity> orderItemMapper;

    public List<OrderEntity> domainToEntity(List<Order> orders) {
        List<OrderEntity> orderEntities = new ArrayList<OrderEntity>();
        for(Order o : orders) {
            orderEntities.add(domainToEntity(o));
        }
        return orderEntities;
    }
    
    public OrderEntity domainToEntity(Order order) {
        if (order == null ){
            return null;
        }
        OrderEntity orderEntity =  OrderEntity.builder()
                            .id(order.getId())
                            .memberEntity(memberMapper.domainToEntity(order.getMember()))
                            .orderDate(order.getOrderDate())
                            .orderState(order.getOrderState())
                            .build();

        setOrderEntitis(orderEntity, order.getOrderItems());
        return orderEntity;
    }

    private void setOrderEntitis(OrderEntity orderEntity, List<OrderItem> orderItems) {
        for(OrderItem oi : orderItems) {
            orderEntity.addOrderItemEntities(orderItemMapper.domainToEntity(oi));
        }
    }

    public List<Order> entityToDomain(List<OrderEntity> orderEntities) {
        List<Order> orders = new ArrayList<Order>();
        for(OrderEntity oe : orderEntities) {
            orders.add(entityToDomain(oe));
        }
        return orders;                   
    }

    public Order entityToDomain(OrderEntity orderEntity) {
        if (orderEntity == null ){
            return null;
        }
        return Order.builder()
                    .id(orderEntity.getId())
                    .member(memberMapper.entityToDomain(orderEntity.getMemberEntity()))
                    .orderItems(orderItemMapper.entityToDomain(orderEntity.getOrderItemEntities()))
                    .orderDate(orderEntity.getOrderDate())
                    .orderState(orderEntity.getOrderState())
                    .build();
    }

    
}
