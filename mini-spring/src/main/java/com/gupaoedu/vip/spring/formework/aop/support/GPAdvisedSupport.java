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

    public Object getTarget() {
        return this.target;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    /**
     * 这个其实相当于是GPAdvisedSupport的初始化函数
     * 调用了parse,里面会向methodCache存放缓存
     *
     * @param targetClass
     */
    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        //玩正则
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));


    }

    /**
     * 本质是从缓存中拿东西，感觉逻辑有点问题，一定要从缓存中拿
     * 先根据传入参数method从缓存中拿，传参拿不到，就使用本地的targetClass的method来拿
     * 初步推测是因为parse()方法有放入cache过，所以一定能拿到；
     *
     * @param method
     * @return
     * @throws Exception
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method) throws Exception {
        List<Object> result = methodCache.get(method);
        if (null == result) {
            Method targetClassMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            /**
             * 这里应该是parse()中有根据targetClass存入缓存中，所以默认一定能拿到
             */
            result = methodCache.get(targetClassMethod);
            this.methodCache.put(method, result);
        }
        return result;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
