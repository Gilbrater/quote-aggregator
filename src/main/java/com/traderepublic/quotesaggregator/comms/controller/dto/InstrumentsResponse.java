package com.traderepublic.quotesaggregator.comms.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InstrumentsResponse {
    List<InstrumentPrice> instruments;
}
