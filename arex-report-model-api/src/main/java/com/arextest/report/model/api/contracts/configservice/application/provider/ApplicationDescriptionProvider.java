package com.arextest.report.model.api.contracts.configservice.application.provider;


import com.arextest.report.model.api.contracts.configservice.application.ApplicationDescription;

/**
 * The basic application info provider,eg:appName,owner,
 * @author jmo
 * @since 2022/1/21
 */
public interface ApplicationDescriptionProvider {
    ApplicationDescription get(String appId);
}
