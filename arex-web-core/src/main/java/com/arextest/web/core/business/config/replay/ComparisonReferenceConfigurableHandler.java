package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonReferenceConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Slf4j
@Component
public class ComparisonReferenceConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonReferenceConfiguration> {

  @Resource
  FSInterfaceRepository fsInterfaceRepository;
  @Resource
  ComparisonListSortConfigurableHandler listSortHandler;
  @Resource
  ListKeyCycleDetectionHandler listKeyCycleDetectionHandler;
  @Resource
  ComparisonReferenceConfigurationRepositoryImpl referenceConfigurationRepository;
  @Resource
  ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;
  @Resource
  AppContractRepository appContractRepository;


  protected ComparisonReferenceConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ComparisonReferenceConfiguration> repositoryProvider,
      @Autowired AppContractRepository appContractRepository) {
    super(repositoryProvider, appContractRepository);
  }

  @Override
  public List<ComparisonReferenceConfiguration> queryByInterfaceId(String interfaceId) {

    // get operationId
    FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
    String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();
    return this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
  }

  @Override
  public boolean update(ComparisonReferenceConfiguration configuration) {
    ComparisonReferenceConfiguration oldConfiguration = repositoryProvider.queryById(
        configuration.getId());
    oldConfiguration.setPkPath(configuration.getPkPath());
    oldConfiguration.setFkPath(configuration.getFkPath());
    listKeyCycleDetectionHandler.judgeWhetherCycle(this, listSortHandler, oldConfiguration);
    return super.update(configuration);
  }

  @Override
  public boolean insertList(List<ComparisonReferenceConfiguration> configurationList) {
    this.addDependencyId(configurationList);
    for (ComparisonReferenceConfiguration configuration : configurationList) {
      listKeyCycleDetectionHandler.judgeWhetherCycle(this, listSortHandler, configuration);
    }
    return super.insertList(configurationList);
  }

  public PageQueryComparisonResponseType pageQueryComparisonConfig(
      PageQueryComparisonRequestType requestType) {
    PageQueryComparisonDto pageQueryComparisonDto = PageQueryComparisonMapper.INSTANCE.dtoFromContract(
        requestType);
    PageQueryComparisonResultDto<ComparisonReferenceConfiguration> queryResult =
        referenceConfigurationRepository.pageQueryComparisonConfig(
            pageQueryComparisonDto);

    // get the information of interface and dependency involved in the configuration
    List<ComparisonReferenceConfiguration> configs = queryResult.getConfigs();
    Map<String, String> operationInfos = getOperationInfos(configs,
        applicationOperationConfigurationRepository);
    Map<String, Dependency> dependencyInfos = getDependencyInfos(configs, appContractRepository);

    PageQueryComparisonResponseType result = new PageQueryComparisonResponseType();
    result.setTotalCount(queryResult.getTotalCount());
    result.setReferenceInfos(contractFromDto(configs, operationInfos, dependencyInfos));
    return result;
  }

  private List<PageQueryComparisonResponseType.ReferenceInfo> contractFromDto(
      List<ComparisonReferenceConfiguration> dto, Map<String, String> operationInfo,
      Map<String, Dependency> dependencyInfo) {
    List<PageQueryComparisonResponseType.ReferenceInfo> result = new ArrayList<>();
    for (ComparisonReferenceConfiguration item : dto) {
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
