package com.arextest.report.core.business.configservice;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.arextest.report.core.business.util.ConfigServiceUtils;
import com.arextest.report.model.api.contracts.configservice.CompareConfig;
import com.arextest.report.model.api.contracts.configservice.ConfigTemplate;
import com.arextest.report.model.api.contracts.configservice.DynamicClass;
import com.arextest.report.model.api.contracts.configservice.DynamicClassConfiguration;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.RecordConfig;
import com.arextest.report.model.api.contracts.configservice.ReplayConfig;
import com.arextest.report.model.api.contracts.configservice.ServiceCollect;
import com.arextest.report.model.mapper.DynamicClassMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ConfigService {

    private static final String RECORD_SERVICE_COLLECTION_URL = "/api/config/serviceCollect/useResult/appId/";
    private static final String DYNAMIC_CLASS_URL = "/api/config/dynamicClass/useResultAsList/appId/";
    private static final String SCHEDULE_URL = "/api/config/schedule/useResult/appId/";

    private static final String RECORD_SERVICE_COLLECTION_UPDATE_URL = "/api/config/serviceCollect/modify/UPDATE";
    private static final String DYNAMIC_CLASS_REMOVE_URL = "/api/config/dynamicClass/batchModify/REMOVE";
    private static final String DYNAMIC_CLASS_INSERT_URL = "/api/config/dynamicClass/batchModify/INSERT";
    private static final String SCHEDULE_UPDATE_URL = "/api/config/schedule/modify/UPDATE";

    private static final String APP_ID = "appId";

    @Value("${arex.config.service.url}")
    private String configServiceUrl;

    @Resource
    private ComparisonConfigService comparisonConfigService;

    public QueryConfigTemplateResponseType queryConfigTemplate(QueryConfigTemplateRequestType request) {
        QueryConfigTemplateResponseType response = new QueryConfigTemplateResponseType();
        ConfigTemplate configTemplate = getConfigTemplate(request.getAppId());
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(false);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer();
        representer.addClassTag(ConfigTemplate.class, Tag.MAP);
        Yaml yaml = new Yaml(representer, options);
        response.setConfigTemplate(yaml.dump(configTemplate));
        return response;
    }

    public PushConfigTemplateResponseType pushConfigTemplate(PushConfigTemplateRequestType request) {
        ConfigTemplate preTemplate = getConfigTemplate(request.getAppId());
        PushConfigTemplateResponseType response = new PushConfigTemplateResponseType();
        response.setSuccess(true);
        String appId = request.getAppId();
        String configTemplate = request.getConfigTemplate();
        Yaml yaml = new Yaml();
        ConfigTemplate templateObj = null;
        try {
            Map load = (Map) yaml.load(configTemplate);
            templateObj = JSONUtil.toBean(new JSONObject(load), ConfigTemplate.class);
        } catch (Exception e) {
        }

        if (templateObj == null) {
            response.setSuccess(false);
        } else {
            if (!updateServiceCollection(templateObj, appId) || !updateDynamicClassCollection(templateObj, appId)
                    || !updateSchedule(templateObj, appId) || !comparisonConfigService.updateComparison(templateObj,
                    appId)) {
                updateServiceCollection(preTemplate, appId);
                updateDynamicClassCollection(preTemplate, appId);
                updateSchedule(preTemplate, appId);
                comparisonConfigService.updateComparison(preTemplate, appId);
                response.setSuccess(false);
            }
        }
        return response;
    }

    private ConfigTemplate getConfigTemplate(String appId) {
        ConfigTemplate template = new ConfigTemplate();

        // recordConfig set
        RecordConfig recordConfig = new RecordConfig();
        recordConfig.setServiceCollection(getServiceCollection(appId));
        recordConfig.setDynamicClass(getDynamicClass(appId));
        template.setRecordConfig(recordConfig);

        // replayConfig set
        ReplayConfig replayConfig = getSchedule(appId);
        template.setReplayConfig(replayConfig);

        // compareConfig set
        CompareConfig compareConfig = comparisonConfigService.getCompareConfig(appId);
        template.setCompareConfig(compareConfig);
        return template;
    }

    private ServiceCollect getServiceCollection(String appId) {
        String url = configServiceUrl + RECORD_SERVICE_COLLECTION_URL + appId;
        return ConfigServiceUtils.produceEntity(ConfigServiceUtils.sendGetHttpRequest(url), ServiceCollect.class);
    }

    private List<DynamicClass> getDynamicClass(String appId) {
        List<DynamicClassConfiguration> dynamicClassConfiguration = getDynamicClassConfiguration(appId);
        if (dynamicClassConfiguration == null) {
            return null;
        }
        return dynamicClassConfiguration.stream()
                .map(DynamicClassMapper.INSTANCE::formConfig)
                .collect(Collectors.toList());
    }

    private List<DynamicClassConfiguration> getDynamicClassConfiguration(String appId) {
        String url = configServiceUrl + DYNAMIC_CLASS_URL + appId;
        return ConfigServiceUtils.produceListEntity(ConfigServiceUtils.sendGetHttpRequest(url),
                DynamicClassConfiguration.class);
    }

    private ReplayConfig getSchedule(String appId) {
        String url = configServiceUrl + SCHEDULE_URL + appId;
        return ConfigServiceUtils.produceEntity(ConfigServiceUtils.sendGetHttpRequest(url), ReplayConfig.class);
    }


    private boolean updateServiceCollection(ConfigTemplate templateObj, String appId) {
        String url = configServiceUrl + RECORD_SERVICE_COLLECTION_UPDATE_URL;
        ServiceCollect updateObj = null;
        if (templateObj.getRecordConfig() == null || templateObj.getRecordConfig().getServiceCollection() == null) {
            updateObj = new ServiceCollect();
        } else {
            updateObj = templateObj.getRecordConfig().getServiceCollection();
        }
        JSONObject requestObj = JSONUtil.parseObj(updateObj);
        requestObj.set(APP_ID, appId);
        return ConfigServiceUtils.sendPostHttpRequest(url, requestObj);
    }

    private boolean updateDynamicClassCollection(ConfigTemplate templateObj, String appId) {
        boolean result = true;
        List<DynamicClassConfiguration> updateEntity = null;
        if (templateObj.getRecordConfig() == null || templateObj.getRecordConfig().getDynamicClass() == null) {
            updateEntity = new ArrayList<>();
        } else {
            updateEntity = templateObj.getRecordConfig().getDynamicClass().stream()
                    .map(DynamicClassMapper.INSTANCE::toConfig)
                    .collect(Collectors.toList());
            updateEntity.stream().forEach(item -> item.setAppId(appId));
        }
        List<DynamicClassConfiguration> removeEntity = getDynamicClassConfiguration(appId);
        if (removeEntity != null && !removeEntity.isEmpty()) {
            result = result
                    && ConfigServiceUtils.sendPostHttpRequest(configServiceUrl
                    + DYNAMIC_CLASS_REMOVE_URL, removeEntity);
        }
        if (updateEntity != null && !updateEntity.isEmpty()) {
            result = result
                    && ConfigServiceUtils.sendPostHttpRequest(configServiceUrl
                    + DYNAMIC_CLASS_INSERT_URL, updateEntity);
        }
        return result;
    }

    private boolean updateSchedule(ConfigTemplate templateObj, String appId) {
        String url = configServiceUrl + SCHEDULE_UPDATE_URL;
        ReplayConfig updateObj = null;
        if (templateObj.getReplayConfig() == null) {
            updateObj = new ReplayConfig();
        } else {
            updateObj = templateObj.getReplayConfig();
        }
        JSONObject requestObj = JSONUtil.parseObj(updateObj);
        requestObj.set(APP_ID, appId);
        requestObj.set("sendMaxQps", 20);
        requestObj.set("targetEnv", new JSONArray());
        return ConfigServiceUtils.sendPostHttpRequest(url, requestObj);
    }
}
