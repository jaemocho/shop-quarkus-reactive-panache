package org.acme.shop.web;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dto.ReqOrderDto;
import org.acme.shop.data.dto.RespOrderDto;
import org.acme.shop.data.entity.Order;
import org.acme.shop.data.entity.OrderItem;
import org.acme.shop.service.OrderService;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/api/v1/shop")
public class OrderController {

    @Inject
    OrderService orderService;

    @POST
    @Path("/order")
    public Uni<RespOrderDto> createOrder(ReqOrderDto reqOrderDto) throws ShopException{
        return orderService.createOrder(reqOrderDto)
                .onItem()
                .transform(o -> entityToRespDto(o));
    }

    @DELETE
    @Path("/order/{id}")
    public Uni<Response> cancelOrder(@PathParam("id") Long id) throws ShopException {
        return orderService.cancelOrder(id)
                .onItem()
                .transform(o -> Response.ok(o).build());
    }

    @GET
    @Path("/order/{id}")
    public Uni<RespOrderDto> getOrderInfoByOrderId(@PathParam("id") Long id) throws ShopException {
        return orderService.getOrderInfoByOrderId(id)
                .onItem()
                .transform(o -> entityToRespDto(o));
    }

    @GET
    @Path("/order/member/{memberId}")
    public Uni<List<RespOrderDto>> getOrderInfoByMemberId(@PathParam("memberId") String memberId) throws ShopException {
        return orderService.getOrderInfoByMemberId(memberId)
                .onItem()
                .transform(o -> entityToRespDto(o));
    }
    
    private List<RespOrderDto> entityToRespDto(List<Order> orderEntities) {
        List<RespOrderDto> respOrderDtos = new ArrayList<RespOrderDto>();

        for( Order o : orderEntities ) {
            respOrderDtos.add(entityToRespDto(o));
        }
        return respOrderDtos;
    }

    private RespOrderDto entityToRespDto(Order order) {

        List<RespOrderDto.OrderItemInfo> orderIteminfos
                 = new ArrayList<RespOrderDto.OrderItemInfo>();
        for( OrderItem o : order.getOrderItems()) {
            orderIteminfos.add(RespOrderDto.OrderItemInfo.builder()
                                        .itemId(o.getItem().getId())
                                        .itemName(o.getItem().getName())
                                        .itemPrice(o.getItem().getPrice())
                                        .itemRequestQty(o.getCount())
                                        .categoryId(o.getItem().getCategory().getId())
                                        .categoryName(o.getItem().getCategory().getName())
                                        .build());
        }
        return RespOrderDto.builder()
                        .orderId(order.getId())
                        .memberId(order.getMember().getId())
                        .orderItemInfos(orderIteminfos)
                        .orderDate(order.getOrderDate())
                        .orderState(order.getOrderState())
                        .build();
    }
}
