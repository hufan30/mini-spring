package com.gupaoedu.vip.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 * 使用模板设计GPAdstractAspectJAdvice类，封装拦截器回调的通用逻辑，在本mini-spring中拦截器主要是实现aop，本质还是代理的invoke
 * 这里就是描述了拦截器回调的通用逻辑，用大白话就是拦截器只是用成员变量保存了反射的对象和方法，这里用invoke去真正调用
 * 这个反射的对象和方法，不一定是target，也可能是你自定义的aop的前置或者后置方法；
 */
public abstract class GPAbstractAspectAdvice implements GPAdvice {
    private Method aspectMethod;
    private Object aspectTarget;

    public GPAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(GPJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable {
        Class<?>[] paramTypes = this.aspectMethod.getParameterTypes();
        if (null == paramTypes || paramTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == GPJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = tx;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }


}
