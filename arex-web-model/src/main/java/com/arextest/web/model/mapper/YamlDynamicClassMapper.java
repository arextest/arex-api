package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.record.DynamicClassConfiguration;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.DynamicClassTemplateConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface YamlDynamicClassMapper {

    YamlDynamicClassMapper INSTANCE = Mappers.getMapper(YamlDynamicClassMapper.class);

    DynamicClassTemplateConfig toYaml(DynamicClassConfiguration dynamicClassConfiguration);

    DynamicClassConfiguration fromYaml(DynamicClassTemplateConfig dynamicClassTemplateConfig);

}
