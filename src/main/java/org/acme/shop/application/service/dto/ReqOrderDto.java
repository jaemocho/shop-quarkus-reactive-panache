package org.acme.shop.application.service.dto;

import java.util.List;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqOrderDto {
    
    @NotNull
    private String memberId;

    @NotNull
    private List<RequestItem> requestItem;
    
    @Builder
    @Getter
    public static class RequestItem {
        
        private Long itemId;
        
        private int requestQty;
    }


}
