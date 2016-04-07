package com.cmcc.syw.example6;

import com.rabbitmq.client.*;
import org.apache.commons.lang.math.NumberUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * practise RPC server implement
 * <p/>
 * Created by sunyiwei on 16/4/7.
 */
public class Server {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "RPC";
    private static final String REQUEST_QUEUE = "REQUEST_QUEUE";
    private static final String RESPONSE_QUEUE = "RESPONSE_QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "direct");
        channel.queueDeclare(REQUEST_QUEUE, false, false, false, null);
        channel.queueBind(REQUEST_QUEUE, EXCHANGE, REQUEST_QUEUE);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(REQUEST_QUEUE, false, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            AMQP.BasicProperties requestProperties = delivery.getProperties();
            AMQP.BasicProperties responseProperties = new AMQP.BasicProperties().builder()
                    .correlationId(requestProperties.getCorrelationId())
                    .build();

            int value = NumberUtils.toInt(new String(delivery.getBody()), 0);
            String routingKey = requestProperties.getReplyTo();
            channel.basicPublish(EXCHANGE, routingKey, responseProperties, String.valueOf(value + ":" + fib(value)).getBytes());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }

    private static int fib(int n) {
        if (n == 0) {
            return 0;
        }

        if (n == 1) {
            return 1;
        }

        return fib(n - 1) + fib(n - 2);
    }
}
