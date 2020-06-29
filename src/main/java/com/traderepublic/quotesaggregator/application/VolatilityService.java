package com.traderepublic.quotesaggregator.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sonus21.rqueue.annotation.RqueueListener;
import com.traderepublic.quotesaggregator.model.api.Instrument;
import com.traderepublic.quotesaggregator.model.api.Quote;
import com.traderepublic.quotesaggregator.model.enumerations.VolatilityDirection;
import com.traderepublic.quotesaggregator.model.repository.CandleStickDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VolatilityService {
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @RqueueListener(
            value = "${instrument.volatility.queue.name}",
            active = "true",
            numRetries = "3",
            visibilityTimeout = "60*60*1000",
            concurrency = "1-3",
            deadLetterQueue = "dl-quotes-name")
    public void isVolatile(Quote quote) {
        Instant timestamp = Instant.parse(quote.getTimestamp()).minus(5, ChronoUnit.MINUTES);
        CandleStickDao candleStickDao = null;

        String key = quote.getIsin() + ":" + timestamp.toString();
        Map oldCandleStick = redisTemplate.opsForHash().entries(key);
        if (oldCandleStick.size() > 0) {
            candleStickDao = objectMapper.convertValue(oldCandleStick, CandleStickDao.class);
        }

        boolean isVolatile = false;
        VolatilityDirection newVolatilityDirection;

        if (candleStickDao != null) {
            VolatilityDirection oldVolatilityDirection = null;
            Object volatilityData = redisTemplate.opsForValue().get(quote.getIsin() + ":volatile");

            if (volatilityData != null) {
                oldVolatilityDirection = objectMapper.convertValue(volatilityData, VolatilityDirection.class);
            }

            if (oldVolatilityDirection != null && oldVolatilityDirection == VolatilityDirection.NEGATIVE) {
                isVolatile = checkForNegativeVolatility(quote.getPrice(), candleStickDao.getClosePrice());
                newVolatilityDirection = VolatilityDirection.POSITIVE;
            } else {
                isVolatile = checkForPositiveVolatility(quote.getPrice(), candleStickDao.getClosePrice());
                newVolatilityDirection = VolatilityDirection.NEGATIVE;
            }

            if (isVolatile && newVolatilityDirection == VolatilityDirection.NEGATIVE) {
                //TODO: Emit Event composed of quote
                redisTemplate.opsForValue().set(quote.getIsin() + ":volatile", newVolatilityDirection);
            }
        }
    }

    public boolean removeVolatilityRecordsForInstrument(Instrument instrument) {
        redisTemplate.delete(instrument.getIsin() + ":volatile");
        return true;
    }

    private boolean checkForNegativeVolatility(Double currentPrice, Double oldPrice) {
        return currentPrice < oldPrice && ((oldPrice - currentPrice) / oldPrice * 100) >= 10;
    }

    private boolean checkForPositiveVolatility(Double currentPrice, Double oldPrice) {
        return currentPrice > oldPrice && ((currentPrice - oldPrice) / oldPrice * 100) >= 10;
    }
}
