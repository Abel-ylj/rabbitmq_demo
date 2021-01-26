package cn.ylj.rabbit.topic_route;

import cn.ylj.rabbit.util.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 通配符模式；消费者接收消息
 * 当前的消费者， bind_key = [item.update, item.delete]
 * 收不到生产者的 item.insert 消息
 */
public class Consumer1 {
    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //1. exchage Topic
        channel.exchangeDeclare(Producer.TOPIC_EXCHAGE, BuiltinExchangeType.TOPIC);

        //2. queue
        channel.queueDeclare(Producer.TOPIC_QUEUE_1, true, false, false, null);

        //3. bind queue and topic ,  determinate bind_key(topic pattern)
        channel.queueBind(Producer.TOPIC_QUEUE_1, Producer.TOPIC_EXCHAGE, "item.update");
        channel.queueBind(Producer.TOPIC_QUEUE_1, Producer.TOPIC_EXCHAGE, "item.delete");

        //4. 创建消费者（接收消息并处理消息）；
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //路由key
                System.out.println("路由key为：" + envelope.getRoutingKey());
                //交换机
                System.out.println("交换机为：" + envelope.getExchange());
                //消息id
                System.out.println("消息id为：" + envelope.getDeliveryTag());
                //接收到的消息
                System.out.println("消费者1 --- 接收到的消息为：" + new String(body, "utf-8"));
            }
        };
        //5. 监听队列
        /**
         * 参数1：队列名
         * 参数2：是否要自动确认；设置为true表示消息接收到自动向MQ回复接收到了，MQ则会将消息从队列中删除；
         * 如果设置为false则需要手动确认
         * 参数3：消费者
         */
        channel.basicConsume(Producer.TOPIC_QUEUE_1, true, defaultConsumer);
    }
}
