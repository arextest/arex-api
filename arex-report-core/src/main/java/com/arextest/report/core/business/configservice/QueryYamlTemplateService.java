package com.arextest.report.core.business.configservice;

import com.arextest.report.core.business.configservice.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.record.ServiceCollectConfiguration;
import com.arextest.report.model.api.contracts.configservice.replay.ScheduleConfiguration;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.DynamicClass;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.OperationCompareConfig;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.RecordConfig;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.ReplayConfig;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.ServiceConfig;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.YamlTemplate;
import com.arextest.report.model.mapper.YamlDynamicClassMapper;
import com.arextest.report.model.mapper.YamlReplayConfigMapper;
import com.arextest.report.model.mapper.YamlServiceConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2022/9/28.
 */
@Slf4j
@Component
public class QueryYamlTemplateService {

    @Resource
    ConfigurableHandler<ServiceCollectConfiguration> serviceCollectConfigurableHandler;

    @Resource
    ConfigurableHandler<com.arextest.report.model.api.contracts.configservice.record.DynamicClassConfiguration> dynamicClassConfigurableHandler;

    @Resource
    ConfigurableHandler<ScheduleConfiguration> scheduleConfigurableHandler;


    @Resource
    private ComparisonConfigService comparisonConfigService;

    public QueryConfigTemplateResponseType queryConfigTemplate(QueryConfigTemplateRequestType request) {
        QueryConfigTemplateResponseType response = new QueryConfigTemplateResponseType();
        YamlTemplate yamlTemplate = getConfigTemplate(request.getAppId());
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(false);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer();
        representer.addClassTag(YamlTemplate.class, Tag.MAP);
        Yaml yaml = new Yaml(representer, options);
        response.setConfigTemplate(yaml.dump(yamlTemplate));
        return response;
    }

    public YamlTemplate getConfigTemplate(String appId) {
        YamlTemplate template = new YamlTemplate();

        // recordConfig set
        RecordConfig recordConfig = new RecordConfig();
        recordConfig.setServiceConfig(getServiceConfig(appId));
        recordConfig.setDynamicClass(getDynamicClass(appId));
        template.setRecordConfig(recordConfig);

        // replayConfig set
        ReplayConfig replayConfig = getSchedule(appId);
        template.setReplayConfig(replayConfig);

        // compareConfig set
        List<OperationCompareConfig> compareConfig = comparisonConfigService.getCompareConfig(appId);
        template.setCompareConfig(compareConfig);
        return template;
    }

    private ServiceConfig getServiceConfig(String appId) {
        ServiceCollectConfiguration serviceCollectConfiguration = serviceCollectConfigurableHandler.useResult(appId);
        return serviceCollectConfiguration == null ? null : YamlServiceConfigMapper.INSTANCE.toYaml(serviceCollectConfiguration);
    }

    private List<DynamicClass> getDynamicClass(String appId) {
        List<com.arextest.report.model.api.contracts.configservice.record.DynamicClassConfiguration> dynamicClassConfigurations = dynamicClassConfigurableHandler.useResultAsList(appId);
        return Optional.ofNullable(dynamicClassConfigurations).orElse(Collections.emptyList()).stream()
                .map(YamlDynamicClassMapper.INSTANCE::toYaml).collect(Collectors.toList());
    }

    private ReplayConfig getSchedule(String appId) {
        ScheduleConfiguration scheduleConfiguration = scheduleConfigurableHandler.useResult(appId);
        return scheduleConfiguration == null ? null : YamlReplayConfigMapper.INSTANCE.toYaml(scheduleConfiguration);
    }

}
