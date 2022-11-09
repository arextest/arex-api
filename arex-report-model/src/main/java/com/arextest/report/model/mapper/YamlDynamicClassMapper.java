package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.config.record.DynamicClassConfiguration;
import com.arextest.report.model.api.contracts.config.yamlTemplate.entity.DynamicClassTemplateConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface YamlDynamicClassMapper {

    YamlDynamicClassMapper INSTANCE = Mappers.getMapper(YamlDynamicClassMapper.class);

    DynamicClassTemplateConfig toYaml(DynamicClassConfiguration dynamicClassConfiguration);

    DynamicClassConfiguration fromYaml(DynamicClassTemplateConfig dynamicClassTemplateConfig);

}
