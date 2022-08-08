package com.arextest.report.web.api.service.beans;

import com.arextest.report.common.EnvProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.Resource;


@Slf4j
@Configuration
public class MongodbConfiguration {

    @Resource
    private EnvProperty envProperty;

    @Bean
    public MongoDatabaseFactory mongoDbFactory() {
        try {
            return new SimpleMongoClientDatabaseFactory(envProperty.getString(EnvProperty.AREX_REPORT_MONGO_URI));
        } catch (Exception e) {
            LOGGER.error("cannot connect mongodb", e);
        }
        return null;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(this.mongoDbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(this.mongoDbFactory(), converter);
    }
}
