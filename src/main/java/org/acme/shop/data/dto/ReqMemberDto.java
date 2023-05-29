package org.acme.shop.data.dto;

import io.smallrye.common.constraint.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqMemberDto {
    
    @NotNull
    private String id;

    @NotNull
    private String address;

    @NotNull
    private String phoneNumber;

}
