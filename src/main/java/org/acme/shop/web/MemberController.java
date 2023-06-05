package org.acme.shop.web;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.common.interceptor.Logging;
import org.acme.shop.data.dto.ReqMemberDto;
import org.acme.shop.data.dto.RespMemberDto;
import org.acme.shop.data.entity.Member;
import org.acme.shop.service.MemberService;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Logging
@ApplicationScoped
@Path("/api/v1/shop")
public class MemberController {
    
    @Inject
    MemberService memberService;

    @POST
    @Path("/member")
    public Uni<Response> join(@Valid ReqMemberDto reqMemberDto) throws ShopException {
        return memberService.addMember(reqMemberDto)
                .map(m -> Response.created(
                                    UriBuilder.fromResource(MemberController.class)
                                            .build("/member/" + m.getId())
                                    ).entity(entityToRespDto(m))
                                    .build());
    }

    @DELETE
    @Path("/member/{id}")
    public Uni<Response> removeMember(@NotNull @PathParam("id") String id) throws ShopException {
        return memberService.removeMember(id)
                    .map(m -> Response.ok(m).build());
    }

    @GET
    @Path("/member/{id}")
    public Uni<Response> getMemberById(@NotNull @PathParam("id") String id) throws ShopException{
        return memberService.getMemberById(id)
                        .map(m -> Response.ok(entityToRespDto(m)).build());      
    }

    @PUT
    @Path("/member/{id}")
    public Uni<Response> updateMember(@NotNull @PathParam("id") String id, ReqMemberDto reqMemberDto) throws ShopException {
        return memberService.updateMember(id, reqMemberDto)
                        .map(m -> Response.ok(entityToRespDto(m)).build());      
    }

    @GET
    @Path("/members")
    public Uni<Response> getAllMember() {
        return memberService.getAllMember()
                    .map(m -> Response.ok(entityToRespDto(m)).build());      
    }


    private List<RespMemberDto> entityToRespDto(List<Member> memberEntities) {
        List<RespMemberDto> respMemberDtos = new ArrayList<RespMemberDto>();
        for(Member m : memberEntities) {
            respMemberDtos.add(entityToRespDto(m));
        }
        return respMemberDtos;
    }

    private RespMemberDto entityToRespDto(Member m) {
        return RespMemberDto.builder()
                        .id(m.getId())
                        .address(m.getAddress())
                        .phoneNumber(m.getPhoneNumber())
                        .build();
    }
}
