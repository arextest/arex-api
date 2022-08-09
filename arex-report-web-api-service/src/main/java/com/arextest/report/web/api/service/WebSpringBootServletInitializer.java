package com.arextest.report.web.api.service;

import com.arextest.report.common.EnvProperty;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Slf4j
@EnableOpenApi
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@SpringBootApplication(scanBasePackages = "com.arextest.report")
public class WebSpringBootServletInitializer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(WebSpringBootServletInitializer.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        if (!checkEnv()) {
            throw new RuntimeException("Environments error");
        }
        return application.sources(WebSpringBootServletInitializer.class);
    }

    private boolean checkEnv() {
        Field[] fields = EnvProperty.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() != String.class) {
                continue;
            }
            if (!Modifier.isPublic(field.getModifiers())) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            try {
                String val = (String) field.get(EnvProperty.class);
                if (StringUtils.isEmpty(System.getenv(val))) {
                    LOGGER.error(String.format("Environment [%s] is empty", val));
                    return false;
                }
                LOGGER.info(String.format("Environment [%s]:%s", val, System.getenv(val)));
            } catch (IllegalAccessException e) {
                LOGGER.error("Failed to get environment variable", e);
            }
        }
        return true;
    }
}
