package com.traderepublic.quotesaggregator.comms.api;

import com.github.sonus21.rqueue.core.RqueueMessageSender;
import com.google.gson.Gson;
import com.traderepublic.quotesaggregator.model.api.InstrumentDto;
import com.traderepublic.quotesaggregator.model.api.QuoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.Instant;

import static com.traderepublic.quotesaggregator.model.api.InstrumentDto.InstrumentActionType.ADD;
import static com.traderepublic.quotesaggregator.model.api.InstrumentDto.InstrumentActionType.DELETE;

@RequiredArgsConstructor
@Service
@Slf4j
public class PartnerServiceApi {
    @Value("${quotes.queue.name}")
    private String quotesQueueName;

    @Value("${add.instruments.queue.name}")
    private String addInstrumentsQueueName;

    @Value("${delete.instruments.queue.name}")
    private String deleteInstrumentsQueueName;

    @Value("${instruments.socket}")
    private String instrumentsSocket;

    @Value("${quotes.socket}")
    private String quotesSocket;

    private final RqueueMessageSender rqueueMessageSender;
    private final Gson gson;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        getInstruments();
        getQuotes();
        //TODO: Handle reconnections
    }

    public void getInstruments() {
        try {
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            webSocketClient.doHandshake(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) {
                    try {
                        InstrumentDto instrumentDto = gson.fromJson(message.getPayload(), InstrumentDto.class);

                        if (ADD == instrumentDto.getType()) {
                            rqueueMessageSender.enqueue(addInstrumentsQueueName, instrumentDto.getData());
                        } else if (DELETE == instrumentDto.getType()) {
                            rqueueMessageSender.enqueue(deleteInstrumentsQueueName, instrumentDto.getData());
                        }
                    } catch (Exception ex) {
                        log.error(ex.getLocalizedMessage());
                    }
                }

                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("established connection - " + session);
                }
            }, new WebSocketHttpHeaders(), URI.create(instrumentsSocket)).get();
        } catch (Exception e) {
            log.error("Exception while accessing websockets", e);
        }
    }

    public void getQuotes() {
        try {
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            webSocketClient.doHandshake(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) {
                    try {
                        QuoteDto quoteDto = gson.fromJson(message.getPayload(), QuoteDto.class);
                        quoteDto.getData().setTimestamp(Instant.now().toString());
                        rqueueMessageSender.enqueue(quotesQueueName, quoteDto.getData());
                    } catch (Exception ex) {
                        log.error(ex.getLocalizedMessage());
                    }
                }

                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("established connection - " + session);
                }
            }, new WebSocketHttpHeaders(), URI.create(quotesSocket)).get();
        } catch (Exception e) {
            log.error("Exception while accessing websockets", e);
        }
    }
}
