package com.arextest.web.core.repository.expectation;

import com.arextest.web.core.repository.RepositoryProvider;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptQueryRequest;
import com.arextest.web.model.dao.mongodb.expectation.ExpectationScriptCollection;
import com.arextest.web.model.dao.mongodb.expectation.ExpectationScriptCollection.Fields;
import com.arextest.web.model.mapper.expectation.ExpectationScriptMapper;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class ExpectationScriptRepository implements RepositoryProvider {
    @Resource
    private MongoTemplate mongoTemplate;

    public boolean insert(ExpectationScriptModel model) {
        ExpectationScriptCollection entity = ExpectationScriptMapper.INSTANCE.toCollection(model);
        if (entity.getExpirationTime() == null) {
            // 2099-01-01 12:00:00
            entity.setExpirationTime(4070923200000L);
        }
        entity.setValid(BooleanUtils.toBooleanDefaultIfNull(model.getValid(), true));
        entity.setDataChangeCreateTime(System.currentTimeMillis());
        entity.setDataChangeUpdateTime(entity.getDataChangeCreateTime());
        mongoTemplate.insert(entity);
        return StringUtils.isNotBlank(entity.getId());
    }
    public boolean update(ExpectationScriptModel model) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DASH_ID).is(model.getId()));
        query.addCriteria(Criteria.where(Fields.appId).is(model.getAppId()));
        query.addCriteria(Criteria.where(DATA_CHANGE_UPDATE_TIME).is(model.getDataChangeUpdateTime()));

        Update update = getConfigUpdate();
        update.set(Fields.content, model.getContent());
        update.set(Fields.normalizedContent, model.getNormalizedContent());
        update.set(Fields.extractOperationList, model.getExtractOperationList());
        update.set(Fields.valid, BooleanUtils.toBooleanDefaultIfNull(model.getValid(), true));
        if (model.getExpirationTime() != null) {
            update.set(Fields.expirationTime, model.getExpirationTime());
        }
        update.set(Fields.scope, model.getScope());
        update.set(Fields.dataChangeUpdateBy, model.getDataChangeUpdateBy());

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ExpectationScriptCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    public boolean delete(String id, String appId) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DASH_ID).is(id));
        query.addCriteria(Criteria.where(Fields.appId).is(appId));
        return mongoTemplate.remove(query, ExpectationScriptCollection.class).getDeletedCount() > 0;
    }

    public List<ExpectationScriptModel> query(ExpectationScriptQueryRequest request){
        Query query = new Query();
        query.addCriteria(Criteria.where(Fields.appId).is(request.getAppId()));
        if (StringUtils.isNotEmpty(request.getOperationId())) {
            query.addCriteria(Criteria.where(Fields.operationId).is(request.getOperationId()));
        }
        if (request.getValid() != null) {
            query.addCriteria(Criteria.where(Fields.valid).is(request.getValid()));
        }
        if (request.getExpirationTime() != null) {
            query.addCriteria(Criteria.where(Fields.expirationTime).lte(request.getExpirationTime()));
        }
        if (request.getScope() != null) {
            query.addCriteria(Criteria.where(Fields.scope).is(request.getScope()));
        }
        List<ExpectationScriptCollection> collectionList = mongoTemplate.find(query, ExpectationScriptCollection.class);
        return ExpectationScriptMapper.INSTANCE.toModelList(collectionList);
    }
}
