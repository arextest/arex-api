package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.model.dao.mongodb.iosummary.CaseSummaryCollection;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.mapper.CaseSummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Component
public class CaseSummaryRepositoryImpl implements CaseSummaryRepository {

    private static final String PLAN_ITEM_ID = "planItemId";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean save(CaseSummary summary) {
        CaseSummaryCollection caseSummaryCollection = CaseSummaryMapper.INSTANCE.daoFromDto(summary);
        CaseSummaryCollection insert = mongoTemplate.insert(caseSummaryCollection);
        return insert.getId() != null;
    }

    @Override
    public List<CaseSummary> query(String planItemId) {
        Query query = Query.query(Criteria.where(PLAN_ITEM_ID).is(planItemId));
        List<CaseSummaryCollection> caseSummaryCollections = mongoTemplate.find(query, CaseSummaryCollection.class);
        return caseSummaryCollections
                .stream()
                .map(CaseSummaryMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }
}
