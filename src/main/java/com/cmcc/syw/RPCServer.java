package com.cmcc.syw;

import com.rabbitmq.client.*;
import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/5.
 */
public class RPCServer {
    private static final String QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //声明要消费的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        channel.basicQos(1);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, consumer);

        System.out.println("Waiting to the request...");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            String body = new String(delivery.getBody());
            System.out.println("RequestBody: " + body);

            BasicProperties basicProperties = delivery.getProperties();
            AMQP.BasicProperties replyProps =
                    new AMQP.BasicProperties().builder().correlationId(basicProperties.getCorrelationId()).build();

            //process
            int index = NumberUtils.toInt(String.valueOf(body), 1);
            int response = Utils.calFibonaci(index);

            channel.basicPublish("", basicProperties.getReplyTo(), replyProps, String.valueOf(response).getBytes());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }
}
