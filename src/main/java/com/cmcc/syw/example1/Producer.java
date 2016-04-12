package com.cmcc.syw.example1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 16-4-5.
 */
public class Producer {
    private final static String QUEUE_NAME = "no.ha.all";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost("192.168.32.68");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("sunyiwei");
        connectionFactory.setPassword("syw514912821");

        final Connection connection
                = connectionFactory.newConnection();

        final int COUNT = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT);

        for (int i = 0; i < COUNT; i++) {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        Channel channel = connection.createChannel();
                        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                        String message = "hello world";

                        while (true) {
                            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
