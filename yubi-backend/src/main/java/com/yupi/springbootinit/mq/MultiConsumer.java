package com.yupi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


public class MultiConsumer
{

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv)
            throws Exception
    {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        final Connection connection = factory.newConnection();
        for (int i = 0; i < 2 ; i++)
        {
            final Channel channel = connection.createChannel();


            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            // 控制单个消费者的处理任务积压数, 这里每个消费者最多处理一个任务
            channel.basicQos(1);
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                try
                {
                    // 处理任务
                    System.out.println(" [x] Received '" + "编号" + finalI + ": " +  message + "'");
                    // 休眠20s模拟机器的处理能力有限
                    Thread.sleep(20000);
                    // 消息任务完成后进行确认, 第一个参数确定当前确认的是队列中的哪一条消息, 第二个参数是指是否要一次性确认所有积压消息, 直到当前这条消息
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
                catch (InterruptedException e)
                {
                    // 指定拒绝哪一条消息, 第一个参数确定当前确认的是队列中的哪一条消息, 第二个参数是指是否要一次性拒绝所有积压消息, 第三个参数是指是否将当前拒绝的消息从新放入队列中
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
                    throw new RuntimeException(e);
                }
                finally
                {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            // 开始消费监听
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }

}

