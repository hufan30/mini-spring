package com.gupaoedu.vip.spring.formework.beans.support;


import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.formework.annotation.GPService;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GPBeanDefinitionReader {

//    private static final Logger log = LoggerFactory.getLogger(GPBeanDefinitionReader.class);
    /**
     * eg：com.gupaoedu.vip.spring.demo.action.MyAction
     */
    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    private static final String SCAN_PACKAGE = "scanPackage";

    public GPBeanDefinitionReader(String... configLocations) {
        /**
         * 这里是从web.xml中穿过来的参数，只是说明classpath:application.properties
         * 具体的内容还需要去读取；而且需要替换路径中的名称
         */
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(StringUtils.substringAfter(configLocations[0], ":"));
        try {
            config.load(inputStream);
        } catch (IOException e) {
            log.info(e.getMessage(),e);
        } finally {
            /**
             * 注意这里输入输出流，需要关闭close
             */
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 1.转换为文件路径，实际上就是把.替换为/就OK了
     * 2.然后把扫描路径里面的class,全部添加到registyBeanClasses
     * 这里的套路是判断路径对应的file，是不是文件夹，是就继续scan
     * 是文件就添加name到全部添加到registyBeanClasses
     *
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {

        /**
         * 参数为com.gupaoedu.vip.spring.demo
         * 书上代码版本，拿到的结果是file:/G:/spring5-samples/gupaoedu-vip-spring-2.0/target/classes/com/gupaoedu/vip/spring/demo/
         * 从target中拿结果
         */
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());

        /**
         * 下面为实验测试版本，filePath拿不到,
         * user.dir拿到的是工程路径，这里是tomcat的安装路径，不是这个代码工程的路径
         */
        String userDir = System.getProperty("user.dir");
        String filePath = "/" + scanPackage.replaceAll("\\.", "/");
        File classPath2 = new File(userDir);

        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String beanName = scanPackage + "." + file.getName().replace(".class", "");
                this.registyBeanClasses.add(beanName);
            }
        }
    }

    /**
     * 根据已经扫描到的类，将其解析为beanDefinition
     * 应该是加载到类后，使用反射解析里面的@autowired或者构造器类
     * 1.拿到对应的class类
     * 2.使用反射
     *
     * @return
     */
    public List<GPBeanDefinition> loadBeanDefinitions() {
        List<GPBeanDefinition> result = new ArrayList<GPBeanDefinition>();
        try {
            for (String beanClassName : registyBeanClasses) {
                Class<?> beanClass = Class.forName(beanClassName);
                //如果是一个接口，是不能实例化的
                //用它实现类来实例化
                if (beanClass.isInterface()) {
                    continue;
                }
                /**
                 * 判断类的注解，是否有@GPComponent，@GPService,
                 * 这里改成只有扫描到注解的才解析beanDefination
                 * 根据spring，它的父类接口也需要解析
                 */
                //TODO 同原版不一样
                if (beanClass.isAnnotationPresent(GPController.class) || beanClass.isAnnotationPresent(GPService.class)) {
                    result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                    Class<?>[] interfaces = beanClass.getInterfaces();
                    /**
                     * 它的接口父类也要加进去
                     */
                    for (Class<?> i : interfaces) {
                        result.add(doCreateBeanDefinition(i.getSimpleName(), i.getName()));
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            log.info(e.getMessage(), e);
        }
        return result;
    }

    private GPBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    //如果类名本身是小写字母，确实会出问题
    //但是我要说明的是：这个方法是我自己用，private的
    //传值也是自己传，类也都遵循了驼峰命名法
    //默认传入的值，存在首字母小写的情况，也不可能出现非字母的情况

    //为了简化程序逻辑，就不做其他判断了，大家了解就OK
    //其实用写注释的时间都能够把逻辑写完了
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private boolean isContainsGP(Annotation[] annotations) {
        for (Annotation annotation : annotations) {

        }
        return false;
    }

}

