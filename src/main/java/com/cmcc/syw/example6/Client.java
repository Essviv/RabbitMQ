package com.cmcc.syw.example6;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 16/4/7.
 */
public class Client {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "RPC";
    private static final String REQUEST_QUEUE = "REQUEST_QUEUE";
    private static final String RESPONSE_QUEUE = "RESPONSE_QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "direct");
        channel.queueDeclare(REQUEST_QUEUE, false, false, false, null);
        channel.queueDeclare(RESPONSE_QUEUE, false, false, false, null);
        channel.queueBind(REQUEST_QUEUE, EXCHANGE, REQUEST_QUEUE);
        channel.queueBind(RESPONSE_QUEUE, EXCHANGE, RESPONSE_QUEUE);

        final QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        channel.basicConsume(RESPONSE_QUEUE, true, queueingConsumer);

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
                        System.out.println(new String(delivery.getBody()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        final int COUNT = 20;
        for (int i = 0; i < COUNT; i++) {
            publish(channel, String.valueOf(i));
        }
    }

    private static void publish(Channel channel, String message) throws IOException, InterruptedException {
        String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                .builder().correlationId(corrId).replyTo(RESPONSE_QUEUE).build();

        channel.basicPublish(EXCHANGE, REQUEST_QUEUE, basicProperties, message.getBytes());
    }
}
