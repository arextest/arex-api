package com.arextest.web.core.business.config.yamltemplate;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.arextest.config.model.dto.record.DynamicClassConfiguration;
import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.record.DynamicClassConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.PushYamlTemplateRequestType;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.PushYamlTemplateResponseType;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ReplayTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ServiceTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.YamlTemplate;
import com.arextest.web.model.mapper.YamlDynamicClassMapper;
import com.arextest.web.model.mapper.YamlReplayConfigMapper;
import com.arextest.web.model.mapper.YamlServiceConfigMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

/**
 * Created by rchen9 on 2022/9/28.
 */
@Slf4j
@Component
public class UpdateYamlTemplateService {

  @Resource
  QueryYamlTemplateService queryYamlTemplateService;

  @Resource
  ComparisonConfigService comparisonConfigService;

  @Resource
  ConfigurableHandler<ServiceCollectConfiguration> serviceCollectConfigurableHandler;

  @Resource
  DynamicClassConfigurableHandler dynamicClassConfigurableHandler;

  @Resource
  ConfigurableHandler<ScheduleConfiguration> scheduleConfigurableHandler;

  public PushYamlTemplateResponseType pushConfigTemplate(PushYamlTemplateRequestType request) {
    String appId = request.getAppId();
    String configTemplate = request.getConfigTemplate();
    PushYamlTemplateResponseType response = new PushYamlTemplateResponseType();
    response.setSuccess(true);
    YamlTemplate preTemplate = queryYamlTemplateService.getConfigTemplate(request.getAppId());
    LogUtils.info(LOGGER, "pushConfigTemplate.preTemplate:{}", JSONUtil.toJsonStr(preTemplate));
    Yaml yaml = new Yaml();
    YamlTemplate templateObj = null;
    try {
      Map load = (Map) yaml.load(configTemplate);
      templateObj = JSONUtil.toBean(new JSONObject(load), YamlTemplate.class);
    } catch (Exception e) {
      LogUtils.error(LOGGER, "UpdateYamlTemplateService.pushConfigTemplate", e);
    }

    if (templateObj == null) {
      response.setSuccess(false);
    } else {
      if (!updateServiceCollection(templateObj, appId) || !updateDynamicClassCollection(templateObj,
          appId)
          || !updateSchedule(templateObj, appId)
          || !comparisonConfigService.updateComparison(templateObj, appId)) {
        updateServiceCollection(preTemplate, appId);
        updateDynamicClassCollection(preTemplate, appId);
        updateSchedule(preTemplate, appId);
        comparisonConfigService.updateComparison(preTemplate, appId);
        response.setSuccess(false);
      }
    }

    return response;
  }

  private boolean updateServiceCollection(YamlTemplate templateObj, String appId) {
    ServiceTemplateConfig serviceTemplateConfig = templateObj.getRecordTemplateConfig()
        .getServiceTemplateConfig();
    ServiceCollectConfiguration serviceCollectConfiguration =
        YamlServiceConfigMapper.INSTANCE.fromYaml(serviceTemplateConfig);
    serviceCollectConfiguration.setAppId(appId);

    return serviceCollectConfigurableHandler.update(serviceCollectConfiguration);
  }

  private boolean updateDynamicClassCollection(YamlTemplate templateObj, String appId) {
    boolean result = true;

    List<DynamicClassConfiguration> updateEntity = null;
    if (templateObj.getRecordTemplateConfig() == null
        || templateObj.getRecordTemplateConfig().getDynamicClassTemplateConfigs() == null) {
      updateEntity = new ArrayList<>();
    } else {
      updateEntity = templateObj.getRecordTemplateConfig().getDynamicClassTemplateConfigs().stream()
          .map(YamlDynamicClassMapper.INSTANCE::fromYaml).collect(Collectors.toList());
      updateEntity.forEach(item -> item.setAppId(appId));
    }

    result = dynamicClassConfigurableHandler.removeByAppId(appId);

    if (CollectionUtils.isNotEmpty(updateEntity)) {
      result = result && dynamicClassConfigurableHandler.insertList(updateEntity);
    }
    return result;
  }

  private boolean updateSchedule(YamlTemplate templateObj, String appId) {

    ScheduleConfiguration scheduleConfiguration =
        YamlReplayConfigMapper.INSTANCE.fromYaml(templateObj.getReplayTemplateConfig() == null
            ? new ReplayTemplateConfig() : templateObj.getReplayTemplateConfig());
    scheduleConfiguration.setAppId(appId);
    if (scheduleConfiguration.getSendMaxQps() == null) {
      scheduleConfiguration.setSendMaxQps(20);
    }
    if (scheduleConfiguration.getTargetEnv() == null) {
      scheduleConfiguration.setTargetEnv(Collections.emptySet());
    }
    return scheduleConfigurableHandler.update(scheduleConfiguration);
  }

}
