package com.arextest.web.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.oas.annotations.EnableOpenApi;

@Slf4j
@EnableOpenApi
// @EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@SpringBootApplication(scanBasePackages = "com.arextest.web")
public class WebSpringBootServletInitializer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebSpringBootServletInitializer.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSpringBootServletInitializer.class);
    }
}
