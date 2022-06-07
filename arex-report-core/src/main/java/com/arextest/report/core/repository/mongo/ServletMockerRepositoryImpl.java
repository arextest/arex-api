package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.ServletMockerRepository;
import com.arextest.report.model.dao.mongodb.ServletMockerCollection;
import com.arextest.report.model.dto.ServletMockerDto;
import com.arextest.report.model.mapper.ServletMockerMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServletMockerRepositoryImpl implements ServletMockerRepository {
    private static final String INDEX = "index";
    private static final String DASH_ID = "_id";

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

        return result.stream().map(ServletMockerMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }
}
