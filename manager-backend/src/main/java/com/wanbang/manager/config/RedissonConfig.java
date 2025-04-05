package com.wanbang.manager.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() { // 返回类型明确为接口类型
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setPassword("123123");
        config.setCodec(new JsonJacksonCodec()); // 使用 Jackson 序列化
        return Redisson.create(config);
    }
}
