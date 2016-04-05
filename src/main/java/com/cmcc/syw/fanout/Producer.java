package com.cmcc.syw.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * fanout exchange producer demo
 * Created by sunyiwei on 16/4/5.
 */
public class Producer {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("fanout", "fanout", false, true, null);
        channel.queueDeclare(QUEUE_NAME, false, false, true, null);

        int count = 0;
        while (count++ < 1000) {
            channel.basicPublish("fanout", QUEUE_NAME, null, "hello world".getBytes());
        }

        channel.close();
        connection.close();
    }
}
