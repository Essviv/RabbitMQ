package com.cmcc.syw.LoadBalance;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * demo to show load balance with prefetch count
 *
 * Created by sunyiwei on 16/4/9.
 */
public class SlowConsumer {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "LOAD_BALANCE";
    private static final String QUEUE = "QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "direct");
        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.queueBind(QUEUE, EXCHANGE, "loadBalance");
        channel.basicQos(1357);

        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(QUEUE, false, consumer);
    }
}
