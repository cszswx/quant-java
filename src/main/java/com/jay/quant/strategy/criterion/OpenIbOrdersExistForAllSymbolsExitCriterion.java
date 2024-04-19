package com.jay.quant.strategy.criterion;

import com.ib.client.OrderStatus;
import com.jay.quant.context.TradingContext;
import com.jay.quant.exception.CriterionViolationException;
import com.jay.quant.exception.NoOrderAvailable;
import com.jay.quant.strategy.Criterion;

import java.util.List;
import com.jay.trading.lib.model.Order;

/**
 * Created by beastie on 1/23/17.
 */
public class OpenIbOrdersExistForAllSymbolsExitCriterion implements Criterion {

  protected final List<String> symbols;
  protected final TradingContext tradingContext;

  public OpenIbOrdersExistForAllSymbolsExitCriterion(TradingContext tradingContext,
                                                     List<String> symbols) {
     this.tradingContext = tradingContext;
     this.symbols = symbols;
  }

  @Override
  public boolean isMet() throws CriterionViolationException {
    for(String symbol : symbols) {
      try {
        Order order = tradingContext.getLastOrderBySymbol(symbol);
        if(order.getOrderStatus() != OrderStatus.Filled) {
          return false;
        }
      } catch (NoOrderAvailable e) {
        return false;
      }
    }
    return true;
  }

}
