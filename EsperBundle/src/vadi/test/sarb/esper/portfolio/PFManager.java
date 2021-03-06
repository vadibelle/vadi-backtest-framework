package vadi.test.sarb.esper.portfolio;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import vadi.test.sarb.esper.db.DbUtil;
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.*;

public class PFManager {
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper.portfolio");
	

	double portfolio = 0;
	volatile double ammount = 10000;
	volatile double cash = ammount;
	 volatile double shortCash = 0;
	 double stopLossAmmount = 1500;

	 
	 double tradeSize = 0.2; // 20% each time
	ConcurrentHashMap<String, Long> positions, short_positions;
	ConcurrentHashMap<String, Double> lastPrice;
	ConcurrentHashMap<String, Double> highPrice;
	ConcurrentHashMap<String, Double> lowPrice;
	ConcurrentHashMap<String, Boolean> hasExit;
	ConcurrentHashMap<String, Double> drawDown;
	ConcurrentHashMap<String, Integer> noOfTrades;
	ConcurrentHashMap<String, String> lastTrade;
		
	HashMap<String,Portfolio> pfList ;
	
	double slippage = 100;
	boolean print = false;
	
	boolean stopOpen = false;
	private static PFManager pfMgr = null;// new PFManager();
	DbUtil dbutil ;
	double stopLoss = 0.2;
	

	
	private PFManager() {
		super();
		/*positions = new ConcurrentHashMap<String, Long>();
		short_positions = new ConcurrentHashMap<String, Long>();
		lastPrice = new ConcurrentHashMap<String, Double>();
		highPrice = new ConcurrentHashMap<String, Double>();
		lowPrice = new ConcurrentHashMap<String, Double>();*/
		// pfMgr = new PFManager();
		dbutil = new DbUtil();
		
		tradeSize = Double.parseDouble(vadi.test.sarb.esper.Messages.getString("trade.size"));
		ammount = Double.parseDouble(vadi.test.sarb.esper.Messages.getString("original.ammount"));
		stopLossAmmount =  Double.parseDouble(vadi.test.sarb.esper.Messages.getString("stop.loss.ammount"));
		stopLoss = Double.parseDouble(vadi.test.sarb.esper.Messages.getString("stop.loss.exit.ratio"));
			
		/*hasExit = new ConcurrentHashMap<String, Boolean>();
		drawDown = new ConcurrentHashMap<String, Double>();
		noOfTrades = new ConcurrentHashMap<String, Integer>();
		lastTrade = new ConcurrentHashMap<String, String>();*/
		print = Utility.getInstance().doPrint();
		pfList = new HashMap<String,Portfolio>();
		
	}

	public synchronized static PFManager getInstance() {
		if (pfMgr == null)
			pfMgr = new PFManager();
		return pfMgr;
	}

	public synchronized double getPortfolio() {
		return portfolio;
	}

	public synchronized void setPortfolio(double portfolio) {
		this.portfolio = portfolio;
	}

	public synchronized double getAmmount() {
		return ammount;
	}

	public synchronized void setAmmount(double ammount) {
		this.ammount = ammount;
	}

	public synchronized double getCash() {
		return cash;
	}

	public synchronized void setCash(double cash) {
		this.cash = cash;
	}

	public synchronized double getTradeSize() {
		return tradeSize;
	}

	public synchronized void setTradeSize(double tradeSize) {
		this.tradeSize = tradeSize;
	}

	public synchronized ConcurrentHashMap<String, Long> getPositions() {
		return positions;
	}

	public synchronized void setPositions(
			ConcurrentHashMap<String, Long> positions) {
		this.positions = positions;
	}

	public synchronized ConcurrentHashMap<String, Long> getShort_positions() {
		return short_positions;
	}

	public synchronized void setShort_positions(
			ConcurrentHashMap<String, Long> short_positions) {
		this.short_positions = short_positions;
	}

	public synchronized ConcurrentHashMap<String, Double> getLastPrice() {
		return lastPrice;
	}

