package com.traderepublic.quotesaggregator.model.api;

import lombok.Data;

@Data
public class QuoteDto {
    private Quote data;
    private String type;
}
