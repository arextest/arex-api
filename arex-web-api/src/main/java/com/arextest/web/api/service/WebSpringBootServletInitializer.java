package com.arextest.web.api.service;

import com.arextest.common.metrics.PrometheusConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@SpringBootApplication(scanBasePackages = "com.arextest.web", exclude = {
    MongoAutoConfiguration.class})
public class WebSpringBootServletInitializer
    extends SpringBootServletInitializer
    implements ApplicationListener<ApplicationReadyEvent> {

  @Value("${arex.prometheus.port}")
  String prometheusPort;

  public static void main(String[] args) {
    SpringApplication.run(WebSpringBootServletInitializer.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(WebSpringBootServletInitializer.class);
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    PrometheusConfiguration.initMetrics(prometheusPort);
  }
}