	public synchronized void setLastPrice(
			ConcurrentHashMap<String, Double> lastPrice) {
		this.lastPrice = lastPrice;
	}

	
	public synchronized double getFunds() {
		return (tradeSize * ammount > cash ? cash : (tradeSize * ammount));
	}
	public synchronized Double getFunds(String symbol) {
		if ( pfList.containsKey(symbol)) {
			return pfList.get(symbol).getFunds();
		}
		else
			return ammount;
	}

	public  void addLongPosition(TradeSignal sig) {
		try {
			
			if ( pfList.containsKey(sig.symbol)){
				//System.out.println(" has symbol "+sig.symbol);
				pfList.get(sig.symbol).addLongPosition(sig);
				//System.out.println(pfList.get(sig.symbol).toString());
				return;
			}
			else
			{
				Portfolio p = new Portfolio(sig.symbol);
				p.ammount = ammount;
				p.cash = cash;
				p.print = print;
				p.stopLossAmmount = stopLossAmmount;
				p.stopLoss = stopLoss;
				p.tradeSize = tradeSize;
				p.highPrice = Double.parseDouble(sig.high);
				p.lastPrice = Double.parseDouble(sig.close);
				p.lowPrice = Double.parseDouble(sig.low);
				
			//	tradeSize = 0.2; // 20% each time
				pfList.put(sig.symbol, p);
				p.addLongPosition(sig);
				return;
				
			}
			
			
			/*if ( stopOpen )
			{
				//if ( print)
				log.info("Cannot open, short cover reached");
				return;
			}
			if ( Utility.isSimMode() && Long.parseLong(sig.getPrice_timestamp()) <
					Utility.getInstance().getCurrentTime())
			{
				//if (print)
				log.info("add long old event "+sig.getPrice_timestamp());
				return;
			}
			//double price = Double.parseDouble(sig.getHigh());
			//buy on open +100 slippage
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
				if (positions.containsKey(symbol)) {
					stock = positions.get(symbol);
					stock += n;
					if ( stock*price/ammount > 0.2)
					{
						log.info("More than 20% ");
						cash = prevCash;
						return;
					}
					positions.put(symbol, stock);
					//portfolio += stock*price;
					int noft = noOfTrades.get(symbol);
					noOfTrades.put(symbol, ++noft);
					} else {
					positions.put(symbol, n);
					noOfTrades.put(symbol,1);
				//	portfolio += n*price;
					}
					
					double cb = price*n+slippage;
					Date dt = new Date(Long.parseLong(sig.price_timestamp));
					insertDb(symbol,n,"BUY",
							price,cb,dt);
					
					updateCash();
					if ( positions.containsKey(symbol))
						if ( highPrice.containsKey(symbol))
						{
							double chp = highPrice.get(symbol); 
						
							if (close > chp)
								highPrice.put(symbol, close);
						}
						else
							highPrice.put(symbol, close);
				
			}
			//m2m(sig.getSymbol(), Double.parseDouble(sig.getClose()));
			 
			 */
		} 
		
		catch (Throwable e) {
			log.info("ERROR adding position " + sig.toString());
			e.printStackTrace();
		}

	}

