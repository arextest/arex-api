package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonScriptConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonResponseType;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import com.arextest.web.model.dto.config.PageQueryComparisonResultDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.mapper.PageQueryComparisonMapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ComparisonScriptConfigurableHandler extends
    AbstractComparisonConfigurableHandler<ComparisonScriptConfiguration> {

  @Resource
  FSInterfaceRepository fsInterfaceRepository;

  @Resource
  ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;

  @Resource
  ComparisonScriptConfigurationRepositoryImpl comparisonScriptConfigurationRepository;

  @Resource
  ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

  @Resource
  AppContractRepository appContractRepository;

  protected ComparisonScriptConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ComparisonScriptConfiguration> repositoryProvider,
      @Autowired AppContractRepository appContractRepository) {
    super(repositoryProvider, appContractRepository);
  }

  @Override
  public List<ComparisonScriptConfiguration> queryByInterfaceId(String interfaceId) {
    // get operationId
    FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
    String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();

    List<ComparisonScriptConfiguration> result =
        this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
    if (StringUtils.isNotEmpty(operationId)) {
      ApplicationOperationConfiguration applicationOperationConfiguration =
          applicationOperationConfigurableHandler.useResultByOperationId(operationId);
      if (applicationOperationConfiguration != null) {
        List<ComparisonScriptConfiguration> globalConfig =
            this.useResultAsList(applicationOperationConfiguration.getAppId(), null);
        result.addAll(globalConfig);
      }
    }
    return result;
  }

  public PageQueryComparisonResponseType pageQueryComparisonConfig(
      PageQueryComparisonRequestType requestType) {
    PageQueryComparisonDto pageQueryComparisonDto = PageQueryComparisonMapper.INSTANCE.dtoFromContract(
        requestType);
    PageQueryComparisonResultDto<ComparisonScriptConfiguration> queryResult =
        comparisonScriptConfigurationRepository.pageQueryComparisonConfig(
            pageQueryComparisonDto);

    // get the information of interface and dependency involved in the configuration
    List<ComparisonScriptConfiguration> configs = queryResult.getConfigs();
    Map<String, String> operationInfos = getOperationInfos(configs,
        applicationOperationConfigurationRepository);
    Map<String, Dependency> dependencyInfos = getDependencyInfos(configs, appContractRepository);

    PageQueryComparisonResponseType result = new PageQueryComparisonResponseType();
    result.setTotalCount(queryResult.getTotalCount());
    result.setScriptInfos(contractFromDto(configs, operationInfos, dependencyInfos));
    return result;
  }

  private List<PageQueryComparisonResponseType.ScriptInfo> contractFromDto(
      List<ComparisonScriptConfiguration> dto, Map<String, String> operationInfo,
      Map<String, Dependency> dependencyInfo) {
    List<PageQueryComparisonResponseType.ScriptInfo> result = new ArrayList<>();
    for (ComparisonScriptConfiguration item : dto) {
      if (item.getOperationId() != null && operationInfo.get(item.getOperationId()) == null) {
        continue;
      }
      if (item.getDependencyId() != null && dependencyInfo.get(item.getDependencyId()) == null) {
        continue;
      }
      result.add(
          PageQueryComparisonMapper.INSTANCE.contractFromDto(item, operationInfo, dependencyInfo));
    }
    return result;
  }
}
