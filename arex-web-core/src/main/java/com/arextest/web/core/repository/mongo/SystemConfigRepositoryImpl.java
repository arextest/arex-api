package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.SystemConfigRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.SystemConfigCollection;
import com.arextest.web.model.contract.contracts.config.SystemConfig;
import com.arextest.web.model.mapper.SystemConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wildeslam.
 * @create 2023/9/25 18:51
 */
@Slf4j
@Repository
public class SystemConfigRepositoryImpl implements SystemConfigRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<SystemConfig> listSystemConfigs() {
        SystemConfigMapper systemConfigMapper = SystemConfigMapper.INSTANCE;
        return mongoTemplate.findAll(SystemConfigCollection.class).stream()
            .map(systemConfigMapper::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public SystemConfig queryByType(Integer systemConfigType) {
        Query query = new Query();
        Criteria criteria = Criteria.where(SystemConfigCollection.Fields.configType).is(systemConfigType);
        query.addCriteria(criteria);

        return SystemConfigMapper.INSTANCE.dtoFromDao(mongoTemplate.findOne(query, SystemConfigCollection.class));
    }

    @Override
    public boolean saveList(List<SystemConfig> systemConfigDtos) {
        if (CollectionUtils.isEmpty(systemConfigDtos)) {
            return false;
        }
        try {
            BulkOperations bulkOperations =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SystemConfigCollection.class);
            for (SystemConfig systemConfig : systemConfigDtos) {
                Update update = MongoHelper.getUpdate();
                MongoHelper.appendFullProperties(update, systemConfig);

                Query query = Query.query(Criteria.where(SystemConfigCollection.Fields.configType)
                    .is(systemConfig.getConfigType()));
                bulkOperations.upsert(query, update);
            }
            bulkOperations.execute();
        } catch (Exception e) {
            LogUtils.error(LOGGER, "exclusion saveList failed! list:{}, exception:{}", systemConfigDtos, e);
            return false;
        }
        return true;
    }
}
