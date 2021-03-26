package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import com.gupaoedu.vip.spring.formework.context.GPApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

@Slf4j
public class GPDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private GPApplicationContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        /**
         * 这里的config.getInitParameter是从web.xml读取配置，是Tomcat的启动内容；
         */
        context = new GPApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
//        initStrategies(context);
    }
}
