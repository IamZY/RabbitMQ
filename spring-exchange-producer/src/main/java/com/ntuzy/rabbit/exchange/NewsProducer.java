package com.ntuzy.rabbit.exchange;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

public class NewsProducer {

    private RabbitTemplate rabbitTemplate = null;

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNews(String routingKey, News news) {

        // convertAndSend 用于向exchange发送数据
        // 第一参数 routingKey
        // 第二个参数 是我们需要传递的对象  实现了序列化结构的对象
        rabbitTemplate.convertAndSend(routingKey, news);
        System.out.println("新闻发送成功");

    }
    
    public static void main(String[] args){
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        NewsProducer newsProducer = (NewsProducer) ac.getBean("newsProducer");
        newsProducer.sendNews("us.20190101", new News("新华社", "特朗普退群", new Date(), "国际新闻内容"));
        newsProducer.sendNews("china.20190101", new News("凤凰TV", "xxx企业荣登世界500强", new Date(), "国内新闻内容"));
    }


}
