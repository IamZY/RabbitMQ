package com.ntuzy.rabbit.exchange;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class RabbitAdminTestor {

    @Resource(name = "rabbitAdmin")
    private RabbitAdmin rabbitAdmin;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testCreateExchange() {
//        无则创建有则什么都不干
        rabbitAdmin.declareExchange(new FanoutExchange("test.exchange.fanout", true, true));
        rabbitAdmin.declareExchange(new DirectExchange("test.exchange.direct", true, true));
        rabbitAdmin.declareExchange(new TopicExchange("test.exchange.topic", true, true));
    }


    @Test
    public void testQueueAndBind() {
        rabbitAdmin.declareQueue(new Queue("test.queue"));
        rabbitAdmin.declareBinding(new Binding(
                "test.queue",
                Binding.DestinationType.QUEUE,
                "test.exchange.topic",
                "#",
                new HashMap<String, Object>()
        ));

        rabbitTemplate.convertAndSend("test.exchange.topic", "hello", "abc123");
    }

    @Test
    public void testDelete() {
        rabbitAdmin.declareQueue(new Queue("test.queue"));
        rabbitAdmin.deleteExchange("test.exchange.fanout");
        rabbitAdmin.deleteExchange("test.exchange.direct");
        rabbitAdmin.deleteExchange("test.exchange.topic");
    }


}
