package vadi.test.sarb.esper.portfolio;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import vadi.test.sarb.esper.db.DbUtil;
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.EODQuote;
import vadi.test.sarb.event.StopLoss;
import vadi.test.sarb.event.TradeSignal;

public class Portfolio {
	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper.portfolio");
	public double ammount = 10000;
	public double cash = ammount;
	public double shortCash = 0;
	public String symbol;

	public long positions, short_positions, noOfTrades;
	public double lastPrice, highPrice, lowPrice, drawDown;
	public boolean hasExit;
	public String lastTrade,lastPosition;
	public double slippage = 100;
	public boolean print = false;
	public double tradeSize = 0.2;
	PFManager pfm ;
	public double stopLossAmmount = 1500;
	 public  double stopLoss = 0.2;
	 	
	public Portfolio(String symbol) {
		super();
		positions = short_positions = noOfTrades = 0;
		lastPrice = highPrice = 0.0;
		lowPrice = ammount;
		drawDown = cash;
		
		hasExit = false;
		dbutil = new DbUtil();
		pfm = PFManager.getInstance();
		this.symbol = symbol;
		
	}

	double portfolio = 0;
	public boolean stopOpen = false;
	DbUtil dbutil ;

	
	public  double getFunds() {
		return (tradeSize * ammount > cash ? cash : (tradeSize * ammount));
	}
	
	public  void addLongPosition(TradeSignal sig) {
		try {
			if ( stopOpen )
			{
				//if ( print)
				log.info("Cannot open, short cover reached");
				return;
			}
			if ( hasExit ){
				if (print)
				log.info("has pending close long");
				return;
			}
			//double price = Double.parseDouble(sig.getHigh());
			//buy on open +100 slippage
			if ( print)
				log.info(sig.toString());
			double price = Double.parseDouble(sig.getOpen());
			double close = Double.parseDouble(sig.getClose());
			String symbol = sig.getSymbol();
			// System.out.println("OPENING LONG");
			double funds = tradeSize * ammount > cash ? cash
					: (tradeSize * ammount);
			
			if (funds > price ) {
				BigDecimal bd = new BigDecimal(funds / price);
				long stock = 0;
				long n = bd.longValue();
				// long n =(long) Math.floor(cash
				// /(Double.parseDouble(b.getPrice())));
				double prevCash = cash;
				if (cash > n * price)
					cash = cash - n * (price)-slippage;
				else
					n = 0;
				if (print)
				log.info("@@Buying " + n + " stocks ");
				if ( n <= 0 )
					return;
				positions += n;
				noOfTrades ++;
									
					double cb = price*n+slippage;
					lastPosition = sig.toString();
					Date dt = new Date(Long.parseLong(sig.price_timestamp));
					pfm.insertDb(symbol,n,"BUY",
							price,cb,dt);
						
			}
			
		} catch (Throwable e) {
			if ( print ) {
			log.info("ERROR adding position " +e.getMessage()+" "+ sig.toString());
			//e.printStackTrace();
			}
		}

	}

	public  void closeLongPosition(TradeSignal sig) {
		try {
		//	double price = Double.parseDouble(sig.getLow());
			double price = Double.parseDouble(sig.getOpen());
			String symbol = sig.getSymbol();
			

			if (positions > 0) {
				// log.info("@@Selling "+positions.get(evt.getSymbol())+" stocks");
				if (print)
				log.info("@@Selling signal received " + cash + " " + price
						+ " " + sig.getSymbol());
				long stock = positions;
				cash +=  stock * (price) -slippage;
				positions = 0;
			//	portfolio -= stock*price;
				
				double cb = stock*price-slippage;
				lastPosition = sig.toString();
				Date dt = new Date(Long.parseLong(sig.price_timestamp));
				pfm.removeDb(sig.getSymbol(),stock,"CLOSE_LONG",price,cb,dt);
				highPrice = 0;
				hasExit = false;
											
			}
			lastPrice =  Double.parseDouble(sig.getClose());
			
		} catch (Throwable e) {
			if ( print)
			log.info("ERROR closeLongPostion " + sig);
		}

	}

	public double positionValue(boolean pr)
	{
		double ret = 0;
		//loadPositions();
		
		StringBuilder text = new StringBuilder();
		text.replace(0, text.length(), "");
		text.append("cash="+cash+"\n");
		text.append("shortCash="+shortCash+"\n");

		if ( positions > 0) {
			text.append(" Long ");
			ret += positions * lastPrice;
			text.append(symbol);
			text.append("-----------");
			text.append(positions);
			text.append("\n");

		}
		
			if ( short_positions > 0) {
			ret -= short_positions * lastPrice;
			text.append("Short "+symbol);
			text.append("-----------");
			text.append(short_positions);
			text.append("\n");
		}
		ret += cash;
		ret += shortCash;
		text.append(" value=" + ret+"\n");
		//text.append(" portfolio="+portfolio+"\n");
		if ( pr && print )
			log.info(text.toString());

		// status.setText(text.toString());
		return ret;
	}
	
	public  double positionValue() {
		return positionValue(false);
	}

