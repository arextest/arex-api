package com.arextest.report.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvProperty {

    public static final String AREX_REPORT_MONGO_URI = "arex.report.mongo.uri";
    public static final String AREX_CONFIG_SERVICE_URL = "arex.config.service.url";
    public static final String AREX_REPORT_EMAIL_HOST = "arex.report.email.host";
    public static final String AREX_REPORT_EMAIL_FROM = "arex.report.email.from";
    public static final String AREX_REPORT_EMAIL_PWD = "arex.report.email.pwd";


    public String getString(String key) {
        return System.getenv(key);
    }
}
