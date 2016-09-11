package com.cmcc.syw;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * abstract producer
 *
 * Created by sunyiwei on 16/9/11.
 */
public class QueueEndPoint {
    protected static void declare(Connection connection, final String queue) throws IOException, TimeoutException {
        final String EXCHANGE = "amq.rabbitmq.log";
        Channel channel = connection.createChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, EXCHANGE, queue);

        channel = connection.createChannel();
        channel.basicConsume(queue, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(queue + ":" + new String(body));
            }
        });
    }

    protected static Connection build() throws IOException, TimeoutException {
        final String HOST = "localhost";
        final int PORT = 5672;
        final String USERNAME = "guest";
        final String PASSWORD = "guest";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);

        return connectionFactory.newConnection();
    }
}
