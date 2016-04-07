package com.cmcc.syw.multiConsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * demo to show multi consumer within one same channel
 * <p/>
 * Created by sunyiwei on 16/4/5.
 */
public class Producer {
    private final static String QUEUE_NAME = "HELLO";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);

        Connection connection
                = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, true, null);

        String message = "hello world";

        int count = 0;
        while (count++ <= 10000) {
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("[X] Send: " + message);
        }

        channel.close();
        connection.close();
    }
}
