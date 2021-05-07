package com.gupaoedu.vip.spring.formework.aop;

import com.gupaoedu.vip.spring.formework.aop.intercept.GPMethodInvocation;
import com.gupaoedu.vip.spring.formework.aop.support.GPAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by Tom on 2019/4/14.
 */
public class GPJdkDynamicAopProxy implements GPAopProxy, InvocationHandler {

    private GPAdvisedSupport advised;

    public GPJdkDynamicAopProxy(GPAdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advised.getTargetClass().getInterfaces(), this);
    }

    /**
     * 执行代理的关键入口
     * 这里是为什么AOP能生效的入口，本来没有用aop的时候，bean就是bean，在HandlerAdapter中invoke方法的时候是直接去了target方法
     * 但是现在在初始化bean，也就是applicationContext里面，此时初始化出来的bean就是$Proxy了
     * 比如mini-spring中的MyAction，里面的service，如果没有aop就直接调用service的方法，但是现在注入MyAction的service是$Proxy
     * 在执行对于的controller方法的时候，是来到这里；
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**
         * 将每一个JoinPoint也就是被代理的业务方法（method）封装成一个拦截器，组合成一个拦截器链
         */
        List<Object> interceptorsAndDynamicMethodMatchers = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method);
        /**
         * 交给拦截器链MethodInvocation的proceed()方法执行
         */
        GPMethodInvocation invocation = new GPMethodInvocation(proxy, this.advised.getTarget(), method, args, this.advised.getTargetClass(), interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
