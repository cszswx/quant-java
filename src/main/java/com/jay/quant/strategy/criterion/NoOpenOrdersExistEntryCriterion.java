package com.jay.quant.strategy.criterion;

import com.jay.quant.context.TradingContext;
import com.jay.quant.exception.CriterionViolationException;
import com.jay.quant.exception.NoOrderAvailable;
import com.jay.quant.strategy.Criterion;

import java.util.List;
import com.jay.trading.lib.model.Order;


/**
 * Checks that no open orders available for specified symbols
 */
public class NoOpenOrdersExistEntryCriterion implements Criterion {

  protected final List<String> symbols;
  protected final TradingContext tradingContext;

  public NoOpenOrdersExistEntryCriterion(TradingContext tradingContext, List<String> symbols) {
    this.tradingContext = tradingContext;
    this.symbols = symbols;
  }

  @Override
  public boolean isMet() throws CriterionViolationException {
    for(String symbol : symbols) {
      try {
        Order order = tradingContext.getLastOrderBySymbol(symbol);
        if(order != null) {
          return false;
        }
      } catch (NoOrderAvailable ignored) {
      }
    }
    return true;
  }
}