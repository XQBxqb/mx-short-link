package cn.mx.link.controller;

import cn.mx.link.config.RedissonService;
import cn.mx.link.dto.MQDataEntity;
import cn.mx.link.mq.publisher.ShortLinkPublisher;
import cn.mx.link.service.ShortLinkService;
import cn.mx.link.vo.ResultResponse;
import cn.mx.link.vo.ShortLinkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShortLinkController {
    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    RedissonService redissonService;

    @Autowired
    private ShortLinkPublisher shortLinkPublisher;
    @PostMapping("/short/link")
    public ResultResponse shortLink(@RequestBody ShortLinkRequest shortLinkRequest){
        return shortLinkService.generateShortUrl(shortLinkRequest);
    }
    @PostMapping("/short/link/pub")
    public ResultResponse shortLinkPub(@RequestBody ShortLinkRequest shortLinkRequest){
        shortLinkPublisher.publishAsync(shortLinkRequest);
        return ResultResponse.success("finish");
    }
    @PostMapping("/short/link/consume")
    public ResultResponse shortLinkConsume(@RequestBody ShortLinkRequest shortLinkRequest){
        return shortLinkService.generateShortUrl(shortLinkRequest);
    }
}
