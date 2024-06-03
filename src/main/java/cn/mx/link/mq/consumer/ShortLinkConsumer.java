package cn.mx.link.mq.consumer;

import cn.mx.link.config.RedisService;
import cn.mx.link.dao.ShortUrlMapper;
import cn.mx.link.dto.MQDataEntity;
import cn.mx.link.consts.MQConsts;
import cn.mx.link.service.ShortLinkService;
import cn.mx.link.vo.ResultResponse;
import cn.mx.link.vo.ShortLinkRequest;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ShortLinkConsumer {
    @Autowired
    private ShortUrlMapper shortUrlMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ShortLinkService shortLinkService;

    @RabbitListener(queues = MQConsts.deadQueueName)
    public void consumeMsg(MQDataEntity mqDataEntity, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException{
        shortUrlMapper.deleteById(mqDataEntity.getId());
        redisService.delete(mqDataEntity.getSourceUrl());
        log.info("delay queue work delete hashVal "+mqDataEntity.getId());
    }

    @RabbitListener(queues = MQConsts.dirQueueName)
    public void consumeMsg(ShortLinkRequest shortLinkRequest, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException{
        ResultResponse resultResponse = shortLinkService.generateShortUrl(shortLinkRequest);
        log.info("consume short link : "+shortLinkRequest.getUrl() +" short url : "+resultResponse.getData());
    }
}
