package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.model.contract.contracts.compare.CategoryDetail;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:57
 */
@Slf4j
@Component
public class ComparisonIgnoreCategoryConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonIgnoreCategoryConfiguration> {

  private static final Set<String> CATEGORIES =
      MockCategoryType.DEFAULTS.stream().map(MockCategoryType::getName)
          .collect(java.util.stream.Collectors.toSet());
  @Resource
  FSInterfaceRepository fsInterfaceRepository;
  @Resource
  ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;
  @Resource
  private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

  protected ComparisonIgnoreCategoryConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ComparisonIgnoreCategoryConfiguration> repositoryProvider,
      @Autowired AppContractRepository appContractRepository) {
    super(repositoryProvider, appContractRepository);
  }

  @Override
  public List<ComparisonIgnoreCategoryConfiguration> queryByInterfaceId(String interfaceId) {
    // get operationId
    FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
    String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();

    List<ComparisonIgnoreCategoryConfiguration> result =
        this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
    if (StringUtils.isNotEmpty(operationId)) {
      ApplicationOperationConfiguration applicationOperationConfiguration =
          applicationOperationConfigurableHandler.useResultByOperationId(operationId);
      List<ComparisonIgnoreCategoryConfiguration> globalConfig =
          this.useResultAsList(applicationOperationConfiguration.getAppId(), null);
      result.addAll(globalConfig);
    }
    return result;
  }

  private void checkBeforeModify(ComparisonIgnoreCategoryConfiguration configuration) {
    CategoryDetail category = configuration.getIgnoreCategory();
    if (category == null) {
      return;
    }
    if (!CATEGORIES.contains(category.getOperationType())) {
      throw new IllegalArgumentException("Invalid category: " + category);
    }
    if (MockCategoryType.create(category.getOperationType()).isEntryPoint()) {
      throw new IllegalArgumentException("Cannot ignore entrypoint category: " + category);
    }

  }

  @Override
  public boolean insert(ComparisonIgnoreCategoryConfiguration configuration) {
    checkBeforeModify(configuration);
    return super.insert(configuration);
  }

  @Override
  public boolean remove(ComparisonIgnoreCategoryConfiguration configuration) {
    checkBeforeModify(configuration);
    return super.remove(configuration);
  }

  @Override
  public boolean update(ComparisonIgnoreCategoryConfiguration configuration) {
    checkBeforeModify(configuration);
    return super.update(configuration);
  }
}
