package com.wanbang.driver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan({"com.wanbang.driver.mapper","com.wanbang.console.mapper"})
@SpringBootApplication
public class Driver8001 {

    public static void main(String[] args) {
        SpringApplication.run(Driver8001.class, args);
    }

}
