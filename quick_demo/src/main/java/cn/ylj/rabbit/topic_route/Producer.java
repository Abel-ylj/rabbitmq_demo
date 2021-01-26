package cn.ylj.rabbit.topic_route;

import cn.ylj.rabbit.util.ConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 通配符模式：发送消息, 消息生产方 只负责将消息(带route_key)发到指定交换机上，
 * exchange类型为topic时，会去【模式匹配由bind_key指定】消息的route_key, 投递到对应的queue
 * 交换机绑定queue的事情让 消费方去做
 */
public class Producer {
    //交换机名称
    static final String TOPIC_EXCHAGE = "topic_exchage";
    //队列名称
    static final String TOPIC_QUEUE_1 = "topic_queue_1";
    //队列名称
    static final String TOPIC_QUEUE_2 = "topic_queue_2";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        //1. exchange(topic(pattern)) 声明交换机；参数1：交换机名称，参数2：交换机类型（fanout,direct,topic）
        channel.exchangeDeclare(TOPIC_EXCHAGE, BuiltinExchangeType.TOPIC);
        
        //2. 发送消息1；
        String message = "商品新增。通配符模式 ；routing key 为 item.insert ";
        channel.basicPublish(TOPIC_EXCHAGE, "item.insert", null, message.getBytes());
        System.out.println("已发送消息：" + message);

        message = "商品修改。通配符模式 ；routing key 为 item.update ";
        channel.basicPublish(TOPIC_EXCHAGE, "item.update", null, message.getBytes());
        System.out.println("已发送消息：" + message);

        message = "商品删除。通配符模式 ；routing key 为 item.delete ";
        channel.basicPublish(TOPIC_EXCHAGE, "item.delete", null, message.getBytes());
        System.out.println("已发送消息：" + message);

        channel.close();
        connection.close();
    }
}
