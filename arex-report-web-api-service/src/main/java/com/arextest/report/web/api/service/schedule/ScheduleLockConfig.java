package com.arextest.report.web.api.service.schedule;

import com.mongodb.client.MongoCollection;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

/**
 * @author b_yu
 * @since 2022/6/6
 */
@Configuration
public class ScheduleLockConfig {
    private static final String TIME_WORK = "ScheduleLock";

    @Resource
    private MongoTemplate mongoTemplate;

    @Bean
    public LockProvider lockProvider() {
        MongoCollection<Document> collection = mongoTemplate.getCollection(TIME_WORK);
        return new MongoLockProvider(collection);
    }
}
