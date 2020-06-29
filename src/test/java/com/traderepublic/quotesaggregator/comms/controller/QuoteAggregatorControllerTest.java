package com.traderepublic.quotesaggregator.comms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traderepublic.quotesaggregator.application.CandleStickService;
import com.traderepublic.quotesaggregator.application.InstrumentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.traderepublic.quotesaggregator.util.TestData.getInstrumentsResponse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuoteAggregatorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CandleStickService candleStickService;

    @MockBean
    InstrumentService instrumentService;

    @Test
    public void get_Aggregation_returnsOkWithAggregatedCandleSticksForInstrument() throws Exception {
//        String isin = "ABCDEFG";
//        Mockito.when(candleStickService.getAggregation(isin)).thenReturn(getAggregatedCandleStickResponse(isin));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/candlestick/summary/"+isin).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
    }

    @Test
    public void get_Instruments_returnsOkWitInstrumentsAndTheirPrice() throws Exception {
//        Mockito.when(instrumentService.getAllInstruments()).thenReturn(getInstrumentsResponse());
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/instruments").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
    }
}