package cn.ylj.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 这里 consumer只需要侦听指定的queue就行，
 * 不用在申明一遍exchange，queue， bind(bind_key)，因为是幂等的，在prod工程已经申明过了，这里再申明一遍也是多余的，效果不会改变
 */
@Component
public class MyListener {

    /**
     * 接收队列消息
     * @param message 接收到的消息
     */
    @RabbitListener(queues = "item_queue")
    public void myListener1(String message){
        System.out.println("消费者接收到消息：" + message);
    }
}
