package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.mapper.AppContractMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AppContractRepositoryImpl implements AppContractRepository {
    private static final String OPERATION_ID = "operationId";
    private static final String OPERATION_NAME = "operationName";
    private static final String OPERATION_TYPE = "operationType";
    private static final String CONTRACT = "contract";
    private static final String IS_ENTRY = "isEntry";
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean update(List<AppContractDto> appContractDtos) {
        try {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
                    AppContractCollection.class);
            List<Pair<Query, Update>> updates = new ArrayList<>();
            for (AppContractDto appContractDto : appContractDtos) {
                AppContractCollection collection = AppContractMapper.INSTANCE.daoFromDto(appContractDto);
                Query query = new Query();
                if (appContractDto.getId() != null) {
                    query.addCriteria(Criteria.where(DASH_ID).is(collection.getId()));
                } else if (appContractDto.getOperationId() != null) {
                    query.addCriteria(Criteria.where(OPERATION_ID).is(appContractDto.getOperationId())
                            .and(IS_ENTRY).is(true));
                }

                Update update = new Update();
                MongoHelper.appendFullProperties(update, collection);
                updates.add(Pair.of(query, update));
            }
            bulkOperations.updateMulti(updates);
            bulkOperations.execute();
        } catch (Exception e) {
            LogUtils.error(LOGGER, "updateById failed! list:{}", appContractDtos, e);
            return false;
        }
        return true;
    }

    @Override
    public List<AppContractDto> insert(List<AppContractDto> appContractDtos) {
        return mongoTemplate.insertAll(new ArrayList<>(appContractDtos.stream().map(AppContractMapper.INSTANCE::daoFromDto).collect(Collectors.toList())))
                .stream().map(AppContractMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }


    @Override
    public List<AppContractDto> queryAppContractListByOpId(List<String> operationList, List<String> filterFields) {
        Query query = new Query().addCriteria(Criteria.where(OPERATION_ID).in(operationList));

        if (CollectionUtils.isNotEmpty(filterFields)) {
            for (String filterField : filterFields) {
                query.fields().exclude(filterField);
            }
        }

        return mongoTemplate.find(query, AppContractCollection.class)
                .stream()
                .map(AppContractMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public AppContractDto queryEntryPointContract(String operationId) {
        Query query = new Query().addCriteria(Criteria.where(OPERATION_ID).is(operationId).and(IS_ENTRY).is(true));

        return AppContractMapper.INSTANCE.dtoFromDao(mongoTemplate.findOne(query, AppContractCollection.class));
    }

    @Override
    public AppContractDto queryById(String id) {
        Query query = new Query().addCriteria(Criteria.where(DASH_ID).is(id));
        return AppContractMapper.INSTANCE.dtoFromDao(mongoTemplate.findOne(query, AppContractCollection.class));
    }
}
