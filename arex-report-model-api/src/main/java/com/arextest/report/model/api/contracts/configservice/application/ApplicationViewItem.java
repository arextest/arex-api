package com.arextest.report.model.api.contracts.configservice.application;

import com.arextest.report.model.api.contracts.common.enums.FeatureType;
import lombok.Data;

import java.util.Set;

/**
 * @author jmo
 * @since 2021/12/22
 */
@Data
public class ApplicationViewItem {
    private String appId;
    private int sampleRate;
    /**
     * status -1 means suspend
     */
    private int status;
    private String groupName;
    /**
     * agent版本
     */
    private String agentVersion;
    private String appName;
    private String description;
    /**
     * Web UI, Web api,Job,etc.
     */
    private String category;
    private String owner;
    /**
     * flight,hotel,etc.
     */
    private String departmentName;
    /**
     * Bit flag composed of bits that indicate which
     * {@link FeatureType}s
     * are enabled.
     */
    private int features;

    /**
     * 默认回放Case范围
     */
    private Integer replayCaseRange;

    /**
     * 默认回放环境
     */
    private String replayEnv;
    private String appImportance;
    private long countOfRecorded;
    private Set<String> excludeDependencyOperationSet;
    private Set<String> excludeDependencyService;
    /**
     * excludeOperations
     */
    private Set<String> excludeOperationSet;
    /**
     * format
     */
    private String format;
    /**
     * java_tomcat or java_app nodejs etc.
     */
    private String appContainer;
    private String groupId;
    private String organizationId;
}
