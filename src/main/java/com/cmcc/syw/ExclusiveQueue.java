package com.cmcc.syw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 16/4/9.
 */
public class ExclusiveQueue {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "DIRECT";
    private static final String QUEUE = "EXCLUSIVE_QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();

        //to prove that exclusive queue is not restricted to channel
        Channel channelA = connection.createChannel();
        channelA.exchangeDeclare(EXCHANGE, "direct");
        channelA.queueDeclare(QUEUE, true, false, true, null);
        channelA.queueBind(QUEUE, EXCHANGE, "test");
        channelA.close();

        Channel channelB = connection.createChannel();
        channelB.basicPublish(EXCHANGE, "test", null, "hello world".getBytes());
        channelB.close();
        connection.close();
    }
}
