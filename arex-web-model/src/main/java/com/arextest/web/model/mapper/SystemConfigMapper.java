package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.config.SystemConfig;
import com.arextest.web.model.dao.mongodb.SystemConfigCollection;

/**
 * @author wildeslam.
 * @create 2023/9/25 16:58
 */
@Mapper
public interface SystemConfigMapper {
    SystemConfigMapper INSTANCE = Mappers.getMapper(SystemConfigMapper.class);

    SystemConfig dtoFromDao(SystemConfigCollection dao);

    @Mappings({@Mapping(target = "id", expression = "java(null)"),
        @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
        @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")})
    SystemConfigCollection daoFromDto(SystemConfig dto);

}
