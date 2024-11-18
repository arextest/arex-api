package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonSummaryConfiguration.ReplayScriptMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScriptInfoMapper {

  ScriptInfoMapper INSTANCE = Mappers.getMapper(ScriptInfoMapper.class);

  @Mappings({
      @Mapping(target = "functionName", source = "functionId", qualifiedByName = "functionIdToFunctionName"),
  })
  ReplayScriptMethod toScriptMethodInfo(ComparisonScriptConfiguration.ScriptMethod scriptMethod);


  @Named("functionIdToFunctionName")
  default String functionIdToFunctionName(String functionId) {
    return "func_" + functionId;
  }


}
