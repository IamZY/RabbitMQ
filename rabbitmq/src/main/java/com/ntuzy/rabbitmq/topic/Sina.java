package com.ntuzy.rabbitmq.topic;

import com.ntuzy.rabbitmq.utils.RabbitConstant;
import com.ntuzy.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

public class Sina {
    public static void main(String[] args) throws IOException {
        Connection conn = RabbitUtils.getConnection();

        final Channel channel = conn.createChannel();
        channel.queueDeclare(RabbitConstant.QUEUE_SINA, false, false, false, null);
        // queueBind 将队列和交换机绑定
        // 队列名
        // 交换机名
        // 路由key
        channel.queueBind(RabbitConstant.QUEUE_SINA, RabbitConstant.EXCHANGE_WEATHER_TOPIC, "us.#");
        channel.basicQos(1);
        channel.basicConsume(RabbitConstant.QUEUE_SINA, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("新浪收到气象信息: " + new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });

    }
}
