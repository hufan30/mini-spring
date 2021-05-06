package com.gupaoedu.vip.spring.formework.aop.support;

import com.gupaoedu.vip.spring.formework.aop.config.GPAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom on 2019/4/14.
 */
public class GPAdvisedSupport {

    private Class<?> targetClass;
    private Object target;

    private transient Map<Method, List<Object>> methodCache;

    private GPAopConfig config;
    private Pattern pointCutClassPattern;


    public GPAdvisedSupport(GPAopConfig config) {
        this.config = config;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget(){
        return this.target;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());

            cached = methodCache.get(m);

            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m,cached);
        }

        return cached;
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        //玩正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));


    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
