package com.gupaoedu.vip.spring.formework.aop;

/**
 * Created by Tom.
 * 代理工厂的顶层接口，提供获取代理对象的顶层入口
 * 默认使用JDK动态代理
 */
public interface GPAopProxy {

    /**
     * 获取一个代理对象
     * @return
     */
    Object getProxy();

    /**
     * 使用自定义的类加载器，来获取一个代理对象
     * @param classLoader
     * @return
     */
    Object getProxy(ClassLoader classLoader);
}
