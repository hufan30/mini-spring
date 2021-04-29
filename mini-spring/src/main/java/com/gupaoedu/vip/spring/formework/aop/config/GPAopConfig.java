package com.gupaoedu.vip.spring.formework.aop.config;

import lombok.Data;

/**
 * Created by Tom on 2019/4/15.
 * 定义AOP配置信息的封装类
 */
@Data
public class GPAopConfig {
    /**
     * 以下配置和Properties中定义的对应
     */
    /**
     * 切面表达式
     */
    private String pointCut;
    /**
     * 前置通知方法名
     */
    private String aspectBeforeMethod;
    /**
     * 后置通知方法名
     */
    private String aspectAfterMethod;
    /**
     * 要织入的切面类
     */
    private String aspectClass;
    /**
     * 异常通知的方法名
     */
    private String aspectAfterThrowMethod;
    /**
     * 异常要通知的类型
     */
    private String aspectAfterThrowingType;

}