	public  void openShortPosition(TradeSignal sig) {
		try {
			//double price = Double.parseDouble(sig.getLow());
			double price = Double.parseDouble(sig.getOpen());
			String symbol = sig.getSymbol();
			double close = Double.parseDouble(sig.getClose());
			double funds = tradeSize * ammount> cash ? cash
					: (tradeSize * ammount);
			if ( hasExit ){
				if (print)
				log.info("has pending close long");
				return;
			}
			
			BigDecimal bd = new BigDecimal(funds / price);
			long stock = 0;
			long n = bd.longValue();
			if (  n <= 0 )
				return;
			if( print)
			log.info("@@@ Sell short " + n + " " + sig.getSymbol());
			if (short_positions > 0)
				stock = short_positions;
	
			stock += n;
			short_positions = stock;
			//portfolio -= stock*price;
			noOfTrades ++;
						
			shortCash +=  n * price - slippage;
			double cb = n*price - slippage;
			lastPosition = sig.toString();
			Date dt = new Date(Long.parseLong(sig.price_timestamp));
			pfm.insertDb(sig.getSymbol(),n,"SELL",
					price,cb,dt);
			
						
		} catch (Throwable e) {
			
			log.info("Error openshort postion "+sig);
		}
	
	}

	public synchronized void closeShortPosition(TradeSignal sig) {
		try {
		//	double price = Double.parseDouble(sig.getHigh());
			double price = Double.parseDouble(sig.getOpen());
			String symbol = sig.getSymbol();
	
			
			if (short_positions > 0) {
	
				long lp = short_positions;
				if ( print)
				log.info("@@@ Short cover " + lp + " " + sig.getSymbol());
				if ((cash+shortCash)*0.9 >= lp * price) {
					double tmpc = cash+shortCash-slippage;
					cash = tmpc - lp * price;
					shortCash = 0;
					
					//portfolio += lp*price;
					short_positions = 0;
					stopOpen = false;
					double cb = lp*price+slippage;
					lastPosition = sig.toString();
					Date dt = new Date(Long.parseLong(sig.price_timestamp));
					pfm.removeDb(sig.getSymbol(),lp,"CLOSE_SHORT",
							price,cb,dt);
					
					lowPrice = ammount;
					hasExit = false;
									
					} else {
					log.info("FATAL Cannot cover short " + cash + " " + lp * price);
					stopOpen = true;
					return;
				}
			}
		} catch (Throwable e) {
		
			log.info("Error short cover "+sig);
		}
	}

	public  void updateLastPrice(EODQuote eq) {
		
		String symbol = eq.getSymbol();
		double price = Double.parseDouble(eq.getClose());
		double high = Double.parseDouble(eq.getHigh());
		double low = Double.parseDouble(eq.getLow());
		lastPrice = price;
		
		
		if ( high > highPrice)
			highPrice = high;
		if ( low < lowPrice)
			lowPrice = low;
		
		double d = positionValue(true);
		//System.out.println("inside drawdown "+print);
		if ( print)
			log.fine("d = "+d+"dd = "+drawDown);
		if ( d < drawDown )
			 drawDown = d;
		
	}