	public  void closeLongPosition(TradeSignal sig) {
		try {
			if (!pfList.containsKey(sig.symbol))
			{
				if ( print )
					log.info("No position in the portfolio ");
				return;
			}
			pfList.get(sig.symbol).closeLongPosition(sig);
			return;
			/*
		//	double price = Double.parseDouble(sig.getLow());
			double price = Double.parseDouble(sig.getOpen());
			String symbol = sig.getSymbol();
//			if ( Utility.isSimMode() && Long.parseLong(sig.getPrice_timestamp()) <
//					Utility.getInstance().getCurrentTime())
//			{
//				log.info("close long old event "+sig.getPrice_timestamp());
//				return;
//			}
			if (positions.containsKey(sig.getSymbol())) {
				// log.info("@@Selling "+positions.get(evt.getSymbol())+" stocks");
				if (print)
				log.info("@@Selling signal received " + cash + " " + price
						+ " " + sig.getSymbol());
				long stock = positions.get(sig.getSymbol());
				cash +=  stock * (price) -slippage;
				positions.remove(sig.getSymbol());
			//	portfolio -= stock*price;
				double cb = stock*price-slippage;
				Date dt = new Date(Long.parseLong(sig.price_timestamp));
				removeDb(sig.getSymbol(),stock,"CLOSE_LONG",price,cb,dt);
				updateCash();
				if ( highPrice.containsKey(symbol))
					highPrice.remove(symbol);
				if ( hasExit.containsKey(symbol))
					hasExit.remove(symbol);
				
			}
			
			m2m(sig.getSymbol(), Double.parseDouble(sig.getClose()));
			*/
		} catch (Throwable e) {
			if ( print)
			log.info("ERROR:Exception closeLongPostion, problem with signal " + sig);
		}

	}

	public synchronized void m2m(String sym, double d) {
		lastPrice.put(sym, d);
		
	}

	public  double positionValue(boolean pr)
	{
		double ret = 0;
		//loadPositions();
		for ( Portfolio p : pfList.values()){
			ret += p.positionValue(pr);
		}
		
		return ret;
		
		/*
		StringBuilder text = new StringBuilder();
		text.replace(0, text.length(), "");
		text.append("cash="+cash+"\n");
		text.append("shortCash="+shortCash+"\n");

		for (String smbl : positions.keySet()) {
			text.append(" Long ");
			ret += positions.get(smbl) * lastPrice.get(smbl);
			text.append(smbl);
			text.append("-----------");
			text.append(positions.get(smbl));
			text.append("\n");

		}
		
		for (String smbl : short_positions.keySet()) {
			text.append(" Short ");
			ret -= short_positions.get(smbl) * lastPrice.get(smbl);
			text.append(smbl);
			text.append("-----------");
			text.append(short_positions.get(smbl));
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
		*/
	}
	
	public  double positionValue() {
		return positionValue(false);
	}

	public  void openShortPosition(TradeSignal sig) {
		try {
			//double price = Double.parseDouble(sig.getLow());
			if (pfList.containsKey(sig.symbol))
			{
				pfList.get(sig.symbol).openShortPosition(sig);
				return;
			}
			else
			{
				Portfolio p = new Portfolio(sig.symbol);
				p.ammount = ammount;
				p.cash = cash;
				p.print = print;
				pfList.put(sig.symbol, p);
				p.openShortPosition(sig);
				return;
			}
			/*
			if ( Utility.isSimMode() && Long.parseLong(sig.getPrice_timestamp()) <
					Utility.getInstance().getCurrentTime())
			{
				log.info("add short old event "+sig.getPrice_timestamp());
				return;
			}
			double price = Double.parseDouble(sig.getOpen());
			String symbol = sig.getSymbol();
			double close = Double.parseDouble(sig.getClose());
			double funds = tradeSize * ammount> cash ? cash
					: (tradeSize * ammount);
			
			BigDecimal bd = new BigDecimal(funds / price);
			long stock = 0;
			long n = bd.longValue();
			if (  n <= 0 )
				return;
			if( print)
			log.info("@@@ Sell short " + n + " " + sig.getSymbol());
			if (short_positions.containsKey(sig.getSymbol()))
				stock = short_positions.get(sig.getSymbol());

			stock += n;
			short_positions.put(sig.getSymbol(), stock);
			//portfolio -= stock*price;
			if ( noOfTrades.containsKey(symbol))
			{
				int i = noOfTrades.get(symbol);
				i++; noOfTrades.put(symbol,i);
			}
			else
				noOfTrades.put(symbol,1);
			
			shortCash +=  n * price - slippage;
			double cb = n*price - slippage;
			Date dt = new Date(Long.parseLong(sig.price_timestamp));
			insertDb(sig.getSymbol(),n,"SELL",
					price,cb,dt);
			updateCash();
			
			if ( short_positions.containsKey(symbol))
				if ( lowPrice.containsKey(symbol))
				{
					double chp = lowPrice.get(symbol); 
				
					if (close < chp)
						lowPrice.put(symbol, close);
					
				}
				else
					lowPrice.put(symbol, close);
					*/
			
		} 
		catch (Throwable e) {
			
			log.info("Error openshort postion "+sig);
		}

	}

