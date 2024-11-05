package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dao.config.SystemConfigurationCollection.KeySummary;
import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.model.dto.system.SystemConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.core.business.SystemConfigurationService;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonTransformConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonResponseType;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonResponseType.RootTransformInfo;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import com.arextest.web.model.dto.config.PageQueryComparisonResultDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.mapper.PageQueryComparisonMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.annotation.Resource;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

  @Resource
  ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;
  @Resource
  AppContractRepository appContractRepository;

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

  public PageQueryComparisonResponseType pageQueryComparisonConfig(
      PageQueryComparisonRequestType requestType) {
    PageQueryComparisonDto pageQueryComparisonDto = PageQueryComparisonMapper.INSTANCE.dtoFromContract(
        requestType);
    PageQueryComparisonResultDto<ComparisonTransformConfiguration> queryResult =
        comparisonTransformConfigurationRepository.pageQueryComparisonConfig(
            pageQueryComparisonDto);

    // get the information of interface and dependency involved in the configuration
    List<ComparisonTransformConfiguration> configs = queryResult.getConfigs();
    Pair<Map<String, String>, Map<String, Dependency>> operationAndDependencyInfos =
        getOperationAndDependencyInfos(configs, applicationOperationConfigurationRepository,
            appContractRepository);
    Map<String, String> operationInfos = operationAndDependencyInfos.getLeft();
    Map<String, Dependency> dependencyInfos = operationAndDependencyInfos.getRight();
    PageQueryComparisonResponseType result = new PageQueryComparisonResponseType();
    result.setTotalCount(queryResult.getTotalCount());
    result.setRootTransformInfos(contractFromDto(configs, operationInfos, dependencyInfos));
    return result;
  }

  private List<PageQueryComparisonResponseType.RootTransformInfo> contractFromDto(
      List<ComparisonTransformConfiguration> dto, Map<String, String> operationInfo,
      Map<String, Dependency> dependencyInfo) {
    List<PageQueryComparisonResponseType.RootTransformInfo> result = new ArrayList<>();
    for (ComparisonTransformConfiguration item : dto) {
      if (item.getOperationId() != null && operationInfo.get(item.getOperationId()) == null) {
        continue;
      }
      if (item.getDependencyId() != null && dependencyInfo.get(item.getDependencyId()) == null) {
        continue;
      }
      result.add(
          PageQueryComparisonMapper.INSTANCE.contractFromDto(item, operationInfo, dependencyInfo)
      );
    }
    return result;
  }
}
