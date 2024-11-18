package com.arextest.web.model.mapper;

import com.arextest.config.model.dto.application.Dependency;
import com.arextest.web.model.contract.contracts.compare.TransformDetail;
import com.arextest.web.model.contract.contracts.config.replay.AbstractComparisonDetailsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonResponseType;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import java.util.Map;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PageQueryComparisonMapper {


  PageQueryComparisonMapper INSTANCE = Mappers.getMapper(PageQueryComparisonMapper.class);

  PageQueryComparisonDto dtoFromContract(PageQueryComparisonRequestType contract);

  @Mappings({
      @Mapping(target = "exclusionPath", source = "dto.exclusions"),
      @Mapping(target = "operationName", source = "dto", qualifiedByName = "getOperationName"),
      @Mapping(target = "dependencyName", source = "dto", qualifiedByName = "getDependencyName"),
      @Mapping(target = "dependencyType", source = "dto", qualifiedByName = "getDependencyType"),
      @Mapping(target = "expirationDate", expression = "java(dto.getExpirationDate() != null ? dto.getExpirationDate().getTime() : null)"),
  })
  PageQueryComparisonResponseType.ExclusionInfo contractFromDto(
      ComparisonExclusionsConfiguration dto, @Context Map<String, String> operationInfo,
      @Context Map<String, Dependency> dependencyInfo);

  @Mappings({
      @Mapping(target = "inclusionPath", source = "dto.inclusions"),
      @Mapping(target = "operationName", source = "dto", qualifiedByName = "getOperationName"),
      @Mapping(target = "dependencyName", source = "dto", qualifiedByName = "getDependencyName"),
      @Mapping(target = "dependencyType", source = "dto", qualifiedByName = "getDependencyType"),
      @Mapping(target = "expirationDate", expression = "java(dto.getExpirationDate() != null ? dto.getExpirationDate().getTime() : null)"),
  })
  PageQueryComparisonResponseType.InclusionInfo contractFromDto(
      ComparisonInclusionsConfiguration dto, @Context Map<String, String> operationInfo,
      @Context Map<String, Dependency> dependencyInfo);


  @Mappings({
      @Mapping(target = "operationName", source = "dto", qualifiedByName = "getOperationName"),
      @Mapping(target = "dependencyName", source = "dto", qualifiedByName = "getDependencyName"),
      @Mapping(target = "dependencyType", source = "dto", qualifiedByName = "getDependencyType"),
      @Mapping(target = "expirationDate", expression = "java(dto.getExpirationDate() != null ? dto.getExpirationDate().getTime() : null)"),
  })
  PageQueryComparisonResponseType.IgnoreCategoryInfo contractFromDto(
      ComparisonIgnoreCategoryConfiguration dto, @Context Map<String, String> operationInfo,
      @Context Map<String, Dependency> dependencyInfo);

  @Mappings({
      @Mapping(target = "operationName", source = "dto", qualifiedByName = "getOperationName"),
      @Mapping(target = "dependencyName", source = "dto", qualifiedByName = "getDependencyName"),
      @Mapping(target = "dependencyType", source = "dto", qualifiedByName = "getDependencyType"),
      @Mapping(target = "expirationDate", expression = "java(dto.getExpirationDate() != null ? dto.getExpirationDate().getTime() : null)"),
  })
  PageQueryComparisonResponseType.ListSortInfo contractFromDto(
      ComparisonListSortConfiguration dto, @Context Map<String, String> operationInfo,
      @Context Map<String, Dependency> dependencyInfo);

  @Mappings({
      @Mapping(target = "operationName", source = "dto", qualifiedByName = "getOperationName"),
      @Mapping(target = "dependencyName", source = "dto", qualifiedByName = "getDependencyName"),
      @Mapping(target = "dependencyType", source = "dto", qualifiedByName = "getDependencyType"),
      @Mapping(target = "expirationDate", expression = "java(dto.getExpirationDate() != null ? dto.getExpirationDate().getTime() : null)"),
  })
  PageQueryComparisonResponseType.ReferenceInfo contractFromDto(
      ComparisonReferenceConfiguration dto, @Context Map<String, String> operationInfo,
      @Context Map<String, Dependency> dependencyInfo);

  @Mappings({
      @Mapping(target = "transformMethodName", source = "dto.transformDetail", qualifiedByName = "toTransformMethodName"),
      @Mapping(target = "operationName", source = "dto", qualifiedByName = "getOperationName"),
      @Mapping(target = "dependencyName", source = "dto", qualifiedByName = "getDependencyName"),
      @Mapping(target = "dependencyType", source = "dto", qualifiedByName = "getDependencyType"),
      @Mapping(target = "expirationDate", expression = "java(dto.getExpirationDate() != null ? dto.getExpirationDate().getTime() : null)"),
  })
  PageQueryComparisonResponseType.RootTransformInfo contractFromDto(
      ComparisonTransformConfiguration dto, @Context Map<String, String> operationInfo,
      @Context Map<String, Dependency> dependencyInfo);

  @Mappings({
      @Mapping(target = "operationName", source = "dto", qualifiedByName = "getOperationName"),
      @Mapping(target = "dependencyName", source = "dto", qualifiedByName = "getDependencyName"),
      @Mapping(target = "dependencyType", source = "dto", qualifiedByName = "getDependencyType"),
      @Mapping(target = "expirationDate", expression = "java(dto.getExpirationDate() != null ? dto.getExpirationDate().getTime() : null)"),
  })
  PageQueryComparisonResponseType.ScriptInfo contractFromDto(
      ComparisonScriptConfiguration dto, @Context Map<String, String> operationInfo,
      @Context Map<String, Dependency> dependencyInfo);



  @Named("getOperationName")
  default <T extends AbstractComparisonDetailsConfiguration> String getOperationName(T dto,
      @Context Map<String, String> operationInfo) {
    return operationInfo.get(dto.getOperationId());
  }

  @Named("getDependencyName")
  default <T extends AbstractComparisonDetailsConfiguration> String getDependencyName(T dto,
      @Context Map<String, Dependency> dependencyInfo) {
    String dependencyId = dto.getDependencyId();
    if (dependencyId == null) {
      return null;
    }
    Dependency dependency = dependencyInfo.get(dependencyId);
    if (dependency == null) {
      return null;
    }
    return dependency.getOperationName();
  }

  @Named("getDependencyType")
  default <T extends AbstractComparisonDetailsConfiguration> String getDependencyType(T dto,
      @Context Map<String, Dependency> dependencyInfo) {
    String dependencyId = dto.getDependencyId();
    if (dependencyId == null) {
      return null;
    }
    Dependency dependency = dependencyInfo.get(dependencyId);
    if (dependency == null) {
      return null;
    }
    return dependency.getOperationType();
  }

  @Named("toTransformMethodName")
  default String toTransformMethodName(TransformDetail transformDetail) {
    return transformDetail.getTransformMethods().get(0).getMethodName();
  }


}