	public  void closeShortPosition(TradeSignal sig) {
		try {
			if (pfList.containsKey(sig.symbol))
				pfList.get(sig.symbol).closeShortPosition(sig);
			return;
			/*
		//	double price = Double.parseDouble(sig.getHigh());
			double price = Double.parseDouble(sig.getOpen());
			String symbol = sig.getSymbol();

			if ( Utility.isSimMode() && Long.parseLong(sig.getPrice_timestamp()) <
					Utility.getInstance().getCurrentTime())
			{
				log.info("close short old event "+sig.getPrice_timestamp());
				return;
			}
			if (short_positions.containsKey(sig.getSymbol())) {

				long lp = short_positions.get(sig.getSymbol());
				if ( print)
				log.info("@@@ Short cover " + lp + " " + sig.getSymbol());
				if ((cash+shortCash)*0.9 >= lp * price) {
					double tmpc = cash+shortCash-slippage;
					cash = tmpc - lp * price;
					shortCash = 0;
					
					//portfolio += lp*price;
					short_positions.remove(sig.getSymbol());
					stopOpen = false;
					double cb = lp*price+slippage;
					Date dt = new Date(Long.parseLong(sig.price_timestamp));
					removeDb(sig.getSymbol(),lp,"CLOSE_SHORT",
							price,cb,dt);
					updateCash();
					if ( lowPrice.containsKey(symbol))
						lowPrice.remove(symbol);
					if ( hasExit.containsKey(symbol))
						hasExit.remove(symbol);
					
				} else {
					log.info("FATAL Cannot cover short " + cash + " " + lp * price);
					stopOpen = true;
					return;
				}
			}*/
		} catch (Throwable e) {
		
			log.info("Error short cover "+sig);
		}
	}
	
