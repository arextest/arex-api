package com.arextest.web.api.service.beans;

import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author b_yu
 * @since 2023/4/6
 */
@Slf4j
@Configuration
public class RedisConfiguration {
    @Value("${arex.redis.uri}")
    private String redisUri;

    @Bean
    public RedissonClient redissonClient() {
        return createRedissonClientByAnalyze(redisUri);
    }

    private RedissonClient createRedissonClientByAnalyze(String redisUri) {
        Config config = new Config();
        ReplicatedServersConfig replicatedServersConfig = config.useReplicatedServers().setScanInterval(2000);
        String[] redisHostAndPort = getRedisHostAndPort(redisUri);
        replicatedServersConfig.addNodeAddress(redisHostAndPort);

        Pair<String, String> userNameAndPassword = getUserNameAndPassword(redisUri);
        String user = userNameAndPassword.getKey();
        String password = userNameAndPassword.getValue();
        if (StringUtils.isNotEmpty(user)) {
            replicatedServersConfig.setUsername(user);
        }
        if (StringUtils.isNotEmpty(password)) {
            replicatedServersConfig.setPassword(password);
        }
        return Redisson.create(config);
    }

    private Pair<String, String> getUserNameAndPassword(String redisUri) {
        String user = "";
        String password = "";
        // 解析user和password
        Pattern userPattern = Pattern.compile("redis://([^:]+):([^@]+)@");
        Matcher userMatcher = userPattern.matcher(redisUri);
        if (userMatcher.find()) {
            user = userMatcher.group(1);
            password = userMatcher.group(2);
            System.out.println("user: " + user);
            System.out.println("password: " + password);
        }
        return new MutablePair<>(user, password);
    }

    private String[] getRedisHostAndPort(String redisUri) {
        String[] result = null;
        Pattern pattern = Pattern.compile("redis://(.*?)(@.*?)?/([0-9]+)");
        Matcher matcher = pattern.matcher(redisUri);
        if (matcher.matches()) {
            String hosts = matcher.group(2);
            String[] nodes = hosts.split(",");
            result = new String[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                String[] parts = nodes[i].split(":");
                String host = parts[0];
                int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 6379;
                result[i] = host + ":" + port;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String redisUri = "redis://:111222@10.5.153.1:16380";
        Pattern pattern = Pattern.compile("redis://(.*?)(@.*?)?/([0-9]+)");
        Matcher matcher = pattern.matcher(redisUri);
        if (matcher.find()){
            String group = matcher.group(3);
            System.out.println();
        }
    }
}
