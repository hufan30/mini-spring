package com.gupaoedu.vip.spring.formework.context;

import com.gupaoedu.vip.spring.formework.beans.GPBeanWrapper;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;
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
    private String[] configLocations;
    private GPBeanDefinitionReader reader;

    public GPApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            log.info(e.getMessage(),e);
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
    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                log.info("已经存在该类名："+beanDefinition.getFactoryBeanName());
                continue;
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return null;
    }
}
