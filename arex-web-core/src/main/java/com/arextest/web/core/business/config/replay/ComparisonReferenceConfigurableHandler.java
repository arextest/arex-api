package com.arextest.web.core.business.config.replay;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Slf4j
@Component
public class ComparisonReferenceConfigurableHandler
    extends AbstractComparisonConfigurableHandler<ComparisonReferenceConfiguration> {
    @Resource
    FSInterfaceRepository fsInterfaceRepository;
    @Resource
    ComparisonListSortConfigurableHandler listSortHandler;
    @Resource
    ListKeyCycleDetectionHandler listKeyCycleDetectionHandler;

    protected ComparisonReferenceConfigurableHandler(
        @Autowired ConfigRepositoryProvider<ComparisonReferenceConfiguration> repositoryProvider,
        @Autowired AppContractRepository appContractRepository) {
        super(repositoryProvider, appContractRepository);
    }

    @Override
    public List<ComparisonReferenceConfiguration> queryByInterfaceId(String interfaceId) {

        // get operationId
        FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
        String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();
        return this.queryByOperationIdAndInterfaceId(interfaceId, operationId);
    }

    @Override
    public boolean update(ComparisonReferenceConfiguration configuration) {
        ComparisonReferenceConfiguration oldConfiguration = repositoryProvider.queryById(configuration.getId());
        oldConfiguration.setPkPath(configuration.getPkPath());
        oldConfiguration.setFkPath(configuration.getFkPath());
        listKeyCycleDetectionHandler.judgeWhetherCycle(this, listSortHandler, oldConfiguration);
        return super.update(configuration);
    }

    @Override
    public boolean insertList(List<ComparisonReferenceConfiguration> configurationList) {
        this.addDependencyId(configurationList);
        for (ComparisonReferenceConfiguration configuration : configurationList) {
            listKeyCycleDetectionHandler.judgeWhetherCycle(this, listSortHandler, configuration);
        }
        return super.insertList(configurationList);
    }
}
