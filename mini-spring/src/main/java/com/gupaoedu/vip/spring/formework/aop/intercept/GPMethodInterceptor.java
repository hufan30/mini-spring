package com.gupaoedu.vip.spring.formework.aop.intercept;

/**
 * Created by Tom on 2019/4/14.
 * 方法拦截器是AOP代码增强的基本组成单元。其子类主要有GPMethoBeforeAdvice,GPMethodAfterAdvice,GPAfterThrowingAdvice;
 */
public interface GPMethodInterceptor {
    Object invoke(GPMethodInvocation invocation) throws Throwable;
}
