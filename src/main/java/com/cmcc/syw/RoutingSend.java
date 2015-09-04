package com.cmcc.syw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by cmcc on 2015/9/4.
 */
public class RoutingSend {
    private static String EXCHANGE_NAME="Routing";
    private static String ROUTING_KEY = "routing_key";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //establish connection
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //publish
        String msg = Utils.getMsg(args);
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, msg.getBytes());

        //close
        channel.close();
        connection.close();
    }
}
