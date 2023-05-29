package org.acme.shop.service.impl;

import java.util.Date;
import java.util.List;

import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.Constants.ExceptionClass;
import org.acme.shop.common.ShopConstants.OrderState;
import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dao.ItemDAO;
import org.acme.shop.data.dao.OrderDAO;
import org.acme.shop.data.dto.ReqOrderDto;
import org.acme.shop.data.entity.Item;
import org.acme.shop.data.entity.Member;
import org.acme.shop.data.entity.Order;
import org.acme.shop.data.entity.OrderItem;
import org.acme.shop.service.ItemService;
import org.acme.shop.service.MemberService;
import org.acme.shop.service.OrderService;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderServiceImpl implements OrderService {

    @Inject
    OrderDAO orderPersistencePort;
    
    @Inject
    ItemDAO itemPersistencePort;

    @Inject
    MemberService memberService;

    @Inject
    ItemService itemService;

    @WithTransaction
    public Uni<Order> createOrder(ReqOrderDto reqOrderDto) throws ShopException {
        Order order = createNewOrder();
        Uni<Member> orderMemberUni = memberService.getMemberById(reqOrderDto.getMemberId());
        Uni<Order> orderUni = orderMemberUni
                            .invoke(m -> order.setMember(m))
                            .map(m -> order);
        orderUni = addOrderItemToOrder(reqOrderDto, order, orderUni);
        return orderUni.flatMap(o -> orderPersistencePort.save(o));
    }

    private Order createNewOrder() {
        Order order = Order.builder()
                            .orderDate(new Date())
                            .orderState(OrderState.REQUEST)
                            .build();
        return order;
    }

    private Uni<Order> addOrderItemToOrder(ReqOrderDto reqOrderDto, Order order, Uni<Order> orderUni) {
        return orderUni.map(o -> reqOrderDto)
            .onItem().transformToMulti(r -> Multi.createFrom().iterable(r.getRequestItem()))
            .onItem().transformToUniAndConcatenate(r -> 
                itemService.getItemForUpdate(r.getItemId())
                .invoke(i -> i.removeRemainQty(r.getRequestQty()))
                .map(i -> createOrderItem(i, r.getRequestQty()))
            ).collect().asList()
            .map(oi -> addOrderItems(oi, order));
    }

    private OrderItem createOrderItem(Item item, int requestQty) {
        OrderItem orderItem = OrderItem.builder()
                                    .item(item)
                                    .count(requestQty)
                                    .build();
        return orderItem;                                    
    }

    private Order addOrderItems(List<OrderItem> orderItems, Order order) {
        for(OrderItem oi : orderItems) {
            order.addOrderItems(oi);
        }
        return order;
    }

    // private Uni<Order> addOrderItemToOrder(ReqOrderDto reqOrderDto
    //                                             , Order orderEntity, Uni<Order> order) {
    //     for( ReqOrderDto.RequestItem requestItem : reqOrderDto.getRequestItem() ) {
    //         order = order.map(o -> requestItem)
    //         .flatMap(r -> itemService.getItemForUpdate(r.getItemId()))
    //         .invoke(i -> i.removeRemainQty(requestItem.getRequestQty()))
    //         .map(i -> createOrderItem(i, requestItem.getRequestQty()))
    //         .map(oi -> setOrderToOrderItem(oi, orderEntity));
    //     }
    //     return order;
    // }


    public Uni<Order> getOrderInfoByOrderId(Long orderId) throws ShopException {
        Uni<Order> orderUni = orderPersistencePort.findOrderInfoByOrderId(orderId);
        return nullCheck(orderUni);
    }

    private Uni<Order> nullCheck(Uni<Order> orderUni) {
        return orderUni.onItem().ifNull()
                .failWith(CommonUtils.throwShopException(Order.class.getSimpleName()));
    }

    public Uni<List<Order>> getOrderInfoByMemberId(String memberId) {
        return orderPersistencePort.findOrderInfoByMemberId(memberId);
    }

    @WithTransaction
    public Uni<Void> cancelOrder(Long orderId) throws ShopException {
        Uni<Order> orderUni = orderPersistencePort.findOrderInfoByOrderId(orderId);
        orderUni = nullCheck(orderUni);
        orderUni = orderUni.invoke(o -> vaildateOrderStateForCancel(o))
                .invoke(o -> updateOrderStatus(o, OrderState.CANCEL));
        return cancelOrderItem(orderUni);
    }

    private void vaildateOrderStateForCancel(Order orderEntity) {
        if( !OrderState.REQUEST.equals(orderEntity.getOrderState())) {
            throw new ShopException(ExceptionClass.SHOP
            , HttpResponseStatus.BAD_REQUEST
            , "REQUEST 상태일 때만 취소 가능합니다. 관리자에게 문의하세요");
        }
    }

    private void updateOrderStatus(Order order, OrderState orderState) {
        order.updateOrderStatus(orderState);
    }

    private Uni<Void> cancelOrderItem(Uni<Order> orderUni) {
        return orderUni
        .onItem().transformToMulti(oi -> Multi.createFrom().iterable(oi.getOrderItems()))
        .onItem().transformToUniAndConcatenate(oi ->  
                            itemService.getItemForUpdate(oi.getItem().getId())
                            .onItem().invoke(i -> addRemainQty(i, oi.getCount()))
        ).collect().asList().replaceWithVoid();
    }

    private void addRemainQty(Item item, int count) {
        item.addRemainQty(count);
    }

    // public Long updateOrderStatus(Long orderId, OrderState orderState) throws ShopException {
    //     // order 는 공유자원이 아니라 for update 없이 
    //     Order order = orderPersistencePort.findById(orderId);
    //     CommonUtils.nullCheckAndThrowException(order, Order.class.getName());
    //     updateOrderStatus(order, orderState);
    //     return orderId;
    // }



}
