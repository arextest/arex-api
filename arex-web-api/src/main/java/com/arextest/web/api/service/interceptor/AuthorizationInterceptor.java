package com.arextest.web.api.service.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.common.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationInterceptor extends AbstractInterceptorHandler {

  ObjectMapper mapper = new ObjectMapper();

  @Value("${arex.interceptor.patterns}")
  private String interceptorPatterns;

  @Resource
  private JWTService jwtService;

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o)
      throws Exception {
    String authorization = httpServletRequest.getHeader("access-token");
    if (!jwtService.verifyToken(authorization)) {
      httpServletResponse.setStatus(200);
      httpServletResponse.setContentType("application/json");
      httpServletResponse.setCharacterEncoding("UTF-8");
      Response no_permission =
          ResponseUtils.errorResponse("Authentication verification failed",
              ResponseCode.AUTHENTICATION_FAILED);
      httpServletResponse.getWriter().write(mapper.writeValueAsString(no_permission));
      LogUtils.info(LOGGER,
          String.format("access-token invalid; path: %s", httpServletRequest.getServletPath()));
      return false;
    }
    return true;
  }

  @Override
  public Integer getOrder() {
    return 2;
  }

  @Override
  public List<String> getPathPatterns() {
    return new ArrayList<String>() {
      {
        add("/**");
      }
    };
  }

  @Override
  public List<String> getExcludePathPatterns() {
    List<String> defaultPatterns = new ArrayList<>(20);
    // error
    defaultPatterns.add("/error");
    // static resource
    defaultPatterns.add("/js/**");
    defaultPatterns.add("/css/**");
    defaultPatterns.add("/images/**");
    defaultPatterns.add("/lib/**");
    defaultPatterns.add("/fonts/**");
    // swagger-ui
    defaultPatterns.add("/swagger-resources/**");
    defaultPatterns.add("/webjars/**");
    defaultPatterns.add("/v3/**");
    defaultPatterns.add("/swagger-ui/**");
    defaultPatterns.add("/api/login/verify");
    defaultPatterns.add("/api/login/getVerificationCode/**");
    defaultPatterns.add("/api/login/loginAsGuest");
    defaultPatterns.add("/api/login/oauthLogin");
    defaultPatterns.add("/api/login/oauthInfo/**");
    defaultPatterns.add("/api/login/refresh/**");
    // healthCheck
    defaultPatterns.add("/vi/health");
    // called by arex-schedule
    defaultPatterns.add("/api/report/init");
    defaultPatterns.add("/api/report/pushCompareResults");
    defaultPatterns.add("/api/report/pushReplayStatus");
    defaultPatterns.add("/api/report/updateReportInfo");
    defaultPatterns.add("/api/report/analyzeCompareResults");
    defaultPatterns.add("/api/report/removeRecordsAndScenes");
    defaultPatterns.add("/api/report/removeErrorMsg");
    defaultPatterns.add("/api/system/config/list");
    defaultPatterns.add("/api/config/comparison/summary/queryConfigOfCategory");
    defaultPatterns.add("/api/report/queryPlanStatistic");
    defaultPatterns.add("/api/desensitization/listJar");

    // exclude configuration services
    defaultPatterns.add("/api/config/**");
    defaultPatterns.add("/api/report/listCategoryType");
    // exclude logs services
    defaultPatterns.add("/api/logs/**");
    // invite to workspace
    defaultPatterns.add("/api/filesystem/validInvitation");

    // add custom patterns
    if (StringUtils.isNotBlank(interceptorPatterns)) {
      String[] patterns = interceptorPatterns.split(",");
      defaultPatterns.addAll(Arrays.asList(patterns));
    }
    return defaultPatterns;
  }
}