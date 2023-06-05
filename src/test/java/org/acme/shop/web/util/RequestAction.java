package org.acme.shop.web.util;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public interface RequestAction {

    public ExtractableResponse<Response> doAction(String httpMethod, String uri, Object body, int expectedStatusCode);
}