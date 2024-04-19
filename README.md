# Jay Sun, Thomas Dank

# Automate Quant Java framework
This quant Java framework allows development of automated algorithmic trading strategies, supports backtesting using historical data taken from Interactive Brokers, Yahoo Finance, local database or CSV files and
paper or live trade execution via Interactive Brokers TWS Java API.

# Preparation
1. Maven 
2. TWS on port 7497
3. Download TWS jar http://interactivebrokers.github.io/.
4. Install jar locally
5. Add/update maven dependency

# Strategy execution
`$ mvn exec:java@app -Dexec.args="-h localhost -p 7497 -l "SPY,IWM""`

# Backtest execution
`mvn exec:java@test`

