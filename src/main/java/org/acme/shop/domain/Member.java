package org.acme.shop.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    
	private String id;

    private String address;

    private String phoneNumber;

    @Builder.Default
    private List<Order> orders = new ArrayList<Order>();

    public void addOrders(Order order) {
        this.orders.add(order);

        if(order.getMember() != this) {
            order.setMember(this);
        }
    }

    public void updateMember(String address, String phoneNumber){
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

}
