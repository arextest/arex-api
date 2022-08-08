package com.arextest.report.web.api.service.config;

import com.arextest.report.web.api.service.interceptor.AuthorizationInterceptor;
import com.arextest.report.web.api.service.interceptor.RefreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    RefreshInterceptor refreshInterceptor;

    @Autowired
    AuthorizationInterceptor authorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(refreshInterceptor)
                .addPathPatterns("/api/login/refresh/**");

        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/**")
                // 静态资源
                .excludePathPatterns("/js/**", "/css/**", "/images/**", "/lib/**",
                        "/fonts/**")
                // swagger-ui
                .excludePathPatterns("/swagger-resources/**", "/webjars/**",
                        "/v3/**", "/swagger-ui/**")
                .excludePathPatterns("/api/login/verify")
                .excludePathPatterns("/api/login/getVerificationCode/**")
                .excludePathPatterns("/api/login/refresh/**");
    }
}
