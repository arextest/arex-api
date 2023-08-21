package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsCategoryConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCategoryCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author wildeslam.
 * @create 2023/8/18 15:15
 */
@Mapper
public interface ConfigComparisonExclusionsCategoryMapper {
    ConfigComparisonExclusionsCategoryMapper INSTANCE = Mappers.getMapper(ConfigComparisonExclusionsCategoryMapper.class);

    @Mappings({
        @Mapping(target = "modifiedTime", expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")
    })
    ComparisonExclusionsCategoryConfiguration dtoFromDao(ConfigComparisonExclusionsCategoryCollection dao);

    @Mappings({
        @Mapping(target = "id", expression = "java(null)"),
        @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
        @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")
    })
    ConfigComparisonExclusionsCategoryCollection daoFromDto(ComparisonExclusionsCategoryConfiguration dto);
}
