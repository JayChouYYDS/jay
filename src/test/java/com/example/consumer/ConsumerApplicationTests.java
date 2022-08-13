package com.example.consumer;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

class ConsumerApplicationTests {
    public static void main(String[] args) throws IOException, TimeoutException {
        //连接信息对象，保存相关的连接信息
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //账号
        connectionFactory.setUsername("guest");
        //密码
        connectionFactory.setPassword("guest");
        //rabbitmq服务所在的ip地址
        connectionFactory.setHost("127.0.0.1");
        //连接对象
        Connection connection =connectionFactory.newConnection();
        //通过连接对象创建信道对象
        Channel channel = connection.createChannel();
        channel.queueDeclare("test1", false, false, false, null);
        channel.queueBind("test1","rptest","");
        //线程回调的作用就是可以把子线程的消息重新传回到主进程中
        Consumer consumer = new DefaultConsumer(channel) {
            //body:消息队列中的消息
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(Thread.currentThread().getName()+message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    //对消息进行手动回执，当前消费者正常消费消息之后，需要手动告诉消息队列，当前消息被接收消费了。这时消息队列才会销毁当前消息，如果消息队列发了但是没有回执，会把消息做到死信队列
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };


        //需要手动重写接口中的方法（线程回调）
//        //线程回调的作用就是可以把子线程的消息重新传回到主进程中
//        DeliverCallback callback = new DeliverCallback() {
//            //通过当前方法接收指定队列中的消息
//            @Override
//            public void handle(String s, Delivery delivery) throws IOException {
//                String message = new String(delivery.getBody(), "UTF-8");
//                System.out.println(Thread.currentThread().getName()+message);
//            }
//        };
//        //把接收消息对象和信道绑定起来
//        channel.basicConsume("test", true,callback, new CancelCallback() {
//            public void handle(String consumerTag) throws IOException {
//            }
//        });
        //是否进行手动应答
        //consumer:用来处理消息的线程回调实现


        boolean autoAck = false;
        channel.basicConsume("test1", autoAck, consumer);
        System.out.println("xxxxxxxxxx");
    }
}
