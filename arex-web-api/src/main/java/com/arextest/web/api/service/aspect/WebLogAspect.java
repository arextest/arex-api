package com.arextest.web.api.service.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Slf4j
@Component
@Aspect
public class WebLogAspect {
    @Pointcut("execution(* com.arextest.web.api.service.controller.*.*(..)) " +
            "&& !execution(* com.arextest.web.api.service.controller.CheckHealthController.*(..))" +
            "&& !execution(* com.arextest.web.api.service.controller.ReportQueryController.downloadReplayMsg(..))")
    public void webLog() {
    }

    @Resource
    private ObjectMapper mapper;


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        try {
            LOGGER.info("========================================== Start ==========================================");
            LOGGER.info("URL            : {}", request.getRequestURL().toString());
            LOGGER.info("HTTP Method    : {}", request.getMethod());
            LOGGER.info("Class Method   : {}.{}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            LOGGER.info("IP             : {}", request.getRemoteAddr());
            LOGGER.info("Request Args   : {}", mapper.writeValueAsString(joinPoint.getArgs()));
        } catch (Exception e) {
            LOGGER.error("Failed to log info", e);
        }
    }


    @After("webLog()")
    public void doAfter() throws Throwable {
        LOGGER.info("=========================================== End ===========================================");

        LOGGER.info("");
    }


    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();

        LOGGER.info("Response Args  : {}", new Gson().toJson(result));

        LOGGER.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

}
