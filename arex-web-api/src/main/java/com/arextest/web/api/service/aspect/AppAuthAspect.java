package com.arextest.web.api.service.aspect;

import com.arextest.common.annotation.AppAuth;
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
    @Pointcut("@annotation(com.arextest.common.annotation.AppAuth)")
    public void appAuth(){}

    @Before("appAuth() && @annotation(auth)")
    public void doBefore(JoinPoint point, AppAuth auth) {

        int i = 1;
    }

}
