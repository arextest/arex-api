package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.config.record.DynamicClassConfiguration;
import com.arextest.report.model.api.contracts.config.yamlTemplate.DynamicClass;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface YamlDynamicClassMapper {

    YamlDynamicClassMapper INSTANCE = Mappers.getMapper(YamlDynamicClassMapper.class);

    DynamicClass toYaml(DynamicClassConfiguration dynamicClassConfiguration);

    DynamicClassConfiguration fromYaml(DynamicClass dynamicClass);

}
