package org.acme.shop.data.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ReqItemDto {
    
    @NotBlank
    private String name;
    
    @NotNull @Min(0)
    private Integer price;

    @NotNull @Min(0)
    private Integer remainQty;

    private Long categoryId;
}
