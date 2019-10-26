package com.ntuzy.rabbitmq;

import com.ntuzy.rabbit.exchange.News;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NewsConsumer {
    public void recv(News news) {
        System.out.println("接收到最新新闻: " + news.getTitle() + ":" + news.getSource());
    }

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }

}
