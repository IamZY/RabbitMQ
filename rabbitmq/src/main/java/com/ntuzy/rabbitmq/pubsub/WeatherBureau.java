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
