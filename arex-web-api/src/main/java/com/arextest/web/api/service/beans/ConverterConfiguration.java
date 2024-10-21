package com.arextest.web.api.service.beans;

import com.arextest.web.api.service.converter.ZstdJacksonMessageConverter;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfiguration {

  @Resource
  private ZstdJacksonMessageConverter zstdJacksonMessageConverter;

  @Bean
  public HttpMessageConverters customConverters() {
    return new HttpMessageConverters(zstdJacksonMessageConverter);
  }
}
