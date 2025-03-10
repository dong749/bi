package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TtlConsumer
{
    private final static String QUEUE_NAME = "ttl_queue";

    public static void main(String[] args)
        throws Exception
    {
        // 创建连接与mq交互
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 创建队列, 并指定队列的过期时间
        Map<String, Object> argv = new HashMap<String, Object>();
        argv.put("x-message-ttl", 5000);
        channel.queueDeclare(QUEUE_NAME, false, false, false, argv);
        System.out.println("  [*] Waiting for messages. To exit press CTRL+C]");

        //定义了如何处理消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
        };
        // 消费消息，持续阻塞
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
