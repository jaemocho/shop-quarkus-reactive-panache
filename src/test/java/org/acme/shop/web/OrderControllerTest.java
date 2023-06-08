package org.acme.shop.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.acme.shop.data.dto.ReqCategoryDto;
import org.acme.shop.data.dto.ReqItemDto;
import org.acme.shop.data.dto.ReqMemberDto;
import org.acme.shop.data.dto.ReqOrderDto;
import org.acme.shop.web.util.RequestAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.logging.Log;
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
public class OrderControllerTest {
    
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
        setInitData() ;
        ReqOrderDto reqOrderDto;
        
        
        // 신규 order test 
        // 주문자 : memberA
        // items 2종류 
        ReqOrderDto.RequestItem requestItem1 
                        = ReqOrderDto.RequestItem.builder()
                                            .itemId(1L)
                                            .requestQty(30)
                                            .build();

        ReqOrderDto.RequestItem requestItem2 
                        = ReqOrderDto.RequestItem.builder()
                                            .itemId(2L)
                                            .requestQty(50)
                                            .build();

        List<ReqOrderDto.RequestItem> requestItems = 
                new ArrayList<ReqOrderDto.RequestItem>();
        requestItems.add(requestItem1);
        requestItems.add(requestItem2);
                                                                    
        
        reqOrderDto = ReqOrderDto.builder()
                                .memberId("memberA")
                                .requestItem(requestItems)
                                .build();
                                
        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/order"
                                , reqOrderDto, 201);

        // 주문 member id null test 
        reqOrderDto = ReqOrderDto.builder()
                                // .memberId("memberA")
                                .requestItem(requestItems)
                                .build();
        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/order"
                                , reqOrderDto, 400);

        responseJsonPath = response.jsonPath();

        assertEquals("createOrder.reqOrderDto.memberId"
                    , responseJsonPath.getString("[0].path.string"));

        // 주문  requestItems null test 
        reqOrderDto = ReqOrderDto.builder()
                                .memberId("memberA")
                                //.requestItem(requestItems)
                                .build();
        
        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/order"
                                , reqOrderDto, 400);
        
        responseJsonPath = response.jsonPath();

        assertEquals("createOrder.reqOrderDto.requestItem"
                    , responseJsonPath.getString("[0].path.string"));
        
        // id 1 번 조회 test
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/order/1"
                                ,null , 200);
        
        responseJsonPath = response.jsonPath();

        assertEquals("memberA"  , responseJsonPath.getString("memberId"));
        assertEquals(1L         , responseJsonPath.getLong("orderItemInfos[0].itemId"));
        assertEquals("T-shirt"  , responseJsonPath.getString("orderItemInfos[0].itemName"));
        assertEquals(30         , responseJsonPath.getInt("orderItemInfos[0].itemRequestQty"));
        assertEquals(1L         , responseJsonPath.getLong("orderItemInfos[0].categoryId"));
        assertEquals("WOMEN"    , responseJsonPath.getString("orderItemInfos[0].categoryName"));
        assertEquals(2L         , responseJsonPath.getLong("orderItemInfos[1].itemId"));
        assertEquals("Y-shirt"  , responseJsonPath.getString("orderItemInfos[1].itemName"));
        assertEquals(50         , responseJsonPath.getInt("orderItemInfos[1].itemRequestQty"));
        assertEquals(1L         , responseJsonPath.getLong("orderItemInfos[1].categoryId"));
        assertEquals("WOMEN"    , responseJsonPath.getString("orderItemInfos[1].categoryName"));
        assertEquals("REQUEST"  , responseJsonPath.getString("orderState"));

        // memberA 주문 추가 
        reqOrderDto = ReqOrderDto.builder()
                                .memberId("memberA")
                                .requestItem(requestItems)
                                .build();
        
        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/order"
                                , reqOrderDto, 201);

        // memberid로 조회 테스트 
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/order/member/memberA"
                                ,null , 200);
        
        responseJsonPath = response.jsonPath();
        Log.info(responseJsonPath.toString());

        List<Map<String,Object>> jsonPaths = responseJsonPath.get(".");
        // id long type인데 integer 로 반환 되는 것 확인 필요 
        // JsonPath.get
        jsonPaths.forEach(j -> {
            assertEquals("memberA"  , (String)j.get("memberId"));
            assertEquals("REQUEST"  , (String)j.get("orderState"));
        });

                // 주문 취소 테스트 (1번 주문)
        response = requestAction.doAction(HttpMethod.DELETE, "/api/v1/shop/order/1"
        , reqOrderDto, 200);

        
        // id 1 번 조회 test (주문 취소 후 상태 변경 확인)
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/order/1"
                            ,null , 200);
        
        responseJsonPath = response.jsonPath();
        
        assertEquals("memberA"  , responseJsonPath.getString("memberId"));
        assertEquals("CANCEL"  , responseJsonPath.getString("orderState"));
    }
    
    private void setInitData() throws Exception {
        
        // member insert
        List<ReqMemberDto> reqMemberDtos = createTestReqMemberDto();
        for(ReqMemberDto reqMemberDto : reqMemberDtos) {
            requestAction.doAction(HttpMethod.POST, "/api/v1/shop/member"
                    , reqMemberDto, 201);
        }

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
    
    
    private List<ReqMemberDto> createTestReqMemberDto() {
        List<ReqMemberDto> reqMemberDtos = new ArrayList<ReqMemberDto>();

        ReqMemberDto reqMemberDto1 = ReqMemberDto.builder().id("memberA").address("서울").phoneNumber("01111111111").build();
        ReqMemberDto reqMemberDto2 = ReqMemberDto.builder().id("memberB").address("서울").phoneNumber("01111111111").build();

        reqMemberDtos.add(reqMemberDto1);
        reqMemberDtos.add(reqMemberDto2);

        return reqMemberDtos;
        
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
