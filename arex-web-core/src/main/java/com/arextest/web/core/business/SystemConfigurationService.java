package com.arextest.web.core.business;

import com.arextest.common.model.classloader.RemoteJarClassLoader;
import com.arextest.common.utils.RemoteJarLoaderUtils;
import com.arextest.config.model.dao.config.SystemConfigurationCollection;
import com.arextest.config.model.dto.system.ComparePluginInfo;
import com.arextest.config.model.dto.system.SystemConfiguration;
import com.arextest.config.repository.SystemConfigurationRepository;
import com.arextest.diff.service.DecompressService;
import com.arextest.web.core.business.config.replay.ComparisonScriptContentHandler;
import com.arextest.web.model.contract.contracts.config.SystemConfigWithProperties;
import com.arextest.web.model.contract.contracts.config.SystemConfigWithProperties.ScriptContentInfo;
import com.arextest.web.model.dto.config.ComparisonScriptContent;
import com.arextest.web.model.mapper.ScriptInfoMapper;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/2/23 15:21
 */
@Slf4j
@Service
public class SystemConfigurationService {

  @Resource
  private SystemConfigurationRepository systemConfigurationRepository;

  @Resource
  private ConfigLoadService configLoadService;

  @Resource
  private ComparisonScriptContentHandler comparisonScriptContentHandler;

  @Value("${arex.compare.scriptTemplate}")
  private String compareTemplate;

  public boolean saveConfig(SystemConfiguration systemConfiguration) {
    List<SystemConfiguration> systemConfigurations = new ArrayList<>();
    List<String> removeKeys = new ArrayList<>();
    if (StringUtils.isNotBlank(systemConfiguration.getCallbackUrl())) {
      SystemConfiguration callbackUrl = new SystemConfiguration();
      callbackUrl.setCallbackUrl(systemConfiguration.getCallbackUrl());
      callbackUrl.setKey(SystemConfigurationCollection.KeySummary.CALLBACK_URL);
      systemConfigurations.add(callbackUrl);
    } else {
      removeKeys.add(SystemConfigurationCollection.KeySummary.CALLBACK_URL);
    }
    if (systemConfiguration.getDesensitizationJar() != null
        && StringUtils.isNotBlank(systemConfiguration.getDesensitizationJar().getJarUrl())) {
      SystemConfiguration desensitizationJar = new SystemConfiguration();
      desensitizationJar.setDesensitizationJar(systemConfiguration.getDesensitizationJar());
      desensitizationJar.setKey(SystemConfigurationCollection.KeySummary.DESERIALIZATION_JAR);
      systemConfigurations.add(desensitizationJar);
    } else {
      removeKeys.add(SystemConfigurationCollection.KeySummary.DESERIALIZATION_JAR);
    }
    if (systemConfiguration.getComparePluginInfo() != null
        && StringUtils.isNotBlank(
        systemConfiguration.getComparePluginInfo().getComparePluginUrl())) {
      SystemConfiguration comparePluginInfoConfig = new SystemConfiguration();
      ComparePluginInfo comparePluginInfo = systemConfiguration.getComparePluginInfo();
      comparePluginInfo.setTransMethodList(identifyTransformMethod(comparePluginInfo));
      comparePluginInfoConfig.setComparePluginInfo(comparePluginInfo);
      comparePluginInfoConfig.setKey(SystemConfigurationCollection.KeySummary.COMPARE_PLUGIN_INFO);
      systemConfigurations.add(comparePluginInfoConfig);
    } else {
      removeKeys.add(SystemConfigurationCollection.KeySummary.COMPARE_PLUGIN_INFO);
    }
    boolean flag = true;
    for (SystemConfiguration config : systemConfigurations) {
      try {
        systemConfigurationRepository.saveConfig(config);
      } catch (Exception e) {
        LOGGER.error("Failed to save system configuration: {}", config, e);
        flag = false;
      }
    }
    for (String key : removeKeys) {
      try {
        systemConfigurationRepository.deleteConfig(key);
      } catch (Exception e) {
        LOGGER.error("Failed to delete system configuration: {}", key, e);
        flag = false;
      }
    }
    return flag;
  }


  public SystemConfiguration getSystemConfigByKey(String key) {
    return systemConfigurationRepository.getSystemConfigByKey(key);
  }

