package com.arextest.web.api.service.aspect;

import com.arextest.web.common.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@Aspect
public class WebLogAspect {
    @Pointcut("execution(* com.arextest.web.api.service.controller.*.*(..)) " +
            "&& !execution(* com.arextest.web.api.service.controller.CheckHealthController.*(..))" +
            "&& !execution(* com.arextest.web.api.service.controller.ReportQueryController.downloadReplayMsg(..))")
    public void webLog() {
    }

    private static final String CLASS_METHOD = "ClassMethod";

    @Resource
    private ObjectMapper mapper;


    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        long startTime = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("========================================== Start ==========================================\r\n");
        sb.append("URL            : ").append(request.getRequestURL().toString()).append("\r\n");
        sb.append("HTTP Method    : ").append(request.getMethod()).append("\r\n");
        sb.append(MessageFormat.format("Class Method   : {0}.{1}\r\n",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()));
        sb.append(MessageFormat.format("IP             : {0}\r\n", request.getRemoteAddr()));
        sb.append(MessageFormat.format("Request Args   : {0}\r\n", mapper.writeValueAsString(joinPoint.getArgs())));

        Object result = joinPoint.proceed();

        sb.append(MessageFormat.format("Response Args  : {0}\r\n", mapper.writeValueAsString(result)));
        sb.append(MessageFormat.format("Time-Consuming : {0} ms\r\n", System.currentTimeMillis() - startTime));
        sb.append("=========================================== End ===========================================");
        Map<String, String> tags = new HashMap<>();
        tags.put(CLASS_METHOD,
                MessageFormat.format("{0}.{1}",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName()));
        LogUtils.info(LOGGER, tags, sb.toString());
        return result;
    }

}
