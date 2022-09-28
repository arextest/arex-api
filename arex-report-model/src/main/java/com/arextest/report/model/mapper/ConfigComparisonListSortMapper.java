package com.arextest.report.model.mapper;


import com.arextest.report.model.api.contracts.configservice.replay.ComparisonListSortConfiguration;
import com.arextest.report.model.dao.mongodb.ConfigComparisonListSortCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Mapper
public interface ConfigComparisonListSortMapper {

    ConfigComparisonListSortMapper INSTANCE = Mappers.getMapper(ConfigComparisonListSortMapper.class);

    @Mappings({
            @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    ComparisonListSortConfiguration dtoFromDao(ConfigComparisonListSortCollection dao);

    @Mappings({
            @Mapping(target = "id", expression = "java(null)"),
            @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
            @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")
    })
    ConfigComparisonListSortCollection daoFromDto(ComparisonListSortConfiguration dto);
}
