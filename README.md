# Jay Sun, Thomas Dank

# Automate Quant Java framework
This quant Java framework allows development of automated algorithmic trading strategies, supports backtesting using historical data taken from Interactive Brokers, Yahoo Finance, local database or CSV files and
paper or live trade execution via Interactive Brokers TWS Java API.

# Strategy execution
`$ mvn exec:java@app -Dexec.args="-h localhost -p 7497 -l "SPY,IWM""`

# Backtest execution
`mvn exec:java@test`

