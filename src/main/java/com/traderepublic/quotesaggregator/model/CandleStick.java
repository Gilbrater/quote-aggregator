package com.traderepublic.quotesaggregator.model;

import com.traderepublic.quotesaggregator.model.repository.CandleStickDao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
@AllArgsConstructor
public class CandleStick {
    private @NonNull Double lowPrice;
    private @NonNull Double highPrice;
    private @NonNull String openTimestamp;
    private @NonNull String closeTimestamp;
    private @NonNull Double openPrice;
    private @NonNull Double closePrice;

    public static CandleStick fromCandleStickDao(CandleStickDao candleStickDao) {
        return CandleStick.builder()
                .closePrice(candleStickDao.getClosePrice())
                .closeTimestamp(candleStickDao.getCloseTimestamp())
                .highPrice(candleStickDao.getHighPrice())
                .lowPrice(candleStickDao.getLowPrice())
                .openPrice(candleStickDao.getOpenPrice())
                .openTimestamp(candleStickDao.getOpenTimestamp())
                .build();
    }
}
