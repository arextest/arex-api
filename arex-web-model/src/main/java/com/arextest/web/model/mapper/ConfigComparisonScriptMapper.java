package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonScriptCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Mapper
public interface ConfigComparisonScriptMapper {

  ConfigComparisonScriptMapper INSTANCE = Mappers.getMapper(
      ConfigComparisonScriptMapper.class);

  @Mappings({@Mapping(target = "modifiedTime",
      expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")})
  ComparisonScriptConfiguration dtoFromDao(ConfigComparisonScriptCollection dao);

  @Mappings({@Mapping(target = "id", expression = "java(null)"),
      @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
      @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")})
  ConfigComparisonScriptCollection daoFromDto(ComparisonScriptConfiguration dto);
}