	public  void  generateExits(EODQuote eq){
		try {
		String symbol = eq.getSymbol();
		
		if ( positions == 0 && short_positions ==0)
		{
			//log.info("no position for "+symbol);
			return;
		}
						
		//String sql = "select sum(price*qty)/sum(qty) as cb,lors,symbol from position "+
		//String sql = "select sum(price*qty) as cb,lors,symbol,sum(qty) as tot from position "+
		String sql="select max(price) as cb,lors,symbol,sum(qty) from position "+
				" where symbol='"+symbol+"' group by lors";
		ArrayList<ArrayList> res = dbutil.execute(sql);
		
	
		int i = res.size();
		//log.info(" Checking exit condition "+res.toString());
		int j=1;
		double cb = 0;
		boolean eSig = false;
		double qty = 0;
		double close = Double.parseDouble(eq.getClose());
		
		
		if (i > 1 ) 
		{
				ArrayList arr = res.get(j++);
				cb = Double.parseDouble(arr.get(0).toString());
				qty = Double.parseDouble(arr.get(3).toString());
				
				String act = "";
				if ( arr.get(1).equals("BUY")){
					if ( hasExit){
						//log.info("printing hasExit "+hasExit.toString());
						TradeSignal ts = new TradeSignal(symbol,
								eq.getOpen(),eq.getHigh(),eq.getLow(),eq.getClose(),
								"SELL","STOPLOSS",Long.toString(eq.getTimestamp()));
						this.closeLongPosition(ts);
						return;
					}
						
					double chp = highPrice;
					if ( close > chp ) {
						highPrice = close;
						return;
					}
					if ( close < chp*stopLoss){
						if ( print)
						log.info("Buy: close<.9*high");
						eSig=true;
					}
					if ((cb - close*qty) > stopLossAmmount ){
						eSig=true;
						if (print)
						log.info("cb-close*qty >1200");
					}
					
					/*cb = (1-stopLoss)*cb; // for long if stock price falls below stoploss
					if ( close <= cb )
						eSig = true;*/
					act = "SELL";
					
				}
				else if ( arr.get(1).equals("SELL")){
						if ( hasExit){
						
						TradeSignal ts = new TradeSignal(symbol,
								eq.getOpen(),eq.getHigh(),eq.getLow(),eq.getClose(),
								"BUY","STOPLOSS",Long.toString(eq.getTimestamp()));
						this.closeShortPosition(ts);
						return;
						
					}
					double chp = lowPrice;
					if ( close < chp )
					{
						lowPrice = close;
						return;
					}
					if ( close > chp*(1+stopLoss)){
						eSig=true;
						if (print)
						log.info("sell: close>.1.1*low");
					}
					if ((cb -close*qty) < -stopLossAmmount ){
						eSig=true;
						if (print)
						log.info("cb-close*qty >-1200");
					}
					
					
					/*cb = (1+stopLoss)*cb; // long if price goes above costbasis
					if ( close >= cb )
						eSig = true;*/
					act = "BUY";
				}
				if ( eSig) {
					if (print)
					log.info("Genering stop loss for symbol "+symbol+" price "+close+" "+
							 new Timestamp(eq.getTimestamp()).toString());
					hasExit =  true;
					StopLoss exitSig = new StopLoss(symbol,act,eq.getClose(),
						eq.getTimestamp());
					exitSig.enqueue();
				}
					
		}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		
	}

	public HashMap<String,String> getDetails(String symbol){
		
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		map.put("symbol",symbol);
				
		double d = positionValue(symbol);
		map.put("total",Double.toString(d));
		map.put("returns",Double.toString(d/ammount));
		//if ( positions >0)
		map.put("long",Long.toString(positions));
		
		//if ( short_positions >0)
			map.put("short",Long.toString(short_positions));
		
		//if ( noOfTrades > 0)
			map.put("no of trades",Long.toString(noOfTrades));
			map.put("last price",Double.toString(lastPrice));
		
			map.put("high price",Double.toString(highPrice));
			map.put("low price",Double.toString(lowPrice));
			map.put("drawDown",Double.toString(drawDown/ammount));
			map.put("last Trade",lastTrade);
			map.put("ratio",Double.toString((ammount-drawDown)/d));	
			map.put("cash", Double.toString(cash));
			map.put("last position",lastPosition);
			
		
		return map;
	}

	public double positionValue(String symbol)
	{
		double d = cash;
		double e = 0;
		e = lastPrice;
		if ( positions > 0)
			d += e * positions;
		if ( short_positions > 0)
			d += e*short_positions;
		
		return d;
	}

	public void addLastTrade(String symbol,String signal)
	{
		lastTrade = signal;
	}
	
	public void loadPositions()
	{
		
		try {
			String sql = "select symbol,sum(qty),lors,sum(cost) from position where symbol='"+
				symbol+"'";
			ArrayList<ArrayList> res = dbutil.execute(sql);
			
			int i = res.size();
			if (print)
			log.info(res.toString() +" no of rows "+i);
			int j=1;
			if (i > 1 ) 
			{
				while ( j < i ){
					ArrayList arr = res.get(j++);
					if (arr.get(2).toString().equalsIgnoreCase("BUY")){
						positions = Long.parseLong(arr.get(1).toString());
						
						//@Todo needs update with actual price 
						//not no of stocks
						highPrice =	Double.parseDouble(arr.get(1).toString());
						double d = Double.parseDouble(arr.get(3).toString());
						if ((ammount - d) > 0 )
							cash = (ammount-d);
						else
							cash = 0;
						
					}
					else {
						short_positions=
								Long.parseLong(arr.get(1).toString());
						lowPrice = 
								Double.parseDouble(arr.get(1).toString());
						double d = Double.parseDouble(arr.get(3).toString());
						shortCash = d;
								
					}
					
				}
			/*sql = "select cash,shortcash from liquid_cash";
			 res = dbutil.execute(sql);
			
			 i = res.size();
			 if ( i > 1){
				
				 ArrayList arr = res.get(1);
				 //i=0 meta data, i=1 cash has only one row
				 cash = Double.parseDouble(arr.get(0).toString());
				 shortCash =  Double.parseDouble(arr.get(1).toString());
				 
				 
			 }*/
			}
		String str = "Positions long "+positions+" short "+short_positions+
				" cash "+cash+" shortCash "+shortCash;
		if ( print)
		log.info(str);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public String toString() {
		return "Portfolio [cash=" + cash + ", shortCash=" + shortCash
				+ ", symbol=" + symbol + ", positions=" + positions
				+ ", short_positions=" + short_positions + ", noOfTrades="
				+ noOfTrades + ", lastPrice=" + lastPrice + ", highPrice="
				+ highPrice + ", lowPrice=" + lowPrice + ", drawDown="
				+ drawDown + ", hasExit=" + hasExit + ", lastTrade="
				+ lastTrade + ", lastPosition=" + lastPosition + ", tradeSize="
				+ tradeSize + ", dbutil=" + dbutil + "]";
	}

	
	
}

