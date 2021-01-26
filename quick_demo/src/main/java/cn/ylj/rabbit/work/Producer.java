package cn.ylj.rabbit.work;

import cn.ylj.rabbit.util.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * work
 * 工作度列模式：发送消息
 */
public class Producer {
    static final String QUEUE_NAME = "work_queue";

    public static void main(String[] args) throws Exception {
        //1.创建连接
        Connection connection = ConnectionUtil.getConnection();
        //2.创建频道；
        Channel channel = connection.createChannel();
        //3.声明队列；
        /**
         * 参数1：队列名称
         * 参数2：是否定义持久化队列（消息会持久化保存在服务器上）
         * 参数3：是否独占本连接
         * 参数4：是否在不使用的时候队列自动删除
         * 参数5：其它参数
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        //4. 发送消息；
        String message = "耗时任务 No: %s";

        /**
         * 参数1：交换机名称；如果没有则指定空字符串（表示使用默认的交换机）
         * 参数2：路由key，简单模式中可以使用队列名称
         * 参数3：消息其它属性
         * 参数4：消息内容
         */
        for (int i = 0; i < 20; i++) {
            //发送给默认交换机，路由key为队列名
            channel.basicPublish("", QUEUE_NAME, null, String.format(message, i).getBytes());
            System.out.println("已发送完所有任务消息：" + String.format(message, i));
        }
        //5. 关闭资源
        channel.close();
        connection.close();
    }
}
