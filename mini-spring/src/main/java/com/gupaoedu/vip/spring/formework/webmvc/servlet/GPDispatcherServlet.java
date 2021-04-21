package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.formework.annotation.GPRequestMapping;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;
import com.gupaoedu.vip.spring.formework.context.GPApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class GPDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
    private List<GPHandlerMapping> handlerMappings = new ArrayList<>();
    private List<GPHandlerAdapter> handlerAdapters = new ArrayList();
    private List<GPViewResolver> viewResolvers = new ArrayList<GPViewResolver>();

    private GPApplicationContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        /**
         * 这里的config.getInitParameter是从web.xml读取配置，是Tomcat的启动内容；
         */
        context = new GPApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
        initStrategies(context);
    }

    //初始化策略
    private void initStrategies(GPApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);

        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);

        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);

        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(GPApplicationContext context) {
        //这里是从加载的config,也就是applicaiton.properties中拿配置项
        String templateRoot = context.getConfig().getProperty("templateRoot");
        //Application.properties中配置了视图所处的文件夹名称，拿到对应文件夹的路径名称
        String templateRootPath = context.getClass().getClassLoader().getResource(templateRoot).getFile();
        //根据文件夹路径名称，拿到文件
        File templateRootDir = new File(templateRootPath);
        //这里其实用一个viewResolver就可以搞定
        //只是为了模拟真实，根据文件夹下有几个文件，不同文件按理说对应不同的view解析器
        String[] layouts = templateRootDir.list();
        for (String layout : layouts) {
            GPViewResolver gpViewResolver = new GPViewResolver(templateRootDir);
        }
    }

    private void initViewResolvers(GPApplicationContext context) {

    }

    private void initRequestToViewNameTranslator(GPApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GPApplicationContext context) {
    }

    private void initHandlerMappings(GPApplicationContext context) {
        /**
         * 找@GPController，然后根据里面的@GPRequestMapping，映射成处理器；
         *
         */
        Set<String> beanDefinitonMapNames = context.getBeanDefinitonMapNames();
        try {
            for (String beanDefinitonMapName : beanDefinitonMapNames) {
                Object controllerBean = context.getBean(beanDefinitonMapName);
                Class<?> beanClass = controllerBean.getClass();

                if (!beanClass.isAnnotationPresent(GPController.class)) {
                    continue;
                }
                //拿到这个Handler的baseurl
                String baseUrl = "";
                if (beanClass.isAnnotationPresent(GPRequestMapping.class)) {
                    baseUrl = beanClass.getAnnotation(GPRequestMapping.class).value();
                }
                //拿到具体方法的url;
                Method[] methods = beanClass.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }
                    String methodUrl = method.getAnnotation(GPRequestMapping.class).value();
                    String regex = ("/" + baseUrl + "/" + methodUrl.replaceAll("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new GPHandlerMapping(pattern, controllerBean, method));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参
     * 可想而知，他要拿到HandlerMapping才能干活
     * 就意味着，有几个HandlerMapping就有几个HandlerAdapter
     *
     * @param context
     */
    private void initHandlerAdapters(GPApplicationContext context) {
        this.handlerAdapters.add(new GPHandlerAdapter());
    }

    private void initThemeResolver(GPApplicationContext context) {

    }

    private void initLocaleResolver(GPApplicationContext context) {

    }

    private void initMultipartResolver(GPApplicationContext context) {

    }
}
