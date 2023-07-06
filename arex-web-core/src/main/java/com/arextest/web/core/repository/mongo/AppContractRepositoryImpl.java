package com.arextest.web.core.repository.mongo;

import cn.hutool.core.util.BooleanUtil;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.util.SchemaUtils;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.mapper.AppContractMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AppContractRepositoryImpl implements AppContractRepository {
    private static final String OPERATION_ID = "operationId";
    private static final String OPERATION_NAME = "operationName";
    private static final String OPERATION_TYPE = "operationType";
    private static final String CONTRACT = "contract";
    private static final String NULL_STRING = "null";
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean upsertAppContractList(List<AppContractDto> appContractDtos) {
        try {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
                    AppContractCollection.class);
            for (AppContractDto appContractDto : appContractDtos) {
                AppContractCollection collection = AppContractMapper.INSTANCE.daoFromDto(appContractDto);
                Query query = new Query()
                        .addCriteria(Criteria.where(OPERATION_ID).is(collection.getOperationId()))
                        .addCriteria(Criteria.where(OPERATION_NAME).is(collection.getOperationName()));
                if (BooleanUtil.isFalse(appContractDto.getIsEntryPoint())) {
                    query.addCriteria(Criteria.where(OPERATION_TYPE).is(collection.getOperationType()));
                }
                Update update = MongoHelper.getUpdate();
                MongoHelper.appendFullProperties(update, collection);
                bulkOperations.upsert(query, update);
            }
            bulkOperations.execute();
        } catch (Exception e) {
            LogUtils.error(LOGGER, "saveApplicationList failed! list:{}", appContractDtos, e);
            return false;
        }
        return true;
    }

    @Override
    public List<AppContractDto> upsertAppContractListWithResult(List<AppContractDto> appContractDtos) {
        try {
            // query
            Query query = new Query();
            List<Criteria> orCriteriaList = new ArrayList<>();
            for (AppContractDto appContractDto : appContractDtos) {
                Criteria criteria = Criteria.where(OPERATION_ID).is(appContractDto.getOperationId())
                        .and(OPERATION_NAME).is(appContractDto.getOperationName());
                if (BooleanUtil.isFalse(appContractDto.getIsEntryPoint())) {
                    criteria.and(OPERATION_TYPE).is(appContractDto.getOperationType());
                }
                orCriteriaList.add(criteria);
            }
            query.addCriteria(new Criteria().orOperator(orCriteriaList));

            // type-name : id
            Map<Pair<String, String>, AppContractCollection> existedMap =
                    mongoTemplate.find(query, AppContractCollection.class).stream()
                            .collect(Collectors.toMap(
                                    item -> new ImmutablePair<>(item.getOperationType(), item.getOperationName()),
                                    Function.identity()));

            // separate updates and inserts
            List<AppContractDto> updates = new ArrayList<>();
            List<AppContractDto> inserts = new ArrayList<>();
            for (AppContractDto item : appContractDtos) {
                Pair<String, String> pair = new ImmutablePair<>(item.getOperationType(), item.getOperationName());
                if (existedMap.containsKey(pair)) {
                    item.setId(existedMap.get(pair).getId());
                    updates.add(item);
                } else {
                    inserts.add(item);
                }
            }
            // update existed document
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
                    AppContractCollection.class);
            for (AppContractDto appContractDto : updates) {
                Update update = new Update();
                update.set(DATA_CHANGE_UPDATE_TIME, System.currentTimeMillis());
                // update old contract
                String oldContract = existedMap.get(new ImmutablePair<>(appContractDto.getOperationType(),
                        appContractDto.getOperationName())).getContract();
                if (!StringUtils.equals(oldContract, appContractDto.getContract())) {
                    if (oldContract != null && !oldContract.equals(NULL_STRING)) {
                        String newContract = SchemaUtils.mergeJson(oldContract, appContractDto.getContract());
                        update.set(CONTRACT, newContract);
                    } else {
                        update.set(CONTRACT, appContractDto.getContract());
                    }
                }
                bulkOperations.upsert(new Query(Criteria.where(DASH_ID).is(appContractDto.getId())), update);
            }
            bulkOperations.execute();

            // insert new document
            if (CollectionUtils.isNotEmpty(inserts)) {
                List<AppContractDto> insertResults = mongoTemplate.insertAll(
                                inserts.stream().map(AppContractMapper.INSTANCE::daoFromDto).collect(Collectors.toList()))
                        .stream().map(AppContractMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
                updates.addAll(insertResults);
            }
            return updates;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "upsertAppContractListWithResult failed! list:{}", appContractDtos, e);
            return new ArrayList<>();
        }
    }


    @Override
    public List<AppContractDto> queryAppContractList(String operationId) {
        Query query = new Query().addCriteria(Criteria.where(OPERATION_ID).is(operationId));

        return mongoTemplate.find(query, AppContractCollection.class)
                .stream()
                .map(AppContractMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public AppContractDto queryById(String id) {
        Query query = new Query().addCriteria(Criteria.where(DASH_ID).is(id));
        return AppContractMapper.INSTANCE.dtoFromDao(mongoTemplate.findOne(query, AppContractCollection.class));
    }
}
