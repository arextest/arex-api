package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonInclusionsConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonInclusionsConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonInclusionsConfiguration> {

  @Resource
  FSInterfaceRepository fsInterfaceRepository;
  @Resource
  ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;
  @Resource
  ComparisonInclusionsConfigurationRepositoryImpl inclusionsConfigurationRepository;
  @Resource
  ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;
  @Resource
  AppContractRepository appContractRepository;


  protected ComparisonInclusionsConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ComparisonInclusionsConfiguration> repositoryProvider,
      @Autowired AppContractRepository appContractRepository) {
    super(repositoryProvider, appContractRepository);
  }

  @Override
  public List<ComparisonInclusionsConfiguration> queryByInterfaceId(String interfaceId) {

    // get operationId
    FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
    String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();

    List<ComparisonInclusionsConfiguration> result =
        this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
    if (StringUtils.isNotEmpty(operationId)) {
      ApplicationOperationConfiguration applicationOperationConfiguration =
          applicationOperationConfigurableHandler.useResultByOperationId(operationId);
      if (applicationOperationConfiguration != null) {
        List<ComparisonInclusionsConfiguration> globalConfig =
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
    PageQueryComparisonResultDto<ComparisonInclusionsConfiguration> queryResult =
        inclusionsConfigurationRepository.pageQueryComparisonConfig(
            pageQueryComparisonDto);

    // get the information of interface and dependency involved in the configuration
    List<ComparisonInclusionsConfiguration> configs = queryResult.getConfigs();
    Pair<Map<String, String>, Map<String, Dependency>> operationAndDependencyInfos =
        getOperationAndDependencyInfos(configs, applicationOperationConfigurationRepository,
            appContractRepository);
    Map<String, String> operationInfos = operationAndDependencyInfos.getLeft();
    Map<String, Dependency> dependencyInfos = operationAndDependencyInfos.getRight();
    PageQueryComparisonResponseType result = new PageQueryComparisonResponseType();
    result.setTotalCount(queryResult.getTotalCount());
    result.setInclusions(contractFromDto(configs, operationInfos, dependencyInfos));
    return result;
  }

  private List<PageQueryComparisonResponseType.InclusionInfo> contractFromDto(
      List<ComparisonInclusionsConfiguration> dto, Map<String, String> operationInfo,
      Map<String, Dependency> dependencyInfo) {
    List<PageQueryComparisonResponseType.InclusionInfo> result = new ArrayList<>();
    for (ComparisonInclusionsConfiguration item : dto) {
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
