package com.arextest.web.core.business;

import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.config.model.dto.SystemConfiguration;
import com.arextest.config.repository.SystemConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wildeslam.
 * @create 2024/2/23 15:21
 */
@Slf4j
@Service
public class SystemConfigurationService {

    @Resource
    private SystemConfigurationRepository systemConfigurationRepository;

    public boolean saveConfig(SystemConfiguration systemConfiguration) {
        List<SystemConfiguration> systemConfigurations = new ArrayList<>();
        if (systemConfiguration.getCallbackUrl() != null) {
            SystemConfiguration callbackUrl = new SystemConfiguration();
            callbackUrl.setCallbackUrl(systemConfiguration.getCallbackUrl());
            callbackUrl.setKey(SystemConfigurationCollection.KeySummary.CALLBACK_URL);
            systemConfigurations.add(callbackUrl);
        }
        if (systemConfiguration.getDesensitizationJar() != null) {
            SystemConfiguration desensitizationJar = new SystemConfiguration();
            desensitizationJar.setDesensitizationJar(systemConfiguration.getDesensitizationJar());
            desensitizationJar.setKey(SystemConfigurationCollection.KeySummary.DESERIALIZATION_JAR);
            systemConfigurations.add(desensitizationJar);
        }
        boolean flag = true;
        for (SystemConfiguration config : systemConfigurations) {
            try {
                flag &= systemConfigurationRepository.saveConfig(config);
            } catch (Exception e) {
                LOGGER.error("Failed to save system configuration: {}", config, e);
                flag = false;
            }
        }
        return flag;
    }
}
