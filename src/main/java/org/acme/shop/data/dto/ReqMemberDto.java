package org.acme.shop.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqMemberDto {
    
    @NotBlank
    private String id;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;

}
