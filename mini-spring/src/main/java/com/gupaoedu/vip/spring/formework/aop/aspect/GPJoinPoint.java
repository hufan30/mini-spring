package com.gupaoedu.vip.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 * 定义一个切点的抽象，这是AOP的基础组成单元。我们可以理解为这是一个业务方法的附加信息。
 * 那么很自然的，这个切点应该包含业务方法本身，实参列表和方法所属的实例对象
 */
public interface GPJoinPoint {

    /**
     * 返回该方法所属的实例对象
     * @return
     */
    Object getThisInstanceObject();

    /**
     * 返回该方法的实参列表，这里后续在体会一下，真的是实参，实际参数吗
     * @return
     */
    Object[] getArguments();

    /**
     * 返回业务方法本身
     * @return
     */
    Method getMethod();

    /**
     * 在JoinPoint中添加自定义属性
     * @param key
     * @param value
     */
    void setUserAttribute(String key, Object value);

    /**
     * 从已添加的自定义属性中获取一个属性值
     * @param key
     * @return
     */
    Object getUserAttribute(String key);
}
