package com.arextest.web.api.service.beans;

import com.arextest.web.common.LogUtils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

@Slf4j
@Configuration
public class MongodbConfiguration {

  private static final String APP_ID = "appId";
  private static final String DATE = "date";
  private static final String AREX_STORAGE_DB = "arex_storage_db";
  @Value("${arex.mongo.uri}")
  private String mongoUrl;

  public MongoDatabaseFactory mongoDbFactory() {
    try {
      ConnectionString connectionString = new ConnectionString(mongoUrl);
      String dbName = connectionString.getDatabase();
      if (dbName == null) {
        dbName = AREX_STORAGE_DB;
      }

      CodecRegistry pojoCodecRegistry =
          CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
              CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

      MongoClientSettings settings = MongoClientSettings.builder()
          .applyConnectionString(connectionString)
          .codecRegistry(pojoCodecRegistry).build();
      MongoClient mongoClient = MongoClients.create(settings);
      return new SimpleMongoClientDatabaseFactory(mongoClient, dbName);
    } catch (Exception e) {
      LogUtils.error(LOGGER, "cannot connect mongodb", e);
    }
    return null;
  }

  @Bean
  @ConditionalOnMissingBean
  public MongoTemplate mongoTemplate() {
    DbRefResolver dbRefResolver = new DefaultDbRefResolver(this.mongoDbFactory());
    MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver,
        new MongoMappingContext());
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    MongoTemplate template = new MongoTemplate(this.mongoDbFactory(), converter);
    return template;
  }
}
