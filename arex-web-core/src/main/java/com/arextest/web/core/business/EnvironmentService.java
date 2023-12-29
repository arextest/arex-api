package com.arextest.web.core.business;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.EnvironmentRepository;
import com.arextest.web.model.contract.contracts.environment.DuplicateEnvironmentRequestType;
import com.arextest.web.model.contract.contracts.environment.EnvironmentType;
import com.arextest.web.model.contract.contracts.environment.QueryEnvsByWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.environment.SaveEnvironmentRequestType;
import com.arextest.web.model.contract.contracts.environment.SaveEnvironmentResponseType;
import com.arextest.web.model.dto.EnvironmentDto;
import com.arextest.web.model.mapper.EnvironmentMapper;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvironmentService {

  private static final String DUPLICATE_SUFFIX = "_copy";

  @Resource
  private EnvironmentRepository environmentRepository;

  public SaveEnvironmentResponseType saveEnvironment(SaveEnvironmentRequestType request) {
    SaveEnvironmentResponseType response = new SaveEnvironmentResponseType();
    EnvironmentDto dto = EnvironmentMapper.INSTANCE.dtoFromContract(request.getEnv());
    try {
      if (StringUtils.isEmpty(dto.getId())) {
        dto = environmentRepository.initEnvironment(dto);
      } else {
        dto = environmentRepository.saveEnvironment(dto);
      }
      response.setSuccess(true);
      response.setEnvironmentId(dto.getId());
    } catch (Exception e) {
      LogUtils.error(LOGGER, "failed to save environment", e);
      response.setSuccess(false);
    }
    return response;
  }

  public Boolean removeEnvironment(String id) {
    return environmentRepository.removeEnvironment(id);
  }

  public List<EnvironmentType> queryEnvsByWorkspace(QueryEnvsByWorkspaceRequestType request) {
    List<EnvironmentDto> envs = environmentRepository.queryEnvsByWorkspace(
        request.getWorkspaceId());
    return EnvironmentMapper.INSTANCE.contractFromDtoList(envs);
  }

  public List<EnvironmentType> duplicateEnvironment(DuplicateEnvironmentRequestType request) {
    EnvironmentDto env = environmentRepository.queryById(request.getId());
    if (env == null) {
      return null;
    }
    env.setId(null);
    env.setEnvName(env.getEnvName() + DUPLICATE_SUFFIX);
    env = environmentRepository.initEnvironment(env);
    return EnvironmentMapper.INSTANCE.contractFromDtoList(Collections.singletonList(env));
  }
}
