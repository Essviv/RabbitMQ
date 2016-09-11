package com.cmcc.syw.rpc;

import com.google.gson.Gson;

import com.cmcc.syw.QueueEndPoint;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RPC模式的服务端
 *
 * Created by sunyiwei on 16/9/11.
 */
public class Server extends QueueEndPoint {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = build();

        //声明queue
        final String QUEUE = "ping";
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE, true, false, false, null);

        channel.basicConsume(QUEUE, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String requestBody = new String(body);
                System.out.println(new Gson().toJson(envelope));
                System.out.println("Receive body : " + requestBody);

                //response
                channel.basicPublish("", properties.getReplyTo(), null, StringUtils.reverse(requestBody).getBytes());
            }
        });
    }
}
