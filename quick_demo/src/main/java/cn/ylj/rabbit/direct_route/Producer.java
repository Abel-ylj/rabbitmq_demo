package cn.ylj.rabbit.direct_route;

import cn.ylj.rabbit.util.ConnectionUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 声明交换机(direct 直接匹配route_key,消息的route_key与queue的bind_key 一致时，ex会投递消息到该queue)
 * Route模式：发送消息
 */
public class Producer {
    //交换机名称
    static final String DIRECT_EXCHANGE = "direct_exchange";
    //队列名称
    static final String DIRECT_QUEUE_INSERT = "direct_queue_insert";
    //队列名称
    static final String DIRECT_QUEUE_UPDATE = "direct_queue_update";

    public static void main(String[] args) throws Exception {
        //1. 创建连接；
        Connection connection = ConnectionUtil.getConnection();
        //2. 创建频道；
        Channel channel = connection.createChannel();
        //3. 声明交换机；参数1：交换机名称，参数2：交换机类型（fanout,direct,topic）
        channel.exchangeDeclare(DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT);
        //4. 声明队列；
        /**
         * 参数1：队列名称
         * 参数2：是否定义持久化队列（消息会持久化保存在服务器上）
         * 参数3：是否独占本连接
         * 参数4：是否在不使用的时候队列自动删除
         * 参数5：其它参数
         */
        channel.queueDeclare(DIRECT_QUEUE_INSERT, true, false, false, null);
        channel.queueDeclare(DIRECT_QUEUE_UPDATE, true, false, false, null);

        //5. 队列绑定到交换机；参数1：队列名称，参数2：交换机名称，参数3：bind_key （bind_key 和 消息的route_key一致时会被投递）
        channel.queueBind(DIRECT_QUEUE_INSERT, DIRECT_EXCHANGE, "insert");
        channel.queueBind(DIRECT_QUEUE_UPDATE, DIRECT_EXCHANGE, "update");

        //6. 发送消息；
        String message = "你好！小兔纸。路由模式 ；routing key 为 insert ";

        /**
         * 参数1：交换机名称；如果没有则指定空字符串（表示使用默认的交换机）
         * 参数2：路由key，简单模式中可以使用队列名称
         * 参数3：消息其它属性
         * 参数4：消息内容
         */
//        channel.basicPublish(DIRECT_EXCHANGE, "insert", null, message.getBytes());
//        System.out.println("已发送消息：" + message);
//
//        message = "你好！小兔纸。路由模式 ；routing key 为 update ";

        /**
         * 投递消息(update)，给ex，ex根据消息的route_key 会将它投递给bind到该交换机，且bind_key=update的queue中
         * 参数1：交换机名称；如果没有则指定空字符串（表示使用默认的交换机）
         * 参数2：路由key，简单模式中可以使用队列名称
         * 参数3：消息其它属性
         * 参数4：消息内容
         */
        channel.basicPublish(DIRECT_EXCHANGE, "update", null, message.getBytes());
        System.out.println("已发送消息：" + message);
        //6. 关闭资源
        channel.close();
        connection.close();
    }
}
