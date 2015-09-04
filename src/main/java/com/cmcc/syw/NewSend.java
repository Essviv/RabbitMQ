package com.cmcc.syw;

import com.rabbitmq.client.*;

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
        String msg = getMsg(args);
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
        System.out.println("[X] Send: " + msg);

        //close
        channel.close();
        connection.close();
    }

    private static String getMsg(String[] args){
        if(args.length < 1){
            return QUEUE_NAME;
        }

        return joinStr(args);
    }

    private static String joinStr(String[] args){
        StringBuilder sb = new StringBuilder(args[0]);

        for (int i = 1; i < args.length; i++) {
            sb.append(".").append(args[i]);
        }

        return sb.toString();
    }
}
