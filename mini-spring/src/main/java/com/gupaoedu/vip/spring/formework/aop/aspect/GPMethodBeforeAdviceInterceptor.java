package com.gupaoedu.vip.spring.formework.aop.aspect;

import com.gupaoedu.vip.spring.formework.aop.intercept.GPMethodInterceptor;
import com.gupaoedu.vip.spring.formework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 */
public class GPMethodBeforeAdviceInterceptor extends GPAbstractAspectAdvice implements GPAdvice, GPMethodInterceptor {


    private GPJoinPoint joinPoint;

    public GPMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        //传送了给织入参数
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

    /**
     * 这里和AfterAdvice对比一下，就能明白AdviceInterceptor子类控制执行顺序的含义
     * 作为before的拦截器，是需要自己本身先执行，然后去循环嵌套，看看拦截器链条中是否还有下一个前置before的拦截器，也是在target之前执行；
     * @param mi
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint，这里首次调用的时候joinPoint是null，然后第二次调用的时候重新赋值，覆盖之前的jointPoint;
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThisInstanceObject());
        return mi.proceed();
    }
}
