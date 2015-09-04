package com.cmcc.syw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/4.
 */
public class Send {
    private static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //send
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String msg = "hello World.";
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        System.out.println("[X] Send: " + msg);

        //close
        channel.close();
        connection.close();
    }
}
