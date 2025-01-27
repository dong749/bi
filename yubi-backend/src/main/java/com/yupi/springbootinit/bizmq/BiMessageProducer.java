package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BiMessageProducer
{
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message)
    {
        rabbitTemplate.convertAndSend(BiMQConstant.BI_EXCHANGE_NAME, BiMQConstant.BI_ROUTING_KEY, message);
    }
}
