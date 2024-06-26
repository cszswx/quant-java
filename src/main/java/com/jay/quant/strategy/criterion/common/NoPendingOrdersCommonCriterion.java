package com.jay.quant.strategy.criterion.common;


import com.ib.client.OrderStatus;
import com.jay.quant.context.TradingContext;
import com.jay.quant.exception.CriterionViolationException;
import com.jay.quant.exception.NoOrderAvailable;
import com.jay.quant.strategy.Criterion;

import java.util.List;

/**
 * Test if there are any orders that are in pending (not filled yet) state
 */
public class NoPendingOrdersCommonCriterion implements Criterion {

  private final TradingContext tradingContext;
  private final List<String> symbols;

  public NoPendingOrdersCommonCriterion(TradingContext tradingContext, List<String> symbols) {
    this.tradingContext = tradingContext;
    this.symbols = symbols;
  }

  @Override
  public boolean isMet() throws CriterionViolationException {
    for(String symbol : symbols) {
      try {
        if(tradingContext.getLastOrderBySymbol(symbol) != null
            && tradingContext.getLastOrderBySymbol(symbol).getOrderStatus() != OrderStatus.Filled) {
           return false;
        }
      } catch (NoOrderAvailable noOrderAvailable) {}   // Do nothing here as there is not order
    }
    return true;
  }
}
