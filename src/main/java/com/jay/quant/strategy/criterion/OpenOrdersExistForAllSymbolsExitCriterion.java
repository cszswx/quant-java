package com.jay.quant.strategy.criterion;

import com.jay.quant.context.TradingContext;
import com.jay.quant.exception.CriterionViolationException;
import com.jay.quant.exception.NoOrderAvailable;

import java.util.List;

/**
 * All orders exist for specified
 */
public class OpenOrdersExistForAllSymbolsExitCriterion extends NoOpenOrdersExistEntryCriterion {
  public OpenOrdersExistForAllSymbolsExitCriterion(TradingContext tradingContext,
                                                   List<String> symbols) {
    super(tradingContext, symbols);
  }

  @Override
  public boolean isMet() throws CriterionViolationException {
    for(String symbol : symbols) {
      try {
        tradingContext.getLastOrderBySymbol(symbol);

      } catch (NoOrderAvailable e) {
        return false;
      }
    }
    return true;
  }
}
