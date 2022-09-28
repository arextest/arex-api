package com.arextest.report.core.business.configservice;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.arextest.report.core.business.configservice.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.record.ServiceCollectConfiguration;
import com.arextest.report.model.api.contracts.configservice.replay.ScheduleConfiguration;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.YamlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.util.Map;


@Slf4j
@Component
public class ConfigService {

    private static final String APP_ID = "appId";

    @Resource
    ConfigurableHandler<ServiceCollectConfiguration> serviceCollectConfigurationConfigurableHandler;

    @Resource
    ConfigurableHandler<com.arextest.report.model.api.contracts.configservice.record.DynamicClassConfiguration> dynamicClassConfigurationConfigurableHandler;

    @Resource
    ConfigurableHandler<ScheduleConfiguration> scheduleConfigurationConfigurableHandler;


    @Resource
    private ComparisonConfigService comparisonConfigService;

    public QueryConfigTemplateResponseType queryConfigTemplate(QueryConfigTemplateRequestType request) {
        QueryConfigTemplateResponseType response = new QueryConfigTemplateResponseType();
        // YamlTemplate yamlTemplate = getConfigTemplate(request.getAppId());
        // DumperOptions options = new DumperOptions();
        // options.setIndent(2);
        // options.setPrettyFlow(false);
        // options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        // Representer representer = new Representer();
        // representer.addClassTag(YamlTemplate.class, Tag.MAP);
        // Yaml yaml = new Yaml(representer, options);
        // response.setConfigTemplate(yaml.dump(yamlTemplate));
        return response;
    }

    public PushConfigTemplateResponseType pushConfigTemplate(PushConfigTemplateRequestType request) {
        PushConfigTemplateResponseType response = new PushConfigTemplateResponseType();
        response.setSuccess(true);

        // YamlTemplate preTemplate = getConfigTemplate(request.getAppId());
        // String appId = request.getAppId();
        // String configTemplate = request.getConfigTemplate();
        // Yaml yaml = new Yaml();
        // YamlTemplate templateObj = null;
        // try {
        //     Map load = (Map) yaml.load(configTemplate);
        //     templateObj = JSONUtil.toBean(new JSONObject(load), YamlTemplate.class);
        // } catch (Exception e) {
        // }
        //
        // if (templateObj == null) {
        //     response.setSuccess(false);
        // } else {
        //     if (!updateServiceCollection(templateObj, appId) || !updateDynamicClassCollection(templateObj, appId)
        //             || !updateSchedule(templateObj, appId) || !comparisonConfigService.updateComparison(templateObj,
        //             appId)) {
        //         updateServiceCollection(preTemplate, appId);
        //         updateDynamicClassCollection(preTemplate, appId);
        //         updateSchedule(preTemplate, appId);
        //         comparisonConfigService.updateComparison(preTemplate, appId);
        //         response.setSuccess(false);
        //     }
        // }

        return response;
    }

    // private YamlTemplate getConfigTemplate(String appId) {
    //     YamlTemplate template = new YamlTemplate();
    //
    //     // recordConfig set
    //     RecordConfig recordConfig = new RecordConfig();
    //     recordConfig.setServiceConfig(getServiceConfig(appId));
    //     recordConfig.setDynamicClass(getDynamicClass(appId));
    //     template.setRecordConfig(recordConfig);
    //
    //     // replayConfig set
    //     ReplayConfig replayConfig = getSchedule(appId);
    //     template.setReplayConfig(replayConfig);
    //
    //     // compareConfig set
    //     List<OperationCompareConfig> compareConfig = comparisonConfigService.getCompareConfig(appId);
    //     template.setCompareConfig(compareConfig);
    //     return template;
    // }
    //
    // private ServiceConfig getServiceConfig(String appId) {
    //     ServiceCollectConfiguration serviceCollectConfiguration = serviceCollectConfigurationConfigurableHandler.useResult(appId);
    //     return serviceCollectConfiguration == null ? null : YamlServiceConfigMapper.INSTANCE.toYaml(serviceCollectConfiguration);
    // }
    //
    // private List<DynamicClass> getDynamicClass(String appId) {
    //     List<com.arextest.report.model.api.contracts.configservice.record.DynamicClassConfiguration> dynamicClassConfigurations = dynamicClassConfigurationConfigurableHandler.useResultAsList(appId);
    //     return Optional.ofNullable(dynamicClassConfigurations).orElse(Collections.emptyList()).stream()
    //             .map(YamlDynamicClassMapper.INSTANCE::toYaml).collect(Collectors.toList());
    // }
    //
    // private ReplayConfig getSchedule(String appId) {
    //     ScheduleConfiguration scheduleConfiguration = scheduleConfigurationConfigurableHandler.useResult(appId);
    //     return scheduleConfiguration == null ? null : YamlReplayConfigMapper.INSTANCE.toYaml(scheduleConfiguration);
    // }


    // private boolean updateServiceCollection(YamlTemplate templateObj, String appId) {
    //     String url = configServiceUrl + RECORD_SERVICE_COLLECTION_UPDATE_URL;
    //     ServiceConfig updateObj = null;
    //     if (templateObj.getRecordConfig() == null || templateObj.getRecordConfig().getServiceCollection() == null) {
    //         updateObj = new ServiceConfig();
    //     } else {
    //         updateObj = templateObj.getRecordConfig().getServiceCollection();
    //     }
    //     JSONObject requestObj = JSONUtil.parseObj(updateObj);
    //     requestObj.set(APP_ID, appId);
    //     return ConfigServiceUtils.sendPostHttpRequest(url, requestObj);
    // }

    // private boolean updateDynamicClassCollection(YamlTemplate templateObj, String appId) {
    //     boolean result = true;
    //     List<DynamicClassConfiguration> updateEntity = null;
    //     if (templateObj.getRecordConfig() == null || templateObj.getRecordConfig().getDynamicClass() == null) {
    //         updateEntity = new ArrayList<>();
    //     } else {
    //         updateEntity = templateObj.getRecordConfig().getDynamicClass().stream()
    //                 .map(YamlDynamicClassMapper.INSTANCE::toConfig)
    //                 .collect(Collectors.toList());
    //         updateEntity.stream().forEach(item -> item.setAppId(appId));
    //     }
    //     List<DynamicClassConfiguration> removeEntity = getDynamicClassConfiguration(appId);
    //     if (removeEntity != null && !removeEntity.isEmpty()) {
    //         result = result
    //                 && ConfigServiceUtils.sendPostHttpRequest(configServiceUrl
    //                 + DYNAMIC_CLASS_REMOVE_URL, removeEntity);
    //     }
    //     if (updateEntity != null && !updateEntity.isEmpty()) {
    //         result = result
    //                 && ConfigServiceUtils.sendPostHttpRequest(configServiceUrl
    //                 + DYNAMIC_CLASS_INSERT_URL, updateEntity);
    //     }
    //     return result;
    // }
    //
    // private boolean updateSchedule(YamlTemplate templateObj, String appId) {
    //     String url = configServiceUrl + SCHEDULE_UPDATE_URL;
    //     ReplayConfig updateObj = null;
    //     if (templateObj.getReplayConfig() == null) {
    //         updateObj = new ReplayConfig();
    //     } else {
    //         updateObj = templateObj.getReplayConfig();
    //     }
    //     JSONObject requestObj = JSONUtil.parseObj(updateObj);
    //     requestObj.set(APP_ID, appId);
    //     requestObj.set("sendMaxQps", 20);
    //     requestObj.set("targetEnv", new JSONArray());
    //     return ConfigServiceUtils.sendPostHttpRequest(url, requestObj);
    // }


}
