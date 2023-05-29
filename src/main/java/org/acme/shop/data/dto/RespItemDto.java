package org.acme.shop.data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RespItemDto {
    
    private Long id;

    private String name;
    
    private int price;

    private int remainQty;

    private Long categoryId;

    private String categoryName;
}
