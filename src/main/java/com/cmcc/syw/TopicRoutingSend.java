package com.cmcc.syw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by cmcc on 2015/9/4.
 */
public class TopicRoutingSend {
    private static String EXCHANGE_NAME="TopicRouting";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //establish connection
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        //publish
        String msg = Utils.getMsg(args);
        String routingKey = Utils.getRoutingKey(args);
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());

        //close
        channel.close();
        connection.close();
    }
}
