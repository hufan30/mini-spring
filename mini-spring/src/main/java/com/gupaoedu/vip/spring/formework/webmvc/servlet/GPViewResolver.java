package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Locale;

/**
 * Created by Tom on 2019/4/13.
 */
public class GPViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    //视图文件所处文件夹的路径，也就是layouts；
    private File templateRootDir;

    public GPViewResolver(File templateRootDir) {
        this.templateRootDir = templateRootDir;
    }

    public GPView resolveViewName(String viewName, Object o) {
        if (StringUtils.isBlank(viewName)) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new GPView(templateFile);
    }
}
