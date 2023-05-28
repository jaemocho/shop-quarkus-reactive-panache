package org.acme.shop.application.service.dto;


import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ReqItemDto {
    
    @NotNull
    private String name;
    
    @NotNull
    private Integer price;

    @NotNull
    private Integer remainQty;

    private Long categoryId;
}
