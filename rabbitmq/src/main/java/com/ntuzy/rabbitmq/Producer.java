package com.ntuzy.rabbitmq;

import com.ntuzy.rabbitmq.utils.RabbitConstant;
import com.ntuzy.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {

        // MQ底层通信 物理连接
        Connection connection = RabbitUtils.getConnection();
        // 创建通信通道 相当于 tcp中的虚拟连接
        Channel channel = connection.createChannel();
        // 创建队列 声明并创建一个队列 如果队列已经存在 就使用这个队列
        // 队列名称
        // 是否持久化 false表示不持久化数据 mq停掉数据就会丢失
        // 是否队列私有化 false代表所有消费者都可以访问 true代表第一次拥有的才能一直使用
        // 是否自动删除 false代表连接停掉后不自动删除掉这个队列
        // 代表其他额外参数
        channel.queueDeclare(RabbitConstant.QUEUE_HELLOWORLD, false, false, false, null);
        // 四个参数
        // exchange 交换机暂时不用
        // 队列名称
        // 额外的参数
        // 额外的设置属性
        // 传递数据的字节
        String content = "helloworld";
        channel.basicPublish("", RabbitConstant.QUEUE_HELLOWORLD, null, content.getBytes());

        //
        channel.close();
        connection.close();
        System.out.println("数据发送成功");
    }
}
