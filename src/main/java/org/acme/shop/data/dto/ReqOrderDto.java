package org.acme.shop.data.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqOrderDto {
    
    @NotBlank
    private String memberId;

    @NotNull
    private List<RequestItem> requestItem;
    
    @Builder
    @Getter
    public static class RequestItem {
        
        @NotNull
        private Long itemId;
        
        @NotNull @Min(1)
        private int requestQty;
    }


}
