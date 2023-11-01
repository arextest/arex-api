package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonEncryptionConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonEncryptionCollection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConfigComparisonEncryptionMapper {

  ConfigComparisonEncryptionMapper INSTANCE = Mappers.getMapper(
      ConfigComparisonEncryptionMapper.class);

  @Mappings({@Mapping(target = "modifiedTime",
      expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")})
  ComparisonEncryptionConfiguration dtoFromDao(ConfigComparisonEncryptionCollection dao);

  @Mappings({@Mapping(target = "id", expression = "java(null)"),
      @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
      @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")})
  ConfigComparisonEncryptionCollection daoFromDto(ComparisonEncryptionConfiguration dto);
}
