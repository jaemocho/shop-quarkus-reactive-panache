package org.acme.shop.common.interceptor;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.logging.Log;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Logging
@Interceptor
public class LoggingInterceptor {
    
    static List<Event<Any>> events = new ArrayList<>();
   
    @AroundInvoke
    public Object logging(InvocationContext ctx) throws Exception {
        Log.infof("[ %s ] - %s"
        , ctx.getMethod().getDeclaringClass().getSimpleName(), ctx.getMethod().getName());
        return ctx.proceed();
    }
}