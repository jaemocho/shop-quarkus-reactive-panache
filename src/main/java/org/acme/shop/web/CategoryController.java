package org.acme.shop.web;


import java.util.ArrayList;
import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.data.dto.RespCategoryDto;
import org.acme.shop.data.entity.Category;
import org.acme.shop.service.CategoryService;
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
    CategoryService categoryService;

    @POST
    @Path("/category")
    public Uni<RespCategoryDto> createCategory(ReqCategoryDto reqCategoryDto) {
        return categoryService.addCategory(reqCategoryDto)
                            .onItem()
                            .transform(c -> entityToRespDto(c));
                // .transform(id -> URI.create("/category/"+id)) 
                // .onItem()
                // .transform(uri -> Response.created(uri).build());           
    }
    
    @DELETE
    @Path("/category/{id}")
    public Uni<Response> removeCategory(@PathParam("id") Long id) throws ShopException {
        return categoryService.removeCategory(id)
                .onItem()
                .transform(c -> Response.ok(c).build());
    }

    @GET
    @Path("/category/{id}")
    public Uni<RespCategoryDto> getCategoryById(@PathParam("id") Long id) throws ShopException{
        Log.info("category controller ");
        return categoryService.getCategoryById(id)
                            .onItem()
                            .transform(c -> entityToRespDto(c));
    }

    @GET
    @Path("/categorys")
    public Uni<List<RespCategoryDto>> getCategoryByName(@RestQuery String name) throws ShopException{

        if ( name == null || "".equals(name)) {
            return categoryService.getAllCategory()
                                .onItem()
                                .transform(c -> entityToRespDto(c));
        }

        return categoryService.getCategoryByName(name)
                                .onItem()
                                .transform(c -> entityToRespDto(c));
    }

    private List<RespCategoryDto> entityToRespDto(List<Category> categories){
        List<RespCategoryDto> respCategoryDtos = new ArrayList<RespCategoryDto>();
        for (Category c : categories) {
            respCategoryDtos.add(entityToRespDto(c));
        }
        return respCategoryDtos;
    }

    private RespCategoryDto entityToRespDto(Category c) {
        if ( c == null ) return null;
        return RespCategoryDto.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .build();
    }

}
