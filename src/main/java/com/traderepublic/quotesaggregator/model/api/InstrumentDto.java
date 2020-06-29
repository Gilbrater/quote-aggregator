package com.traderepublic.quotesaggregator.model.api;

import lombok.Data;

@Data
public class InstrumentDto {
    private Instrument data;
    private InstrumentActionType type;

    public enum InstrumentActionType {
        ADD, DELETE
    }
}
