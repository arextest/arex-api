package com.arextest.web.api.service.aspect;

import com.arextest.web.api.service.controller.ControllerException;
import com.arextest.web.common.LogUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@Aspect
public class WebLogAspect {

  private static final String CLASS_METHOD = "ClassMethod";
  private static final String CONTROLLER_EXCEPTION = ControllerException.class.getName();
  @Resource
  private ObjectMapper mapper;

  @Pointcut("execution(* com.arextest.web.api.service.controller.*.*(..)) "
      + "&& !execution(* com.arextest.web.api.service.controller.CheckHealthController.*(..))"
      + "&& !execution(* com.arextest.web.api.service.controller.ReportQueryController.downloadReplayMsg(..))")
  public void webLog() {
  }

  @Around("webLog()")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return joinPoint.proceed();
    }

    if (CONTROLLER_EXCEPTION.equals(joinPoint.getSignature().getDeclaringTypeName())) {
      return joinPoint.proceed();
    }

    long startTime = System.currentTimeMillis();
    long endTime = 0;
    Object result = null;
    try {
      result = joinPoint.proceed();
      endTime = System.currentTimeMillis();
    } catch (Throwable e) {
      endTime = System.currentTimeMillis();
      throw e;
    } finally {
      HttpServletRequest request = attributes.getRequest();
      StringBuilder builder = new StringBuilder();
      builder.append(
          "========================================== Start ==========================================\r\n");
      builder.append("URL            : ").append(request.getRequestURL().toString()).append("\r\n");
      builder.append("HTTP Method    : ").append(request.getMethod()).append("\r\n");
      builder.append("Class Method   : ").append(joinPoint.getSignature().getDeclaringTypeName())
          .append(".")
          .append(joinPoint.getSignature().getName()).append("\r\n");
      builder.append("IP             : ").append(request.getRemoteAddr()).append("\r\n");
      builder.append("Request        : ").append(serializeRequest(joinPoint.getArgs()))
          .append("\r\n");
      builder.append("Response       : ").append(mapper.writeValueAsString(result)).append("\r\n");
      builder.append("Time-Consuming : ").append(endTime - startTime).append(" ms\r\n");
      builder
          .append(
              "=========================================== End ===========================================");
      Map<String, String> tags = new HashMap<>();
      tags.put(CLASS_METHOD,
          MessageFormat.format("{0}.{1}", joinPoint.getSignature().getDeclaringTypeName(),
              joinPoint.getSignature().getName()));
      LogUtils.info(LOGGER, tags, builder.toString());
    }

    return result;
  }

  private String serializeRequest(Object[] args) throws JsonProcessingException {
    if (ArrayUtils.isEmpty(args)) {
      return null;
    }

    for (Object arg : args) {
      // maybe MethodArgumentNotValidException
      if (arg instanceof Throwable) {
        return null;
      }
    }

    return mapper.writeValueAsString(args);
  }
}
