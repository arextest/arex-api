package com.arextest.web.api.service.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.arextest.web.api.service.interceptor.AuthorizationInterceptor;
import com.arextest.web.api.service.interceptor.RefreshInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    RefreshInterceptor refreshInterceptor;

    @Autowired
    AuthorizationInterceptor authorizationInterceptor;

    @Value("${arex.interceptor.patterns}")
    private String interceptorPatterns;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(refreshInterceptor).addPathPatterns("/api/login/refresh/**");

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

        registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**")
            // error
            .excludePathPatterns(defaultPatterns);
    }
}
