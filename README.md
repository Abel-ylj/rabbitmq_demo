## 消息队列MQ

>使用场景：
>
>1. 长耗时 且不需要同步处理的任务。e.g 短信发送，只需告诉用户已经发送就行。
>2. 应用之间通信。 分布式应用，服务与服务之间通信

- 实现：
    1. AMQP: binary wire-level protocal链接协议。定义了规则
    2. JMS: JavaMessage Service 是JAVA给MQ消息中间件厂商的接口规范。给定了API,方式很java
- 常见产品
    1. AcitiveMQ: JMS 
    2. ZeroMQ: c语言
    3. RabbitMQ: AMQP, erlang语言开发，稳定性好
    4. RocketMQ: ali派 JMS
    5. Kafka: 分布式消息系统，高吞吐，大数据

## RabbitMQ

> The core idea in the messaging model in RabbitMQ is that the producer never sends any messages directly to a queue. Actually, quite often the producer doesn't even know if a message will be delivered to any queue at all

- 架构

    1. RabbitMQ是一个引擎，上面可以创建很多VHost，每个VHost都是相互独立的(一般一个项目创建一个)

    2. 对象有:   

        生产者实例prod =>消息{消息体，route_key} =>**交换机exchange**  =>队列queue =>消费者recv

        exchange和queue之间，用bind联系，携带bind_key

    3. 重点是交换机exchange的选择，类型有

        1.default/nameless 

        ​    交换机没有 消息复制，路由选择的功能，进入该交换机的消息出去还是1份

        ​	 后面接1个queue，  在接1个recv就是简单模式， 接多个recv就是工作队列模式(竞争消费)

        2.fanout多播 

        ​    交换机有了多播的功能(消息复制)， 将消息多播到n个queue，每个queue可以有1个recv，或n个recv去竞争消费

        ​    就是发布订阅模式。

        3.direct直接匹配  

        ​    交换机在多播的基础上，有了route功能，此时消息的route_key字段就有了用处，

        ​    ex[direct] 会去判断消息的{route_key} 和 bind到它的bind_key，若一致，就发送到对应的queue 

        4.topic/pattern模式匹配

        ​	在direct的基础加对bind_key添加了pattern匹配

- summary

    1. 简单模式
    2. work模式(工作队列模式)
    3. Publish/Subscribe发布订阅模式
    4. Routing路由模式
    5. Topics主题模式(pattern 通配符模式)
    6. RPC远程调用模式(应该不怎么会用，用dubbo的比较多)

### Virtual Host

>RabbitMQ 是一个mq引擎，可以在上面创建多个mq，每个mq就是Virtual Host,
>
>且每个Virtual Host都是相互isolation。(每个mq都拥有exchange， queue， message，且不互通)

- Virtual Host Name 是以/开头

### Simple Use

exchange [defautl | nameless] ： 

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-01-26-092713.png" alt="image-20210126172713095" style="zoom:50%;" />

### Job|Work

exchange [default | nameless] : 工作队列模式

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-01-26-092558.png" alt="image-20210126172558070" style="zoom:50%;" />

### Publish&Subscribe

exchange [fanout]: 多播模式

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-01-25-123939.png" alt="image-20210125203939257" style="zoom:50%;" />

- 发送者只负责 幂等得创建交换机，将消息给到交换机，后面就不管了

- 接受者要  幂等的创建queue，幂等的创建exchange，将exchange和queue绑定，侦听

- x: exchange： 

    > it just broadcasts all the messages it receives to all the queues it knows
    >
    > ```java
    > //申明一个叫logs的exchange， 类型fanout
    > channel.exchangeDeclare("logs", "fanout");
    > ```

    当交换机exchange后面没有连接的queue时，消息会丢弃

    交换机类型：

    1. Fanout: 广播 ,消息发送给所有绑定到该交换机的队列 

    2. Direct: 定向, 交给指定的路由 routing key 的队列 1:n 

    3. Topic(pattern): 通配符, 将消息交给符合routing pattern 的队列 1:n

    4. Headers 

        

- 消费者从单个queue中获取消息是竞争的，若要每个消费者都拿到消息，就要像图中一样配置

### Direct

exchange[direct]

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-01-26-032137.png" alt="image-20210126112136915" style="zoom:50%;" />

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-01-26-032350.png" alt="image-20210126112349426" style="zoom:50%;" />

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-01-26-032738.png" alt="image-20210126112738547" style="zoom:50%;" />

### Topic(pattern)

Exchange[topic]

<img src="https://yljnote.oss-cn-hangzhou.aliyuncs.com/2021-01-26-033810.png" alt="image-20210126113810068" style="zoom:50%;" />



## SpringBoot整合AMQP

- rabbitMQ是对AMQP协议的实现， 
- RabbitTemplate工具可以发送消息
- @RabbitListener注解可以接受消息
