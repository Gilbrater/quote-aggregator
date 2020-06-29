package com.traderepublic.quotesaggregator.util;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
public class DBUtil {
    private final RedisTemplate redisTemplate;

    public void flushDb() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}
