package com.arextest.web.core.business.config.replay;

import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Component
public class ComparisonExclusionsConfigurableHandler extends AbstractComparisonConfigurableHandler<ComparisonExclusionsConfiguration> {
    protected ComparisonExclusionsConfigurableHandler(@Autowired
            ConfigRepositoryProvider<ComparisonExclusionsConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Resource
    FSInterfaceRepository fsInterfaceRepository;

    @Override
    public List<ComparisonExclusionsConfiguration> queryByInterfaceId(String interfaceId) {

        // get operationId
        FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(interfaceId);
        String operationId = fsInterfaceDto == null ? null : fsInterfaceDto.getOperationId();

        return queryByOperationIdAndInterfaceId(interfaceId, operationId);
    }
}
