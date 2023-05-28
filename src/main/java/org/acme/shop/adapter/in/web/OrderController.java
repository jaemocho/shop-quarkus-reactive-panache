package org.acme.shop.adapter.in.web;

import java.net.URI;

import org.acme.shop.application.port.in.OrderUsecase;
import org.acme.shop.application.service.dto.ReqOrderDto;
import org.acme.shop.common.exception.ShopException;

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
public class OrderController {

    @Inject
    OrderUsecase orderUsecase;

    @POST
    @Path("/order")
    public Uni<Response> createOrder(ReqOrderDto reqOrderDto) throws ShopException{
        return Uni.createFrom()
                .item(orderUsecase.createOrder(reqOrderDto))
                .onItem()
                .transform(id -> URI.create("/order/"+id)) 
                .onItem()
                .transform(uri -> Response.created(uri).build());
    }

    @DELETE
    @Path("/order/{id}")
    public Uni<Response> cancelOrder(@PathParam("id") Long id) throws ShopException {

        return Uni.createFrom()
                .item(orderUsecase.cancelOrder(id))
                .onItem()
                .transform(f -> f != null ? Response.ok("SUCCESS") : Response.ok(null))
                .onItem()
                .transform(Response.ResponseBuilder::build);        
    }

    @GET
    @Path("/order/{id}")
    public Uni<Response> getOrderInfoByOrderId(@PathParam("id") Long id) throws ShopException {
        return Uni.createFrom()
                .item(orderUsecase.getOrderInfoByOrderId(id))
                .onItem()
                .transform(f -> f != null ? Response.ok(f) : Response.ok(null))
                .onItem()
                .transform(Response.ResponseBuilder::build);                
    }

    @GET
    @Path("/order/member/{memberId}")
    public Uni<Response> getOrderInfoByMemberId(@PathParam("memberId") String memberId) throws ShopException {
        return Uni.createFrom()
                .item(orderUsecase.getOrderInfoByMemberId(memberId))
                .onItem()
                .transform(f -> f != null ? Response.ok(f) : Response.ok(null))
                .onItem()
                .transform(Response.ResponseBuilder::build);                        
    }
    
}
