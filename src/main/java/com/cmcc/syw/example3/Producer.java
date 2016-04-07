package com.cmcc.syw.example3;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * demo to show broadcast producer
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
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "topic");
//        channel.queueDeclare(QUEUE, false, false, false, null);
//        channel.queueBind(QUEUE, EXCHANGE, "log.*");

        final int COUNT = 10000;
        for (int i = 0; i < COUNT; i++) {
            channel.basicPublish(EXCHANGE, "log.news", null, "NBA final is coming!".getBytes());
        }

        channel.close();
        connection.close();
    }
}
