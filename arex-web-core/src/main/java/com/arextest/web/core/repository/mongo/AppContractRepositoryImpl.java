package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.mapper.ApplicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AppContractRepositoryImpl implements AppContractRepository {
    private static final String OPERATION_ID = "operationId";
    private static final String OPERATION_NAME = "operationName";
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean saveAppContractList(List<AppContractDto> applicationInfoDtos) {
        try {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
                    AppContractCollection.class);
            for (AppContractDto applicationInfoDto : applicationInfoDtos) {
                AppContractCollection collection = ApplicationMapper.INSTANCE.daoFromDto(applicationInfoDto);
                Query query = new Query()
                        .addCriteria(Criteria.where(OPERATION_ID).is(collection.getOperationId()))
                        .addCriteria(Criteria.where(OPERATION_NAME).is(collection.getOperationName()));
                Update update = MongoHelper.getUpdate();
                MongoHelper.appendFullProperties(update, collection);
                bulkOperations.upsert(query, update);
            }
            bulkOperations.execute();
        } catch (Exception e) {
            LOGGER.error("saveApplicationList failed! list:{}", applicationInfoDtos);
            return false;
        }
        return true;
    }

    @Override
    public List<AppContractDto> queryAppContractList(String operationId) {
        Query query = new Query().addCriteria(Criteria.where(OPERATION_ID).is(operationId));

        return mongoTemplate.find(query, AppContractCollection.class)
                .stream()
                .map(ApplicationMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }
}
