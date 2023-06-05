package org.acme.shop.web;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.web.util.RequestAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HttpMethod;



@Transactional
@QuarkusTest
public class CategoryControllerTest {

    @Inject
    private RequestAction requestAction;

    @Order(1)
    @Test
    public void createCategory_test() {
       ReqCategoryDto reqCategoryDto = ReqCategoryDto.builder()
                                                    .name("WOMEN")
                                                    .build();
        requestAction.doAction(HttpMethod.POST, "/api/v1/shop/category"
                    , reqCategoryDto, 201);
    }

    @Order(2)
    @Test
    public void createCategoryException_test() {
       ReqCategoryDto reqCategoryDto = ReqCategoryDto.builder()
                                                    .build();
        ExtractableResponse<Response> response;
        JsonPath responseJsonPath;

        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/category"
                    , reqCategoryDto, 400);

        responseJsonPath= response.jsonPath();

        Assertions.assertEquals("createCategory.reqCategoryDto.name"
                            , responseJsonPath.getString("[0].path.string"));
    }

    @Order(3)
    @Test
    public void find_test() throws Exception{
        createCategoryListForTest();

        ExtractableResponse<Response> response;
        JsonPath responseJsonPath;
        
        // id 조회
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/category/1"
                            , null, 200);

        responseJsonPath = response.jsonPath();
        Assertions.assertEquals("category1"
                            , responseJsonPath.getString("name"));

        // 전체 조회
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/categorys"
                            , null, 200);
        responseJsonPath = response.jsonPath();
        Assertions.assertEquals(3
                            , responseJsonPath.<JsonPath>getList("").size());

        // categoryName으로 조회 
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/categorys?name=category2"
                            , null, 200);
        responseJsonPath = response.jsonPath();
        Assertions.assertEquals(2
                                , responseJsonPath.<JsonPath>getList("").size());
    }

    @Order(4)
    @Test
    public void delete_test() throws Exception{
        createCategoryListForTest();

        ExtractableResponse<Response> response;
        JsonPath responseJsonPath;

        response = requestAction.doAction(HttpMethod.DELETE, "/api/v1/shop/category/1"
                            , null, 200);
                            

        // 전체 조회
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/categorys"
                            , null, 200);
        responseJsonPath = response.jsonPath();
        Assertions.assertEquals(2, responseJsonPath.<JsonPath>getList("").size());
    }



    private void createCategoryListForTest() throws Exception {
        List<ReqCategoryDto> reqCategoryDtos = createTestReqCategoryDtos();
        
        for(ReqCategoryDto reqCategoryDto : reqCategoryDtos) {
            requestAction.doAction(HttpMethod.POST, "/api/v1/shop/category"
                            , reqCategoryDto, 201);
        }
    }

    private List<ReqCategoryDto> createTestReqCategoryDtos() {
        List<ReqCategoryDto> reqCategoryDtos = new ArrayList<ReqCategoryDto>();
        reqCategoryDtos.add(ReqCategoryDto.builder().name("category1").build());
        reqCategoryDtos.add(ReqCategoryDto.builder().name("category2").build());
        reqCategoryDtos.add(ReqCategoryDto.builder().name("category2").build());

        return reqCategoryDtos;
    }



}
