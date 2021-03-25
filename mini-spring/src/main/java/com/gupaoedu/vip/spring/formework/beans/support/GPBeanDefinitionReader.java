package com.gupaoedu.vip.spring.formework.beans.support;

import sun.security.krb5.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GPBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    private static final String SCAN_PACKAGE = "scanPackage";

    public GPBeanDefinitionReader(String[] configLocations){

    }


}
