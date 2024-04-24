package com.arextest.web.api.service.aspect;

import com.arextest.common.annotation.AppAuth;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

  @Resource
  private AppAuthAspectExecutor appAuthAspectExecutor;

  @Pointcut(AppAuthAspectExecutor.POINT_CONTENT)
  public void appAuth() {
  }

  @Around(AppAuthAspectExecutor.AROUND_CONTENT)
  public Object doAround(ProceedingJoinPoint point, AppAuth auth) throws Throwable {
    return appAuthAspectExecutor.doAround(point, auth);
  }

}
