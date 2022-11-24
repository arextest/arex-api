package com.arextest.web.model.contract.contracts.config.application.provider;


import com.arextest.web.model.contract.contracts.config.application.ApplicationDescription;

/**
 * The basic application info provider,eg:appName,owner,
 * @author jmo
 * @since 2022/1/21
 */
public interface ApplicationDescriptionProvider {
    ApplicationDescription get(String appId);
}
