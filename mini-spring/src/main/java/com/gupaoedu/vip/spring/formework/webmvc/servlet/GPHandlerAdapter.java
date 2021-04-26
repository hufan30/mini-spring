package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import com.gupaoedu.vip.spring.formework.annotation.GPRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tom on 2019/4/13.
 * 那真的感觉这个handler就是一个纯的适配器，字面上的意思
 * 核心方法就是handler方法，无成员变量，就是一个处理逻辑；
 * 根据传入参数的Request，response，handlerMapping，将它们整合处理
 * 关键还是用handlerMapping来处理Request和response
 */
public class GPHandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof GPHandlerMapping);
    }


    /**
     * 核心处理逻辑，下面来体会一下，为什么需要一个专门的逻辑方法来处理；
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    GPModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return null;
    }

}
