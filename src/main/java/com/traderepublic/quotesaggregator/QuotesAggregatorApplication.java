package com.traderepublic.quotesaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class QuotesAggregatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuotesAggregatorApplication.class, args);
    }
}
