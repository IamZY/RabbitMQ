package com.ntuzy.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 用于创建MQ的物理连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        // 5672 是默认端口号
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/test");

        // MQ底层通信 物理连接
        Connection connection = connectionFactory.newConnection();

        // 创建通道
        Channel channel = connection.createChannel();
        channel.queueDeclare("helloworld", false, false, false, null);
        // 创建一个消息消费
        // 是否自动确认收到消息 false 代表手动编程确认消息  MQ推荐做法
        // 第三个参数传入DefaultConsumer的实现类
        channel.basicConsume("helloworld", false, new Receiver(channel));



    }
}


class Receiver extends DefaultConsumer {
    private Channel channel;

    // 重新构造函数 Chanel通道对象需要从外层传入 在HandleDelivery
    public Receiver(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String messageBody = new String(body);
        System.out.println("消费者接收到: " + messageBody);
        // 签收消息 确认消息
        // envelope.getDeliveryTag() 获取这个消息的TagId
        // false 只确认签收当前的消息 设置为true的时候 代表签收该消费者所有未签收的消息
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}
