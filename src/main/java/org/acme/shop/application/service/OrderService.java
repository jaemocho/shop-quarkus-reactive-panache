package org.acme.shop.application.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acme.shop.application.port.in.OrderUsecase;
import org.acme.shop.application.port.out.ItemPersistencePort;
import org.acme.shop.application.port.out.OrderPersistencePort;
import org.acme.shop.application.service.dto.ReqOrderDto;
import org.acme.shop.application.service.dto.RespOrderDto;
import org.acme.shop.common.CommonUtils;
import org.acme.shop.common.Constants.ExceptionClass;
import org.acme.shop.common.ShopConstants.OrderState;
import org.acme.shop.common.exception.ShopException;
import org.acme.shop.domain.Item;
import org.acme.shop.domain.Member;
import org.acme.shop.domain.Order;
import org.acme.shop.domain.OrderItem;

import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderService implements OrderUsecase {

    @Inject
    OrderPersistencePort orderPersistencePort;
    
    @Inject
    ItemPersistencePort itemPersistencePort;

    @Inject
    MemberService memberService;

    @Inject
    ItemService itemService;


    public Long createOrder(ReqOrderDto reqOrderDto) throws ShopException {
        Member orderMember = null ;//memberService.getMember(reqOrderDto.getMemberId());
        CommonUtils.nullCheckAndThrowException(orderMember, Member.class.getName());
        
        Order order = createNewOrder();
        order.setMember(orderMember);
        addOrderItemToOrder(reqOrderDto, order);
        order = orderPersistencePort.save(order);
        return order.getId();
    }

    public RespOrderDto getOrderInfoByOrderId(Long orderId) throws ShopException {

        // 상세 정보 필요해서 fetch join 사용 
        Order order = orderPersistencePort.findOrderInfoByOrderId(orderId);
        CommonUtils.nullCheckAndThrowException(order, Order.class.getName());
        return entityToRespDto(order);
    }

    public List<RespOrderDto> getOrderInfoByMemberId(String memberId) {
        // 상세 정보 필요해서 fetch join 사용 
        return entityToRespDto(orderPersistencePort.findOrderInfoByMemberId(memberId));
    }


    public Long cancelOrder(Long orderId) throws ShopException {
        Order order = orderPersistencePort.findById(orderId);
        CommonUtils.nullCheckAndThrowException(order, Order.class.getName());
        vaildateOrderStateForCancel(order);
        updateOrderStatus(order, OrderState.CANCEL);
        cancelOrderItem(order);
        return orderId;
    }

    public Long updateOrderStatus(Long orderId, OrderState orderState) throws ShopException {
        // order 는 공유자원이 아니라 for update 없이 
        Order order = orderPersistencePort.findById(orderId);
        CommonUtils.nullCheckAndThrowException(order, Order.class.getName());
        updateOrderStatus(order, orderState);
        return orderId;
    }

    private Order createNewOrder() {
        Order order = Order.builder()
                            .orderDate(new Date())
                            .orderState(OrderState.REQUEST)
                            .build();
        return order;
    }

    private void updateOrderStatus(Order order, OrderState orderState) {
        order.updateOrderStatus(orderState);
        orderPersistencePort.save(order);
    }


    private void vaildateOrderStateForCancel(Order order) {
        if( !OrderState.REQUEST.equals(order.getOrderState())) {
            throw new ShopException(ExceptionClass.SHOP
            , HttpResponseStatus.BAD_REQUEST
            , "REQUEST 상태일 때만 취소 가능합니다. 관리자에게 문의하세요");
        }
    }

    
    private void addOrderItemToOrder(ReqOrderDto reqOrderDto, Order order) {
        OrderItem orderItem;
        Item item;
        for( ReqOrderDto.RequestItem requestItem : reqOrderDto.getRequestItem() ) {
            item = null;// itemService.getItemForUpdate(requestItem.getItemId());
            CommonUtils.nullCheckAndThrowException(item, Item.class.getName());
            itemRemoveRemainQty(item, requestItem.getRequestQty());
            orderItem = createOrderItem(item, requestItem.getRequestQty());
            setOrderToOrderItem(orderItem, order);
        }
    }

    private void itemRemoveRemainQty(Item item, int requestQty) {
        item.removeRemainQty(requestQty);
        //itemPersistencePort.save(item);
    }

    private void setOrderToOrderItem(OrderItem orderItem, Order order) {
        order.addOrderItems(orderItem);
    }

    private OrderItem createOrderItem(Item item, int requestQty) {
        OrderItem orderItem = OrderItem.builder()
                                    .item(item)
                                    .count(requestQty)
                                    .build();
        return orderItem;                                    
    }


    private void cancelOrderItem(Order order) {
        Item item; 
        for ( OrderItem oi : order.getOrderItems()) {
            item = null; // itemService.getItemForUpdate(oi.getItem().getId());
            if ( item == null ) continue;
            itemAddRemainQty(item, oi.getCount());
        }
    }

    private void itemAddRemainQty(Item item, int count) {
        item.addRemainQty(count);
        //itemPersistencePort.save(item);
    }


    private List<RespOrderDto> entityToRespDto(List<Order> orders) {
        List<RespOrderDto> respOrderDtos = new ArrayList<RespOrderDto>();

        for( Order o : orders ) {
            respOrderDtos.add(entityToRespDto(o));
        }
        return respOrderDtos;
    }

    private RespOrderDto entityToRespDto(Order order) {

        List<RespOrderDto.OrderItemInfo> orderIteminfos
                 = new ArrayList<RespOrderDto.OrderItemInfo>();
        
        // orderItems resp dto로 반환 
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
