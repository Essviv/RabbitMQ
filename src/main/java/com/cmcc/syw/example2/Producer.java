package com.cmcc.syw.example2;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * demo of header exchange
 * <p/>
 * Created by sunyiwei on 16/4/7.
 */
public class Producer {
    private static final String HOST = "localhost";
    private static final String EXCHANGE = "HEADER";
    private static final String QUEUE = "QUEUE";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory
                = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE, "headers");
        channel.queueDeclare(QUEUE, false, false, false, null);
        channel.queueBind(QUEUE, EXCHANGE, "", buildBindingParams());

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .headers(buildPublishParams()).build();
        channel.basicPublish(EXCHANGE, "", basicProperties, "headers routing test.".getBytes());

        channel.close();
        connection.close();
    }

    private static Map<String, Object> buildBindingParams() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("x-match", "any");
        map.put("name", "sunyiwei");
        map.put("school", "china.bj.*");

        return map;
    }

    private static Map<String, Object> buildPublishParams() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        map.put("name", "sunyiwei");
        map.put("school", "china.bj.CAS");

        return map;
    }
}
