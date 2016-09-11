package com.cmcc.syw.log;

import com.cmcc.syw.QueueEndPoint;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by sunyiwei on 16/9/9.
 */
public class RealTimeLogger extends QueueEndPoint {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = build();

        declare(connection, "info");
        declare(connection, "warning");
        declare(connection, "error");
    }
}
