package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.configservice.record.ServiceCollectConfiguration;
import com.arextest.report.model.api.contracts.configservice.yamlTemplate.ServiceConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface YamlServiceConfigMapper {

    YamlServiceConfigMapper INSTANCE = Mappers.getMapper(YamlServiceConfigMapper.class);

    ServiceConfig toYaml(ServiceCollectConfiguration dao);

    ServiceCollectConfiguration fromYaml(ServiceConfig dto);
}