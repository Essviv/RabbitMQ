package com.cmcc.syw;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/4.
 */
public class NewRecv {
    private static String QUEUE_NAME = "hello world!";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        //Consumes...
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        System.out.println("[X] Waiting for messages. To exit press Ctrl+C...");

        channel.basicQos(1);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("[X] Recv: " + msg);

                try {
                    doWork(msg);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                } finally {
                    System.out.println("[X] Done...");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        channel.basicConsume(QUEUE_NAME, false, consumer);
    }

    private static void doWork(String msg) throws InterruptedException {
        for (char c : msg.toCharArray()) {
            if (c == '.') {
                System.out.println(Thread.currentThread().getName() + "sleeps...zzzz...");
                Thread.sleep(10000);
            }
        }
    }
}
