package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonTransformCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Mapper
public interface ConfigComparisonTransformMapper {

  ConfigComparisonTransformMapper INSTANCE = Mappers.getMapper(
      ConfigComparisonTransformMapper.class);

  @Mappings({@Mapping(target = "modifiedTime",
      expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")})
  ComparisonTransformConfiguration dtoFromDao(ConfigComparisonTransformCollection dao);

  @Mappings({@Mapping(target = "id", expression = "java(null)"),
      @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
      @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")})
  ConfigComparisonTransformCollection daoFromDto(ComparisonTransformConfiguration dto);
}