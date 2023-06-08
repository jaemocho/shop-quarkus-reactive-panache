package org.acme.shop.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.acme.shop.data.dto.ReqMemberDto;
import org.acme.shop.web.util.RequestAction;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
public class MemberControllerTest {
    
    @Inject
    private RequestAction requestAction;

    @Order(1)
    @Test
    public void join_test() throws Exception {
        
        ExtractableResponse<Response> response;
        JsonPath responseJsonPath;
        
        ReqMemberDto reqMemberDto = ReqMemberDto.builder()
                                    .id("newMember")
                                    .address("한국")
                                    .phoneNumber("01111111111")
                                    .build();

        requestAction.doAction(HttpMethod.POST, "/api/v1/shop/member"
                    , reqMemberDto, 201);

        
        // id 누락 test
        reqMemberDto = ReqMemberDto.builder()
                        //.id("newMember")
                        .address("한국")
                        .phoneNumber("01111111111")
                        .build();

        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/member"
                        , reqMemberDto, 400);
        
        responseJsonPath = response.jsonPath();

        assertEquals("join.reqMemberDto.id"
                        , responseJsonPath.getString("[0].path.string"));
        

        // address 누락 test    
        reqMemberDto = ReqMemberDto.builder()
                        .id("newMember")
                        //.address("한국")
                        .phoneNumber("01111111111")
                        .build();

        response = requestAction.doAction(HttpMethod.POST, "/api/v1/shop/member"
                        , reqMemberDto, 400);
        
        responseJsonPath = response.jsonPath();

        assertEquals("join.reqMemberDto.address"
                        , responseJsonPath.getString("[0].path.string"));
    }

    @Order(2)
    @Test
    public void get_test() throws Exception{

        // member date insert
        setMemberList();

        ExtractableResponse<Response> response;
        JsonPath responseJsonPath;

        // member id 조회 테스트
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/member/member1"
                ,null, 200);
        
        responseJsonPath = response.jsonPath();

        assertEquals("member1", responseJsonPath.getString("id"));
        assertEquals("한국", responseJsonPath.getString("address"));
        assertEquals("01111111111", responseJsonPath.getString("phoneNumber"));
        

        // 전체 member 조회 테스트 
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/members"
                        ,null, 200);

        responseJsonPath = response.jsonPath();

        List<Map<String,Object>> jsonPathList = responseJsonPath.get(".");
        assertEquals(3, jsonPathList.size());

        assertEquals("member1", responseJsonPath.getString("[0].id"));
        assertEquals("member2", responseJsonPath.getString("[1].id"));
        assertEquals("member3", responseJsonPath.getString("[2].id"));
        
    }

    @Order(3)
    @Test
    public void delete_update_test() throws Exception{
        
        // member date insert
        setMemberList();

        ExtractableResponse<Response> response;
        JsonPath responseJsonPath;

        // member id 삭제 테스트
        response = requestAction.doAction(HttpMethod.DELETE, "/api/v1/shop/member/member1"
                ,null, 200);
        
        // 삭제 후 전체 member 조회 테스트 
        response = requestAction.doAction(HttpMethod.GET, "/api/v1/shop/members"
                ,null, 200);

        responseJsonPath = response.jsonPath();
        
        List<Map<String,Object>> jsonPathList = responseJsonPath.get(".");
        assertEquals(2, jsonPathList.size());

        // member update test
        ReqMemberDto reqMemberDto = ReqMemberDto.builder()
                                            .id("member2")
                                            .address("미국")
                                            .phoneNumber("0222222222")
                                            .build();

        response = requestAction.doAction(HttpMethod.PUT, "/api/v1/shop/member/member2"
                    ,reqMemberDto, 200);
        
        responseJsonPath = response.jsonPath();
        
        assertEquals("member2", responseJsonPath.getString("id"));
        assertEquals("미국", responseJsonPath.getString("address"));
        assertEquals("0222222222", responseJsonPath.getString("phoneNumber"));

    }


    private void setMemberList() throws Exception{
        
        List<ReqMemberDto> reqMemberDtos = createTestReqMemberDto();
        
        for(ReqMemberDto reqMemberDto : reqMemberDtos) {
            requestAction.doAction(HttpMethod.POST, "/api/v1/shop/member"
                            , reqMemberDto, 201);
        }
        
    }

    private List<ReqMemberDto> createTestReqMemberDto() {
        List<ReqMemberDto> reqMemberDtos = new ArrayList<ReqMemberDto>();
        
        reqMemberDtos.add(ReqMemberDto.builder().id("member1").address("한국").phoneNumber("01111111111").build());
        reqMemberDtos.add(ReqMemberDto.builder().id("member2").address("한국").phoneNumber("01111111111").build());
        reqMemberDtos.add(ReqMemberDto.builder().id("member3").address("한국").phoneNumber("01111111111").build());

        return reqMemberDtos;
        
    }

}
