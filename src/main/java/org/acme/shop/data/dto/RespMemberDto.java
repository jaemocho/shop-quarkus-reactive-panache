package org.acme.shop.data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RespMemberDto {
    
    private String id;

    private String address;

    private String phoneNumber;

}
