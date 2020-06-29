package com.traderepublic.quotesaggregator.model.api;

import lombok.Data;

import java.time.Instant;

@Data
public class Quote {
    private Double price;
    private String isin;
    private String timestamp;
}
