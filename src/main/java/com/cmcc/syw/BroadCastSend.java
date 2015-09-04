package com.cmcc.syw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 2015/9/4.
 */
public class BroadCastSend {
    private static String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //建立交换器,注意，这里并没有创建queue, 想想为什么
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        //发布消息
        String msg = Utils.getMsg(args);
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());

        //关闭连接
        channel.close();
        connection.close();
    }
}
