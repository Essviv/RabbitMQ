package com.cmcc.syw;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 测试性能
 * Created by sunyiwei on 2015/9/14.
 */
public class RabbitMQBenchmark {
    private static String QUEUE_NAME = "TEST_BENCHMARK";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        final Connection connection = factory.newConnection();

        final int count = 500000;

        //producer
        Channel producer = connection.createChannel();
        producer.queueDeclare(QUEUE_NAME, true, false, false, null);

        int index = 0;
        while (index++ < count) {
            producer.basicPublish("", QUEUE_NAME, null, "patrick".getBytes());
        }

        long startTime = System.currentTimeMillis();

        //consumer
        int threadSize = 10;
        final List<Callable<String>> consumers = new LinkedList<Callable<String>>();
        for (int i = 0; i < threadSize; i++) {
            consumers.add(new Callable<String>() {
                public String call() {
                    try {
                        Channel consumer = connection.createChannel();
                        consumer.basicConsume(QUEUE_NAME, true, new DefaultConsumer(consumer) {
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                                System.out.println(new String(body));
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return "";
                }
            });
        }

        ExecutorService service = Executors.newFixedThreadPool(threadSize);
        service.invokeAll(consumers);

        System.out.println(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) + "Seconds...");
    }
}
