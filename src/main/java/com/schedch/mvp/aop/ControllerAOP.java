package com.schedch.mvp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Slf4j
//@Aspect
//@Component
public class ControllerAOP {

    //모든 controller에 적용
    @Pointcut("execution(* com.schedch.mvp.controller..*.*(..))")
    private void controllerMethodStartEnd() {}

    @Before(value = "controllerMethodStartEnd()")
    public void beforeMethod(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.info("=========== start method {}.{} ===============", method.getDeclaringClass(), method.getName());
    }

    @After(value = "controllerMethodStartEnd()")
    public void afterReturning(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.info("=========== end method {}.{} ===============", method.getDeclaringClass(), method.getName());

    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

}
