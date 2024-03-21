package com.arextest.web.api.service.interceptor;

import com.arextest.common.interceptor.AbstractInterceptorHandler;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class InterceptorConfiguration implements WebMvcConfigurer {

  @Resource
  List<AbstractInterceptorHandler> interceptors;


  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    if (CollectionUtils.isEmpty(interceptors)) {
      return;
    }
    interceptors.sort(Comparator.comparing(AbstractInterceptorHandler::getOrder));
    interceptors.forEach(interceptor -> {
      registry.addInterceptor(interceptor)
          .addPathPatterns(interceptor.getPathPatterns())
          .excludePathPatterns(interceptor.getExcludePathPatterns());
    });
  }
}
