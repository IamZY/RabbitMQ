# RabbitMQ
## 消息队列

跨进程的通信机制，用于上下游传递信息

MQ作为消息中间件最主要的作用就是系统之间的信息传递进行解耦 MQ是数据可靠性的重要保障

![iamge](https://github.com/IamZY/RabbitMQ/blob/master/images/1571821891745.png)

+ 对比

  ![iamge](https://github.com/IamZY/RabbitMQ/blob/master/images/1571829333737.png)

## 应用场景

+ 异构系统的数据传递
+ 高并发程序的流量控制
+ 基于P2P,P2PPP的程序
+ 分布式系统的事务一致性TCC
+ 高可靠性的交易系统

## Windows下安装`RabbitMQ`

+ Erlang

![1571830867904](https://github.com/IamZY/RabbitMQ/blob/master/images/1571830867904.png)

## Linux下安装`RabbitMQ`

+ 下载esl-erlang_21.0-1~centos~7_amd64.rpm
  Erlang运行环境RPM包
+ 下载rabbitmq-server-3.7.7-1.el7.noarch.rpm
  rabbitmq服务器程序
+ mkdir /usr/local/temp
+ cd /usr/local/temp
+ 通过XFTP将文件上传至temp目录
+ rpm -ivh --nodeps esl-erlang_21.0-1~centos~7_amd64.rpm
  安装RPM包
+ rpm -ivh --nodeps rabbitmq-server-3.7.7-1.el7.noarch.rpm
+ rabbitmq-plugins enable rabbitmq_management
  启用控制台
+ rabbitmq-server
+ chown rabbitmq:rabbitmq /var/lib/rabbitmq/.erlang.cookie
  启用服务
+ 防火墙放行5672/15672端口

## 常用命令

+ 启动

  > // 前台启动
  >
  > rabbitmq-server
  >
  > // 后台启动
  >
  > rabbitmq-server detached

+ 关闭

  > rabbitmqctl stop

+ 终止与启动应用

  >rabbitmqctl start_app
  >
  >rabbitmqctl stop_app

## AMQP

![1571901575139](https://github.com/IamZY/RabbitMQ/blob/master/images/1571901575139.png)

## 实战

### 生产者

```java
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

```

### 消费者

```java
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

```























