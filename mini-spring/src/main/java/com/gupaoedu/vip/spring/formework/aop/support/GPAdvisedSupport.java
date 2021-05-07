package com.gupaoedu.vip.spring.formework.aop.support;

import com.gupaoedu.vip.spring.formework.aop.aspect.GPAfterReturningAdviceInterceptor;
import com.gupaoedu.vip.spring.formework.aop.aspect.GPAfterThrowingAdviceInterceptor;
import com.gupaoedu.vip.spring.formework.aop.aspect.GPMethodBeforeAdviceInterceptor;
import com.gupaoedu.vip.spring.formework.aop.config.GPAopConfig;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class GPAdvisedSupport {

    private Class<?> targetClass;
    private Object target;
    /**
     * 这里应该就是初步的回调的方法链，某一个方法，对应一个list，回调的方法链
     */
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
        /**
         * 1.给本类的正则表达式赋值，从成员变量中的config获取相关信息；
         */
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        Pattern pattern = Pattern.compile(pointCut);
        methodCache = new HashMap<Method, List<Object>>();

        /**
         * 2.从config中获取targetClass的method信息
         */
        try {
            Class<?> aspectClass = Class.forName(config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }

            /**
             * 3.从成员变量targetClass中获取method信息，同从config中获取的method信息对比
             */
            for (Method m : targetClass.getMethods()) {
                String methodString = m.toString();
                /**
                 * 这里目前不太清楚为什么包含throws就需要截取methodString
                 */
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每一个方法包装成 MethodIterceptor
                    //before
                    if (!(null == config.getAspectBeforeMethod() || "".equals(config.getAspectBeforeMethod()))) {
                        //创建一个Advivce
                        advices.add(new GPMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBeforeMethod()), aspectClass.newInstance()));
                    }
                    //after
                    if (!(null == config.getAspectAfterMethod() || "".equals(config.getAspectAfterMethod()))) {
                        //创建一个Advivce
                        advices.add(new GPAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfterMethod()), aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if (!(null == config.getAspectAfterThrowMethod() || "".equals(config.getAspectAfterThrowMethod()))) {
                        //创建一个Advivce
                        GPAfterThrowingAdviceInterceptor throwingAdvice =
                                new GPAfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrowMethod()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingType());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(m, advices);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }


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
