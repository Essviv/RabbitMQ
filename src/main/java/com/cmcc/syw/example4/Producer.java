package com.cmcc.syw.example4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * demo to show direct exchange
 *
 * Created by sunyiwei on 16/4/6.
 */
public class Producer {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "BROADCAST";
    private static final String QUEUE = "QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setPort(PORT);
        connectionFactory.setHost(HOST);

        Connection connection
                = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "direct");

        final int COUNT = 1000;
        for (int i = 0; i < COUNT; i++) {
            String key = randomKey();
            channel.basicPublish(EXCHANGE, key, null, key.getBytes());
        }

        channel.close();
        connection.close();
    }

    private static String randomKey(){
        final String[] keys = {"error", "info"};
        return keys[new Random().nextInt(2)];
    }
}
