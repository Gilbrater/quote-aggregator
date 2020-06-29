package com.traderepublic.quotesaggregator.comms.controller;

import com.traderepublic.quotesaggregator.application.CandleStickService;
import com.traderepublic.quotesaggregator.application.InstrumentService;
import com.traderepublic.quotesaggregator.comms.controller.dto.AggregatedCandleStickResponse;
import com.traderepublic.quotesaggregator.comms.controller.dto.InstrumentsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.Writer;

@RestController
@RequiredArgsConstructor
public class QuoteAggregatorController {
    private final CandleStickService candleStickService;
    private final InstrumentService instrumentService;

    @GetMapping(value = "/instruments", produces = MediaType.APPLICATION_JSON_VALUE)
    public InstrumentsResponse getInstrumentsWithPrice() {
        return instrumentService.getAllInstruments();
    }

    @GetMapping(value = "/candlestick/summary/{isin}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AggregatedCandleStickResponse getAggregatedCandleSticksForInstrument(@PathVariable("isin") String isin) {
        return candleStickService.getAggregation(isin);
    }

    @GetMapping(value = "/volatile")
    public void getVolatileInstruments(Writer responseWriter) {
        //TODO: return volatile stream
    }
}
