package com.arextest.web.api.service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author wildeslam.
 * @create 2023/10/8 17:12
 */
@Slf4j
@Aspect
@Component
public class AppAuthAspect {
    @Pointcut("@annotation(com.arextest.web.api.service.annotation.AppAuth)")
    public void appAuth(){}

    @Before("appAuth()")
    public void doBefore(JoinPoint point) {
        int i = 1;
    }

}
