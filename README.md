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

## 消息状态

+ Ready

  消息已被送人队列

+ Unacked

  消费被消费者认领 但没确认被消费

  在这和状态下 消费者断开连接则消息回到 ready

  没有确认 客户没有断开连接 则一直处于Unacked

+ Finished

  调用basicAsk后 表示自己被消费 从队列中删除

## 六种工作模式

![1571906268499](https://github.com/IamZY/RabbitMQ/blob/master/images/1571906268499.png)

### WorkQueue工作队列

发送一些耗时的任务给多个工作者，将消费分派给不同的消费者，每个消费者接受不同的消息 并且可以根据处理消息的速度来接受消息的数量

+ Order

  ```java
  package com.ntuzy.rabbitmq.workQueue;
  
  import com.google.gson.Gson;
  import com.ntuzy.rabbitmq.utils.RabbitConstant;
  import com.ntuzy.rabbitmq.utils.RabbitUtils;
  import com.rabbitmq.client.Channel;
  import com.rabbitmq.client.Connection;
  import sun.plugin.net.protocol.jar.CachedJarURLConnection;
  
  import java.io.IOException;
  import java.util.concurrent.TimeoutException;
  
  public class OrderSystem {
      public static void main(String[] args) throws IOException, TimeoutException {
          Connection conn = RabbitUtils.getConnection();
  
          Channel channel = conn.createChannel();
          channel.queueDeclare(RabbitConstant.QUEUE_SMS, false, false, false, null);
          for (int i = 100; i < 200; i++) {
              SMS sms = new SMS("乘客" + i, "12306", "您的车票预定成功");
              String jsonSms = new Gson().toJson(sms);
              channel.basicPublish("", RabbitConstant.QUEUE_SMS, null, jsonSms.getBytes());
          }
          System.out.println("发送数据成功....");
  
          channel.close();
          conn.close();
  
      }
  }
  
  ```

+ SMSServer

  ```java
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
  
  ```

+ SMSServer2

  ```java
  package com.ntuzy.rabbitmq.workQueue;
  
  
  import com.ntuzy.rabbitmq.utils.RabbitConstant;
  import com.ntuzy.rabbitmq.utils.RabbitUtils;
  import com.rabbitmq.client.*;
  
  import java.io.IOException;
  
  public class SNSServer2 {
      public static void main(String[] args) throws IOException {
          Connection conn = RabbitUtils.getConnection();
          final Channel channel = conn.createChannel();
          channel.basicQos(1);
          channel.queueDeclare(RabbitConstant.QUEUE_SMS, false, false, false, null);
          channel.basicConsume(RabbitConstant.QUEUE_SMS, false, new DefaultConsumer(channel) {
              @Override
              public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                  String jsonSms = new String(body);
                  System.out.println("SMSSender2-短信发送成功" + jsonSms);
                  try {
                      Thread.sleep(100);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  channel.basicAck(envelope.getDeliveryTag(), false);
              }
          });
  
      }
  }
  ```

  

### 发布-订阅模式

+ weatherBureau

  ```java
  package com.ntuzy.rabbitmq.pubsub;
  
  import com.ntuzy.rabbitmq.utils.RabbitConstant;
  import com.ntuzy.rabbitmq.utils.RabbitUtils;
  import com.rabbitmq.client.Channel;
  import com.rabbitmq.client.Connection;
  
  import java.io.IOException;
  import java.util.Scanner;
  import java.util.concurrent.TimeoutException;
  
  public class WeatherBureau {
      public static void main(String[] args) throws IOException, TimeoutException {
          Connection conn = RabbitUtils.getConnection();
          Channel channel = conn.createChannel();
          String input = new Scanner(System.in).next();
          channel.basicPublish(RabbitConstant.EXCHANGE_WEATHER, "", null, input.getBytes());
          channel.close();
          conn.close();
      }
  }
  ```

+ Baidu

  ```java
  package com.ntuzy.rabbitmq.pubsub;
  
  import com.ntuzy.rabbitmq.utils.RabbitConstant;
  import com.ntuzy.rabbitmq.utils.RabbitUtils;
  import com.rabbitmq.client.*;
  
  import java.io.IOException;
  
  public class Baidu {
      public static void main(String[] args) throws IOException {
          Connection conn = RabbitUtils.getConnection();
  
          final Channel channel = conn.createChannel();
          channel.queueDeclare(RabbitConstant.QUEUE_BAIDU, false, false, false, null);
          // queueBind 将队列和交换机绑定
          // 队列名
          // 交换机名
          // 路由key
          channel.queueBind(RabbitConstant.QUEUE_BAIDU, RabbitConstant.EXCHANGE_WEATHER, "");
          channel.basicQos(1);
          channel.basicConsume(RabbitConstant.QUEUE_BAIDU, false, new DefaultConsumer(channel) {
              @Override
              public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                  System.out.println("百度收到气象信息: " + new String(body));
                  channel.basicAck(envelope.getDeliveryTag(), false);
              }
          });
  
      }
  }
  
  ```

+ Sina

  ```java
  package com.ntuzy.rabbitmq.pubsub;
  
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
          channel.queueBind(RabbitConstant.QUEUE_SINA, RabbitConstant.EXCHANGE_WEATHER, "");
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
  
  ```

### 路由模式

在发布订阅模式的基础上对数据进行筛选 从而进一步降低数据传输的中的总量（精确匹配）

+ WeatherBureau

  ```java
  package com.ntuzy.rabbitmq.routing;
  
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
          channel.queueBind(RabbitConstant.QUEUE_SINA, RabbitConstant.EXCHANGE_WEATHER_ROUTING, "us.cal.la.20991012");
          channel.queueBind(RabbitConstant.QUEUE_SINA, RabbitConstant.EXCHANGE_WEATHER_ROUTING, "us.cal.la.20991011");
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
  
  ```

+ Baidu

  ```java
  package com.ntuzy.rabbitmq.routing;
  
  import com.ntuzy.rabbitmq.utils.RabbitConstant;
  import com.ntuzy.rabbitmq.utils.RabbitUtils;
  import com.rabbitmq.client.*;
  
  import java.io.IOException;
  
  public class Baidu {
      public static void main(String[] args) throws IOException {
          Connection conn = RabbitUtils.getConnection();
  
          final Channel channel = conn.createChannel();
          channel.queueDeclare(RabbitConstant.QUEUE_BAIDU, false, false, false, null);
          // queueBind 将队列和交换机绑定
          // 队列名
          // 交换机名
          // 路由key
          channel.queueBind(RabbitConstant.QUEUE_BAIDU, RabbitConstant.EXCHANGE_WEATHER_ROUTING, "china.shandong.qingdao.20991011");
          channel.queueBind(RabbitConstant.QUEUE_BAIDU, RabbitConstant.EXCHANGE_WEATHER_ROUTING, "china.shandong.qingdao.20991012");
          channel.basicQos(1);
          channel.basicConsume(RabbitConstant.QUEUE_BAIDU, false, new DefaultConsumer(channel) {
              @Override
              public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                  System.out.println("百度收到气象信息: " + new String(body));
                  channel.basicAck(envelope.getDeliveryTag(), false);
              }
          });
  
      }
  }
  
  ```

+ Sina

  ```java
  package com.ntuzy.rabbitmq.routing;
  
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
          channel.queueBind(RabbitConstant.QUEUE_SINA, RabbitConstant.EXCHANGE_WEATHER_ROUTING, "us.cal.la.20991012");
          channel.queueBind(RabbitConstant.QUEUE_SINA, RabbitConstant.EXCHANGE_WEATHER_ROUTING, "us.cal.la.20991011");
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
  ```

### 主题Topic模式

在Routing模式上进行模糊匹配

此时队列需要绑定要一个模式上。符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词。因此“audit.#”能够匹配到“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”

+ WeatherBureau

  ```java
  package com.ntuzy.rabbitmq.topic;
  
  import com.ntuzy.rabbitmq.utils.RabbitConstant;
  import com.ntuzy.rabbitmq.utils.RabbitUtils;
  import com.rabbitmq.client.Channel;
  import com.rabbitmq.client.Connection;
  
  import java.io.IOException;
  import java.util.Iterator;
  import java.util.LinkedHashMap;
  import java.util.Map;
  import java.util.concurrent.TimeoutException;
  
  public class WeatherBureau {
      public static void main(String[] args) throws IOException, TimeoutException {
          Map area = new LinkedHashMap<String,String>();
          area.put("china.hebei.shijiazhuang.20991011", "中国河北石家庄20991011天气数据");
          area.put("china.shandong.qingdao.20991011", "中国山东青岛20991011天气数据");
          area.put("china.henan.zhengzhou.20991011", "中国河南郑州20991011天气数据");
          area.put("us.cal.la.20991011", "美国加州洛杉矶20991011天气数据");
  
          area.put("china.hebei.shijiazhuang.20991012", "中国河北石家庄20991012天气数据");
          area.put("china.shandong.qingdao.20991012", "中国山东青岛20991012天气数据");
          area.put("china.henan.zhengzhou.20991012", "中国河南郑州20991012天气数据");
          area.put("us.cal.la.20991012", "美国加州洛杉矶20991012天气数据");
          Connection conn = RabbitUtils.getConnection();
          Channel channel = conn.createChannel();
          Iterator<Map.Entry<String,String>> itr = area.entrySet().iterator();
  
          while (itr.hasNext()){
              Map.Entry<String, String> next = itr.next();
              // Routing key 第二个参数相当于数据筛选的条件
              channel.basicPublish(RabbitConstant.EXCHANGE_WEATHER_TOPIC, next.getKey(), null, next.getValue().getBytes());
          }
  
  
          channel.close();
          conn.close();
      }
  }
  
  ```

+ Baidu

  ```java
  package com.ntuzy.rabbitmq.topic;
  
  import com.ntuzy.rabbitmq.utils.RabbitConstant;
  import com.ntuzy.rabbitmq.utils.RabbitUtils;
  import com.rabbitmq.client.*;
  
  import java.io.IOException;
  
  public class Baidu {
      public static void main(String[] args) throws IOException {
          Connection conn = RabbitUtils.getConnection();
  
          final Channel channel = conn.createChannel();
          channel.queueDeclare(RabbitConstant.QUEUE_BAIDU, false, false, false, null);
          // queueBind 将队列和交换机绑定
          // 队列名
          // 交换机名
          // 路由key
          channel.queueBind(RabbitConstant.QUEUE_BAIDU, RabbitConstant.EXCHANGE_WEATHER_TOPIC, "*.*.*.20991011");
          // 解绑
          channel.queueUnbind(RabbitConstant.QUEUE_BAIDU, RabbitConstant.EXCHANGE_WEATHER_TOPIC, "*.*.*.20991011");
  
          channel.basicQos(1);
          channel.basicConsume(RabbitConstant.QUEUE_BAIDU, false, new DefaultConsumer(channel) {
              @Override
              public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                  System.out.println("百度收到气象信息: " + new String(body));
                  channel.basicAck(envelope.getDeliveryTag(), false);
              }
          });
  
      }
  }
  
  ```

+ Sina

  ```java
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
  
  ```

  















