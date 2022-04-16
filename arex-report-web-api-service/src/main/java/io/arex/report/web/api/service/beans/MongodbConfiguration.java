package io.arex.report.web.api.service.beans;

import com.mongodb.MongoClientURI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;


@Slf4j
@Configuration
public class MongodbConfiguration {
    @Value("${mongo.uri}")
    private String mongoUrl;

    
    @Bean
    public MongoDbFactory mongoDbFactory() {
        try {
            MongoClientURI uri = new MongoClientURI(mongoUrl);
            return new SimpleMongoDbFactory(uri);
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
