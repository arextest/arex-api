package com.arextest.web.api.service.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReplicatedServersConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author b_yu
 * @since 2023/4/6
 */
@Slf4j
@Configuration
public class RedisConfiguration {
    private final static String USERNAME_AND_PASSWORD_REGEX = "redis://(.*?)@(.*?)";
    private final static String DATABASE_REGEX = "redis://(.*?)/(.*?)";
    @Value("${arex.redis.uri}")
    private String redisUri;

    @Bean
    public RedissonClient redissonClient() {
        return createRedissonClientByAnalyze(redisUri);
    }

    private RedissonClient createRedissonClientByAnalyze(String redisUri) {
        Config config = new Config();
        ReplicatedServersConfig replicatedServersConfig = config.useReplicatedServers().setScanInterval(2000);
        List<String> redisHostAndPort = getRedisHostAndPort(redisUri);
        replicatedServersConfig.addNodeAddress(redisHostAndPort.toArray(new String[0]));

        Pair<String, String> userNameAndPassword = getUserNameAndPassword(redisUri);
        String user = userNameAndPassword.getKey();
        String password = userNameAndPassword.getValue();
        if (StringUtils.isNotEmpty(user)) {
            replicatedServersConfig.setUsername(user);
        }
        if (StringUtils.isNotEmpty(password)) {
            replicatedServersConfig.setPassword(password);
        }

        Integer dataBase = getDataBase(redisUri);
        if (dataBase != null) {
            replicatedServersConfig.setDatabase(dataBase);
        }
        return Redisson.create(config);
    }

    private List<String> getRedisHostAndPort(String redisUri) {
        List<String> result = new ArrayList<>();
        String substring = null;
        if (redisUri.contains("@")) {
            int i = redisUri.indexOf('@');
            if (i != -1) {
                substring = redisUri.substring(i + 1);
            }
        } else {
            int i = redisUri.indexOf("redis://");
            if (i != -1) {
                substring = redisUri.substring(i + 7 + 1);
            }
        }

        if (substring == null) {
            return result;
        }
        String[] split = substring.split("/");
        String hostAndPortStr = split[0];
        String[] hostAndPortArr = hostAndPortStr.split(",");
        for (String hostAndPort : hostAndPortArr) {
            result.add("redis://" + hostAndPort);
        }
        return result;
    }

    private Pair<String, String> getUserNameAndPassword(String redisUri) {
        String user = "";
        String password = "";
        Pattern pattern = Pattern.compile(USERNAME_AND_PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(redisUri);
        if (matcher.matches()) {
            String group = matcher.group(1);
            String[] split = group.split(":");
            user = split[0];
            password = split[1];
        }
        return new MutablePair<>(user, password);
    }

    private Integer getDataBase(String redisUri) {
        Integer database = null;
        Pattern pattern = Pattern.compile(DATABASE_REGEX);
        Matcher matcher = pattern.matcher(redisUri);
        if (matcher.matches()) {
            String group = matcher.group(2);
            if (StringUtils.isNotBlank(group)) {
                database = Integer.valueOf(group);
            }
        }
        return database;
    }
}
