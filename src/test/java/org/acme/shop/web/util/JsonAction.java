package org.acme.shop.web.util;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JsonAction implements RequestAction {
    
    public ExtractableResponse<Response> doAction(String httpMethod, String uri, Object body, int expectedStatusCode) {
        RequestSpecification requestSpecification = createRequestSpecification(body);
        Response response = createResponse(httpMethod, uri, requestSpecification);
        return response.then().log().all()
                        .statusCode(expectedStatusCode)
                        .extract();
    }

    private RequestSpecification createRequestSpecification(Object body) {
        return createRequestSpecification(JSON, JSON, body);
    }

    private RequestSpecification createRequestSpecification(ContentType contentType, ContentType accept, Object body) {
        RequestSpecification requestSpecification = RestAssured.given().log().all()
                                                                .when()
                                                                    .contentType(contentType)
                                                                    .accept(accept);
        if (body == null) return requestSpecification;
        return requestSpecification.body(body);
    }

    private Response createResponse(String httpMethod, String uri, RequestSpecification requestSpecification) {
        Response response;
        
        switch(httpMethod) {
            case HttpMethod.GET:
                response = requestSpecification.get(uri);
                break;
            case HttpMethod.POST:
                response = requestSpecification.post(uri);
                break;
            case HttpMethod.DELETE:
                response = requestSpecification.delete(uri);
                break;
            case HttpMethod.PUT:
                response = requestSpecification.put(uri);
                break;
            default:
                response = requestSpecification.get(uri);
        }

        return response;
    }
}
