package com.yupi.springbootinit.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig
{
    private Integer dataBase;

    private String password;

    private String host;

    private Integer port;

    @Bean
    public RedissonClient redissonClient()
    {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                //redis://localhost:6379
                .setDatabase(dataBase)
                .setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
