package com.arextest.web.model.mapper;

import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ServiceTemplateConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface YamlServiceConfigMapper {

  YamlServiceConfigMapper INSTANCE = Mappers.getMapper(YamlServiceConfigMapper.class);

  ServiceTemplateConfig toYaml(ServiceCollectConfiguration dao);

  ServiceCollectConfiguration fromYaml(ServiceTemplateConfig dto);
}