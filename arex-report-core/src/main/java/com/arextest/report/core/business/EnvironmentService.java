package com.arextest.report.core.business;

import com.arextest.report.core.repository.EnvironmentRepository;
import com.arextest.report.model.api.contracts.environment.EnvironmentType;
import com.arextest.report.model.api.contracts.environment.QueryEnvsByWorkspaceRequestType;
import com.arextest.report.model.api.contracts.environment.SaveEnvironmentRequestType;
import com.arextest.report.model.dto.EnvironmentDto;
import com.arextest.report.model.mapper.EnvironmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class EnvironmentService {

    @Resource
    private EnvironmentRepository environmentRepository;

    public Boolean saveEnvironment(SaveEnvironmentRequestType request) {
        EnvironmentDto dto = EnvironmentMapper.INSTANCE.dtoFromContract(request.getEnv());
        try {
            if (StringUtils.isEmpty(dto.getId())) {
                environmentRepository.initEnvironment(dto);
            } else {
                environmentRepository.saveEnvironment(dto);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("failed to save environment", e);
        }
        return false;
    }

    public Boolean removeEnvironment(String id) {
        return environmentRepository.removeEnvironment(id);
    }

    public List<EnvironmentType> queryEnvsByWorkspace(QueryEnvsByWorkspaceRequestType request) {
        List<EnvironmentDto> envs = environmentRepository.queryEnvsByWorkspace(request.getWorkspaceId());
        return EnvironmentMapper.INSTANCE.contractFromDtoList(envs);
    }
}
