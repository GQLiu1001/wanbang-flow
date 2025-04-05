package com.wanbang.manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan(basePackages = "com.wanbang.manager.mapper.db1", sqlSessionFactoryRef = "primarySqlSessionFactory")
@MapperScan(basePackages = "com.wanbang.manager.mapper.db2", sqlSessionFactoryRef = "secondarySqlSessionFactory")
public class Manager8000 {

    public static void main(String[] args) {
        SpringApplication.run(Manager8000.class, args);
    }

}
