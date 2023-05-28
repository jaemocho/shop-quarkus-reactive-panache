package org.acme.shop.application.port.in;

import java.util.List;

import org.acme.shop.application.service.dto.ReqOrderDto;
import org.acme.shop.application.service.dto.RespOrderDto;
import org.acme.shop.common.ShopConstants.OrderState;
import org.acme.shop.common.exception.ShopException;

public interface OrderUsecase {
    public Long createOrder(ReqOrderDto reqOrderDto) throws ShopException;

    public RespOrderDto getOrderInfoByOrderId(Long orderId) throws ShopException;

    public List<RespOrderDto> getOrderInfoByMemberId(String memberId);

    public Long cancelOrder(Long orderId) throws ShopException;

    public Long updateOrderStatus(Long orderId, OrderState orderState) throws ShopException;
}
