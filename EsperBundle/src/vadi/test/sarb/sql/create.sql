drop view  if exists min_result;


 drop table if exists Results ;
CREATE TABLE "RESULTS"
(
   SYMBOL varchar(2147483647),
   CASH double,
   TOTAL double,
   DRAWDOWN double,
   RETURNS double,
   LONG_POSITION integer,
   NO_OF_TRADES integer,
   NO_OF_STOPLOSS integer,
   LAST_PRICE double,
   OPEN_SWING double,
   AVERAGE_SWING double,
   AVERAGE_VOLUME double,
   VOLATILITY double,
   MACD double,
   RSI double,
   SHORT_POSITION integer,
   LAST_TRADE varchar(2147483647),
   LAST_TRADE_CLOSE double,
   LAST_TRADE_TYPE varchar(2147483647),
   LAST_TRADE_INDICATOR varchar(2147483647),
   LAST_TRADE_TIMESTAMP timestamp,
   LAST_POSITION varchar(2147483647),
   LAST_POSTION_CLOSE double,
   LAST_POSITION_TYPE varchar(2147483647),
   LAST_POSITION_INDICATOR varchar(2147483647),
   LAST_POSITION_TIMESTAMP timestamp,
   LI integer,
   RSINT integer,
   MLI integer,
   NUMSYM integer,
   LT integer,
   ST integer,
   VLIMIT integer,
   ML integer,
   MSI integer,
   SI integer,
   CURRDATE timestamp DEFAULT CURRENT_TIMESTAMP()
)
;
create view min_result as select symbol,returns,drawdown,volatility,last_trade_indicator,last_trade_timestamp,last_trade_type,li,si,lt,st
from results;
drop table  if exists LIQUID_CASH;
CREATE TABLE "LIQUID_CASH"
(
   CASH double,
   SHORTCASH double,
   CURRDATE timestamp DEFAULT CURRENT_TIMESTAMP()
)
;
drop table if exists POSITION;
CREATE TABLE "POSITION"
(
   ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   SYMBOL varchar(100),
   QTY integer,
   LORS varchar(20),
   PRICE double,
   COST double,
   DATE timestamp
)
;
--CREATE UNIQUE INDEX PRIMARY_KEY_5 ON "POSITION"(ID)

drop table if exists POSITION_ARCHIVE;
CREATE TABLE "POSITION_ARCHIVE"
(
   ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   SYMBOL varchar(100),
   QTY integer,
   LORS varchar(20),
   PRICE double,
   COST double,
   DATE timestamp,
   CURDATE timestamp DEFAULT CURRENT_TIMESTAMP()
)
;
--CREATE UNIQUE INDEX PRIMARY_KEY_8 ON "POSITION_ARCHIVE"(ID)

drop table if exists SIGNALS;
CREATE TABLE "SIGNALS"
(
   ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   SIGNAL varchar(255),
   CURRDATE timestamp DEFAULT CURRENT_TIMESTAMP()
)
;
--CREATE UNIQUE INDEX PRIMARY_KEY_A ON "SIGNALS"(ID)

