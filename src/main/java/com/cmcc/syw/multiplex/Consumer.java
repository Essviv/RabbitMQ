package com.cmcc.syw.multiplex;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.ChannelN;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 16/4/5.
 */
public class Consumer {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);

        final Connection connection = connectionFactory.newConnection();

        final int COUNT = 10;
        ExecutorService service = Executors.newFixedThreadPool(COUNT);
        for (int i = 0; i < COUNT; i++) {
            service.submit(new Runnable() {
                public void run() {
                    try {
                        Channel channel = connection.createChannel();
                        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

                        com.rabbitmq.client.Consumer consumer
                                = new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                                System.out.println("Received: " + new String(body, "utf-8"));
                            }
                        };

                        int count = 0;
                        while(count++ < 100){
                            channel.basicConsume(QUEUE_NAME, true, consumer);
                        }

                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        service.shutdown();
        while(!service.isTerminated()){
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        }
        connection.close();
    }
}
