package org.acme.shop.adapter.in.web;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.adapter.out.persistence.entity.ItemEntity;
import org.acme.shop.application.port.in.ItemUsecase;
import org.acme.shop.application.service.dto.ReqItemDto;
import org.acme.shop.application.service.dto.RespItemDto;
import org.acme.shop.common.exception.ShopException;
import org.jboss.resteasy.reactive.RestQuery;

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
public class ItemController {
    
    @Inject
    ItemUsecase itemUsecase;

    @POST
    @Path("/item")
    public Uni<RespItemDto> createItem(ReqItemDto reqItemDto) {
        return itemUsecase.addItem(reqItemDto)
                .onItem()
                .transform(i -> entityToRespDto(i));
    }

    @DELETE
    @Path("/item/{id}")
    public Uni<Response> removeItem(@PathParam("id") Long id) throws ShopException {
        return itemUsecase.removeItem(id)
                .onItem()
                .transform(i -> Response.ok(i).build());
    }

    @GET
    @Path("/item/{id}")
    public Uni<RespItemDto> getItemById(@PathParam("id") Long id) throws ShopException{
        return itemUsecase.getItemById(id)
                .onItem()
                .transform(i -> entityToRespDto(i));
    }

    @GET
    @Path("/items")
    public Uni<List<RespItemDto>> getItemByCategoryId(@RestQuery Long categoryId) throws ShopException{
        if(categoryId == null) {
            return itemUsecase.getAllItem()
                    .onItem()
                    .transform(i -> entityToRespDto(i));
        }
        return itemUsecase.getItemByCategoryId(categoryId)
                    .onItem()
                    .transform(i -> entityToRespDto(i));
    }

    @PUT
    @Path("/item/{id}")
    public Uni<RespItemDto> updateItem(@PathParam("id") Long id, ReqItemDto reqItemDto) throws ShopException {
        return itemUsecase.updateItem(id, reqItemDto)
            .onItem()
            .transform(i -> entityToRespDto(i));
    }

    private List<RespItemDto> entityToRespDto(List<ItemEntity> itemEntities){
        
        List<RespItemDto> respItemDtos = new ArrayList<RespItemDto>();

        for (ItemEntity i : itemEntities) {

            respItemDtos.add(entityToRespDto(i));
        }

        return respItemDtos;
    }

    private RespItemDto entityToRespDto(ItemEntity i) {

        CategoryEntity category = i.getCategoryEntity();
        Long categoryId = -1L;
        String categoryName = "";

        if ( category != null) {
            categoryId = category.getId();
            categoryName = category.getName();
        }

        return RespItemDto.builder()
                        .id(i.getId())
                        .name(i.getName())
                        .price(i.getPrice())
                        .remainQty(i.getRemainQty())
                        .categoryId(categoryId)
                        .categoryName(categoryName)
                        .build();
    }
    
}
