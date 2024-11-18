package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptContentRequestType;
import com.arextest.web.model.dao.mongodb.ComparisonScriptContentCollection;
import com.arextest.web.model.dto.config.ComparisonScriptContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConfigComparisonScriptContentMapper {

  ConfigComparisonScriptContentMapper INSTANCE = Mappers.getMapper(
      ConfigComparisonScriptContentMapper.class);

  ComparisonScriptContent dtoFromContract(ComparisonScriptContentRequestType contract);

  @Mappings({
      @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
      @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")})
  ComparisonScriptContentCollection daoFromDto(ComparisonScriptContent dto);

  ComparisonScriptContent dtoFromDao(ComparisonScriptContentCollection dao);

}
