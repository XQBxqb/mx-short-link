package cn.mx.link.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RedissonService {
    @Autowired
    private RedissonClient redissonClient;

    private RBloomFilter<String> rBloomFilter;

    private static final String BLOOM_SHORT_LINK_NAME = "bloomShortLinkName";
    private static final long  expectedCapacity = 100000000L;

    private static final double collisionfactor = 0.03;

    @PostConstruct
    private void init(){
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BLOOM_SHORT_LINK_NAME);
        bloomFilter.tryInit(expectedCapacity,collisionfactor);
        rBloomFilter=bloomFilter;
    }

    public void addShortLink(String key){
        rBloomFilter.add(key);
    }

    public boolean existShortLink(String key){
        return rBloomFilter.contains(key);
    }

    public RLock getLock(String key){
        return redissonClient.getLock(key);
    }
}
