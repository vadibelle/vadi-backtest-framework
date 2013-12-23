package vadi.test.sarb.listeners;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JLabel;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import vadi.test.sarb.esper.GoogleDownload;
import vadi.test.sarb.esper.util.GenericChart;
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.EODQuote;
import vadi.test.sarb.event.TradeSignal;

@Deprecated
public class OldSimulator implements UpdateListener {

	java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	double portfolio = 0;
	volatile double ammount = 10000;
	volatile double cash = ammount;
	double tradeSize = 0.5; //20% each time
	ConcurrentHashMap<String, Long> positions,short_positions;
	ConcurrentHashMap<String, Double> lastPrice;
	long stocks = 0;
	GetStockQuote gsq ;

	ConcurrentLinkedQueue<String>queue = new ConcurrentLinkedQueue<String>();
	// LineChart chart ;
	GenericChart chart;
	volatile int count;
	JLabel status;
	StringBuilder text;

	public OldSimulator() {
		super();
		// TODO Auto-generated constructor stub
		positions = new ConcurrentHashMap<String, Long>();
		lastPrice = new ConcurrentHashMap<String, Double>();
		short_positions = new ConcurrentHashMap<String, Long>();
		chart = new GenericChart();
		chart.setTitle("PnL");
		// chart.addSeries("position");
		chart.setOrientation("V");
		// chart.setChartType("scatter");
		text = new StringBuilder();
		text.append("Portfolio-Details");
		status = new JLabel(text.toString());
		status.setSize(200, 200);
		gsq = new GetStockQuote();
		count = 0;
		// chart.addSeries("position");

		GoogleDownload.getContainer().getContentPane()
				.add(chart.getChart("position"));
		GoogleDownload.getContainer().getContentPane().add(status);
		// GoogleDownload.getContainer().getContentPane().setSize(300 ,200);
		GoogleDownload.getContainer().pack();

		// GoogleDownload.getContainer().setVisible(true);

	}

	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub

		// Event evt = arg0.getMatchingEvent("op");
		
		//GetStockQuote gsq = new GetStockQuote();
		TradeSignal sig = (TradeSignal) arg0[0].getUnderlying();
		log.info("@@@ Trade indicator received "+sig.getType()+" "+sig.getSymbol());
		//GetStockQuote gsq = new GetStockQuote();
		queue.add(sig.getType());
		gsq.setIndicator(sig.getType());
		//Utility.getInstance().registerEventListener(
		//		"select istream * from EODQuote where symbol='"
			//			+ sig.getSymbol() + "'", gsq);
		// istream removed
		
		Utility.registerEventListener("@Audit select istream * from EODQuote where symbol='"
						+ sig.getSymbol() + "'", gsq);
		
		

		lastPrice.put(sig.getSymbol(), Double.parseDouble(sig.getClose()));
	
