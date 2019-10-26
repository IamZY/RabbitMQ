package com.ntuzy.rabbitmq.springboot;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MessageConsumer {

    // @RabbitListener注解用于声明或定义消息接受的队列与exchange绑定的信息
    // Springboot中 消费者使用注解获取消息
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "springboot-queue", durable = "true"),
                    exchange = @Exchange(value = "springboot-exchange", durable = "true", type = "topic"),
                    key = "#"
            )
    )

    // 用于接受消息的方法
    @RabbitHandler // 通知SpringBoot下面的方法用于接收消息 这个方法运行后处于等待状态 有新的消息进来会自动触发下面的消息
    // @Payload 代表运行时将消息反序列化注入到后面的参数中
    public void handleMessage(@Payload Employee employee, Channel channel, @Headers Map<String, Object> headers) throws IOException {
        System.out.println("===============================================");
        System.out.println("接收到: " + employee.getEmpno() + " : " + employee.getName());
        // 所有消息处理后必须进行消息ack channel.basicAck()
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
        System.out.println("===============================================");
    }
}
