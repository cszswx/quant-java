package com.jay.quant;

import com.google.common.collect.ImmutableList;
import com.ib.controller.ApiController;
import com.jay.quant.config.IbConnectionHandler;
import com.jay.quant.context.TradingContext;
import com.jay.quant.strategy.Criterion;
import com.jay.quant.strategy.Strategy;
import com.jay.quant.strategy.criterion.NoOpenOrdersExistEntryCriterion;
import com.jay.quant.strategy.criterion.OpenOrdersExistForAllSymbolsExitCriterion;
import com.jay.quant.strategy.kalman.KalmanFilterStrategy;
import com.jay.quant.util.Helper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import com.jay.trading.lib.backtest.BackTest;
import com.jay.trading.lib.backtest.BackTestTradingContext;
import com.jay.trading.lib.model.ClosedOrder;
import com.jay.trading.lib.series.MultipleDoubleSeries;
import com.jay.trading.main.strategy.kalman.Cointegration;

import static java.lang.String.format;

/**
 * Back test Kalman filter cointegration strategy against SPY/VOO pair using Interactive Brokers
 * historical data
 */
public class BackTestApplication {

  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_IB_PORT = 7497;
  public static final int DEFAULT_CLIENT_ID = 0;
  public static final int DAYS_OF_HISTORY = 7;

  public static void main(String[] args) throws IOException, SQLException {

    ApiController controller =
        new ApiController(new IbConnectionHandler(), valueOf -> {}, valueOf -> {});
    controller.connect(DEFAULT_HOST, DEFAULT_IB_PORT, DEFAULT_CLIENT_ID, null);

    List<String> contracts = ImmutableList.of("SPY", "VOO");

    MultipleDoubleSeries priceSeries =
        Helper.getHistoryForSymbols(controller, DAYS_OF_HISTORY, contracts);
    // initialize the backtesting engine
    int deposit = 30000;
    BackTest backTest = new BackTest(deposit, priceSeries);
    backTest.setLeverage(4);

    TradingContext tradingContext = new BackTestTradingContext();

    Strategy strategy =
        new KalmanFilterStrategy(
            contracts.get(0),
            contracts.get(1),
            tradingContext,
            new Cointegration(1e-4, 1e-3));

    Criterion noOpenOrdersExistCriterion =
        new NoOpenOrdersExistEntryCriterion(tradingContext, contracts);
    Criterion openOrdersExistForAllSymbolsCriterion =
        new OpenOrdersExistForAllSymbolsExitCriterion(tradingContext, contracts);

    strategy.addEntryCriterion(noOpenOrdersExistCriterion);
    strategy.addExitCriterion(openOrdersExistForAllSymbolsCriterion);

    // do the backtest
    BackTest.Result result = backTest.run(strategy);

    // show results
    StringBuilder orders = new StringBuilder();
    orders.append("id,amount,side,instrument,from,to,open,close,pl\n");
    for (ClosedOrder order : result.getOrders()) {
      orders.append(format(Locale.US, "%d,%d,%s,%s,%s,%s,%f,%f,%f\n", order.getId(),
          Math.abs(order.getAmount()), order.isLong() ? "Buy" : "Sell", order.getInstrument(),
          order.getOpenInstant(),
          order.getCloseInstant(),
          order.getOpenPrice(),
          order.getClosePrice(),
          order.getPl()));
    }
    System.out.print(orders);

    System.out.println();
    System.out.println("Backtest result of " + strategy.getClass() + ": " + strategy);
    System.out.println("Prices: " + priceSeries);
    System.out.println(format(Locale.US, "Simulated %d days, Initial deposit %d, Leverage %f",
        DAYS_OF_HISTORY, deposit, backTest.getLeverage()));
    System.out.println(format(Locale.US, "Commissions = %f", result.getCommissions()));
    System.out.println(
        format(Locale.US,
            "P/L = %.2f, Final value = %.2f, Result = %.2f%%, Annualized = %.2f%%, Sharpe (rf=0%%) = %.2f",
            result.getPl(),
            result.getFinalValue(),
            result.getReturn() * 100, result.getReturn() / (DAYS_OF_HISTORY / 251.) * 100, result.getSharpe()));
    // TODO: quick and dirty method to finish the program. Implement a better way
    System.exit(0);
  }

}
