package com.arextest.web.api.service.controller.config;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dto.ModifyType;
import com.arextest.web.core.business.config.replay.ComparisonTransformConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonRootTransformConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import com.arextest.web.model.mapper.ConfigComparisonTransformMapper;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config/comparison/rootTransform")
public class ComparisonRootTransformController {

  @Resource
  ComparisonTransformConfigurableHandler comparisonTransformConfigurableHandler;


  @PostMapping("/queryComparisonConfig")
  public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
    List<ComparisonTransformConfiguration> configs = comparisonTransformConfigurableHandler.
        queryComparisonConfig(request.getAppId(), request.getOperationId(),
            request.getOperationType(), request.getOperationName());
    comparisonTransformConfigurableHandler.removeDetailsExpired(configs,
        request.getFilterExpired());
    List<ComparisonRootTransformConfiguration> rootTransformConfigurations = configs.stream()
        .map(ConfigComparisonTransformMapper.INSTANCE::requestTypeFromDto)
        .filter(item -> item.getTransformMethodName() != null)
        .collect(Collectors.toList());
    return ResponseUtils.successResponse(rootTransformConfigurations);
  }


  @PostMapping("/modify/{modifyType}")
  @AppAuth
  public Response modify(@PathVariable ModifyType modifyType,
      @RequestBody ComparisonRootTransformConfiguration configuration)
      throws Exception {
    if (modifyType == ModifyType.INSERT) {
      configuration.validParameters();
      ComparisonTransformConfiguration transformConfiguration =
          ConfigComparisonTransformMapper.INSTANCE.dotFromRequestType(configuration);
      return ResponseUtils.successResponse(
          comparisonTransformConfigurableHandler.insert(transformConfiguration));
    }
    if (modifyType == ModifyType.UPDATE) {
      ComparisonTransformConfiguration transformConfiguration =
          ConfigComparisonTransformMapper.INSTANCE.dotFromRequestType(configuration);
      return ResponseUtils.successResponse(
          comparisonTransformConfigurableHandler.update(transformConfiguration));
    }
    if (modifyType == ModifyType.REMOVE) {
      ComparisonTransformConfiguration transformConfiguration =
          ConfigComparisonTransformMapper.INSTANCE.dotFromRequestType(configuration);
      return ResponseUtils.successResponse(
          comparisonTransformConfigurableHandler.remove(transformConfiguration));
    }
    return ResponseUtils.resourceNotFoundResponse();
  }


}
