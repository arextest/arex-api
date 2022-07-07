package com.arextest.report.core.business.config;

import lombok.Data;


@Data
public class ApplicationProperties {
    private String configServiceUrl;
    private String emailHost;
    private String emailFrom;
    private String emailPwd;
}
