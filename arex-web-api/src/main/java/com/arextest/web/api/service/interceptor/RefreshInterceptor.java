package com.arextest.web.api.service.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.common.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2022/8/4.
 */
@Slf4j
@Component
public class RefreshInterceptor extends AbstractInterceptorHandler {

  ObjectMapper mapper = new ObjectMapper();

  @Resource
  private JWTService jwtService;

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o)
      throws Exception {
    String authorization = httpServletRequest.getHeader("refresh-token");
    if (!jwtService.verifyToken(authorization)) {
      httpServletResponse.setStatus(200);
      httpServletResponse.setContentType("application/json");
      httpServletResponse.setCharacterEncoding("UTF-8");
      Response no_permission =
          ResponseUtils.errorResponse("Authentication verification failed",
              ResponseCode.AUTHENTICATION_FAILED);
      httpServletResponse.getWriter().write(mapper.writeValueAsString(no_permission));
      LogUtils.info(LOGGER,
          String.format("refresh-token invalid; path: %s", httpServletRequest.getServletPath()));
      return false;
    }
    return true;
  }

  @Override
  public Integer getOrder() {
    return 1;
  }

  @Override
  public List<String> getPathPatterns() {
    return Collections.singletonList("/api/login/refreshToken");
  }

  @Override
  public List<String> getExcludePathPatterns() {
    return Collections.emptyList();
  }
}