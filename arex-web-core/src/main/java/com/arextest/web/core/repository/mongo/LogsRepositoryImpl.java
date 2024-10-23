package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.LogsRepository;
import com.arextest.web.model.dao.mongodb.LogsCollection;
import com.arextest.web.model.dto.LogsDto;
import com.arextest.web.model.mapper.LogsMapper;
import com.arextest.web.model.params.QueryLogsParam;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Component
public class LogsRepositoryImpl implements LogsRepository {

  private static final String LEVEL = "level";
  private static final String MILLIS = "millis";
  private static final String CONTEXT_MAP_DOT = "contextMap.";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public List<LogsDto> queryLogs(QueryLogsParam param) {
    Query query = new Query();
    query.with(Sort.by(Sort.Order.desc(DASH_ID))).limit(param.getPageSize());

    if (StringUtils.isNotBlank(param.getPreviousId())) {
      query.addCriteria(new Criteria(DASH_ID).lt(new ObjectId(param.getPreviousId())));
    }
    if (StringUtils.isNotBlank(param.getLevel())) {
      query.addCriteria(new Criteria(LEVEL).is(param.getLevel()));
    }
    if (param.getStartTime() != null && param.getEndTime() != null) {
      query.addCriteria(new Criteria(MILLIS).gt(param.getStartTime()).lt(param.getEndTime()));
    }
    if (MapUtils.isNotEmpty(param.getTags())) {
      param.getTags().forEach((k, v) -> query.addCriteria(new Criteria(CONTEXT_MAP_DOT + k).is(v)));
    }
    List<LogsCollection> daos = mongoTemplate.find(query, LogsCollection.class);
    return daos.stream().map(LogsMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }
}
