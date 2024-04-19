package com.jay.trading.main.strategy;

import java.util.HashMap;
import java.util.Map;

import com.jay.trading.lib.model.Order;
import com.jay.trading.lib.model.TradingContext;
import com.jay.trading.lib.model.TradingStrategy;

public class BuyAndHold implements TradingStrategy {
    Map<String, Order> mOrders;
    TradingContext mContext;

    @Override public void onStart(TradingContext context) {
        mContext = context;
    }

    @Override public void onTick() {
        if (mOrders == null) {
            mOrders = new HashMap<>();
            mContext.getInstruments().stream().forEach(instrument -> mOrders.put(instrument, mContext.order(instrument, true, 1)));
        }
    }
}
