package org.acme.shop.adapter.in.web;


import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.CategoryEntity;
import org.acme.shop.application.port.in.CategoryUsecase;
import org.acme.shop.application.service.dto.ReqCategoryDto;
import org.acme.shop.application.service.dto.RespCategoryDto;
import org.acme.shop.common.exception.ShopException;
import org.jboss.resteasy.reactive.RestQuery;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/api/v1/shop")
public class CategoryController {
    
    @Inject
    CategoryUsecase categoryUsecase;

    @POST
    @Path("/category")
    public Uni<RespCategoryDto> createCategory(ReqCategoryDto reqCategoryDto) {
        return categoryUsecase.addCategory(reqCategoryDto)
                            .onItem()
                            .transform(c -> entityToRespDto(c));
                // .transform(id -> URI.create("/category/"+id)) 
                // .onItem()
                // .transform(uri -> Response.created(uri).build());
                
    }
    
    @DELETE
    @Path("/category/{id}")
    public Uni<Response> removeCategory(@PathParam("id") Long id) throws ShopException {
        return categoryUsecase.removeCategory(id)
                .onItem()
                .transform(c -> Response.ok(c).build());
    }

    @GET
    @Path("/category/{id}")
    public Uni<RespCategoryDto> getCategoryById(@PathParam("id") Long id) throws ShopException{
        Log.info("category controller ");
        return categoryUsecase.getCategoryById(id)
                            .onItem()
                            .transform(c -> entityToRespDto(c));
    }

    @GET
    @Path("/categorys")
    public Uni<List<RespCategoryDto>> getCategoryByName(@RestQuery String name) throws ShopException{

        if ( name == null || "".equals(name)) {
            return categoryUsecase.getAllCategory()
                                .onItem()
                                .transform(c -> entityToRespDto(c));
        }

        return categoryUsecase.getCategoryByName(name)
                                .onItem()
                                .transform(c -> entityToRespDto(c));
    }

    private List<RespCategoryDto> entityToRespDto(List<CategoryEntity> categoryEntities){
        List<RespCategoryDto> respCategoryDtos = new ArrayList<RespCategoryDto>();

        for (CategoryEntity c : categoryEntities) {
            respCategoryDtos.add(entityToRespDto(c));
        }
        return respCategoryDtos;
    }

    private RespCategoryDto entityToRespDto(CategoryEntity c) {

        if ( c == null ) return null;

        return RespCategoryDto.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .build();
    }

}
