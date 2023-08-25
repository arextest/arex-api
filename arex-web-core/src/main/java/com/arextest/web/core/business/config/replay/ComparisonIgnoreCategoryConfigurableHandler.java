package com.arextest.web.core.business.config.replay;

import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:57
 */
@Component
public class ComparisonIgnoreCategoryConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonIgnoreCategoryConfiguration> {

    protected ComparisonIgnoreCategoryConfigurableHandler(
        @Autowired ConfigRepositoryProvider<ComparisonIgnoreCategoryConfiguration> repositoryProvider,
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

    @Override
    public boolean insert(ComparisonIgnoreCategoryConfiguration comparisonDetail) {
        for (String category : comparisonDetail.getIgnoreCategory()) {
            if (!CATEGORIES.contains(category)) {
                throw new IllegalArgumentException("Invalid category: " + category);
            }
            if (MockCategoryType.create(category).isEntryPoint()) {
                throw new IllegalArgumentException("Cannot ignore entrypoint category: " + category);
            }
        }
        return super.insert(comparisonDetail);
    }
}
