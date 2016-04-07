package com.cmcc.syw.example6.backup;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * demo to show RPC within RMQ
 *
 * Created by sunyiwei on 16/4/7.
 */
public class Client {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "RPC";
    private static final String QUEUE = "response";
    private static final String ROUTING_KEY = "response";
    private static final int PORT = 5672;
    private Channel channel = null;
    private Connection connection = null;
    private QueueingConsumer consumer = null;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Client client = new Client();

        final int COUNT = 1000;
        for (int i = 0; i < COUNT; i++) {
            System.out.println(client.call(String.valueOf(i)));
        }

        client.close();

    }

    public String call(String message) throws IOException, InterruptedException {
        String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .correlationId(corrId)
                .replyTo(ROUTING_KEY)
                .build();

        channel.basicPublish(EXCHANGE, "request", basicProperties, message.getBytes());
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if(delivery.getProperties().getCorrelationId().equals(corrId)){
                return new String(delivery.getBody());
            }
        }
    }

    public void close() throws IOException {
        connection.close();
    }

    public Client() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);
//        connectionFactory.setUsername("sunyiwei");
//        connectionFactory.setPassword("sunyiwei");

        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "direct");
        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.queueBind(QUEUE, EXCHANGE, ROUTING_KEY);

        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE, true, consumer);
    }
}
