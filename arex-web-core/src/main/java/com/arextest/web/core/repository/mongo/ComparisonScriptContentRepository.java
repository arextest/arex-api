package com.arextest.web.core.repository.mongo;

import com.arextest.web.model.dao.mongodb.ComparisonScriptContentCollection;
import com.arextest.web.model.dto.config.ComparisonScriptContent;
import com.arextest.web.model.mapper.ConfigComparisonScriptContentMapper;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ComparisonScriptContentRepository {

  private final MongoTemplate mongoTemplate;


  public List<String> queryAllScriptMethodNames() {
    Query query = new Query();
    query.fields().include(ComparisonScriptContentCollection.Fields.aliasName);
    List<ComparisonScriptContentCollection> daos = mongoTemplate.find(query,
        ComparisonScriptContentCollection.class);

    if (CollectionUtils.isNotEmpty(daos)) {
      return daos.stream()
          .map(ComparisonScriptContentCollection::getAliasName)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public boolean save(ComparisonScriptContent dto) {
    ComparisonScriptContentCollection dao =
        ConfigComparisonScriptContentMapper.INSTANCE.daoFromDto(dto);
    mongoTemplate.save(dao);
    return true;
  }

  public List<ComparisonScriptContent> queryAll() {
    List<ComparisonScriptContentCollection> daos = mongoTemplate.findAll(
        ComparisonScriptContentCollection.class);

    if (CollectionUtils.isNotEmpty(daos)) {
      return daos.stream()
          .map(ConfigComparisonScriptContentMapper.INSTANCE::dtoFromDao)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

}
