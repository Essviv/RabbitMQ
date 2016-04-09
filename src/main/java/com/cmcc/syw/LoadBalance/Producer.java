package com.cmcc.syw.LoadBalance;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * demo to show load balance with prefetch count
 *
 * Created by sunyiwei on 16/4/9.
 */
public class Producer {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "LOAD_BALANCE";
    private static final String QUEUE = "QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "direct");
        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.queueBind(QUEUE, EXCHANGE, "loadBalance");

        while(true){
            Thread.sleep(50);
            channel.basicPublish(EXCHANGE, "loadBalance", null, "hello".getBytes());
        }
    }
}
