package org.acme.shop.data.dto;

import java.util.List;

import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqOrderDto {
    
    @NotBlank
    private String memberId;

    @NotBlank
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
