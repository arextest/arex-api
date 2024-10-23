package com.arextest.web.core.business.config.replay;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import java.util.List;
import jakarta.annotation.Resource;
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
}
