package com.ECommerce.redis;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate redisTemplate;

    public String getCode(String key){
        try{
            Object o = redisTemplate.opsForValue().get(key);
            return (o!=null)? o.toString():null;
        }catch (Exception e){
            log.error("Exception ",e);
            return null;
        }
    }

    public boolean setCode(String key, String value, Long ttl){
        try{
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
            return true;
        }catch (Exception e){
            log.error("Exception " ,e);
            return false;
        }
    }

    public void deleteCode(String email) {
        redisTemplate.delete(email);
    }
}