	public  void updateLastPrice(EODQuote eq) {
		
		if ( pfList.containsKey(eq.symbol))
			pfList.get(eq.symbol).updateLastPrice(eq);
		return;
		/*
		String symbol = eq.getSymbol();
		double price = Double.parseDouble(eq.getClose());
		double high = Double.parseDouble(eq.getHigh());
		double low = Double.parseDouble(eq.getLow());
		lastPrice.put(symbol,price);
		if ( highPrice.containsKey(symbol))
		{
			double pr = highPrice.get(symbol);
			if ( high > pr)
				highPrice.put(symbol, high);
		}
		else
			highPrice.put(symbol, high);
		
		if ( lowPrice.containsKey(symbol))
		{
			double pr = lowPrice.get(symbol);
			if ( low < pr)
				lowPrice.put(symbol, low);
		}
		else
			lowPrice.put(symbol, low);
	
		if ( drawDown.containsKey(symbol))
		{
			double pr = drawDown.get(symbol);
			//not symbol safe. need to get a method per symbol
			double p = positionValue(symbol);
			if (p < pr)
				drawDown.put(symbol, p);
		}
		else
			drawDown.put(symbol, ammount);
		*/
		
	}
	public void insertDb(String symbol,long quantity,
			String lors,double price,double cost, Date dt)
	{
		try {
			String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dt);
			StringBuffer sb = new StringBuffer();
			StringBuffer sb1 = new StringBuffer();
			sb1.append("insert into position ");
			sb.append("(symbol,qty,lors,price,cost,date )");
			sb.append("values ('");
			sb.append(symbol+"',");
			sb.append(quantity+",'");
			sb.append(lors+"',");
			sb.append(price+",");
			sb.append(cost+",'");
			sb.append(date);
			sb.append("')");
			sb1.append(sb.toString());
			
			dbutil.execute(sb1.toString());
			sb1 = new StringBuffer();
			sb1.append("insert into position_archive ");
			sb1.append(sb.toString());
			dbutil.execute(sb1.toString());
			//dbutil.execute("commit");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	private void removeDb(String symbol)
	public void removeDb(String symbol,long quantity,
			String lors,double price,double cost, Date dt)
	{
		try {
			String sql = "delete from position where symbol='"+
					symbol+"'";
			dbutil.execute(sql);
			String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dt);
			StringBuffer sb = new StringBuffer();
			sb.append("insert into position_archive ");
			sb.append("(symbol,qty,lors,price,cost,date )");
			sb.append("values ('");
			sb.append(symbol+"',");
			sb.append(quantity+",'");
			sb.append(lors+"',");
			sb.append(price+",");
			sb.append(cost+",'");
			sb.append(date);
			sb.append("')");
				
			dbutil.execute(sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private synchronized void updateCash()
	{
		try {
			String sql="update liquid_cash set cash = "+cash+
					" ,shortCash="+shortCash;
			
			dbutil.execute(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public void loadPositions()
	{
		ArrayList<ArrayList> res = dbutil.execute("selct distinct symbol from position");
		int i = res.size();
		int j=1;
		if (i > 1 ) 
		{
			while ( j < i ){
				ArrayList arr = res.get(j++);
				loadPosition(arr.get(0).toString());
							
			}
		}
		
	}
	
	public void loadPosition ( String symbol) {
		Portfolio p = new Portfolio(symbol);
		p.ammount = ammount;
		p.cash = cash;
		p.print = print;
		p.stopLossAmmount = stopLossAmmount;
		p.stopLoss = stopLoss;
		p.tradeSize = tradeSize;
		p.print = print;
		p.loadPositions();
		pfList.put(symbol, p);
	
		
	}
	
	
	public synchronized void  generateExits(EODQuote eq){
		try {
		String symbol = eq.getSymbol();
		if ( pfList.containsKey(symbol))
			pfList.get(symbol).generateExits(eq);
		return;
		
	/*	if ( !positions.containsKey(symbol) && !short_positions.containsKey(symbol))
		{
			//log.info("no position for "+symbol);
			return;
		}
		if ( Utility.isSimMode() && eq.getTimestamp() < Utility.getInstance().getCurrentTime())
		{
			log.info("Old event ignore");
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
					if ( hasExit.containsKey(symbol)){
						//log.info("printing hasExit "+hasExit.toString());
						TradeSignal ts = new TradeSignal(symbol,
								eq.getOpen(),eq.getHigh(),eq.getLow(),eq.getClose(),
								"SELL","STOPLOSS",Long.toString(eq.getTimestamp()));
						this.closeLongPosition(ts);
						return;
					}
						
					double chp = highPrice.get(symbol);
					if ( close > chp ) {
						highPrice.put(symbol, close);
						return;
					}
					if ( close < chp*0.85){
						if ( print)
						log.info("Buy: close<.9*high");
						eSig=true;
					}
					if ((cb - close*qty) > 1500 ){
						eSig=true;
						if (print)
						log.info("cb-close*qty >1200");
					}
					
					cb = (1-stopLoss)*cb; // for long if stock price falls below stoploss
					if ( close <= cb )
						eSig = true;
					act = "SELL";
					
				}
				else if ( arr.get(1).equals("SELL")){
						if ( hasExit.containsKey(symbol)){
						
						TradeSignal ts = new TradeSignal(symbol,
								eq.getOpen(),eq.getHigh(),eq.getLow(),eq.getClose(),
								"BUY","STOPLOSS",Long.toString(eq.getTimestamp()));
						this.closeShortPosition(ts);
						return;
						
					}
					double chp = lowPrice.get(symbol);
					if ( close < chp )
					{
						lowPrice.put(symbol, close);
						return;
					}
					if ( close > chp*1.15){
						eSig=true;
						if (print)
						log.info("sell: close>.1.1*low");
					}
					if ((cb -close*qty) < -1500 ){
						eSig=true;
						if (print)
						log.info("cb-close*qty >-1200");
					}
					
					
					cb = (1+stopLoss)*cb; // long if price goes above costbasis
					if ( close >= cb )
						eSig = true;
					act = "BUY";
				}
				if ( eSig) {
					if (print)
					log.info("Genering stop loss for symbol "+symbol+" price "+close+" "+
							 new Timestamp(eq.getTimestamp()).toString());
					hasExit.put(symbol, true);
					StopLoss exitSig = new StopLoss(symbol,act,eq.getClose(),
						eq.getTimestamp());
					exitSig.route();
				}
					
		}*/
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		
	}
	
	public HashMap<String,String> getDetails(String symbol){
		
		
		if ( pfList.containsKey(symbol))
			return pfList.get(symbol).getDetails(symbol);
		else
			return new HashMap<String,String>();
		
		
		/*HashMap<String,String> map = new HashMap<String,String>();
		
		map.put("symbol",symbol);
				
		double d = positionValue(symbol);
		map.put("total",Double.toString(d));
		map.put("returns",Double.toString(d/ammount));
		if ( positions.containsKey(symbol))
			map.put("long",Long.toString(positions.get(symbol)));
		
		if ( short_positions.containsKey(symbol))
			map.put("short",Long.toString(short_positions.get(symbol)));
		
		if ( noOfTrades.containsKey(symbol))
			map.put("no of trades",Long.toString(noOfTrades.get(symbol)));
		if (lastPrice.containsKey(symbol))
			map.put("last price",Double.toString(lastPrice.get(symbol)));
		if ( highPrice.containsKey(symbol))
			map.put("high price",Double.toString(highPrice.get(symbol)));
		if ( lowPrice.containsKey(symbol))
			map.put("low price",Double.toString(lowPrice.get(symbol)));
		if ( drawDown.containsKey(symbol))
			map.put("drawDown",Double.toString(drawDown.get(symbol)/ammount));
		if ( lastTrade.containsKey(symbol));
			map.put("last Trade",lastTrade.get(symbol));
						
		
		return map;*/
	}
	
	public double positionValue(String symbol)
	{
		if ( pfList.containsKey(symbol))
			return pfList.get(symbol).positionValue(symbol);
		else 
			return 0;
		/*double d = cash;
		double e = 0;
		if ( lastPrice.containsKey(symbol))
			e = lastPrice.get(symbol);
		if ( positions.containsKey(symbol))
			d += e * positions.get(symbol);
		if ( short_positions.containsKey(symbol))
			d += e*short_positions.get(symbol);
		
		return d;*/
	}
	
	
	public void addLastTrade(String symbol,String signal)
	{
		if ( pfList.containsKey(symbol))
			pfList.get(symbol).addLastTrade(symbol, signal);
		//lastTrade.put(symbol,signal);
	}
	public void clear()
	{
		pfList.clear();
	}
	public double longPosition(String symbol)
	{
		if ( pfList.containsKey(symbol) )
			return pfList.get(symbol).positions;
		else return 0;
	}
	public boolean hasExit(String symbol)
	{
		if (pfList.containsKey(symbol))
			return pfList.get(symbol).hasExit;
		else
			return false;
	}
	
	public  boolean hasPosition(String symbol)
	{
		return pfList.containsKey(symbol);
	}
	
	public void removePosition(String symbol)
	{
		if (pfList.containsKey(symbol))
			pfList.remove(symbol);
	}
	public void updateStatistics(String symbol,double avgVol,
			double avgSwing,double openSwing)
	{
		if ( pfList.containsKey(symbol))
		{
			Portfolio p = pfList.get(symbol);
			p.avgVol = avgVol;
			p.avgSwing = avgSwing;
			p.openSwing = openSwing;
		}
	}
	
	public Portfolio getPosition(String symbol){
		
			return pfList.get(symbol);
		
	}
}
