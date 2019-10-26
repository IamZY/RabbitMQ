package com.ntuzy.rabbitmq.confirm;

import com.ntuzy.rabbitmq.utils.RabbitConstant;
import com.ntuzy.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class WeatherBureau {
    public static void main(String[] args) throws IOException, TimeoutException {
        Map area = new LinkedHashMap<String, String>();
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

        // 开启Confirm监听模式
        channel.confirmSelect();

        channel.addConfirmListener(new ConfirmListener() {
            public void handleAck(long l, boolean b) throws IOException {
                // 第二参数 代表接受的数据是否被批量接受
                System.out.println("消息已被Broker接受....,Tag: " + l);
            }

            public void handleNack(long l, boolean b) throws IOException {
                System.out.println("消息已被Broker拒受....,Tag: " + l);
            }
        });

        channel.addReturnListener(new ReturnCallback() {
            public void handle(Return r) {
                System.err.println("========================================");
                System.err.println("错误编码: " + r.getReplyCode() + "-" + "错误描述: " + r.getReplyText());
                System.err.println("交换机:" + r.getExchange() + "-路由key:" + r.getRoutingKey());
                System.err.println("消息主体: " + new String(r.getBody()));
                System.err.println("========================================");
            }
        });

        Iterator<Map.Entry<String, String>> itr = area.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, String> next = itr.next();
            // Routing key 第二个参数相当于数据筛选的条件
            // 第三个参数 mandatory true 代表如果消息无法正常投递则return生产者 false 丢弃
            channel.basicPublish(RabbitConstant.EXCHANGE_WEATHER_TOPIC, next.getKey(), true, null, next.getValue().getBytes());
        }


//        channel.close();
//        conn.close();
    }
}