  public SystemConfigWithProperties listSystemConfig() {
    List<SystemConfiguration> systemConfigurations = systemConfigurationRepository.getAllSystemConfigList();
    SystemConfiguration systemConfiguration = SystemConfiguration.mergeConfigs(
        systemConfigurations);
    return this.appendGlobalCompareConfig(
        configLoadService,
        systemConfiguration);
  }

  public boolean deleteSystemConfigByKey(String key) {
    return systemConfigurationRepository.deleteConfig(key);
  }

  private List<String> identifyTransformMethod(ComparePluginInfo comparePluginInfo) {
    String comparePluginUrl = comparePluginInfo.getComparePluginUrl();
    if (StringUtils.isEmpty(comparePluginUrl)) {
      return Collections.emptyList();
    }

    Set<String> result = new HashSet<>();
    try {
      RemoteJarClassLoader remoteJarClassLoader = RemoteJarLoaderUtils.loadJar(comparePluginUrl);
      List<DecompressService> decompressServices = RemoteJarLoaderUtils.loadService(
          DecompressService.class, remoteJarClassLoader);
      decompressServices.forEach(
          decompressService -> {
            if (decompressService.getAliasName() != null) {
              result.add(decompressService.getAliasName());
            }
          });
      remoteJarClassLoader.close();
    } catch (RuntimeException | IOException e) {
      LOGGER.error("identifyTransformMethod failed, url:{}, exception:{}", comparePluginUrl, e);
    }
    return new ArrayList<>(result);
  }

  private SystemConfigWithProperties appendGlobalCompareConfig(ConfigLoadService configLoadService,
      SystemConfiguration latestSystemConfig) {

    SystemConfigWithProperties systemConfigWithProperties = new SystemConfigWithProperties();
    BeanUtils.copyProperties(latestSystemConfig, systemConfigWithProperties);
    try {
      systemConfigWithProperties.setCompareNameToLower(
          Boolean.valueOf(configLoadService.getCompareNameToLower("true")));
      systemConfigWithProperties.setCompareNullEqualsEmpty(
          Boolean.valueOf(configLoadService.getCompareNullEqualsEmpty("true")));
      Long ignoredTimePrecisionMillis = Long.valueOf(
          configLoadService.getCompareIgnoredTimePrecisionMillis("2000"));
      systemConfigWithProperties.setCompareIgnoreTimePrecisionMillis(ignoredTimePrecisionMillis);
      systemConfigWithProperties.setIgnoreNodeSet(configLoadService.getIgnoreNodeSet(""));
      systemConfigWithProperties.setSelectIgnoreCompare(
          Boolean.valueOf(configLoadService.getCompareSelectIgnoreCompare("true")));
      systemConfigWithProperties.setOnlyCompareCoincidentColumn(
          Boolean.valueOf(configLoadService.getCompareOnlyCompareCoincidentColumn("true")));
      systemConfigWithProperties.setUuidIgnore(
          Boolean.valueOf(configLoadService.getCompareUuidIgnore("true")));
      systemConfigWithProperties.setIpIgnore(
          Boolean.valueOf(configLoadService.getCompareIpIgnore("true")));
    } catch (RuntimeException e) {
      LOGGER.error("getCompareIgnoredTimePrecisionMillis error", e);
    }

    List<ComparisonScriptContent> comparisonScriptContents = comparisonScriptContentHandler.queryAll();
    if (CollectionUtils.isNotEmpty(comparisonScriptContents)) {
      List<ScriptContentInfo> scriptContentInfos = new ArrayList<>();
      for (ComparisonScriptContent item : comparisonScriptContents) {
        ScriptContentInfo scriptContentInfo = new ScriptContentInfo();
        String functionId = item.getId();
        scriptContentInfo.setFunctionName(
            ScriptInfoMapper.INSTANCE.functionIdToFunctionName(functionId)
        );

        String scriptContent = item.getContent();
        scriptContentInfo.setScriptContent(
            String.format(compareTemplate, scriptContentInfo.getFunctionName(), scriptContent)
        );
        scriptContentInfos.add(scriptContentInfo);
      }
      systemConfigWithProperties.setScriptContentInfos(scriptContentInfos);
    }

    return systemConfigWithProperties;
  }
}
