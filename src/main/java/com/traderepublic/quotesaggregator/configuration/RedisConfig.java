package com.traderepublic.quotesaggregator.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sonus21.rqueue.config.SimpleRqueueListenerContainerFactory;
import com.github.sonus21.rqueue.listener.RqueueMessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${rqueue.workers.count:3}")
    private int workersCount;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JsonRedisSerializer());
        return template;
    }

    static class JsonRedisSerializer implements RedisSerializer<Object> {

        private final ObjectMapper om;

        public JsonRedisSerializer() {
            this.om = new ObjectMapper().enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }

        @Override
        public byte[] serialize(Object t) throws SerializationException {
            try {
                return om.writeValueAsBytes(t);
            } catch (JsonProcessingException e) {
                throw new SerializationException(e.getMessage(), e);
            }
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {

            if(bytes == null){
                return null;
            }

            try {
                return om.readValue(bytes, Object.class);
            } catch (Exception e) {
                throw new SerializationException(e.getMessage(), e);
            }
        }
    }

    @Bean
    public SimpleRqueueListenerContainerFactory simpleRqueueListenerContainerFactory(
            RqueueMessageHandler rqueueMessageHandler) {
        SimpleRqueueListenerContainerFactory simpleRqueueListenerContainerFactory =
                new SimpleRqueueListenerContainerFactory();
        simpleRqueueListenerContainerFactory.setMaxNumWorkers(workersCount);
        simpleRqueueListenerContainerFactory.setPollingInterval(1);
        simpleRqueueListenerContainerFactory.setRqueueMessageHandler(rqueueMessageHandler);
        return simpleRqueueListenerContainerFactory;
    }
}
