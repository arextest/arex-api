package com.arextest.web.api.service.beans;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author b_yu
 * @since 2023/4/6
 */
@Configuration
public class RedisConfiguration {
    @Value("${arex.redis.uri}")
    private String redisUri;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useReplicatedServers().setScanInterval(2000).addNodeAddress(redisUri.split(","));
        return Redisson.create(config);
    }
}
