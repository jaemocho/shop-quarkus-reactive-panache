package org.acme.shop.application.port.out;

import java.util.List;

import org.acme.shop.domain.OrderItem;


public interface OrderItemPersistencePort {
    public OrderItem save(OrderItem orderItem);

    public List<OrderItem> findByOrderId(Long orderId);

    public void delete(OrderItem orderItem);
}
