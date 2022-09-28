package com.arextest.report.model.api.contracts.configservice.application.provider;

import com.arextest.report.model.api.contracts.configservice.application.ServiceDescription;

import java.util.List;

/**
 * @author jmo
 * @since 2022/1/21
 */
public interface ApplicationServiceDescriptionProvider {
    List<? extends ServiceDescription> get(String appId, String host);
}
