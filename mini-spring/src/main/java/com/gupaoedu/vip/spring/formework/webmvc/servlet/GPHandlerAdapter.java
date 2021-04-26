package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import com.gupaoedu.vip.spring.formework.annotation.GPRequestParam;
import org.apache.commons.lang3.StringUtils;

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
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    GPModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //第一步，先强转handler回原本的类，后续各种提取参数都需要从handlerMapping中提取；
        GPHandlerMapping handlerMapping = (GPHandlerMapping) handler;
        /**
         * 用于存储方法中形参的列表，从本地实体方法中提取，不是从Request中
         * key是形参的名称
         * value是形参在参数列表中的顺序
         */
        HashMap<String, Integer> paramIndexMapping = new HashMap<>();

        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        /**
         * 先提取GPRequestParam这样的参数项
         */
        for (int i = 0; i < pa.length; i++) {
            for (Annotation annotation : pa[i]) {
                if(annotation instanceof GPRequestParam){
                    String param = ((GPRequestParam) annotation).value();
                    if(StringUtils.isNotBlank(param)){
                        paramIndexMapping.put(param,i);
                    }
                }
            }
        }
        /**
         * 然后从形参里面提取httpRequest相关的参数项
         */
        handlerMapping.getMethod().getParameterTypes();
        handlerMapping.getMethod().getParameters();
        return null;
    }

}
