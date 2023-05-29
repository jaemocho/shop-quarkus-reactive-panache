package org.acme.shop.data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RespCategoryDto {
    
    private Long id;

    private String name;
}
