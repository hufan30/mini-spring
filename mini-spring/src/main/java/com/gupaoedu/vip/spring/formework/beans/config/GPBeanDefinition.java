package com.gupaoedu.vip.spring.formework.beans.config;

import lombok.Data;

/**
 * Created by Tom.
 */
@Data
public class GPBeanDefinition {
    /**
     * bean的全类名
     */
    private String beanClassName;
    private boolean lazyInit = false;
    /**
     * bean在map中存储的key名称,在工厂中的名称
     */
    private String factoryBeanName;
    private boolean isSingleton = true;

//    public String getBeanClassName() {
//        return beanClassName;
//    }
//
//    public void setBeanClassName(String beanClassName) {
//        this.beanClassName = beanClassName;
//    }
//
//    public boolean isLazyInit() {
//        return lazyInit;
//    }
//
//    public void setLazyInit(boolean lazyInit) {
//        this.lazyInit = lazyInit;
//    }
//
//    public String getFactoryBeanName() {
//        return factoryBeanName;
//    }
//
//    public void setFactoryBeanName(String factoryBeanName) {
//        this.factoryBeanName = factoryBeanName;
//    }
}
