package com.ntuzy.rabbitmq.workQueue;


import com.ntuzy.rabbitmq.utils.RabbitConstant;
import com.ntuzy.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;

public class SNSServer {
    public static void main(String[] args) throws IOException {
        Connection conn = RabbitUtils.getConnection();
        final Channel channel = conn.createChannel();
        // 不写会将所有请求平均发送给所有的消费者
        // 写的话 不会再一次发送多个请求 二十处理完成消息后再从队列中获取一个新的
        channel.basicQos(1);
        channel.queueDeclare(RabbitConstant.QUEUE_SMS, false, false, false, null);
        channel.basicConsume(RabbitConstant.QUEUE_SMS, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String jsonSms = new String(body);
                System.out.println("SMSSender1-短信发送成功" + jsonSms);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });

    }
}
