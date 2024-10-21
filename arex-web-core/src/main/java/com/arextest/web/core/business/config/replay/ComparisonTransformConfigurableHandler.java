package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dao.config.SystemConfigurationCollection.KeySummary;
import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.system.SystemConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.SystemConfigurationService;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonTransformConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import java.util.Collections;
import java.util.List;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonTransformConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonTransformConfiguration> {

  @Resource
  FSInterfaceRepository fsInterfaceRepository;
  @Resource
  ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;
  @Resource
  ComparisonTransformConfigurationRepositoryImpl comparisonTransformConfigurationRepository;

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
    removeDetailsExpired(configs, true);
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

    return comparePluginInfoConfig == null || comparePluginInfoConfig.getComparePluginInfo() == null
        ? Collections.emptyList()
        : comparePluginInfoConfig.getComparePluginInfo().getTransMethodList();
  }
}
