package com.jay.trading.main.strategy;

import com.jay.trading.lib.model.TradingStrategy;

public abstract class AbstractTradingStrategy implements TradingStrategy {
    double mWeight = 1;

    public double getWeight() {
        return mWeight;
    }

    public void setWeight(double weight) {
        mWeight = weight;
    }
}
