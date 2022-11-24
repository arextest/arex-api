package com.arextest.web.core.business;

import com.arextest.web.core.repository.EnvironmentRepository;
import com.arextest.web.model.contract.contracts.environment.DuplicateEnvironmentRequestType;
import com.arextest.web.model.contract.contracts.environment.EnvironmentType;
import com.arextest.web.model.contract.contracts.environment.QueryEnvsByWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.environment.SaveEnvironmentRequestType;
import com.arextest.web.model.dto.EnvironmentDto;
import com.arextest.web.model.mapper.EnvironmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class EnvironmentService {

    private static final String DUPLICATE_SUFFIX = "_copy";

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

    public Boolean duplicateEnvironment(DuplicateEnvironmentRequestType request) {
        EnvironmentDto env = environmentRepository.queryById(request.getId());
        if (env == null) {
            return false;
        }
        env.setId(null);
        env.setEnvName(env.getEnvName() + DUPLICATE_SUFFIX);
        env = environmentRepository.initEnvironment(env);
        return env.getId() != null;
    }
}
