package com.arextest.report.web.api.service;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@SpringBootApplication(scanBasePackages = "com.arextest.report")
public class WebSpringBootServletInitializer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebSpringBootServletInitializer.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSpringBootServletInitializer.class);
    }
}
