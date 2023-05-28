package org.acme.shop.common.exception;

import org.acme.shop.common.Constants;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ShopException extends RuntimeException{

    private static final long serialVersionUID = 4663380430591151694L;

    private Constants.ExceptionClass exceptionClass;
    private HttpResponseStatus httpResponseStatus;

    public ShopException(Constants.ExceptionClass exceptionClass, HttpResponseStatus httpResponseStatus,
        String message) {
        super(exceptionClass.toString() + message);
        this.exceptionClass = exceptionClass;
        this.httpResponseStatus = httpResponseStatus;
    }

    public Constants.ExceptionClass getExceptionClass() {
        return exceptionClass;
    }

    public int getHttpStatusCode() {
        return httpResponseStatus.code();
    }

    public String getHttpStatusType() {
        return httpResponseStatus.reasonPhrase();
        
    }

    public HttpResponseStatus getHttpResponseStatus() {
        return httpResponseStatus;
    }
    
}
