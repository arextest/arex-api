package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ServletMockerRepository;
import com.arextest.web.model.dao.mongodb.ServletMockerCollection;
import com.arextest.web.model.dto.ServletMockerDto;
import com.arextest.web.model.mapper.ServletMockerMapper;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class ServletMockerRepositoryImpl implements ServletMockerRepository {

  private static final String INDEX = "index";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public List<ServletMockerDto> queryServletMockers(String index, Integer step) {
    List<ServletMockerCollection> result;
    if (StringUtils.isEmpty(index)) {
      Query query = Query.query(new Criteria()).with(Sort.by(Sort.Order.desc(DASH_ID))).limit(step);
      result = mongoTemplate.find(query, ServletMockerCollection.class);
    } else {
      Query query = Query.query(Criteria.where(INDEX).gt(index)).limit(step);
      query.with(Sort.by(Sort.Order.asc(DASH_ID)));
      result = mongoTemplate.find(query, ServletMockerCollection.class);
    }

    return result.stream().map(ServletMockerMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }
}
