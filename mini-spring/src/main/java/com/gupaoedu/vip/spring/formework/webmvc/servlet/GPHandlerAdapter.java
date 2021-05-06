package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import com.gupaoedu.vip.spring.formework.annotation.GPRequestParam;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
         * 从形参里面提取参数的顺序信息，比如name这个形参在第几个参数，age这个参数在第几个
         */
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        /**
         * 然后从形参里面提取形参的类型type信息，比如name形参是string，age这个参数是int
         */
        Class<?>[] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        /**
         * 下面开始准备实参
         * 首先实参个数和类型要和形参对应上
         * 从方法的形参中获取参数列表长度，根据传入的Request向里面填实参
         */
        Object[] paramResult = new Object[paramsTypes.length];
        /**
         * 从Request中获取实参的map,下面根据paramIndexMapping向实参列表里面填入实际参数
         */
        Map<String,String[]> paramFromRequest = request.getParameterMap();
        /**
         * 先提取GPRequestParam这样的参数项,在参数列表中排在第几位
         * 然后从paramFromRequest中找到对应的参数，放在对应的顺序位置
         */
        for (int i = 0; i < pa.length; i++) {
            for (Annotation annotation : pa[i]) {
                if (annotation instanceof GPRequestParam) {
                    String param = ((GPRequestParam) annotation).value();
                    if (StringUtils.isNotBlank(param)) {
                        String valueFromRequest = Arrays.toString(paramFromRequest.get(param)).replaceAll("\\[|\\]","")
                                .replaceAll("\\s",",");
                        paramResult[i] = caseStringValue(valueFromRequest, paramsTypes[i]);
                    }
                }
            }
        }

        for (int i = 0; i < paramsTypes.length; i++) {
            Class<?> type = paramsTypes[i];
            if (type == HttpServletRequest.class) {
                paramResult[i] = request;
            } else if (type == HttpServletResponse.class) {
                paramResult[i] = response;
            }
        }

        Method method = handlerMapping.getMethod();
        /**
         * *****************************************
         * 真正的核心，实际反射调用handler处理方法
         * *****************************************
         */
        Object result = method.invoke(handlerMapping.getController(), paramResult);
        if (result == null || result instanceof Void) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == GPModelAndView.class;
        if (isModelAndView) {
            return (GPModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> paramsType) {
        if (String.class == paramsType) {
            return value;
        }
        //如果是int
        if (Integer.class == paramsType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramsType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return "unkown";
        }
    }

}
