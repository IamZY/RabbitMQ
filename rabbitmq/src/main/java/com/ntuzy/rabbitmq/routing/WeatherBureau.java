package com.ntuzy.rabbitmq.routing;

import com.ntuzy.rabbitmq.utils.RabbitConstant;
import com.ntuzy.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
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
            channel.basicPublish(RabbitConstant.EXCHANGE_WEATHER_ROUTING, next.getKey(), null, next.getValue().getBytes());
        }


        channel.close();
        conn.close();
    }
}
