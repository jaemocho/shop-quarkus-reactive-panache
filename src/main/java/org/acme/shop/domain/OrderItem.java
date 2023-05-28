package org.acme.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    private Long id;

    private Order order;

    private Item item;

    private int count;

    public void setOrder(Order order){
        this.order = order;

        if(!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

}
