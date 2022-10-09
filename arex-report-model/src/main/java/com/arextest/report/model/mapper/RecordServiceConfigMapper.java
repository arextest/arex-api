package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import com.arextest.report.model.dao.mongodb.RecordServiceConfigCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface RecordServiceConfigMapper {

    RecordServiceConfigMapper INSTANCE = Mappers.getMapper(RecordServiceConfigMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    ServiceCollectConfiguration dtoFromDao(RecordServiceConfigCollection dao);

    @Mappings({
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")
    })
    RecordServiceConfigCollection daoFromDto(ServiceCollectConfiguration dto);

}
