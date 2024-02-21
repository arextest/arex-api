package com.arextest.web.model.contract.contracts.config;

import com.arextest.web.model.contract.contracts.common.DesensitizationJar;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author wildeslam.
 * @create 2024/2/21 15:13
 */
@Data
public class SystemConfiguration {
    /**
     * The problem of prohibiting concurrent repeated insertions, the key is unique the function of
     * this record
     */
    private String key;
    private Map<String, Integer> refreshTaskMark;
    private DesensitizationJar desensitizationJar;
    private String callbackUrl;

    public static SystemConfiguration mergeConfigs(List<SystemConfiguration> systemConfigurations) {
        if (CollectionUtils.isEmpty(systemConfigurations)) {
            return new SystemConfiguration();
        }
        SystemConfiguration result = new SystemConfiguration();
        for (SystemConfiguration systemConfiguration : systemConfigurations) {
            if (systemConfiguration == null) {
                continue;
            }
            if (systemConfiguration.getRefreshTaskMark() != null) {
                result.setRefreshTaskMark(systemConfiguration.getRefreshTaskMark());
            }
            if (systemConfiguration.getDesensitizationJar() != null) {
                result.setDesensitizationJar(systemConfiguration.getDesensitizationJar());
            }
            if (systemConfiguration.getCallbackUrl() != null) {
                result.setCallbackUrl(systemConfiguration.getCallbackUrl());
            }
        }
        return result;
    }
}
