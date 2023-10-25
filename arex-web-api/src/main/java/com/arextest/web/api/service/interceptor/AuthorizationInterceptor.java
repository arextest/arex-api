package com.arextest.web.api.service.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.JwtUtil;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.common.LogUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
        throws Exception {
        String authorization = httpServletRequest.getHeader("access-token");
        if (!JwtUtil.verifyToken(authorization)) {
            httpServletResponse.setStatus(200);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            Response no_permission =
                ResponseUtils.errorResponse("Authentication verification failed", ResponseCode.AUTHENTICATION_FAILED);
            httpServletResponse.getWriter().write(mapper.writeValueAsString(no_permission));
            LogUtils.info(LOGGER, String.format("access-token invalid; path: %s", httpServletRequest.getServletPath()));
            return false;
        }
        return true;
    }

}