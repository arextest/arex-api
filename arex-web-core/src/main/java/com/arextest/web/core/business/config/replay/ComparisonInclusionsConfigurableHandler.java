package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonInclusionsConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonInclusionsConfiguration> {
    protected ComparisonInclusionsConfigurableHandler(
        @Autowired ConfigRepositoryProvider<ComparisonInclusionsConfiguration> repositoryProvider,
        @Autowired AppContractRepository appContractRepository) {
        super(repositoryProvider, appContractRepository);
    }

    @Resource
    FSInterfaceRepository fsInterfaceRepository;

    @Resource
    ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;

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

}
