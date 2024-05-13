package com.arextest.web.model.mapper.expectation;

import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.expectation.ScriptExtractOperationModel;
import com.arextest.web.model.dao.mongodb.expectation.ExpectationScriptCollection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ExpectationScriptMapper {
    ExpectationScriptMapper INSTANCE = Mappers.getMapper(ExpectationScriptMapper.class);
    ExpectationScriptCollection toCollection(ExpectationScriptModel model);
    ExpectationScriptModel toModel(ExpectationScriptCollection entity);
    ScriptExtractOperationModel toModel(ExpectationScriptCollection.ScriptExtractOperationCollection entity);
    ExpectationScriptCollection.ScriptExtractOperationCollection toCollection(ScriptExtractOperationModel model);
    List<ExpectationScriptCollection> toCollectionList(List<ExpectationScriptModel> modelList);
    List<ExpectationScriptModel> toModelList(List<ExpectationScriptCollection> collectionList);
}
