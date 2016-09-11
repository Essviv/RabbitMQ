package com.cmcc.syw.rpc;

import com.cmcc.syw.QueueEndPoint;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * RPC client
 *
 * Created by sunyiwei on 16/9/11.
 */
public class Client extends QueueEndPoint {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = build();

        Channel channel = connection.createChannel();
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare();
        String respQueueName = declareOk.getQueue();

        channel.basicConsume(respQueueName, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Server resp body = " + new String(body));
            }
        });

        final String QUEUE = "ping";
        final int COUNT = 10;
        for (int i = 0; i < COUNT; i++) {
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().replyTo(respQueueName).build();
            channel.basicPublish("", QUEUE, basicProperties, randStr(10).getBytes());
        }
    }

    private static String randStr(int length) {
        StringBuilder sb = new StringBuilder();

        Random r = new Random();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + r.nextInt(26)));
        }

        return sb.toString();
    }
}
