package com.jay.trading.lib.util;

import com.jay.trading.lib.series.DoubleSeries;
import rx.Observable;

public interface HistoricalPriceService {
    Observable<DoubleSeries> getHistoricalAdjustedPrices(String symbol);
}
