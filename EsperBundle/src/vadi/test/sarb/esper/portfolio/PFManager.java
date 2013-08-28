package vadi.test.sarb.esper.portfolio;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import vadi.test.sarb.esper.db.DbUtil;
import vadi.test.sarb.event.EODQuote;
import vadi.test.sarb.event.TradeSignal;

public class PFManager {
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.sarb");

	double portfolio = 0;
	volatile double ammount = 10000;
	volatile double cash = ammount;
	 double shortCash = 0;
	double tradeSize = 0.2; // 20% each time
	ConcurrentHashMap<String, Long> positions, short_positions;
	ConcurrentHashMap<String, Double> lastPrice;
	double slippage = 100;
	long stocks = 0;
	boolean stopOpen = false;
	private static PFManager pfMgr = null;// new PFManager();
	DbUtil dbutil ;

	private PFManager() {
		super();
		positions = new ConcurrentHashMap<String, Long>();
		short_positions = new ConcurrentHashMap<String, Long>();
		lastPrice = new ConcurrentHashMap<String, Double>();
		// pfMgr = new PFManager();
		dbutil = new DbUtil();

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

	public synchronized long getStocks() {
		return stocks;
	}

	public synchronized void setStocks(long stocks) {
		this.stocks = stocks;
	}

	public synchronized double getFunds() {
		return (tradeSize * ammount > cash ? cash : (tradeSize * ammount));
	}

	public synchronized void addLongPosition(TradeSignal sig) {
		try {
			if ( stopOpen )
			{
				log.info("Cannot open, short cover reached");
				return;
			}
			//double price = Double.parseDouble(sig.getHigh());
			//buy on open +100 slippage
			double price = Double.parseDouble(sig.getOpen());
			// System.out.println("OPENING LONG");
			double funds = tradeSize * ammount > cash ? cash
					: (tradeSize * ammount);
			
			if (funds > price ) {
				BigDecimal bd = new BigDecimal(funds / price);
				long stock = 0;
				long n = bd.longValue();
				// long n =(long) Math.floor(cash
				// /(Double.parseDouble(b.getPrice())));
				if (cash > n * price)
					cash = cash - n * (price)-slippage;
				else
					n = 0;
				log.info("@@Buying " + n + " stocks ");
				if ( n <= 0 )
					return;
				if (positions.containsKey(sig.getSymbol())) {
					stock = positions.get(sig.getSymbol());
					stock += n;
				
					positions.put(sig.getSymbol(), stock);
					//portfolio += stock*price;
					} else {
					positions.put(sig.getSymbol(), n);
				//	portfolio += n*price;
					
					double cb = price*n-100;
					Date dt = new Date(Long.parseLong(sig.price_timestamp));
					insertDb(sig.getSymbol(),n,"BUY",
							price,cb,dt);
					
					updateCash(cash);
				}
			}
			m2m(sig.getSymbol(), Double.parseDouble(sig.getClose()));
		} catch (Throwable e) {
			log.info("ERROR adding position " + sig.toString());
		}

	}

	public synchronized void closeLongPosition(TradeSignal sig) {
		try {
		//	double price = Double.parseDouble(sig.getLow());
			double price = Double.parseDouble(sig.getOpen());
			if (positions.containsKey(sig.getSymbol())) {
				// log.info("@@Selling "+positions.get(evt.getSymbol())+" stocks");
				log.info("@@Selling signal received " + cash + " " + price
						+ " " + sig.getSymbol());
				long stock = positions.get(sig.getSymbol());
				cash +=  stock * (price) -slippage;
				positions.remove(sig.getSymbol());
			//	portfolio -= stock*price;
				Date dt = new Date(Long.parseLong(sig.price_timestamp));
				removeDb(sig.getSymbol(),stock,"CLOSE_LONG",price,cash,dt);
				updateCash(cash);
				
			}
			
			m2m(sig.getSymbol(), Double.parseDouble(sig.getClose()));
		} catch (Throwable e) {
			log.info("ERROR closeLongPostion " + sig);
		}

	}

	public synchronized void m2m(String sym, double d) {
		lastPrice.put(sym, d);
	}

	public synchronized double positionValue() {
		double ret = 0;
		//loadPositions();
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
		log.info(text.toString());

		// status.setText(text.toString());
		return ret;
	}

	public synchronized void openShortPosition(TradeSignal sig) {
		try {
			//double price = Double.parseDouble(sig.getLow());
			double price = Double.parseDouble(sig.getOpen());
			double funds = tradeSize * ammount> cash ? cash
					: (tradeSize * ammount);
			
			BigDecimal bd = new BigDecimal(funds / price);
			long stock = 0;
			long n = bd.longValue();
			if (  n <= 0 )
				return;
			log.info("@@@ Sell short " + n + " " + sig.getSymbol());
			if (short_positions.containsKey(sig.getSymbol()))
				stock = short_positions.get(sig.getSymbol());

			stock += n;
			short_positions.put(sig.getSymbol(), stock);
			//portfolio -= stock*price;
			shortCash +=  n * price - slippage;
			double cb = n*price - slippage;
			Date dt = new Date(Long.parseLong(sig.price_timestamp));
			insertDb(sig.getSymbol(),n,"SELL",
					price,cb,dt);
			updateCash(cash);
			
		} catch (Throwable e) {
			
			log.info("Error openshort postion "+sig);
		}

	}

	public synchronized void closeShortPosition(TradeSignal sig) {
		try {
		//	double price = Double.parseDouble(sig.getHigh());
			double price = Double.parseDouble(sig.getOpen());

			if (short_positions.containsKey(sig.getSymbol())) {

				long lp = short_positions.get(sig.getSymbol());
				log.info("@@@ Short cover " + lp + " " + sig.getSymbol());
				if (cash+shortCash >= lp * price) {
					double tmpc = cash+shortCash-slippage;
					cash = tmpc - lp * price;
					shortCash = 0;
					
					//portfolio += lp*price;
					short_positions.remove(sig.getSymbol());
					stopOpen = false;
					Date dt = new Date(Long.parseLong(sig.price_timestamp));
					removeDb(sig.getSymbol(),lp,"CLOSE_SHORT",
							price,tmpc,dt);
					updateCash(cash);
					
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
		String price = eq.getClose();
		synchronized (lastPrice) {
			lastPrice.put(symbol, Double.parseDouble(price));
		}
		
	}
	private void insertDb(String symbol,long quantity,
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
	private void removeDb(String symbol,long quantity,
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
	
	private void updateCash(double csh)
	{
		try {
			String sql="update liquid_cash set cash = "+csh;
			dbutil.execute(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public void loadPositions()
	{
		
		try {
			String sql = "select symbol,qty,lors from position group by symbol";
			ArrayList<ArrayList> res = dbutil.execute(sql);
			
			int i = res.size();
			log.info(res.toString() +" no of rows "+i);
			int j=1;
			if (i > 1 ) 
			{
				while ( j < i ){
					ArrayList arr = res.get(j++);
					if (arr.get(2).toString().equalsIgnoreCase("BUY"))
						positions.put(arr.get(0).toString(),
								Long.parseLong(arr.get(1).toString()));
					else
						short_positions.put(arr.get(0).toString(),
								Long.parseLong(arr.get(1).toString()));
					
				}
			sql = "select cash from liquid_cash";
			 res = dbutil.execute(sql);
			
			 i = res.size();
			 if ( i > 1){
				
				 ArrayList arr = res.get(1);
				 //i=0 meta data, i=1 cash has only one row
				 cash = (Double)arr.get(0);
				 
				 
			 }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateExits(String smb, double price){
}
}
