package com.traderepublic.quotesaggregator.comms.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.traderepublic.quotesaggregator.model.api.Instrument;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InstrumentPrice {
    private String description;
    private String isin;
    private Double price;

    public static InstrumentPrice fromInstrument(Instrument instrument, Double price) {
        return InstrumentPrice.builder()
                .description(instrument.getDescription())
                .isin(instrument.getIsin())
                .price(price)
                .build();
    }
}

