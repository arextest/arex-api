package com.arextest.web.api.service.aspect;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.context.ArexContext;
import com.arextest.common.exceptions.ArexException;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.utils.ResponseUtils_New;
import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.config.model.dto.application.ApplicationConfiguration;
import com.arextest.config.model.dto.system.SystemConfiguration;
import com.arextest.config.repository.impl.ApplicationConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.SystemConfigurationRepositoryImpl;
import com.arextest.web.api.service.controller.Constants;
import com.arextest.web.common.exception.ArexApiResponseCode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RequiredArgsConstructor
public class AppAuthAspectExecutor {


  public static final String POINT_CONTENT = "@annotation(com.arextest.common.annotation.AppAuth)";

  public static final String AROUND_CONTENT = "appAuth() && @annotation(auth)";

  private static Boolean authSwitch = null;

  private final ApplicationConfigurationRepositoryImpl applicationConfigurationRepository;

  private final SystemConfigurationRepositoryImpl systemConfigurationRepository;

  private final JWTService jwtService;

  public Object doAround(ProceedingJoinPoint point, AppAuth auth) throws Throwable {
    try {
      if (!judgeByAuth()) {
        return point.proceed();
      }

      ArexContext context = ArexContext.getContext();

      setContext();

      if (context.getAppId() == null) {
        LOGGER.error("header has no appId");
        return reject(point, auth, Constants.NO_APPID, ArexApiResponseCode.APP_AUTH_NO_APP_ID);
      }
      OwnerExistResult ownerExistResult = getOwnerExistResult();
      return processOwnerExistVerify(ownerExistResult, context, point, auth);
    } finally {
      ArexContext.removeContext();
    }
  }


  protected boolean judgeByAuth() {
    if (authSwitch == null) {
      init();
    }
    return authSwitch;
  }

  protected void setContext() {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      return;
    }
    HttpServletRequest request = requestAttributes.getRequest();
    String appId = request.getHeader("appId");
    String accessToken = request.getHeader("access-token");
    String userName = jwtService.getUserName(accessToken);
    ArexContext context = ArexContext.getContext();
    context.setAppId(appId);
    context.setOperator(userName);
  }

  protected OwnerExistResult getOwnerExistResult() {
    ArexContext context = ArexContext.getContext();
    String userName = context.getOperator();

    List<ApplicationConfiguration> applications = applicationConfigurationRepository.listBy(
        context.getAppId());
    if (CollectionUtils.isEmpty(applications)) {
      LOGGER.error("error appId, appId: {}", context.getAppId());
      return new OwnerExistResult(false, Constants.ERROR_APPID,
          ArexApiResponseCode.APP_AUTH_ERROR_APP_ID);
    }
    Set<String> owners = applications.get(0).getOwners();
    if (CollectionUtils.isEmpty(owners) || owners.contains(userName)) {
      return new OwnerExistResult(true, null, null);
    } else {
      return new OwnerExistResult(false, Constants.NO_PERMISSION,
          ArexApiResponseCode.APP_AUTH_NO_PERMISSION);
    }
  }


  private Object processOwnerExistVerify(OwnerExistResult ownerExistResult, ArexContext context,
      ProceedingJoinPoint point, AppAuth auth)
      throws Throwable {
    if (ownerExistResult.getExist()) {
      context.setPassAuth(true);
      return point.proceed();
    } else {
      context.setPassAuth(false);
      return reject(point, auth, ownerExistResult.getRemark(), ownerExistResult.getResponseCode());
    }
  }


  private Object reject(ProceedingJoinPoint point, AppAuth auth, String remark, int responseCode)
      throws Throwable {
    switch (auth.rejectStrategy()) {
      case FAIL_RESPONSE:
        return ResponseUtils_New.errorResponse(remark, responseCode);
      case DOWNGRADE:
        ArexContext.getContext().setPassAuth(false);
        return point.proceed();
      default:
        return point.proceed();
    }
  }


  private void init() {
    authSwitch = Optional.ofNullable(
            systemConfigurationRepository.getSystemConfigByKey(
                SystemConfigurationCollection.KeySummary.AUTH_SWITCH))
        .map(SystemConfiguration::getAuthSwitch)
        .orElse(null);
    if (authSwitch == null) {
      throw new ArexException(ArexApiResponseCode.AUTHENTICATION_FAILED,
          "get authSwitch failed, please update storage version");
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OwnerExistResult {

    private Boolean exist;
    private String remark;
    private Integer responseCode;
  }


}
