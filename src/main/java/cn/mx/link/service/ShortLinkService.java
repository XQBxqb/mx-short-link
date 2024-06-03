package cn.mx.link.service;

import cn.mx.link.config.RedisService;
import cn.mx.link.config.RedissonService;
import cn.mx.link.consts.RedisConsts;
import cn.mx.link.dao.ShortUrlMapper;
import cn.mx.link.dataobjj.ShortUrl;
import cn.mx.link.dto.MQDataEntity;
import cn.mx.link.mq.publisher.ShortLinkPublisher;
import cn.mx.link.utils.BASE62;
import cn.mx.link.vo.ResultResponse;
import cn.mx.link.vo.ShortLinkRequest;
import com.google.common.hash.Hashing;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class ShortLinkService {
    @Autowired
    private RedissonService redissonService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ShortUrlMapper shortUrlMapper;
    @Autowired
    private ShortLinkPublisher shortLinkPublisher;
    public ResultResponse generateShortUrl(ShortLinkRequest shortLinkRequest){
        String sourceUrl = shortLinkRequest.getUrl();
        String shortUrl = getCacheShortUrl(sourceUrl);
        if(shortUrl!=null)
            return ResultResponse.success(domainShortUrl(shortUrl));
        Long hashVal = shortUrlMapper.selectId(sourceUrl);
        if(hashVal!=null){
            shortUrl = parseHashVal(hashVal);
            addCacheData(sourceUrl, shortUrl);
            return ResultResponse.success(domainShortUrl(shortUrl));
        }
        RLock lock = redissonService.getLock(RedisConsts.PREFIX_LOCK+sourceUrl);
        lock.lock();
        try {
            if((shortUrl=getCacheShortUrl(sourceUrl))==null){
                hashVal = generateHashVal(sourceUrl);
                shortUrl=parseHashVal(hashVal);
                updateBloom(shortUrl);
                addCacheData(sourceUrl, shortUrl);
                shortUrlMapper.insert(ShortUrl.builder().id(hashVal).sourceUrl(sourceUrl).build());
                publishDelayMsg(MQDataEntity.builder().id(hashVal).sourceUrl(sourceUrl).build());
            }
        } finally {
            lock.unlock();
        }
        return ResultResponse.success(domainShortUrl(shortUrl));
    }
    private void publishDelayMsg(MQDataEntity mqDataEntity){
        shortLinkPublisher.publishDataMsg(mqDataEntity);
    }
    private long generateHashVal(String sourceUrl){
        long hashVal;
        int count=0;
        while (redissonService.existShortLink(parseHashVal(hashVal=converseHashVal(Hashing.murmur3_32_fixed().hashString(sourceUrl, StandardCharsets.UTF_8).asInt())))){
            sourceUrl+="REPEAT";
            count++;
            if(count>5)
                throw new RuntimeException("访问频繁，请重新尝试");
        };
        return hashVal;
    }

    private long converseHashVal(int hashVal){
        return hashVal < 0 ? (long)Integer.MAX_VALUE -  hashVal : (long)hashVal;
    }

    private String getCacheShortUrl(String sourceUrl) {
        return redisService.get(sourceUrl);
    }

    private void addCacheData(String sourceUrl, String shortUrl) {
        redisService.setString(sourceUrl, shortUrl,30L);
    }

    private String parseHashVal(long hashVal){
        return BASE62.convertDecToBase62(hashVal);
    }

    private void updateBloom(String key){
        redissonService.addShortLink(key);
    }

    private String domainShortUrl(String shortUrl){
        return "http://localhost:8080/short/"+shortUrl;
    }

    public static void main(String[] args) {
        System.out.println(new Long(123123L).toString());
    }
}
