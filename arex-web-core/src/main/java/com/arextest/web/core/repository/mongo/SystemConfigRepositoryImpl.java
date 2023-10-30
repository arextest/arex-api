package com.arextest.web.core.repository.mongo;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.arextest.web.core.repository.SystemConfigRepository;
import com.arextest.web.model.contract.contracts.config.SystemConfig;
import com.arextest.web.model.dao.mongodb.ModelBase;
import com.arextest.web.model.dao.mongodb.SystemConfigCollection;
import com.arextest.web.model.mapper.SystemConfigMapper;

import lombok.extern.slf4j.Slf4j;

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
    public SystemConfig getLatestSystemConfig() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, ModelBase.Fields.dataChangeCreateTime));
        query.limit(1);
        SystemConfigCollection systemConfigCollection = mongoTemplate.findOne(query, SystemConfigCollection.class);
        return SystemConfigMapper.INSTANCE.dtoFromDao(systemConfigCollection);
    }

    @Override
    public boolean saveConfig(SystemConfig systemConfig) {
        mongoTemplate.save(SystemConfigMapper.INSTANCE.daoFromDto(systemConfig));
        return true;
    }
}
