package com.traderepublic.quotesaggregator.model.repository;

import com.traderepublic.quotesaggregator.model.api.Instrument;
import com.traderepublic.quotesaggregator.model.api.InstrumentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@AllArgsConstructor
@RedisHash("instrument")
public class InstrumentDao {
    @Id
    private @NonNull String isin;
    private @NonNull String description;
    private String latestCandleStick;
    private String priceTimeStamp;
    private Double price;

    public static InstrumentDao fromInstrument(Instrument instrument){
        return InstrumentDao.builder()
                .isin(instrument.getIsin())
                .description(instrument.getDescription())
                .build();
    }
}
