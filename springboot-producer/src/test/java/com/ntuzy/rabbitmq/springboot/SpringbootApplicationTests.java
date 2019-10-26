package com.ntuzy.rabbitmq.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SpringbootApplicationTests {

    @Resource
    private MessageProducer messageProducer;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSendMsg() {
        messageProducer.sendMessage(new Employee("3306", "ntuzy", 18));
    }

}
