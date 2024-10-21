package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.LabelRepository;
import com.arextest.web.model.dao.mongodb.LabelCollection;
import com.arextest.web.model.dto.LabelDto;
import com.arextest.web.model.mapper.LabelMapper;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2022/11/17
 */
@Slf4j
@Component
public class LabelRepositoryImpl implements LabelRepository {

  private static String WORKSPACE_ID = "workspaceId";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public boolean saveLabel(LabelDto dto) {
    LabelCollection dao = LabelMapper.INSTANCE.daoFromDto(dto);
    try {
      mongoTemplate.save(dao);
      return true;
    } catch (Exception e) {
      LogUtils.error(LOGGER, "Failed to save label.", e);
      return false;
    }
  }

  @Override
  public boolean removeLabel(String labelId) {
    Query query = Query.query(Criteria.where(DASH_ID).is(labelId));
    DeleteResult deleteResult = mongoTemplate.remove(query, LabelCollection.class);
    return deleteResult.getDeletedCount() > 0;
  }

  @Override
  public List<LabelDto> queryLabelsByWorkspaceId(String workspaceId) {
    Query query = new Query();
    query.addCriteria(new Criteria().orOperator(Criteria.where(WORKSPACE_ID).is(workspaceId),
        Criteria.where(WORKSPACE_ID).isNull()));
    List<LabelCollection> daos = mongoTemplate.find(query, LabelCollection.class);
    return daos.stream().map(LabelMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }
}
