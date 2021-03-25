package com.gupaoedu.vip.spring.formework.beans.support;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class GPBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    private static final String SCAN_PACKAGE = "scanPackage";

    public GPBeanDefinitionReader(String[] configLocations){
        /**
         * 这里是从web.xml中穿过来的参数，只是说明classpath:application.properties
         * 具体的内容还需要去读取；
         */
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configLocations[0]);
        try {
            config.load(inputStream);
        } catch (IOException e) {
           log.info(e.getMessage());
        }finally {
            /**
             * 注意这里输入输出流，需要关闭close
             */
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //转换为文件路径，实际上就是把.替换为/就OK了
        /**
         * 然后把扫描路径里面的class,全部添加到registyBeanClasses
         * 这里的套路是判断路径对应的file，是不是文件夹，是就继续scan
         * 是文件就添加name到全部添加到registyBeanClasses
         */
        URL resource = this.getClass().getResource("/"+scanPackage.replaceAll("\\.","/"));



    }


}
