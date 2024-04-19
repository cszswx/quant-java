package com.jay.trading.lib.model;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import com.jay.trading.lib.series.TimeSeries;

public interface TradingContext {
    Instant getTime();

    double getLastPrice(String instrument);

    Stream<TimeSeries.Entry<Double>> getHistory(String instrument);

    Order order(String instrument, boolean buy, int amount);

    ClosedOrder close(Order order);

    double getPl();

    List<String> getInstruments();

    double getAvailableFunds();

    double getInitialFunds();

    double getNetValue();

    double getLeverage();
}
