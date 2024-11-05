package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonListSortConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonListSortConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonListSortConfiguration> {

  @Resource
  FSInterfaceRepository fsInterfaceRepository;
  @Lazy
  @Resource
  ComparisonReferenceConfigurableHandler referenceHandler;
  @Resource
  ListKeyCycleDetectionHandler listKeyCycleDetectionHandler;
  @Resource
  ComparisonListSortConfigurationRepositoryImpl listSortConfigurationRepository;
  @Resource
  ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;
  @Resource
  AppContractRepository appContractRepository;

  protected ComparisonListSortConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ComparisonListSortConfiguration> repositoryProvider,
      @Autowired AppContractRepository appContractRepository) {
    super(repositoryProvider, appContractRepository);
  }

  @Override
  public List<ComparisonListSortConfiguration> queryByInterfaceId(String interfaceId) {

    // get operationId
    FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
    String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();
    return this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
  }

  @Override
  public boolean update(ComparisonListSortConfiguration configuration) {
    ComparisonListSortConfiguration oldConfiguration = repositoryProvider.queryById(
        configuration.getId());
    oldConfiguration.setListPath(configuration.getListPath());
    oldConfiguration.setKeys(configuration.getKeys());
    listKeyCycleDetectionHandler.judgeWhetherCycle(referenceHandler, this, oldConfiguration);
    return super.update(configuration);
  }

  @Override
  public boolean insertList(List<ComparisonListSortConfiguration> configurationList) {
    this.addDependencyId(configurationList);
    for (ComparisonListSortConfiguration configuration : configurationList) {
      listKeyCycleDetectionHandler.judgeWhetherCycle(referenceHandler, this, configuration);
    }
    return super.insertList(configurationList);
  }

  public PageQueryComparisonResponseType pageQueryComparisonConfig(
      PageQueryComparisonRequestType requestType) {
    PageQueryComparisonDto pageQueryComparisonDto = PageQueryComparisonMapper.INSTANCE.dtoFromContract(
        requestType);
    PageQueryComparisonResultDto<ComparisonListSortConfiguration> queryResult =
        listSortConfigurationRepository.pageQueryComparisonConfig(
            pageQueryComparisonDto);

    // get the information of interface and dependency involved in the configuration
    List<ComparisonListSortConfiguration> configs = queryResult.getConfigs();
    Pair<Map<String, String>, Map<String, Dependency>> operationAndDependencyInfos =
        getOperationAndDependencyInfos(configs, applicationOperationConfigurationRepository,
            appContractRepository);
    Map<String, String> operationInfos = operationAndDependencyInfos.getLeft();
    Map<String, Dependency> dependencyInfos = operationAndDependencyInfos.getRight();
    PageQueryComparisonResponseType result = new PageQueryComparisonResponseType();
    result.setTotalCount(queryResult.getTotalCount());
    result.setListSorts(contractFromDto(configs, operationInfos, dependencyInfos));
    return result;
  }

  private List<PageQueryComparisonResponseType.ListSortInfo> contractFromDto(
      List<ComparisonListSortConfiguration> dto, Map<String, String> operationInfo,
      Map<String, Dependency> dependencyInfo) {
    List<PageQueryComparisonResponseType.ListSortInfo> result = new ArrayList<>();
    for (ComparisonListSortConfiguration item : dto) {
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
