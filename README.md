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