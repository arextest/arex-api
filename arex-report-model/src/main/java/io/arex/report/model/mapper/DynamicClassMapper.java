package io.arex.report.model.mapper;

import io.arex.report.model.api.contracts.configservice.DynamicClass;
import io.arex.report.model.api.contracts.configservice.DynamicClassConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DynamicClassMapper {

    DynamicClassMapper INSTANCE = Mappers.getMapper(DynamicClassMapper.class);

    DynamicClass formConfig(DynamicClassConfiguration dynamicClassConfiguration);

    DynamicClassConfiguration toConfig(DynamicClass dynamicClass);

}
