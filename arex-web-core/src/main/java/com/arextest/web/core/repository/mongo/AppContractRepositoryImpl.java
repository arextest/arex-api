package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import com.arextest.web.model.mapper.AppContractMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
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
    private static final String APP_ID = "appId";
    private static final String CONTRACT_TYPE = "contractType";
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
                    query.addCriteria(Criteria.where(OPERATION_ID).is(appContractDto.getOperationId()));
                } else if (appContractDto.getAppId() != null) {
                    query.addCriteria(Criteria.where(APP_ID).is(appContractDto.getAppId()));
                }
                query.addCriteria(Criteria.where(CONTRACT_TYPE).is(appContractDto.getContractType()));

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
    public boolean upsert(AppContractDto appContractDto) {
        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, appContractDto);
        AppContractCollection collection = AppContractMapper.INSTANCE.daoFromDto(appContractDto);

        Query query = new Query();
        if (appContractDto.getId() != null) {
            query.addCriteria(Criteria.where(DASH_ID).is(collection.getId()));
        } else if (appContractDto.getOperationId() != null) {
            query.addCriteria(Criteria.where(OPERATION_ID).is(appContractDto.getOperationId()));
        } else if (appContractDto.getAppId() != null) {
            query.addCriteria(Criteria.where(APP_ID).is(appContractDto.getAppId()));
        }
        query.addCriteria(Criteria.where(CONTRACT_TYPE).is(appContractDto.getContractType()));

        AppContractCollection dao = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                AppContractCollection.class);
        return dao != null;
    }

    @Override
    public List<AppContractDto> insert(List<AppContractDto> appContractDtos) {
        return mongoTemplate.insertAll(new ArrayList<>(appContractDtos.stream().map(AppContractMapper.INSTANCE::daoFromDto).collect(Collectors.toList())))
                .stream().map(AppContractMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }


    @Override
    public List<AppContractDto> queryAppContractListByOpId(String operationId) {
        Query query = new Query().addCriteria(Criteria.where(OPERATION_ID).is(operationId));

        return mongoTemplate.find(query, AppContractCollection.class)
                .stream()
                .map(AppContractMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public AppContractDto queryAppContractByType(String id, Integer contractType) {
        Query query = new Query();
        String idFieldName;
        switch (ContractTypeEnum.from(contractType)) {
            case GLOBAL:
                idFieldName = APP_ID;
                break;
            case ENTRY:
                idFieldName = OPERATION_ID;
                break;
            case DEPENDENCY:
                idFieldName = DASH_ID;
                break;
            default:
                return null;
        }
        query.addCriteria(Criteria.where(CONTRACT_TYPE).is(contractType).and(idFieldName).is(id));
        return AppContractMapper.INSTANCE.dtoFromDao(mongoTemplate.findOne(query, AppContractCollection.class));
    }

    @Override
    public AppContractDto queryById(String id) {
        Query query = new Query().addCriteria(Criteria.where(DASH_ID).is(id));
        return AppContractMapper.INSTANCE.dtoFromDao(mongoTemplate.findOne(query, AppContractCollection.class));
    }
}
