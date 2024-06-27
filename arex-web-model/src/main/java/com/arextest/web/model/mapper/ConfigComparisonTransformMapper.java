package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.compare.TransformDetail;
import com.arextest.web.model.contract.contracts.compare.TransformDetail.TransformMethod;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonRootTransformConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonTransformCollection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Mapper
public interface ConfigComparisonTransformMapper {

  ConfigComparisonTransformMapper INSTANCE = Mappers.getMapper(
      ConfigComparisonTransformMapper.class);

  String ROOT_NODE_PATH = "arex_root";

  @Mappings({@Mapping(target = "modifiedTime",
      expression = "java(dao.getDataChangeUpdateTime() == null ? null : new java.sql.Timestamp(dao.getDataChangeUpdateTime()))")})
  ComparisonTransformConfiguration dtoFromDao(ConfigComparisonTransformCollection dao);

  @Mappings({@Mapping(target = "id", expression = "java(null)"),
      @Mapping(target = "dataChangeCreateTime", expression = "java(System.currentTimeMillis())"),
      @Mapping(target = "dataChangeUpdateTime", expression = "java(System.currentTimeMillis())")})
  ConfigComparisonTransformCollection daoFromDto(ComparisonTransformConfiguration dto);

  @Mappings({
      @Mapping(target = "transformDetail", source = "transformMethodName", qualifiedByName = "toTransformDetail")
  })
  ComparisonTransformConfiguration dotFromRequestType(
      ComparisonRootTransformConfiguration requestType);


  @Mappings({
      @Mapping(target = "transformMethodName", source = "transformDetail", qualifiedByName = "toTransformMethodName")
  })
  ComparisonRootTransformConfiguration requestTypeFromDto(ComparisonTransformConfiguration dto);

  @Named("toTransformDetail")
  default TransformDetail toTransformDetail(String transformMethodName) {
    TransformDetail transformDetail = new TransformDetail();
    transformDetail.setNodePath(Collections.singletonList(ROOT_NODE_PATH));
    TransformMethod transformMethod = new TransformMethod();
    transformMethod.setMethodName(transformMethodName);
    transformDetail.setTransformMethods(Collections.singletonList(transformMethod));
    return transformDetail;
  }

  @Named("toTransformMethodName")
  default String toTransformMethodName(TransformDetail transformDetail) {
    List<String> nodePath = transformDetail.getNodePath();
    List<TransformMethod> transformMethods = transformDetail.getTransformMethods();
    if (CollectionUtils.isNotEmpty(nodePath) && nodePath.contains(ROOT_NODE_PATH)
        && CollectionUtils.isNotEmpty(transformMethods)) {
      return transformMethods.get(0).getMethodName();
    }
    return null;
  }


}