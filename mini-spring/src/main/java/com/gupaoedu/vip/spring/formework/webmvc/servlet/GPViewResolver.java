package com.gupaoedu.vip.spring.formework.webmvc.servlet;

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

}
