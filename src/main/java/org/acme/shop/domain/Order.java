package org.acme.shop.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acme.shop.common.ShopConstants.OrderState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;

    private Member member;

    public void setMember(Member member) {
        this.member = member;

        if(!member.getOrders().contains(this)) {
            member.getOrders().add(this);
        }
    }
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();
    
    
    public void addOrderItems(OrderItem orderItem) {
        this.orderItems.add(orderItem);

        if (orderItem.getOrder() != this) {
            orderItem.setOrder(this);
        }
        
    }

    private Date orderDate;

    private OrderState orderState;

    public void updateOrderStatus(OrderState orderState) {
        this.orderState = orderState;
    }

}

