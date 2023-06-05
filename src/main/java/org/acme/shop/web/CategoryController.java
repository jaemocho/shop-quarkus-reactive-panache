package org.acme.shop.web;


import java.util.ArrayList;
import java.util.List;

import org.acme.shop.common.exception.ShopException;
import org.acme.shop.common.interceptor.Logging;
import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.data.dto.RespCategoryDto;
import org.acme.shop.data.entity.Category;
import org.acme.shop.service.CategoryService;
import org.jboss.resteasy.reactive.RestQuery;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Logging
@ApplicationScoped
@Path("/api/v1/shop")
public class CategoryController {
    
    @Inject
    CategoryService categoryService;

    @POST
    @Path("/category")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> createCategory(@Valid ReqCategoryDto reqCategoryDto) {
        return categoryService.addCategory(reqCategoryDto)
                            .map(c ->  
                                Response.created(
                                    UriBuilder.fromResource(CategoryController.class)
                                    .build("/category/" + c.getId())
                                ).entity(entityToRespDto(c))
                                .build());
                // .transform(id -> URI.create("/category/"+id)) 
                // .onItem()
                // .transform(uri -> Response.created(uri).build());           
    }
    
    @DELETE
    @Path("/category/{id}")
    public Uni<Response> removeCategory(@PathParam("id") Long id) throws ShopException {
        return categoryService.removeCategory(id)
                            .map(c -> Response.ok(c).build());
    }

    @GET
    @Path("/category/{id}")
    public Uni<Response> getCategoryById(@PathParam("id") Long id) throws ShopException{
        return categoryService.getCategoryById(id)
                            .map(c -> Response.ok(entityToRespDto(c)).build());
    }

    @GET
    @Path("/categorys")
    public Uni<Response> getCategoryByName(@RestQuery String name) throws ShopException{

        if ( name == null || "".equals(name)) {
            return categoryService.getAllCategory()
                                .map(c -> Response.ok(entityToRespDto(c)).build());
        }

        return categoryService.getCategoryByName(name)
                                .map(c -> Response.ok(entityToRespDto(c)).build());
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
