package org.acme.shop.adapter.in.web;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;
import org.acme.shop.application.port.in.MemberUsecase;
import org.acme.shop.application.service.dto.ReqMemberDto;
import org.acme.shop.application.service.dto.RespMemberDto;
import org.acme.shop.common.exception.ShopException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/api/v1/shop")
public class MemberController {
    
    @Inject
    MemberUsecase memberUsecase;

    @POST
    @Path("/member")
    public Uni<RespMemberDto> join(ReqMemberDto reqMemberDto) throws ShopException {
        return memberUsecase.addMember(reqMemberDto)
                .onItem()
                .transform(m -> m != null ? entityToRespDto(m) : null);
        
    }

    @DELETE
    @Path("/member/{id}")
    public Uni<Response> removeMember(@PathParam("id") String id) throws ShopException {
        return memberUsecase.removeMember(id)
                .onItem()
                .transform(m -> Response.ok(m).build());
    }

    @GET
    @Path("/member/{id}")
    public Uni<RespMemberDto> getMemberById(@PathParam("id") String id) throws ShopException{
        return memberUsecase.getMemberById(id)
               .onItem()
                .transform(m -> entityToRespDto(m));      
    }

    @PUT
    @Path("/member/{id}")
    public Uni<RespMemberDto> updateMember(@PathParam("id") String id, ReqMemberDto reqMemberDto) throws ShopException {
        return memberUsecase.updateMember(id, reqMemberDto)
                .onItem()
                .transform(m -> m != null ? entityToRespDto(m) : null);
    }

    @GET
    @Path("/members")
    public Uni<List<RespMemberDto>> getAllMember() {
        return memberUsecase.getAllMember()
                .onItem()
                .transform(f -> entityToRespDto(f));
    }


    private List<RespMemberDto> entityToRespDto(List<MemberEntity> memberEntities) {
        
        List<RespMemberDto> respMemberDtos = new ArrayList<RespMemberDto>();

        for(MemberEntity m : memberEntities) {
            respMemberDtos.add(entityToRespDto(m));
        }

        return respMemberDtos;
    }

    private RespMemberDto entityToRespDto(MemberEntity m) {
        return RespMemberDto.builder()
                        .id(m.getId())
                        .address(m.getAddress())
                        .phoneNumber(m.getPhoneNumber())
                        .build();
    }
}
