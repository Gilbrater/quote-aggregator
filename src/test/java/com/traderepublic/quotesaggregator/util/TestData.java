package com.traderepublic.quotesaggregator.util;

import com.traderepublic.quotesaggregator.comms.controller.dto.AggregatedCandleStickResponse;
import com.traderepublic.quotesaggregator.comms.controller.dto.InstrumentPrice;
import com.traderepublic.quotesaggregator.comms.controller.dto.InstrumentsResponse;
import com.traderepublic.quotesaggregator.model.CandleStick;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestData {
    public static InstrumentsResponse getInstrumentsResponse() {
        return new InstrumentsResponse(getInstrumentPriceList());
    }

    public static InstrumentPrice getInstrumentPrice(Double price, String isin, String description) {
        return InstrumentPrice.builder()
                .price(price)
                .description(isin)
                .isin(description)
                .build();
    }

    public static List<InstrumentPrice> getInstrumentPriceList() {
        List<InstrumentPrice> instrumentPriceList = new ArrayList<>();
        instrumentPriceList.add(getInstrumentPrice(23.45, "QWFTETGD", "description 1"));
        instrumentPriceList.add(getInstrumentPrice(33.45, "HDGFJRNM", "description 2"));
        instrumentPriceList.add(getInstrumentPrice(43.45, "NDNVHDJD", "description 3"));
        return instrumentPriceList;
    }

    public static AggregatedCandleStickResponse getAggregatedCandleStickResponse(String isin) {
        String firstKey;
        Map<String, CandleStick> candleSticks = new HashMap<>();

        CandleStick candlestick1 = getCandleStick();
        CandleStick candlestick2 = getCandleStick();

        candleSticks.put(isin + ":" + candlestick1.getCloseTimestamp(), candlestick1);
        candleSticks.put(isin + ":" + candlestick2.getCloseTimestamp(), candlestick2);

        firstKey = isin + ":" + candlestick1.getCloseTimestamp();

        return new AggregatedCandleStickResponse(isin, firstKey, candleSticks);
    }

    public static CandleStick getCandleStick() {
        return CandleStick.builder()
                .closePrice(2.0)
                .highPrice(3.0)
                .lowPrice(1.0)
                .openPrice(2.5)
                .openTimestamp(Instant.now().minus(5, ChronoUnit.MINUTES).toString())
                .closeTimestamp(Instant.now().truncatedTo(ChronoUnit.MINUTES).toString()).build();
    }

}
