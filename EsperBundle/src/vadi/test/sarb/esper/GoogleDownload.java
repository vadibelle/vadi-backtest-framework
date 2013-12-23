package vadi.test.sarb.esper;


import java.awt.FlowLayout;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import com.espertech.esper.client.annotation.Description;

import vadi.test.sarb.esper.db.DbUtil;
import vadi.test.sarb.esper.util.GenericChart;

//mport org.eclipse.tptp.trace.arm.internal.model.ArmWrapper;

@Deprecated
public class GoogleDownload implements Serializable {
	private static final long serialVersionUID = -5810851685267610906L;
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	static final int NUM_THREADS = 10000;
	static  DbUtil dbUtil = null;
	static final ExecutorService executor = Executors
			.newFixedThreadPool(NUM_THREADS);
	
	static final JFrame container ;
	static {
		container = new JFrame("Dashboard");
		container.setLayout(new FlowLayout());
		container.setSize(700, 700);
		//container.getContentPane().setLayout(new GridLayout());
		//container.getContentPane().setLayout(new BorderLayout());
	//container.getContentPane().setLayout(new GridLayout());
		//container.getContentPane().setSize(400, 150);
		//container.getContentPane().setLayout(new GridBagLayout());
		//container.getContentPane().setLayout(new SpringLayout());

		container.pack();
		container.setVisible(true);
		container.addWindowListener(new java.awt.event.WindowAdapter(){		
		 @Override
		public void windowClosing(java.awt.event.WindowEvent e){
             System.exit(0);
     }});

		
	}
	
	public static JFrame getContainer() {
		return container;
	}

	public static GenericChart addChart(String name)
	{
		GenericChart c = new GenericChart();
		c.setTitle(name);
		getContainer().getContentPane().add(c.getChart(name));
		return c;
		 
	}
	/**
	 * No argument constructor used by the Apama Java framework on application
	 * loading
	 */
	public GoogleDownload() {
		super();
		/*
		 * int armStatus = ArmWrapper.FAILED; try {
		 * ArmWrapper.startTransaction(this, "GoogleDownload", "GoogleDownload",
		 * new Object[] {}); armStatus = ArmWrapper.GOOD; } finally {
		 * ArmWrapper.stopTransaction("GoogleDownload", "GoogleDownload",
		 * armStatus); }
		 */

	}

	/**
	 * Implementation of the Monitor interface onLoad method.
	 */
	/*public void onLoad() {
		// int armStatus = ArmWrapper.FAILED;

		try {

			MatchListener opQuote = new OptionQuoteListener();
			MatchListener ivCalc = new IVCalculator();
			MatchListener eodQuote = new StartEOD();
			MatchListener resend = new ResendMessage();
		//	MatchListener statArb = new StatArbListener();
			MatchListener macd = new MacdListerner();
			EventExpression eventExpr = new EventExpression(
					"all vadi.test.event.StartOptionQuote():op");
			eventExpr.addMatchListener(opQuote);
			eventExpr.addMatchListener(resend);
			// log.info("Start quo");

			EventExpression opExpr = new EventExpression(
					"all vadi.test.event.OptionPrice():op");
			opExpr.addMatchListener(ivCalc);

			EventExpression eodExpr = new EventExpression(
					"all vadi.test.event.StartEODQuote():op");
			eodExpr.addMatchListener(eodQuote);
			// eodExpr.addMatchListener(resend);

			EventExpression eodqExpr = new EventExpression(
					"all vadi.test.event.EODQuote():op");
			//eodqExpr.addMatchListener(ivCalc);
	eodqExpr.addMatchListener(macd);
			

			EventExpression sqExpr = new EventExpression(
					"all vadi.test.event.StartQuote(*):op");
			sqExpr.addMatchListener(ivCalc);
			sqExpr.addMatchListener(resend);
			
			
			EventExpression lasteod = new EventExpression(
			"all vadi.test.event.LastEOD(*):op");
			lasteod.addMatchListener(ivCalc);
			
			EventExpression stockQ = new EventExpression(
			"all vadi.test.event.StockQuote():op");
			stockQ.addMatchListener(ivCalc);
			
			EventExpression aa1 = new EventExpression(
			"all vadi.test.event.Correlate():op");
			aa1.addMatchListener(new CorCovCalculator());
			
			EventExpression sArb = new EventExpression(
			"all vadi.test.event.StatArb():op");
			sArb.addMatchListener(statArb);
			
	
			EventExpression dbQuote = new EventExpression("all vadi.test.event.StockQuoteDB():op");
			dbQuote.addMatchListener(ivCalc);
							
			
			
			// armStatus = ArmWrapper.GOOD;
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}*/
}
