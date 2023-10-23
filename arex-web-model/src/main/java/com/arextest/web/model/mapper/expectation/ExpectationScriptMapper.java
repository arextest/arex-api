package com.arextest.web.model.mapper.expectation;

import com.arextest.web.model.contract.contracts.config.expectation.ExpectationScriptModel;
import com.arextest.web.model.dao.mongodb.expectation.ExpectationScriptEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExpectationScriptMapper {
    ExpectationScriptMapper INSTANCE = Mappers.getMapper(ExpectationScriptMapper.class);
    ExpectationScriptEntity toEntity(ExpectationScriptModel model);
    ExpectationScriptModel toModel(ExpectationScriptEntity entity);
    List<ExpectationScriptEntity> toEntityList(List<ExpectationScriptModel> modelList);
    List<ExpectationScriptModel> toModelList(List<ExpectationScriptEntity> entityList);
}
