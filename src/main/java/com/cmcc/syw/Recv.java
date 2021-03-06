package com.cmcc.syw;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/4.
 */
public class Recv {
    private static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //Consumes...
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("[X] Waiting for messages. To exit press Ctrl+C...");

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("[X] Recv: " + msg);
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
