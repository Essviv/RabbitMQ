package com.cmcc.syw.example6.backup;

import com.rabbitmq.client.*;
import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * demo to show RPC within RMQ
 * <p/>
 * Created by sunyiwei on 16/4/7.
 */
public class Server {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "RPC";
    private static final String QUEUE = "REQUEST_QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "direct");
        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.queueBind(QUEUE, EXCHANGE, "request");
        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE, false, consumer);
        while (true) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                BasicProperties basicProperties = delivery.getProperties();
                AMQP.BasicProperties respProperties = new AMQP.BasicProperties()
                        .builder()
                        .correlationId(basicProperties.getCorrelationId())
                        .build();

                String message = new String(delivery.getBody());
                int n = NumberUtils.toInt(message, 2);
                int result = fibonacci(n);

                channel.basicPublish(EXCHANGE, basicProperties.getReplyTo(), respProperties,
                        String.valueOf(result).getBytes());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int fibonacci(int n) {
        if (n == 0) {
            return 0;
        }

        if (n == 1) {
            return 1;
        }

        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