		log.info("@@Total cash=" + cash);
		double pos = positionValue();
		log.info("@@Position value " + pos);
		// chart.addSeries("position");
		chart.addData("position", (double) count, pos);
		// chart.addData("position",pos,(double)count);
		count++;
		
		
	}

	private double positionValue() {
		double ret = 0;
		text.replace(0, text.length(), "");
		
		synchronized (this){
		for (String smbl : positions.keySet()) {
			text.append("Portfolio-Details Long ");
			ret += positions.get(smbl) * lastPrice.get(smbl);
			text.append(smbl);
			text.append("-----------");
			text.append(positions.get(smbl));
			text.append("\t");
		}
		text.append("Portfolio-Details Short ");
		for (String smbl : short_positions.keySet()) {
			ret -= short_positions.get(smbl) * lastPrice.get(smbl);
			text.append(smbl);
			text.append("-----------");
			text.append(short_positions.get(smbl));
			text.append("\n");
		}
		ret +=cash;
		log.info(text.toString());
		}
		// status.setText(text.toString());
		return ret;
	}

	public class GetStockQuote implements UpdateListener {
		volatile String indicator;
		volatile boolean done = false;
		public GetStockQuote() {
			super();
		}

		public String getIndicator() {
			return this.indicator;
		}

		public void setIndicator(String indicator) {
			this.indicator = indicator;
			log.info("@@Setting " + indicator + " indicator");
		}

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1) {
			// TODO Auto-generated method stub
			/*if ( done )
			{
				log.info("Object handled event");
				return;
			}
			else
				done = true;*/
			if ( queue.isEmpty())
				return;
			else
				this.indicator = queue.remove();
			log.info("Trade simulate " + arg0.toString());
			
			EODQuote evt = (EODQuote) arg0[0].getUnderlying();
			lastPrice.put(evt.getSymbol(),
					Double.parseDouble(evt.getClose()));

			Timestamp ts = new Timestamp(evt.getTimestamp());
			Timestamp ct = new Timestamp(System.currentTimeMillis());

			// log.info("Cash "+cash+" Stock "+stocks);
			// Date d = new Date(sig.price_timestamp);
			final StringBuilder sb = new StringBuilder();
			sb.append("insert into tradesignal ");
			sb.append(" (smbl,price,type,indicator,price_timestamp,timestamp)");
			sb.append("values (");
			sb.append("'");
			sb.append(evt.symbol);
			sb.append("','");
			if (this.getIndicator().equals("Buy")) {
				double price = Double.parseDouble(evt.getHigh());
				sb.append(evt.high);
				sb.append("','");
				sb.append("Buy");
				sb.append("','");
				log.info("@@Buying signal received " + cash + " " + price + " "
						+ evt.getSymbol());
				
				//cover short during buy signal
				if ( short_positions.containsKey(evt.getSymbol())){
					
					long lp = short_positions.get(evt.getSymbol());
					log.info("@@@ Short cover "+lp+" "+evt.getSymbol());
					if ( cash >= lp*price) {
						cash = cash -lp*price;
						short_positions.remove(evt.getSymbol());
					}
					else
					{
						log.info("FATAL Cannot cover short "+cash+" "+lp*price);
						return;
					}
				}
				//buying tradesize
				double funds = tradeSize*ammount > cash ? cash : (tradeSize*ammount);
				if (funds > price &&  cash > 0) {
					BigDecimal bd = new BigDecimal(funds / price);
					long stock = 0;
					long n = bd.longValue();
					// long n =(long) Math.floor(cash
					// /(Double.parseDouble(b.getPrice())));
					if ( cash > n*price)
						cash = cash - n * (price);
					else n=0;
					log.info("@@Buying " + n + " stocks ");
					if (positions.containsKey(evt.getSymbol())) {
						stock = positions.get(evt.getSymbol());
						stock += n;

						positions.put(evt.getSymbol(), stock);
						
					} else {
						positions.put(evt.getSymbol(), n);
					}
					//log.info("@@Cash " + cash + "Stock " + evt.getSymbol()
					//		+ "value " + stock * price);
					sb.append(indicator);
					sb.append("','");
					sb.append(ts.toString());
					sb.append("','");
					sb.append(ct.toString());
					sb.append("')");
					log.info(sb.toString());
					/*Utility.getInstance().getExecutor().execute(new Runnable() {
						public void run() {
							Utility.getInstance().getDbUtil()
									.execute(sb.toString());
						}
					});*/
				}
				
			}
			if (indicator.equals("Sell")) {
				double price;
				log.info("@@Sell signal received");
				price = Double.parseDouble(evt.getLow());

				if (positions.containsKey(evt.getSymbol())) {
					// log.info("@@Selling "+positions.get(evt.getSymbol())+" stocks");
					log.info("@@Selling signal received " + cash + " " + price
							+ " " + evt.getSymbol());
					long stock = positions.get(evt.getSymbol());
					cash = cash + stock * (price);
					positions.remove(evt.getSymbol());
					sb.append(evt.low);
					sb.append("','");
					sb.append("Sell");
					sb.append("','");
					sb.append(indicator);
					sb.append("','");
					sb.append(ts.toString());
					sb.append("','");
					sb.append(ct.toString());
					sb.append("')");
					log.info(sb.toString());
					/*Utility.getInstance().getExecutor().execute(new Runnable() {
						public void run() {
							Utility.getInstance().getDbUtil()
									.execute(sb.toString());
						}
					});
*/
				}
				 
					//Sell short
					double funds = tradeSize*ammount > cash ? cash : (tradeSize*ammount);
					BigDecimal bd = new BigDecimal(funds / price);
					long stock = 0;
					long n = bd.longValue();
					log.info("@@@ Sell short "+n+" "+evt.getSymbol());
					if ( short_positions.containsKey(evt.getSymbol()))
						stock = short_positions.get(evt.getSymbol());
					
						stock  += n;
						short_positions.put(evt.getSymbol(), stock);
						cash = cash + n*price;
				

				// log.info("Cash "+cash+" Stock "+stocks*Double.parseDouble(sig.getPrice()));
				// chart.addData("cash", (double)count,cash);
				// count++;
			}
		//	Utility.getInstance().getEpSer
			log.info("@@Total cash=" + cash);
			double pos = positionValue();
			log.info("@@Position value " + pos);
			/*chart.addSeries("position");
			chart.addData("position", (double) count, pos);*/
			// chart.addData("position",pos,(double)count);
			count++;
			// Timestamp ts = new
			// Timestamp(Long.parseLong(sig.price_timestamp));
			// Timestamp ct = new Timestamp(System.currentTimeMillis());

		}

	}

}
