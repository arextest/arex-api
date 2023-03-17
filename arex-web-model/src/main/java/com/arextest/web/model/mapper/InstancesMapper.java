package com.arextest.web.model.mapper;


import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import com.arextest.web.model.dao.mongodb.InstancesCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface InstancesMapper {

    InstancesMapper INSTANCE = Mappers.getMapper(InstancesMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    InstancesConfiguration dtoFromDao(InstancesCollection dao);

    @Mappings({
            @Mapping(target = "id", expression = "java(null)"),
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")
    })
    InstancesCollection daoFromDto(InstancesConfiguration dto);
}
