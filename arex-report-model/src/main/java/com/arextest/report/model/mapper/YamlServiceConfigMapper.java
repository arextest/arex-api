package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import com.arextest.report.model.api.contracts.config.yamlTemplate.entity.ServiceTemplateConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface YamlServiceConfigMapper {

    YamlServiceConfigMapper INSTANCE = Mappers.getMapper(YamlServiceConfigMapper.class);

    ServiceTemplateConfig toYaml(ServiceCollectConfiguration dao);

    ServiceCollectConfiguration fromYaml(ServiceTemplateConfig dto);
}