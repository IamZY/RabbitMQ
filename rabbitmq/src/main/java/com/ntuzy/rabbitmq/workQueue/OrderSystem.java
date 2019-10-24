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
