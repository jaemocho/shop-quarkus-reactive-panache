package org.acme.shop.service;

import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dto.ReqOrderDto;
import org.acme.shop.data.entity.Order;

import io.smallrye.mutiny.Uni;

public interface OrderService {
    public Uni<Order> createOrder(ReqOrderDto reqOrderDto) throws ShopException;

    public Uni<Order> getOrderInfoByOrderId(Long orderId) throws ShopException;

    public Uni<List<Order>> getOrderInfoByMemberId(String memberId);

    public Uni<Void> cancelOrder(Long orderId) throws ShopException;

    //public Long updateOrderStatus(Long orderId, OrderState orderState) throws ShopException;
}
