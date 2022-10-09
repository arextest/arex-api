package com.arextest.report.core.business.config;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.arextest.report.core.business.config.dynamic.DynamicClassConfigurableHandler;
import com.arextest.report.core.business.config.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.PushYamlTemplateRequestType;
import com.arextest.report.model.api.contracts.config.PushYamlTemplateResponseType;
import com.arextest.report.model.api.contracts.config.record.DynamicClassConfiguration;
import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import com.arextest.report.model.api.contracts.config.replay.ScheduleConfiguration;
import com.arextest.report.model.api.contracts.config.yamlTemplate.ReplayConfig;
import com.arextest.report.model.api.contracts.config.yamlTemplate.ServiceConfig;
import com.arextest.report.model.api.contracts.config.yamlTemplate.YamlTemplate;
import com.arextest.report.model.mapper.YamlDynamicClassMapper;
import com.arextest.report.model.mapper.YamlReplayConfigMapper;
import com.arextest.report.model.mapper.YamlServiceConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        LOGGER.info("pushConfigTemplate.preTemplate:", JSONUtil.toJsonStr(preTemplate));
        Yaml yaml = new Yaml();
        YamlTemplate templateObj = null;
        try {
            Map load = (Map) yaml.load(configTemplate);
            templateObj = JSONUtil.toBean(new JSONObject(load), YamlTemplate.class);
        } catch (Exception e) {
            LOGGER.error("UpdateYamlTemplateService.pushConfigTemplate", e);
        }

        if (templateObj == null) {
            response.setSuccess(false);
        } else {
            if (!updateServiceCollection(templateObj, appId) || !updateDynamicClassCollection(templateObj, appId)
                    || !updateSchedule(templateObj, appId) || !comparisonConfigService.updateComparison(templateObj, appId)) {
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
        ServiceConfig serviceConfig = templateObj.getRecordConfig().getServiceConfig();
        ServiceCollectConfiguration serviceCollectConfiguration = YamlServiceConfigMapper.INSTANCE.fromYaml(serviceConfig);
        serviceCollectConfiguration.setAppId(appId);

        return serviceCollectConfigurableHandler.update(serviceCollectConfiguration);
    }

    private boolean updateDynamicClassCollection(YamlTemplate templateObj, String appId) {
        boolean result = true;

        List<DynamicClassConfiguration> updateEntity = null;
        if (templateObj.getRecordConfig() == null || templateObj.getRecordConfig().getDynamicClass() == null) {
            updateEntity = new ArrayList<>();
        } else {
            updateEntity = templateObj.getRecordConfig().getDynamicClass().stream()
                    .map(YamlDynamicClassMapper.INSTANCE::fromYaml)
                    .collect(Collectors.toList());
            updateEntity.forEach(item -> item.setAppId(appId));
        }

        result = dynamicClassConfigurableHandler.removeByAppId(appId);

        if (CollectionUtils.isNotEmpty(updateEntity)) {
            result = result && dynamicClassConfigurableHandler.insertList(updateEntity);
        }
        return result;
    }

    private boolean updateSchedule(YamlTemplate templateObj, String appId) {

        ScheduleConfiguration scheduleConfiguration = YamlReplayConfigMapper.INSTANCE.fromYaml(
                templateObj.getReplayConfig() == null ? new ReplayConfig() : templateObj.getReplayConfig());
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
