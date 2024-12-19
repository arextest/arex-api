package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ComparisonIgnoreCategoryConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.compare.CategoryDetail;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryCategoryRequestType;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonResponseType;
import com.arextest.web.model.dto.config.PageQueryCategoryDto;
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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:57
 */
@Slf4j
@Component
public class ComparisonIgnoreCategoryConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonIgnoreCategoryConfiguration> {

  @Resource
  FSInterfaceRepository fsInterfaceRepository;
  @Resource
  ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;
  @Resource
  private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;
  @Resource
  ComparisonIgnoreCategoryConfigurationRepositoryImpl ignoreCategoryConfigurationRepository;
  @Resource
  AppContractRepository appContractRepository;

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
    CategoryDetail category = configuration.getIgnoreCategoryDetail();
    if (category == null) {
      return;
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


  public PageQueryComparisonResponseType pageQueryComparisonConfig(
      PageQueryCategoryRequestType requestType) {
    PageQueryCategoryDto pageQueryComparisonDto = PageQueryComparisonMapper.INSTANCE.dtoFromContract(
        requestType);
    queryIdsByKeywords(pageQueryComparisonDto, applicationOperationConfigurationRepository);
    PageQueryComparisonResultDto<ComparisonIgnoreCategoryConfiguration> queryResult =
        ignoreCategoryConfigurationRepository.pageQueryComparisonConfig(
            pageQueryComparisonDto);

    // get the information of interface and dependency involved in the configuration
    List<ComparisonIgnoreCategoryConfiguration> configs = queryResult.getConfigs();
    Map<String, String> operationInfos = getOperationInfos(configs,
        applicationOperationConfigurationRepository);
    Map<String, Dependency> dependencyInfos = getDependencyInfos(configs, appContractRepository);

    PageQueryComparisonResponseType result = new PageQueryComparisonResponseType();
    result.setTotalCount(queryResult.getTotalCount());
    result.setIgnoreCategories(contractFromDto(configs, operationInfos, dependencyInfos));
    return result;
  }

  private List<PageQueryComparisonResponseType.IgnoreCategoryInfo> contractFromDto(
      List<ComparisonIgnoreCategoryConfiguration> dto, Map<String, String> operationInfo,
      Map<String, Dependency> dependencyInfo) {
    List<PageQueryComparisonResponseType.IgnoreCategoryInfo> result = new ArrayList<>();
    for (ComparisonIgnoreCategoryConfiguration item : dto) {
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
