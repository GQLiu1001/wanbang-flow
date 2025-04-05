package com.wanbang.manager.util;/*
package com.wanbang.manager.util;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQTestRunner implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            rabbitTemplate.convertAndSend("direct.exchange", "test.routing.key", "Test message from startup");
            System.out.println("RabbitMQ connection test successful");
        } catch (Exception e) {
            System.err.println("RabbitMQ connection test failed: " + e.getMessage());
            throw e; // 抛出异常以确保日志显示
        }
    }
}*/
