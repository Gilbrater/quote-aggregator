package com.traderepublic.quotesaggregator.comms.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.traderepublic.quotesaggregator.model.CandleStick;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregatedCandleStickResponse {
    private String isin;
    private String firstKey;
    private Map<String, CandleStick> candleSticks;
}
