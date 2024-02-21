package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.SystemConfiguration;
import com.arextest.web.model.dao.mongodb.SystemConfigurationCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author wildeslam.
 * @create 2024/2/21 15:20
 */
@Mapper
public interface SystemConfigurationMapper {

    SystemConfigurationMapper INSTANCE = Mappers.getMapper(SystemConfigurationMapper.class);

    SystemConfiguration dtoFromDao(SystemConfigurationCollection dao);

    @Mappings({@Mapping(target = "id", expression = "java(null)"),
        @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
        @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")})
    SystemConfigurationCollection daoFromDto(SystemConfiguration dto);
}
