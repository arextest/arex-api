package com.arextest.web.core.business.config.replay;

import com.arextest.common.model.classloader.RemoteJarClassLoader;
import com.arextest.common.utils.RemoteJarLoaderUtils;
import com.arextest.config.model.dao.config.SystemConfigurationCollection.KeySummary;
import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.system.ComparePluginInfo;
import com.arextest.config.model.dto.system.SystemConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.diff.service.DecompressService;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.SystemConfigurationService;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonTransformConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Slf4j
@Component
public class ComparisonTransformConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonTransformConfiguration> {

  @Resource
  FSInterfaceRepository fsInterfaceRepository;
  @Resource
  ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;
  @Resource
  ComparisonTransformConfigurationRepositoryImpl comparisonTransformConfigurationRepository;

  @Lazy
  @Resource
  SystemConfigurationService systemConfigurationService;

  protected ComparisonTransformConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ComparisonTransformConfiguration> repositoryProvider,
      @Autowired AppContractRepository appContractRepository) {
    super(repositoryProvider, appContractRepository);
  }

  public List<ComparisonTransformConfiguration> queryConfigOfCategory(String appId,
      String operationId, List<String> dependencyIds) {
    List<ComparisonTransformConfiguration> configs = comparisonTransformConfigurationRepository.queryConfigOfCategory(
        appId, operationId, dependencyIds);
    configs.removeIf(this::removeDetailsExpired);
    return configs;
  }

  @Override
  public List<ComparisonTransformConfiguration> queryByInterfaceId(String interfaceId) {

    // get operationId
    FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
    String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();

    List<ComparisonTransformConfiguration> result =
        this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
    if (StringUtils.isNotEmpty(operationId)) {
      ApplicationOperationConfiguration applicationOperationConfiguration =
          applicationOperationConfigurableHandler.useResultByOperationId(operationId);
      if (applicationOperationConfiguration != null) {
        List<ComparisonTransformConfiguration> globalConfig =
            this.useResultAsList(applicationOperationConfiguration.getAppId(), null);
        result.addAll(globalConfig);
      }
    }
    return result;
  }

  public List<String> getTransformMethodList() {
    SystemConfiguration comparePluginInfoConfig = systemConfigurationService.getSystemConfigByKey(
        KeySummary.COMPARE_PLUGIN_INFO);
    return getTransformMethodList(comparePluginInfoConfig);
  }

  public List<String> getTransformMethodList(SystemConfiguration comparePluginSystemConfig) {
    if (comparePluginSystemConfig == null
        || comparePluginSystemConfig.getComparePluginInfo() == null) {
      return Collections.emptyList();
    }

    ComparePluginInfo comparePluginInfo = comparePluginSystemConfig.getComparePluginInfo();
    String comparePluginUrl = comparePluginInfo.getComparePluginUrl();
    List<String> transMethodList = comparePluginInfo.getTransMethodList();

    if (StringUtils.isNotEmpty(comparePluginUrl) && transMethodList != null) {
      return transMethodList;
    }

    // update transformMethod in SystemConfiguration
    List<String> transformMethod = identifyTransformMethod(comparePluginInfo);
    comparePluginInfo.setTransMethodList(transformMethod);
    systemConfigurationService.saveConfig(comparePluginSystemConfig);
    return transformMethod;
  }

  private List<String> identifyTransformMethod(ComparePluginInfo comparePluginInfo) {
    String comparePluginUrl = comparePluginInfo.getComparePluginUrl();
    if (StringUtils.isEmpty(comparePluginUrl)) {
      return Collections.emptyList();
    }

    LogUtils.info(LOGGER, ImmutableMap.of("title", "identifyTransformMethod"),
        "identifyTransformMethod, url:{}", comparePluginUrl);
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
      LogUtils.error(LOGGER, ImmutableMap.of("title", "identifyTransformMethod"),
          "identifyTransformMethod failed, url:{}, exception:{}", comparePluginUrl, e);
    }
    return new ArrayList<>(result);
  }


}
