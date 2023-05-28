package org.acme.shop.common;

import org.acme.shop.common.Constants.ExceptionClass;
import org.acme.shop.common.exception.ShopException;

import io.netty.handler.codec.http.HttpResponseStatus;

public class CommonUtils {
    private static final String NOT_FOUND = "Not Found ";
    
    public static void nullCheckAndThrowException(Object object, String className){
        if(object == null) {
             throw new ShopException(ExceptionClass.SHOP
             , HttpResponseStatus.BAD_REQUEST, NOT_FOUND +" "+ className); 
        }
    }

    public static Exception throwShopException(String className){
        return new ShopException(ExceptionClass.SHOP, HttpResponseStatus.BAD_REQUEST, NOT_FOUND +" "+ className); 
    }

}
