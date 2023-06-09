package org.acme.shop.data.dto;

import java.util.Date;
import java.util.List;

import org.acme.shop.common.ShopConstants.OrderState;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RespOrderDto {
    
    private Long orderId;

    private String memberId;

    private List<OrderItemInfo> orderItemInfos;

    @Builder
    @Getter
    public static class OrderItemInfo {
        
        private Long itemId;

        private String itemName;

        private int itemPrice;
        
        private int itemRequestQty;

        private Long categoryId;

        private String categoryName;
    }

    private Date orderDate;

    private OrderState orderState;

}
