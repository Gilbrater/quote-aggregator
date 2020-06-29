package com.traderepublic.quotesaggregator.model.repository;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class CandleStickDao {
    @Id
    private @NonNull String isin;
    private @NonNull Double lowPrice;
    private @NonNull Double highPrice;
    private @NonNull String openTimestamp;
    private @NonNull String closeTimestamp;
    private @NonNull Double openPrice;
    private @NonNull Double closePrice;
}
