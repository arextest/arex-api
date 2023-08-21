package com.arextest.web.core.business.config.replay;

import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsCategoryConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
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
@Component
public class ComparisonExclusionsCategoryConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonExclusionsCategoryConfiguration> {

    protected ComparisonExclusionsCategoryConfigurableHandler(
        @Autowired ConfigRepositoryProvider<ComparisonExclusionsCategoryConfiguration> repositoryProvider,
        @Autowired AppContractRepository appContractRepository) {
        super(repositoryProvider, appContractRepository);
    }

    private static final Set<String> CATEGORIES = MockCategoryType.DEFAULTS.stream()
        .map(MockCategoryType::getName)
        .collect(java.util.stream.Collectors.toSet());

    @Resource
    FSInterfaceRepository fsInterfaceRepository;

    @Resource
    ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;

    @Resource
    private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

    @Override
    public List<ComparisonExclusionsCategoryConfiguration> queryByInterfaceId(String interfaceId) {
        // get operationId
        FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
        String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();

        List<ComparisonExclusionsCategoryConfiguration> result =
            this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
        if (StringUtils.isNotEmpty(operationId)) {
            ApplicationOperationConfiguration applicationOperationConfiguration =
                applicationOperationConfigurableHandler.useResultByOperationId(operationId);
            List<ComparisonExclusionsCategoryConfiguration> globalConfig =
                this.useResultAsList(applicationOperationConfiguration.getAppId(), null);
            result.addAll(globalConfig);
        }
        return result;
    }

    @Override
    public boolean insert(ComparisonExclusionsCategoryConfiguration comparisonDetail) {
        for (String category : comparisonDetail.getExclusionsCategory()) {
            if (!CATEGORIES.contains(category)) {
                throw new IllegalArgumentException("Invalid category: " + category);
            }
        }
        return super.insert(comparisonDetail);
    }


    @Override
    public List<ComparisonExclusionsCategoryConfiguration> queryComparisonConfig(
        String appId, String operationId, String operationType, String operationName) {
        List<ComparisonExclusionsCategoryConfiguration> result =
            super.queryComparisonConfig(appId, operationId, operationType, operationName);


        List<String> candidateCategories = new ArrayList<>();
        // add candidateCategories
        // for dependency
        if (operationType != null || operationName != null) {
            for (ComparisonExclusionsCategoryConfiguration configuration : result) {
                candidateCategories.add(configuration.getOperationType());
            }
        }
        // for interface
        else if (operationId != null) {
            ApplicationOperationConfiguration applicationOperationConfiguration =
                applicationOperationConfigurationRepository.listByOperationId(operationId);
            candidateCategories.addAll(applicationOperationConfiguration.getOperationTypes());
        }
        // global
        else {
            applicationOperationConfigurationRepository.listBy(appId).forEach(applicationOperationConfiguration ->
                candidateCategories.addAll(applicationOperationConfiguration.getOperationTypes()));
        }

        for (ComparisonExclusionsCategoryConfiguration configuration : result) {
            configuration.setCandidateCategories(candidateCategories);
        }
        return result;
    }
}
