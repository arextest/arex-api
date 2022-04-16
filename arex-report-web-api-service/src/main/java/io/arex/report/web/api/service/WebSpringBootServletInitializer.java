package io.arex.report.web.api.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SpringBootApplication(scanBasePackages = "io.arex.report")
public class WebSpringBootServletInitializer extends SpringBootServletInitializer {
    
    public static void main(String[] args) {
        SpringApplication.run(WebSpringBootServletInitializer.class, args);
    }

    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSpringBootServletInitializer.class);
    }
}
