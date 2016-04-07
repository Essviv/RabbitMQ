package com.cmcc.syw.example4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * demo to show direct exchange
 * <p/>
 * Created by sunyiwei on 16/4/6.
 */
public class Consumer {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "BROADCAST";
    private static final String QUEUE = "QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setPort(PORT);
        connectionFactory.setHost(HOST);

        final Connection connection
                = connectionFactory.newConnection();

        final String[] keys = {"error", "debug", "info"};
        final ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (String key : keys) {
            final String KEY = key;
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        String queueName = QUEUE + KEY;
                        Channel channel = connection.createChannel();
                        channel.exchangeDeclare(EXCHANGE, "direct");
                        channel.queueDeclare(queueName, false, false, false, null);
                        channel.queueBind(queueName, EXCHANGE, KEY);
                        channel.queueBind(queueName, EXCHANGE, "test");

                        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                                System.out.format("%s: %s. %n", consumerTag,
                                        new String(body, StandardCharsets.UTF_8));
                            }
                        };

                        channel.basicConsume(queueName, true, consumer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
