package com.arextest.web.core.business.config.application;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.common.Dependency;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Component
@Slf4j
public final class ApplicationOperationConfigurableHandler extends AbstractConfigurableHandler<ApplicationOperationConfiguration> {

    protected ApplicationOperationConfigurableHandler(@Autowired ConfigRepositoryProvider<ApplicationOperationConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Autowired
    ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

    @Autowired
    private AppContractRepository appContractRepository;

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
        List<AppContractDto> appContractDtoList = appContractRepository.queryAppContractListByOpId(Arrays.asList(operationId), null);

        List<Dependency> dependencyList = new ArrayList<>();

        for (AppContractDto appContractDto : appContractDtoList) {
            if (Objects.equals(appContractDto.getContractType(), ContractTypeEnum.DEPENDENCY.getCode())) {
                Dependency dependency = new Dependency();
                dependency.setDependencyId(appContractDto.getId());
                dependency.setDependencyType(appContractDto.getOperationType());
                dependency.setDependencyName(appContractDto.getOperationName());
                dependencyList.add(dependency);
            }
        }
        result.setDependencyList(dependencyList);
        return result;
    }
}
