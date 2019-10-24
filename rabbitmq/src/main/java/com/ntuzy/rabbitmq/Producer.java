package com.ntuzy.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.xpath.internal.functions.FuncFalse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
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
        // 创建通信通道 相当于 tcp中的虚拟连接
        Channel channel = connection.createChannel();
        // 创建队列 声明并创建一个队列 如果队列已经存在 就使用这个队列
        // 队列名称
        // 是否持久化 false表示不持久化数据 mq停掉数据就会丢失
        // 是否队列私有化 false代表所有消费者都可以访问 true代表第一次拥有的才能一直使用
        // 是否自动删除 false代表连接停掉后不自动删除掉这个队列
        // 代表其他额外参数
        channel.queueDeclare("helloworld", false, false, false, null);
        // 四个参数
        // exchange 交换机暂时不用
        // 队列名称
        // 额外的参数
        // 额外的设置属性
        // 传递数据的字节
        String content = "helloworld";
        channel.basicPublish("", "helloworld", null, content.getBytes());

        //
        channel.close();
        connection.close();
        System.out.println("数据发送成功");
    }
}
