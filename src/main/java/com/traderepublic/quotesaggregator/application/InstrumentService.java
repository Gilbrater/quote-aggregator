package com.traderepublic.quotesaggregator.application;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import com.traderepublic.quotesaggregator.comms.controller.dto.InstrumentPrice;
import com.traderepublic.quotesaggregator.comms.controller.dto.InstrumentsResponse;
import com.traderepublic.quotesaggregator.model.api.Instrument;
import com.traderepublic.quotesaggregator.model.repository.InstrumentDao;
import com.traderepublic.quotesaggregator.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class InstrumentService {
    private final CandleStickService candleStickService;
    private final VolatilityService volatilityService;
    private final InstrumentRepository repository;
    private final RedisTemplate redisTemplate;

    @RqueueListener(
            value = "${add.instruments.queue.name}",
            active = "true",
            numRetries = "3",
            visibilityTimeout = "60*60*1000",
            concurrency = "1-3",
            deadLetterQueue = "dl-add-instruments-name")
    public void add(Instrument instrument) {
        repository.save(InstrumentDao.fromInstrument(instrument));
    }

    @RqueueListener(
            value = "${delete.instruments.queue.name}",
            active = "true",
            numRetries = "3",
            visibilityTimeout = "60*60*1000",
            concurrency = "1-3",
            deadLetterQueue = "dl-delete-instruments-name")
    public void delete(Instrument instrument) {
        repository.delete(InstrumentDao.fromInstrument(instrument));
        candleStickService.removeCandleSticksForInstrument(instrument);
        volatilityService.removeVolatilityRecordsForInstrument(instrument);
    }

    public InstrumentsResponse getAllInstruments() {
        List<InstrumentPrice> instrumentsList = StreamSupport.stream(
                repository.findAll().spliterator(), false)
                .map(instrumentDao ->
                        InstrumentPrice.builder()
                                .isin(instrumentDao.getIsin())
                                .description(instrumentDao.getDescription())
                                .price(instrumentDao.getPrice())
                                .build()
                )
                .collect(Collectors.toList());
        return new InstrumentsResponse(instrumentsList);
    }
}
