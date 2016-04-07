package com.cmcc.syw.example3;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * demo to show broadcast consumer
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
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        final Connection connection = connectionFactory.newConnection();
        final int COUNT = 10;

        ExecutorService service = Executors.newFixedThreadPool(COUNT);
        for (int i = 0; i < COUNT; i++) {
            final int INDEX = i;
            service.submit(new Runnable() {
                               public void run() {
                                   try {
                                       String queueName = QUEUE + INDEX;
                                       final Channel channel = connection.createChannel();
                                       channel.exchangeDeclare(EXCHANGE, "topic");
                                       channel.queueDeclare(queueName, false, false, false, null);
                                       channel.queueBind(queueName, EXCHANGE, "log.*");

                                       com.rabbitmq.client.Consumer consumer
                                               = new DefaultConsumer(channel) {
                                           @Override
                                           public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                                               System.out.format("%s: %s. %n", consumerTag,
                                                       new String(body, StandardCharsets.UTF_8));
                                           }
                                       };

                                       channel.basicConsume(queueName, true, consumer);
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                               }
                           }
            );
        }
    }
}
