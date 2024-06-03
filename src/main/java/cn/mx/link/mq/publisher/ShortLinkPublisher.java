package cn.mx.link.mq.publisher;

import cn.mx.link.dto.MQDataEntity;
import cn.mx.link.consts.MQConsts;
import cn.mx.link.vo.ShortLinkRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShortLinkPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishDataMsg(MQDataEntity mqDataEntity){
        rabbitTemplate.setExchange(MQConsts.delayExchangeName);
        rabbitTemplate.setRoutingKey(MQConsts.delayKey);
        rabbitTemplate.convertAndSend(mqDataEntity);
    }

    public void publishAsync(ShortLinkRequest shortLinkRequest){
        rabbitTemplate.setExchange(MQConsts.dirExchangeName);
        rabbitTemplate.setRoutingKey(MQConsts.dirKey);
        rabbitTemplate.convertAndSend(shortLinkRequest);
    }

}
