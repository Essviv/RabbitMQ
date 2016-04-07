package com.cmcc.syw.example2;

/**
 * demo of header exchange
 * <p/>
 * Created by sunyiwei on 16/4/7.
 */

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Consumer {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "HEADER";
    private static final String QUEUE = "QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "headers");
        channel.queueDeclare(QUEUE, false, false, false, null);

        com.rabbitmq.client.Consumer consumer
                = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        };

        channel.basicConsume(QUEUE, true, consumer);
    }
}
