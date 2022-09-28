package com.arextest.report.model.api.contracts.configservice.application;


import com.arextest.report.model.api.contracts.common.enums.FeatureType;
import com.arextest.report.model.api.contracts.configservice.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Getter
@Setter
public class ApplicationConfiguration extends AbstractConfiguration implements ApplicationDescription {
    private String id;
    private String appId;
    /**
     * Bit flag composed of bits that indicate which {@link FeatureType}s are enabled.
     */
    private int features;
    private String groupName;
    private String groupId;
    private String agentVersion;
    private String agentExtVersion;
    private String appName;
    private String description;

    /**
     * java_web_service
     * nodeJs_Web_service
     */
    private String category;
    private String owner;
    private String organizationName;
    private Integer recordedCaseCount;
    private String defaultFormatter;

    /**
     * organization_id
     */
    private String organizationId;
}
