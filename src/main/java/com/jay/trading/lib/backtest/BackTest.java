package com.jay.trading.lib.backtest;

import com.jay.trading.lib.model.ClosedOrder;
import com.jay.trading.lib.series.DoubleSeries;
import com.jay.trading.lib.series.MultipleDoubleSeries;
import com.jay.trading.lib.series.TimeSeries;
import com.jay.trading.lib.util.Statistics;
import com.jay.trading.lib.util.Util;
import com.jay.quant.strategy.Strategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.jay.trading.lib.util.Util.check;

public class BackTest {

  public static class Result {
    DoubleSeries mPlHistory;
    DoubleSeries mMarginHistory;
    double mPl;
    List<ClosedOrder> mOrders;
    double mInitialFund;
    double mFinalValue;
    double mCommissions;

    public Result(double pl, DoubleSeries plHistory, DoubleSeries marginHistory, List<ClosedOrder> orders, double initialFund, double finalValue, double commisions) {
      mPl = pl;
      mPlHistory = plHistory;
      mMarginHistory = marginHistory;
      mOrders = orders;
      mInitialFund = initialFund;
      mFinalValue = finalValue;
      mCommissions = commisions;
    }

    public DoubleSeries getMarginHistory() {
      return mMarginHistory;
    }

    public double getInitialFund() {
      return mInitialFund;
    }

    public DoubleSeries getAccountValueHistory() {
      return getPlHistory().plus(mInitialFund);
    }

    public double getFinalValue() {
      return mFinalValue;
    }

    public double getReturn() {
      return mFinalValue / mInitialFund - 1;
    }

    public double getAnnualizedReturn() {
      return getReturn() * 250 / getDaysCount();
    }

    public double getSharpe() {
      return Statistics.sharpe(Statistics.returns(getAccountValueHistory().toArray()));
    }

    public double getMaxDrawdown() {
      return Statistics.drawdown(getAccountValueHistory().toArray())[0];
    }

    public double getMaxDrawdownPercent() {
      return Statistics.drawdown(getAccountValueHistory().toArray())[1];
    }

    public int getDaysCount() {
      return mPlHistory.size();
    }

    public DoubleSeries getPlHistory() {
      return mPlHistory;
    }

    public double getPl() {
      return mPl;
    }

    public double getCommissions() {
      return mCommissions;
    }

    public List<ClosedOrder> getOrders() {
      return mOrders;
    }
  }

  MultipleDoubleSeries mPriceSeries;
  double mDeposit;
  double mLeverage = 1;

  Strategy mStrategy;
  BackTestTradingContext mContext;

  Iterator<TimeSeries.Entry<List<Double>>> mPriceIterator;
  Result mResult;

  public BackTest(double deposit, MultipleDoubleSeries priceSeries) {
    Util.check(priceSeries.isAscending());
    mDeposit = deposit;
    mPriceSeries = priceSeries;
  }

  public void setLeverage(double leverage) {
    mLeverage = leverage;
  }

  public double getLeverage() {
    return mLeverage;
  }

  public Result run(Strategy strategy) {
    initialize(strategy);
    while (nextStep()) ;
    return mResult;
  }

  public void initialize(Strategy strategy) {
    mStrategy = strategy;
    mContext = (BackTestTradingContext) strategy.getTradingContext();

    mContext.mInstruments = mPriceSeries.getNames();
    mContext.mHistory = new MultipleDoubleSeries(mContext.mInstruments);
    mContext.mInitialFunds = mDeposit;
    mContext.mLeverage = mLeverage;

    mPriceIterator = mPriceSeries.iterator();
    nextStep();
  }

  public boolean nextStep() {
    if (!mPriceIterator.hasNext()) {
      finish();
      return false;
    }

    TimeSeries.Entry<List<Double>> entry = mPriceIterator.next();

    mContext.mPrices = entry.getItem();
    mContext.mInstant = entry.getInstant();
    mContext.mPl.add(mContext.getPl(), entry.getInstant());
    mContext.mFundsHistory.add(mContext.getAvailableFunds(), entry.getInstant());
    if (mContext.getAvailableFunds() < 0) {
      finish();
      return false;
    }

    mStrategy.onTick();

    mContext.mHistory.add(entry);

    return true;
  }

  public Result getResult() {
    return mResult;
  }

  private void finish() {
    for (SimpleOrder order : new ArrayList<>(mContext.mOrders)) {
      mContext.close(order);
    }

    // TODO (replace below code with BackTest results implementation
//        mStrategy.onEnd();

    List<ClosedOrder> orders = Collections.unmodifiableList(mContext.mClosedOrders);
    mResult = new Result(mContext.mClosedPl, mContext.mPl, mContext.mFundsHistory, orders, mDeposit, mDeposit + mContext.mClosedPl, mContext.mCommissions);
  }
}
