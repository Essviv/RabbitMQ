package com.cmcc.syw.example1;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 16-4-5.
 */
public class Consumer {
    private final static String QUEUE_NAME = "HELLO";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);

        Connection connection
                = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("[*]Waiting for messages. To exit press Ctrl-C.");

        final int COUNT = 10;
        for (int i = 0; i < COUNT; i++) {
            com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "utf-8");
                    System.out.println("[x] received: " + message);
                    channel.basicAck(envelope.getDeliveryTag(), true);
                }
            };

            channel.basicConsume(QUEUE_NAME, false, consumer);
        }

    }
}
