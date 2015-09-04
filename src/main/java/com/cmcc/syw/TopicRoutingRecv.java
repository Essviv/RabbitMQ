package com.cmcc.syw;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/4.
 */
public class TopicRoutingRecv {
    private static String EXCHANGE_NAME="TopicRouting";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //establish connection
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        //declare exchange, queue, binding
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String queueName = channel.queueDeclare().getQueue();
        String routingKey = Utils.getRoutingKey(args);
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("[X] Recv: " + msg);

                try {
                    Utils.doWork(msg);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                } finally {
                    System.out.println("[X] Done...");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        });
    }
}
