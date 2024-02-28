package com.arextest.web.api.service.aspect;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.context.ArexContext;
import com.arextest.common.exceptions.ArexException;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.JwtUtil;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.config.model.dto.SystemConfiguration;
import com.arextest.config.model.dto.application.ApplicationConfiguration;
import com.arextest.config.repository.impl.ApplicationConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.SystemConfigurationRepositoryImpl;
import com.arextest.web.api.service.controller.Constants;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.arextest.web.common.exception.ArexApiResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author wildeslam.
 * @create 2023/10/8 17:12
 */
@Slf4j
@Aspect
@Component
public class AppAuthAspect {
  private static Boolean authSwitch = null;

  @Resource
  private ApplicationConfigurationRepositoryImpl applicationConfigurationRepository;

  @Resource
  private SystemConfigurationRepositoryImpl systemConfigurationRepository;

  @Pointcut("@annotation(com.arextest.common.annotation.AppAuth)")
  public void appAuth() {
  }

  @Around("appAuth() && @annotation(auth)")
  public Object doAround(ProceedingJoinPoint point, AppAuth auth) throws Throwable {
    if (authSwitch == null) {
      init();
    }
    if (!authSwitch) {
      return point.proceed();
    }
    ArexContext context = ArexContext.getContext();
    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();
    String appId = request.getHeader("appId");
    String accessToken = request.getHeader("access-token");
    String userName = JwtUtil.getUserName(accessToken);
    context.setAppId(appId);
    context.setOperator(userName);
    if (appId == null) {
      LOGGER.error("header has no appId");
      return reject(point, auth, Constants.NO_APPID);
    }
    List<ApplicationConfiguration> applications = applicationConfigurationRepository.listBy(
        context.getAppId());
    if (CollectionUtils.isEmpty(applications)) {
      LOGGER.error("error appId");
      return reject(point, auth, Constants.ERROR_APPID);
    }
    ApplicationConfiguration application = applications.get(0);
    Object result;
    if (CollectionUtils.isEmpty(application.getOwners()) || application.getOwners()
        .contains(userName)) {
      context.setPassAuth(true);
      result = point.proceed();
    } else {
      context.setPassAuth(false);
      result = reject(point, auth, Constants.NO_PERMISSION);
    }
    ArexContext.removeContext();
    return result;
  }

  private Object reject(ProceedingJoinPoint point, AppAuth auth, String remark) throws Throwable {
    switch (auth.rejectStrategy()) {
      case FAIL_RESPONSE:
        return ResponseUtils.errorResponse(remark, ResponseCode.AUTHENTICATION_FAILED);
      case DOWNGRADE:
        ArexContext.getContext().setPassAuth(false);
      default:
        return point.proceed();
    }
  }

  private void init() {
    authSwitch = Optional.ofNullable(
            systemConfigurationRepository.getSystemConfigByKey(SystemConfigurationCollection.KeySummary.AUTH_SWITCH))
        .map(SystemConfiguration::getAuthSwitch)
        .orElse(null);
    if (authSwitch == null) {
      throw new ArexException(ArexApiResponseCode.AUTHENTICATION_FAILED, "get authSwitch failed, please update "
          + "storage version");
    }
  }

}
