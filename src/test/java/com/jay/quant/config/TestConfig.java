package com.jay.quant.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import com.ib.client.OrderType;
import com.ib.controller.ApiController;
import com.jay.quant.context.IbTradingContext;
import com.jay.quant.context.TradingContext;
import com.jay.quant.strategy.IbPerMinuteStrategyRunner;
import com.jay.quant.strategy.StrategyRunner;

import java.sql.SQLException;

/**
 * Test configuration
 */
public class TestConfig extends AbstractModule {

  private static final String HOST = "localhost";
  private static final int PORT = 7497;

  @Override
  protected void configure() {
    bind(StrategyRunner.class).to(IbPerMinuteStrategyRunner.class);
  }

  @Provides
  ApiController apiController() {
    ApiController controller =
        new ApiController(new IbConnectionHandler(), valueOf -> {
        }, valueOf -> {});
    controller.connect(HOST, PORT, 0, null);
    return controller;
  }

  @Provides
  TradingContext tradingContext(ApiController controller) throws SQLException, ClassNotFoundException {
    return new IbTradingContext(
        controller,
        new ContractBuilder(),
        OrderType.MKT,
        2
    );
  }

}
