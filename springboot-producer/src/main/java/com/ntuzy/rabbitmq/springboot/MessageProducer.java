package com.ntuzy.rabbitmq.springboot;

import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class MessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {

        /**
         *
         * @param correlationData  消息附加信息 即 自定义id
         * @param isack 代表消息是否被broker 接受 true代表接受 false代表拒收
         * @param cause 代表消息拒收得原因 帮助我们进行后续得操作
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean isack, String cause) {
            System.out.println(correlationData);
            System.out.println("ack: " + isack);
            if (isack == false) {
                System.err.println(cause);
            }
        }
    };

    RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("Code: " + replyCode + ",Text: " + replyText);
            System.err.println("Exchange: " + exchange + ",RoutingKey: " + routingKey);
        }
    };


    public void sendMessage(Employee emp) {
        // CorrelationData 作为消息得附加信息进行传递 用它保存消息得自定义id
        CorrelationData cd = new CorrelationData(emp.getEmpno() + "-" + new Date().getTime());
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.convertAndSend("springboot-exchange", "hr.employee", emp, cd);
    }


}
