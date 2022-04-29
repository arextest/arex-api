package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.configservice.DynamicClass;
import com.arextest.report.model.api.contracts.configservice.DynamicClassConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DynamicClassMapper {

    DynamicClassMapper INSTANCE = Mappers.getMapper(DynamicClassMapper.class);

    DynamicClass formConfig(DynamicClassConfiguration dynamicClassConfiguration);

    DynamicClassConfiguration toConfig(DynamicClass dynamicClass);

}
