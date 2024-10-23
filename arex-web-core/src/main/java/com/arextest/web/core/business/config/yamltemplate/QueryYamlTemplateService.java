package com.arextest.web.core.business.config.yamltemplate;

import com.arextest.config.model.dto.record.DynamicClassConfiguration;
import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.QueryYamlTemplateRequestType;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.QueryYamlTemplateResponseType;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.DynamicClassTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.OperationCompareTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.RecordTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ReplayTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ServiceTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.YamlTemplate;
import com.arextest.web.model.mapper.YamlDynamicClassMapper;
import com.arextest.web.model.mapper.YamlReplayConfigMapper;
import com.arextest.web.model.mapper.YamlServiceConfigMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Created by rchen9 on 2022/9/28.
 */
@Slf4j
@Component
public class QueryYamlTemplateService {

  @Resource
  ConfigurableHandler<ServiceCollectConfiguration> serviceCollectConfigurableHandler;

  @Resource
  ConfigurableHandler<DynamicClassConfiguration> dynamicClassConfigurableHandler;

  @Resource
  ConfigurableHandler<ScheduleConfiguration> scheduleConfigurableHandler;

  @Resource
  private ComparisonConfigService comparisonConfigService;

  public QueryYamlTemplateResponseType queryConfigTemplate(QueryYamlTemplateRequestType request) {
    QueryYamlTemplateResponseType response = new QueryYamlTemplateResponseType();
    YamlTemplate yamlTemplate = getConfigTemplate(request.getAppId());
    DumperOptions options = new DumperOptions();
    options.setIndent(2);
    options.setPrettyFlow(false);
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    Representer representer = new Representer(new DumperOptions());
    representer.addClassTag(YamlTemplate.class, Tag.MAP);
    Yaml yaml = new Yaml(representer, options);
    response.setConfigTemplate(yaml.dump(yamlTemplate));
    return response;
  }

  public YamlTemplate getConfigTemplate(String appId) {
    YamlTemplate template = new YamlTemplate();

    // recordConfig set
    RecordTemplateConfig recordTemplateConfig = new RecordTemplateConfig();
    recordTemplateConfig.setServiceTemplateConfig(getServiceConfig(appId));
    recordTemplateConfig.setDynamicClassTemplateConfigs(getDynamicClass(appId));
    template.setRecordTemplateConfig(recordTemplateConfig);

    // replayConfig set
    ReplayTemplateConfig replayTemplateConfig = getSchedule(appId);
    template.setReplayTemplateConfig(replayTemplateConfig);

    // compareConfig set
    List<OperationCompareTemplateConfig> compareConfig = comparisonConfigService.getCompareConfig(
        appId);
    template.setCompareTemplateConfigs(compareConfig);
    return template;
  }

  private ServiceTemplateConfig getServiceConfig(String appId) {
    ServiceCollectConfiguration serviceCollectConfiguration = serviceCollectConfigurableHandler.useResult(
        appId);
    return serviceCollectConfiguration == null ? null
        : YamlServiceConfigMapper.INSTANCE.toYaml(serviceCollectConfiguration);
  }

  private List<DynamicClassTemplateConfig> getDynamicClass(String appId) {
    List<DynamicClassConfiguration> dynamicClassConfigurations =
        dynamicClassConfigurableHandler.useResultAsList(appId);
    return Optional.ofNullable(dynamicClassConfigurations).orElse(Collections.emptyList()).stream()
        .map(YamlDynamicClassMapper.INSTANCE::toYaml).collect(Collectors.toList());
  }

  private ReplayTemplateConfig getSchedule(String appId) {
    ScheduleConfiguration scheduleConfiguration = scheduleConfigurableHandler.useResult(appId);
    return scheduleConfiguration == null ? null
        : YamlReplayConfigMapper.INSTANCE.toYaml(scheduleConfiguration);
  }

}
