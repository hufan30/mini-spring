package com.gupaoedu.vip.spring.formework.context;

import com.gupaoedu.vip.spring.formework.beans.GPBeanWrapper;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanPostProcessor;
import com.gupaoedu.vip.spring.formework.beans.support.GPBeanDefinitionReader;
import com.gupaoedu.vip.spring.formework.beans.support.GPDefaultListableBeanFactory;
import com.gupaoedu.vip.spring.formework.core.GPBeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    /**
     * 存放config地址
     */
    private final String[] configLocations;
    private GPBeanDefinitionReader reader;

    //单例的IOC容器缓存，这玩意就相当于是个临时的工具，缓存一下bean，目的是为了创建单例bean,从map里面已有的拿现成的，避免重复创建
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    //通用的IOC容器,这个才是真正存放实例化后bean的
    private final Map<String, GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
    }

    @Override
    public void refresh() throws Exception {
        //1、定位，定位配置文件
        reader = new GPBeanDefinitionReader(this.configLocations);

        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);

        //4、把不是延时加载的类，有提前初始化
        doAutowrited();
    }

    private void doAutowrited() {
        for (Map.Entry<String, GPBeanDefinition> entry : super.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if (!entry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                log.info("已经存在该类名：" + beanDefinition.getFactoryBeanName());
                continue;
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    //1、保留原来的OOP关系
    //2、我需要对它进行扩展，增强（为了以后AOP打基础）
    @Override
    public Object getBean(String beanName) throws Exception {
        GPBeanDefinition gpBeanDefinition = super.beanDefinitionMap.get(beanName);
        Object bean = null;
        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式,对bean进行预处理
        GPBeanPostProcessor gpBeanPostProcessor = new GPBeanPostProcessor();
        gpBeanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        //基本实例化bean
        bean = instantiateBean(beanName, gpBeanDefinition);
        //3、把这个对象封装到BeanWrapper中
        GPBeanWrapper beanWrapper = new GPBeanWrapper(bean);
        //后置处理
        //TODO 这里有一个疑问，beanWrpper封装了bean实例，这时候再对bean实例做处理，beanWrapper里面推测应该是会跟着变化的；
        gpBeanPostProcessor.postProcessAfterInitialization(bean, beanName);


        return bean;
    }

    private Object instantiateBean(String beanName, GPBeanDefinition gpBeanDefinition) {
        //1.这里是需要完整的类名来反射实例化；com.gupaoedu.vip.spring.demo.service.IModifyService
        String beanClassName = gpBeanDefinition.getBeanClassName();
        //反射实例化，得到一个对象
        Object instance = null;
        /*
          这里下面的Cache其实是相当于是个临时的变量，就是为了这个方法循环的时候，避免重复创建实例，创建好了就放到cache里；
          下次再需要创建的时候，就从map里面先看一下；
          那为什么不在这里变成临时变量呢？因为循环不是在这里方法里面的，循环是在外层方法，如果是本地循环，可以放这里；
          外层循环，每次进来cache都new一下的话，那不就废了，所以cache要从外面穿进来，两种思路，一种是变成全局变量，也就是现在这样；直接使用this.cache;
          还有一种就是在外层方法循环的时候，创建，然后传参一路传进来，相比之下，用全局好像更加直观；
        */
        if (!this.factoryBeanObjectCache.containsKey(beanClassName)) {
            try {
                Class<?> beanClass = Class.forName(beanClassName);
                instance = beanClass.newInstance();
                //TODO 预留以后AOP的处理
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            this.factoryBeanObjectCache.put(beanClassName, instance);
            this.factoryBeanObjectCache.put(beanName, instance);
        } else {
            instance = factoryBeanObjectCache.get(beanName);
        }
        return instance;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }
}
