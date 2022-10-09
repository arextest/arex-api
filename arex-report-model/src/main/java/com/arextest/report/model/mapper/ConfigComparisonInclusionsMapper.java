package com.arextest.report.model.mapper;


import com.arextest.report.model.api.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.report.model.dao.mongodb.ConfigComparisonInclusionsCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConfigComparisonInclusionsMapper {

    ConfigComparisonInclusionsMapper INSTANCE = Mappers.getMapper(ConfigComparisonInclusionsMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    ComparisonInclusionsConfiguration dtoFromDao(ConfigComparisonInclusionsCollection dao);

    @Mappings({
            @Mapping(target = "id", expression = "java(null)"),
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")
    })
    ConfigComparisonInclusionsCollection daoFromDto(ComparisonInclusionsConfiguration dto);
}