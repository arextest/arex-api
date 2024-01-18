package com.arextest.web.core.business.config.application;

import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Slf4j
@Component
public final class ApplicationServiceConfigurableHandler
    extends AbstractConfigurableHandler<ApplicationServiceConfiguration> {

  @Autowired
  private AppContractRepository appContractRepository;

  protected ApplicationServiceConfigurableHandler(
      @Autowired ConfigRepositoryProvider<ApplicationServiceConfiguration> repositoryProvider) {
    super(repositoryProvider);
  }

  @Override
  public boolean insert(ApplicationServiceConfiguration configuration) {
    // note: this method is not supported in arex-api, it is in storage-api
    throw new UnsupportedOperationException("insert is not supported");
  }

  @Override
  public List<ApplicationServiceConfiguration> useResultAsList(String appId) {
    List<ApplicationServiceConfiguration> result = super.useResultAsList(appId);
    if (result.isEmpty()) {
      return result;
    }
    ApplicationServiceConfiguration applicationServiceConfiguration = result.get(0);

    if (CollectionUtils.isEmpty(applicationServiceConfiguration.getOperationList())) {
      return result;
    }

    List<AppContractDto> appContractDtoList = appContractRepository.queryAppContracts(appId);
    Map<String, List<AppContractDto>> appContractMap = appContractDtoList.stream()
        .filter(appContractDto -> appContractDto.getOperationId() != null && appContractDto.getOperationType() != null)
        .collect(Collectors.groupingBy(AppContractDto::getOperationId));

    applicationServiceConfiguration.getOperationList().forEach(operation -> {
      List<AppContractDto> appContracts = appContractMap.get(operation.getId());
      if (CollectionUtils.isNotEmpty(appContracts)) {
        operation.setDependencyList(appContracts.stream().map(appContractDto -> {
          Dependency dependency = new Dependency();
          dependency.setDependencyId(appContractDto.getId());
          dependency.setOperationType(appContractDto.getOperationType());
          dependency.setOperationName(appContractDto.getOperationName());
          return dependency;
        }).collect(Collectors.toList()));
      }
    });


    return result;
  }
}
