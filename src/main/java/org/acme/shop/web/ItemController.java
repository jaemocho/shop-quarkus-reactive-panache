package org.acme.shop.web;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dto.ReqItemDto;
import org.acme.shop.data.dto.RespItemDto;
import org.acme.shop.data.entity.Category;
import org.acme.shop.data.entity.Item;
import org.acme.shop.service.ItemService;
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
    ItemService itemService;

    @POST
    @Path("/item")
    public Uni<RespItemDto> createItem(ReqItemDto reqItemDto) {
        return itemService.addItem(reqItemDto)
                .onItem()
                .transform(i -> entityToRespDto(i));
    }

    @DELETE
    @Path("/item/{id}")
    public Uni<Response> removeItem(@PathParam("id") Long id) throws ShopException {
        return itemService.removeItem(id)
                .onItem()
                .transform(i -> Response.ok(i).build());
    }

    @GET
    @Path("/item/{id}")
    public Uni<RespItemDto> getItemById(@PathParam("id") Long id) throws ShopException{
        return itemService.getItemById(id)
                .onItem()
                .transform(i -> entityToRespDto(i));
    }

    @GET
    @Path("/items")
    public Uni<List<RespItemDto>> getItemByCategoryId(@RestQuery Long categoryId) throws ShopException{
        if(categoryId == null) {
            return itemService.getAllItem()
                    .onItem()
                    .transform(i -> entityToRespDto(i));
        }
        return itemService.getItemByCategoryId(categoryId)
                    .onItem()
                    .transform(i -> entityToRespDto(i));
    }

    @PUT
    @Path("/item/{id}")
    public Uni<RespItemDto> updateItem(@PathParam("id") Long id, ReqItemDto reqItemDto) throws ShopException {
        return itemService.updateItem(id, reqItemDto)
            .onItem()
            .transform(i -> entityToRespDto(i));
    }

    private List<RespItemDto> entityToRespDto(List<Item> items){
        List<RespItemDto> respItemDtos = new ArrayList<RespItemDto>();
        for (Item i : items) {
            respItemDtos.add(entityToRespDto(i));
        }
        return respItemDtos;
    }

    private RespItemDto entityToRespDto(Item i) {
        Category category = i.getCategory();
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