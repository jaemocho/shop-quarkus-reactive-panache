package org.acme.shop.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.data.dto.ReqItemDto;
import org.acme.shop.web.util.RequestAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HttpMethod;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@QuarkusTest
public class ItemControllerTest {
    
    @Inject
    private RequestAction requestAction;

    @Test
    @DisplayName("통합테스트")
    public void test() throws Exception {

        ExtractableResponse<Response> response;
        JsonPath responseJsonPath;

        // category data insert
        // category 3개 
        // item 4개 입력 
        setInitData();

        ReqItemDto reqItemDto;

        // MEN Category T-shirt insert test 
        // 201 성공
        // item 5개
        reqItemDto = ReqItemDto.builder()
                                        .name("Y-shirt")
                                        .price(5000)
                                        .remainQty(1000)
                                        .categoryId(2L)
                                        .build();

        requestAction.doAction(HttpMethod.POST, "/api/v1/shop/item"
                        , reqItemDto, 201);


        // WOMEN Category T-shirt insert test (name null test )
        // 400 error 실패 
        reqItemDto = ReqItemDto.builder()
                                        .price(5000)
                                        .remainQty(1000)
                                        .categoryId(2L)
                                        .build();

        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/item"
                        , reqItemDto, 400);

        responseJsonPath = response.jsonPath();

        assertEquals("createItem.reqItemDto.name"
                    , responseJsonPath.getString("[0].path.string"));

        // MEN Category T-shirt delete test 
        // 200 성공
        // item 4개 
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/item/5"
                    , reqItemDto, 200);

        // id 1 번 조회 test
        // 200 성공
        // item 4개
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/item/1"
                        , reqItemDto, 200);
        
        responseJsonPath = response.jsonPath();

        assertEquals("T-shirt", responseJsonPath.getString("name"));
        assertEquals(5000, responseJsonPath.getInt("price"));
        assertEquals(1000, responseJsonPath.getInt("remainQty"));
        assertEquals(1l, responseJsonPath.getLong("id"));
        assertEquals("WOMEN", responseJsonPath.getString("categoryName"));

        // category로 item 조회 test
        // 200 성공
        // WOMEN Category t-shirt/y-shirt 두개 
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/items?categoryId=1"
                        , reqItemDto, 200);
       
        responseJsonPath = response.jsonPath();

        List<Map<String,Object>> jsonPaths = responseJsonPath.get(".");
        // id long type인데 integer 로 반환 되는 것 확인 필요 
        // JsonPath.get
        jsonPaths.forEach(j -> {
            assertEquals(1, (int)j.get("categoryId"));
            assertEquals("WOMEN", (String)j.get("categoryName"));
        });

        // update test 
        // 200 성공
        reqItemDto = ReqItemDto.builder()
                            .name("pants")
                            .price(10000)
                            .remainQty(9000)
                            .build();
        
        response = requestAction.doAction(HttpMethod.PUT, "/api/v1/shop/item/1"
                            , reqItemDto, 200);
           
        responseJsonPath = response.jsonPath();

        assertEquals("pants", responseJsonPath.getString("name"));
        assertEquals(10000, responseJsonPath.getInt("price"));
        assertEquals(9000, responseJsonPath.getInt("remainQty"));
        assertEquals(1L, responseJsonPath.getLong("categoryId"));
        assertEquals("WOMEN", responseJsonPath.getString("categoryName"));

    }

    private void setInitData() throws Exception {
        
        // category insert 
        List<ReqCategoryDto> reqCategoryDtos = createTestReqCategoryDtos();
        
        for(ReqCategoryDto reqCategoryDto : reqCategoryDtos) {
            requestAction.doAction(HttpMethod.POST, "/api/v1/shop/category"
                            , reqCategoryDto, 201);
        }

        // item insert 
        List<ReqItemDto> reqItemDtos = createTestReqItemDtos();
        for(ReqItemDto reqItemDto : reqItemDtos) {
            requestAction.doAction(HttpMethod.POST, "/api/v1/shop/item"
                            , reqItemDto, 201);
       }
    }

    private List<ReqCategoryDto> createTestReqCategoryDtos() {
        List<ReqCategoryDto> reqCategoryDtos = new ArrayList<ReqCategoryDto>();
        
        ReqCategoryDto reqCategoryDto1 = ReqCategoryDto.builder().name("WOMEN").build();
        ReqCategoryDto reqCategoryDto2 = ReqCategoryDto.builder().name("MEN").build();
        ReqCategoryDto reqCategoryDto3 = ReqCategoryDto.builder().name("KIDS").build();
        
        reqCategoryDtos.add(reqCategoryDto1);
        reqCategoryDtos.add(reqCategoryDto2);
        reqCategoryDtos.add(reqCategoryDto3);

        return reqCategoryDtos;
    }

    private List<ReqItemDto> createTestReqItemDtos() {
        List<ReqItemDto> reqItemDtos = new ArrayList<ReqItemDto>();
        
        ReqItemDto reqItemDto1 = ReqItemDto.builder().name("T-shirt").price(5000).remainQty(1000).categoryId(1L).build();
        ReqItemDto reqItemDto2 = ReqItemDto.builder().name("Y-shirt").price(4000).remainQty(500).categoryId(1L).build();
        ReqItemDto reqItemDto3 = ReqItemDto.builder().name("T-shirt").price(3000).remainQty(200).categoryId(2L).build();
        ReqItemDto reqItemDto4 = ReqItemDto.builder().name("T-shirt").price(2000).remainQty(200).categoryId(3L).build();

        reqItemDtos.add(reqItemDto1);
        reqItemDtos.add(reqItemDto2);
        reqItemDtos.add(reqItemDto3);
        reqItemDtos.add(reqItemDto4);

        return reqItemDtos;
    }
}
