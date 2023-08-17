package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dao.mongodb.iosummary.CaseSummaryCollection;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.mapper.CaseSummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Component
public class CaseSummaryRepositoryImpl implements CaseSummaryRepository {

    private static final String PLAN_ID = "planId";

    private static final String PLAN_ITEM_ID = "planItemId";

    private static final String RECORD_ID = "recordId";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean save(CaseSummary summary) {
        CaseSummaryCollection caseSummaryCollection = CaseSummaryMapper.INSTANCE.daoFromDto(summary);
        CaseSummaryCollection insert = mongoTemplate.insert(caseSummaryCollection);
        return insert.getId() != null;
    }

    @Override
    public boolean upsert(CaseSummary summary) {
        CaseSummaryCollection caseSummaryCollection = CaseSummaryMapper.INSTANCE.daoFromDto(summary);
        Query query = Query.query(Criteria.where(PLAN_ID).is(summary.getPlanId())
            .and(PLAN_ITEM_ID).is(summary.getPlanItemId())
            .and(RECORD_ID).is(summary.getRecordId()));

        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, caseSummaryCollection);

        CaseSummaryCollection dao = mongoTemplate.findAndModify(query, update,
            FindAndModifyOptions.options().returnNew(true).upsert(true), CaseSummaryCollection.class);
    return dao != null;
    }

    @Override
    public List<CaseSummary> query(String planId, String planItemId) {
        Query query = Query.query(Criteria.where(PLAN_ID).is(planId)
            .and(PLAN_ITEM_ID).is(planItemId));
        List<CaseSummaryCollection> caseSummaryCollections = mongoTemplate.find(query, CaseSummaryCollection.class);
        return caseSummaryCollections
            .stream()
            .map(CaseSummaryMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }
}
