package com.arextest.web.core.business.config.application;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Component
@Slf4j
public final class ApplicationOperationConfigurableHandler
    extends AbstractConfigurableHandler<ApplicationOperationConfiguration> {

  @Autowired
  ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;
  @Autowired
  private AppContractRepository appContractRepository;

  protected ApplicationOperationConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ApplicationOperationConfiguration> repositoryProvider) {
    super(repositoryProvider);
  }

  @Override
  public boolean insert(ApplicationOperationConfiguration configuration) {
    if (configuration.getServiceId() == null) {
      return false;
    }
    if (StringUtils.isEmpty(configuration.getOperationName())) {
      return false;
    }
    return super.insert(configuration);
  }

  public ApplicationOperationConfiguration useResultByOperationId(String operationId) {
    ApplicationOperationConfiguration result =
        applicationOperationConfigurationRepository.listByOperationId(operationId);
    List<AppContractDto> appContractDtoList =
        appContractRepository.queryAppContractListByOpIds(Collections.singletonList(operationId),
            null);

    List<Dependency> dependencyList = new ArrayList<>();

    for (AppContractDto appContractDto : appContractDtoList) {
      if (Objects.equals(appContractDto.getContractType(), ContractTypeEnum.DEPENDENCY.getCode())) {
        Dependency dependency = new Dependency();
        dependency.setDependencyId(appContractDto.getId());
        dependency.setOperationType(appContractDto.getOperationType());
        dependency.setOperationName(appContractDto.getOperationName());
        dependencyList.add(dependency);
      }
    }
    result.setDependencyList(dependencyList);
    return result;
  }
}
