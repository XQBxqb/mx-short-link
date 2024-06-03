package cn.mx.link.mq;

import cn.mx.link.consts.MQConsts;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitMqConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        // 定义消息监听器所在的容器工厂
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // 设置容器工厂所用的实例
        factory.setConnectionFactory(connectionFactory);
        // 设置消息在传输中的格式，在这里采用JSON的格式进行传输
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        // 设置并发消费者实例的初始数量。在这里为1个
        factory.setConcurrentConsumers(1);
        // 设置并发消费者实例的最大数量。在这里为1个
        factory.setMaxConcurrentConsumers(1);
        // 设置并发消费者实例中每个实例拉取的消息数量-在这里为1个
        factory.setPrefetchCount(1);
        return factory;
    }
    @Bean(name = "dirQueue")
    public Queue dirQueue(){
        return new Queue(MQConsts.dirQueueName,true);
    }
    @Bean(name = "dirExchange")
    public DirectExchange directExchange(){
        return new DirectExchange(MQConsts.dirExchangeName,true,false);
    }

    @Bean(name = "dirBinding")
    public Binding dirBinding(@Qualifier("dirQueue") Queue queue,@Qualifier("dirExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MQConsts.dirKey);
    }

    @Bean("deadQueue")
    public Queue deadQueue(){return new Queue(MQConsts.deadQueueName,true);}
    @Bean("deadExchange")
    public DirectExchange deadExchange(){return new DirectExchange(MQConsts.deadExchangeName,true,false);}
    @Bean("deadBinding")
    public Binding deadBing(@Qualifier("deadQueue") Queue queue,@Qualifier("deadExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MQConsts.deadKey);
    }

    @Bean("delayQueue")
    public Queue delayQueue(){
        // 创建延迟队列的组成成分map，用于存放组成成分的相关成员
        Map<String, Object> args = new <String, Object>HashMap();
        // 设置消息过期之后的死信交换机(真正消费的交换机)
        args.put("x-dead-letter-exchange", MQConsts.deadExchangeName);
        // 设置消息过期之后死信队列的路由(真正消费的路由)
        args.put("x-dead-letter-routing-key", MQConsts.deadKey);
        // 设定消息的TTL，单位为ms，在这里指的是s
        args.put("x-message-ttl", MQConsts.DELAY_TTL);
        return new Queue(MQConsts.delayQueueName, true,false,false, args);}
    @Bean("delayExchange")
    public DirectExchange delayExchange(){return new DirectExchange(MQConsts.delayExchangeName,true,false);}
    @Bean("delayBinding")
    public Binding delayBing(@Qualifier("delayQueue") Queue queue,@Qualifier("delayExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MQConsts.delayKey);
    }

}
