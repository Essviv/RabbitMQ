package com.cmcc.syw;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/5.
 */
public class RPCClient {
    private static String QUEUE_NAME = "rpc_queue";
    private Channel channel;
    private Connection connection;
    private QueueingConsumer consumer;
    private String replyQueue;

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        consumer = new QueueingConsumer(channel);
        replyQueue = channel.queueDeclare().getQueue();
        channel.basicConsume(replyQueue, false, consumer);
    }

    public void call(int index) throws IOException, InterruptedException {
        String uuid = UUID.randomUUID().toString();
        AMQP.BasicProperties basicProperties =
                new AMQP.BasicProperties().builder().correlationId(uuid).replyTo(replyQueue).build();
        channel.basicPublish("", QUEUE_NAME, basicProperties, String.valueOf(index).getBytes());

        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if(uuid.equalsIgnoreCase(delivery.getProperties().getCorrelationId())) {
                System.out.println(new String(delivery.getBody()));
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        RPCClient client = new RPCClient();

        for (int i = 0; i < 10; i++) {
            client.call(i);
        }

        client.close();
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
