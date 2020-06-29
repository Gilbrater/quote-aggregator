package com.traderepublic.quotesaggregator.model.api;

import lombok.Data;

@Data
public class Instrument {
    private String description;
    private String isin;
}
