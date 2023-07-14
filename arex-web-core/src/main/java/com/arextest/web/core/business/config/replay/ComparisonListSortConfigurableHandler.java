package com.arextest.web.core.business.config.replay;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonListSortConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonListSortConfiguration> {
    protected ComparisonListSortConfigurableHandler(
        @Autowired ConfigRepositoryProvider<ComparisonListSortConfiguration> repositoryProvider,
        @Autowired AppContractRepository appContractRepository) {
        super(repositoryProvider, appContractRepository);
    }

    @Resource
    FSInterfaceRepository fsInterfaceRepository;

    @Resource
    ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;

    @Override
    public List<ComparisonListSortConfiguration> queryByInterfaceId(String interfaceId) {

        // get operationId
        FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
        String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();

        List<ComparisonListSortConfiguration> result = this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
        if (StringUtils.isNotEmpty(operationId)) {
            ApplicationOperationConfiguration applicationOperationConfiguration =
                applicationOperationConfigurableHandler.useResultByOperationId(operationId);
            if (applicationOperationConfiguration != null) {
                List<ComparisonListSortConfiguration> globalConfig =
                    this.useResultAsList(applicationOperationConfiguration.getAppId(), null);
                result.addAll(globalConfig);
            }
        }
        return result;
    }
}
