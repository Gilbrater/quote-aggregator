package com.traderepublic.quotesaggregator.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sonus21.rqueue.annotation.RqueueListener;
import com.github.sonus21.rqueue.core.RqueueMessageSender;
import com.traderepublic.quotesaggregator.comms.controller.dto.AggregatedCandleStickResponse;
import com.traderepublic.quotesaggregator.model.CandleStick;
import com.traderepublic.quotesaggregator.model.api.Instrument;
import com.traderepublic.quotesaggregator.model.api.Quote;
import com.traderepublic.quotesaggregator.model.repository.CandleStickDao;
import com.traderepublic.quotesaggregator.model.repository.InstrumentDao;
import com.traderepublic.quotesaggregator.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandleStickService {
    @Value("${instrument.volatility.queue.name}")
    private String instrumentVolatilityQueueName;

    @Value("${aggregation.length.of.time}")
    private int noOfCandleSticksInAggregation;

    private final RqueueMessageSender rqueueMessageSender;
    private final RedisTemplate redisTemplate;
    private final InstrumentRepository instrumentRepository;
    private final ObjectMapper objectMapper;

    @RqueueListener(
            value = "quotes-queue",
            active = "true",
            numRetries = "3",
            visibilityTimeout = "60*60*1000",
            concurrency = "1-3",
            deadLetterQueue = "dl-quotes-name")
    public void handleQuote(Quote quote) {
        boolean isPresent = instrumentRepository.existsById(quote.getIsin());
        if (!isPresent) return;

        //TODO: Lock InstrumentDao Key using redis distributed locking
        InstrumentDao instrumentDao = instrumentRepository.findById(quote.getIsin()).get();
        Instant timestamp = Instant.parse(quote.getTimestamp()).truncatedTo(ChronoUnit.MINUTES);
        quote.setTimestamp(timestamp.toString());
        rqueueMessageSender.enqueue(instrumentVolatilityQueueName, quote);

        if (instrumentDao.getPriceTimeStamp() != null) {
            Instant previousQuoteTimestamp = Instant.parse(instrumentDao.getPriceTimeStamp());
            Instant currentQuoteTimestamp = Instant.parse(quote.getTimestamp());

            //This handles possible race conditions
            if (currentQuoteTimestamp.compareTo(previousQuoteTimestamp) >= 0) {
                instrumentDao.setPrice(quote.getPrice());
                instrumentDao.setPriceTimeStamp(quote.getTimestamp());
                instrumentRepository.save(instrumentDao);
            }
        } else {
            instrumentDao.setPrice(quote.getPrice());
            instrumentDao.setPriceTimeStamp(quote.getTimestamp());
            instrumentRepository.save(instrumentDao);
        }


        String key = quote.getIsin() + ":" + timestamp;
        //TODO: Lock CandleStickDao Key using redis distributed locking
        CandleStickDao candleStickDao = null;

        Map data = redisTemplate.opsForHash().entries(key);
        if (data.size() > 0) {
            candleStickDao = objectMapper.convertValue(data, CandleStickDao.class);
        }

        if (candleStickDao == null) {
            candleStickDao = CandleStickDao.builder()
                    .lowPrice(quote.getPrice())
                    .highPrice(quote.getPrice())
                    .closePrice(quote.getPrice())
                    .openPrice(quote.getPrice())
                    .openTimestamp(quote.getTimestamp())
                    .closeTimestamp(quote.getTimestamp())
                    .isin(quote.getIsin())
                    .build();
        } else {
            if (quote.getPrice() < candleStickDao.getLowPrice()) {
                candleStickDao.setLowPrice(quote.getPrice());
            }

            if (quote.getPrice() > candleStickDao.getHighPrice()) {
                candleStickDao.setHighPrice(quote.getPrice());
            }

            if (quote.getTimestamp().compareTo(candleStickDao.getCloseTimestamp()) >= 0) {
                candleStickDao.setCloseTimestamp(quote.getTimestamp());
                candleStickDao.setClosePrice(quote.getPrice());
            }
        }

        Map<String, Object> updatedCandleStick = objectMapper.convertValue(candleStickDao, Map.class);

        redisTemplate.opsForHash().putAll(key, updatedCandleStick);
        redisTemplate.expire(key, noOfCandleSticksInAggregation, TimeUnit.MINUTES);

        //TODO: Unlock InstrumentDao and CandleStickDao Key using redis distributed locking
    }

    public AggregatedCandleStickResponse getAggregation(String isin) {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        String firstKey = "";
        Map<String, CandleStick> candleSticks = new HashMap<>();
        boolean isfirstKeySet = false;

        for (int i = 0; i < noOfCandleSticksInAggregation; i++) {
            String key = isin + ":" + timestamp;

            CandleStickDao candleStickDao = null;

            Map data = redisTemplate.opsForHash().entries(key);
            if (data.size() > 0) {
                candleStickDao = objectMapper.convertValue(data, CandleStickDao.class);
            }

            if (candleStickDao != null) {
                candleSticks.put(key, CandleStick.fromCandleStickDao(candleStickDao));

                if (!isfirstKeySet) {
                    firstKey = isin + timestamp;
                }
            }

            timestamp = timestamp.minus(1, ChronoUnit.MINUTES);
        }
        return new AggregatedCandleStickResponse(isin, firstKey, candleSticks);
    }

    public boolean removeCandleSticksForInstrument(Instrument instrument) {
        String pattern = instrument.getIsin() + ":*";
        redisTemplate.delete(redisTemplate.keys(pattern));
        return true;
    }
}
