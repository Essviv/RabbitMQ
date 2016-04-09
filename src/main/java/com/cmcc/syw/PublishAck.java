package com.cmcc.syw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * demo of acknowledgement of publish
 * <p/>
 * Created by sunyiwei on 16/4/9.
 */
public class PublishAck {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "DIRECT";
    private static final String QUEUE = "EXCLUSIVE_QUEUE";
    private static final int PORT = 5672;
    private static final int COUNT = 100000;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();

        transaction(connection);

        publisherConfirmation(connection);

        connection.close();
    }

    private static void transaction(Connection connection) throws IOException {
        Channel channelA = connection.createChannel();
        channelA.exchangeDeclare(EXCHANGE, "direct");
        channelA.queueDeclare(QUEUE, true, false, true, null);
        channelA.queueBind(QUEUE, EXCHANGE, "test");
        channelA.queuePurge(QUEUE);

        long begin = System.currentTimeMillis();
        for (int i = 0; i < COUNT; i++) {
            channelA.txSelect();
            channelA.basicPublish(EXCHANGE, "test", null, "hello world".getBytes());
            channelA.txCommit();
        }

        System.out.format("It takes %#.2f seconds to publish %d messages with transaction. %n",
                (double) (System.currentTimeMillis() - begin) / 1000, COUNT);
    }

    private static void publisherConfirmation(Connection connection) throws IOException, TimeoutException {
        Channel channelA = connection.createChannel();
        channelA.exchangeDeclare(EXCHANGE, "direct");
        channelA.queueDeclare(QUEUE, true, false, true, null);
        channelA.queueBind(QUEUE, EXCHANGE, "test");
        channelA.queuePurge(QUEUE);

        channelA.addConfirmListener(new ConfirmListener() {
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
            }

            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
            }
        });

        long begin = System.currentTimeMillis();
        channelA.confirmSelect();

        for (int i = 0; i < COUNT; i++) {
            channelA.basicPublish(EXCHANGE, "test", null, "hello_world".getBytes());
        }

        System.out.format("It takes %#.2f seconds to publish %d messages with publisher confirmation. %n",
                (double) (System.currentTimeMillis() - begin) / 1000, COUNT);

        channelA.close();
    }
}
