package com.cmcc.syw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/4.
 */
public class NewSend {
    private static String QUEUE_NAME = "hello world!";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //send
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        String msg = Utils.getMsg(args);
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
        System.out.println("[X] Send: " + msg);

        //close
        channel.close();
        connection.close();
    }
}
