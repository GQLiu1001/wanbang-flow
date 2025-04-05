package com.wanbang.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.wanbang.console.mapper")
@SpringBootApplication
@EnableTransactionManagement  // 添加这一行 事务原子性
public class Console8080 {
    //TODO 增加redis数据缓存
    public static void main(String[] args) {
        SpringApplication.run(Console8080.class, args);
    }


}
