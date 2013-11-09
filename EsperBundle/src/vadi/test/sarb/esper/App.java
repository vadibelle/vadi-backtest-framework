package vadi.test.sarb.esper;



import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.StartEODQuote;
import vadi.test.sarb.event.StatArb;
import vadi.test.sarb.listeners.StartEOD;
import vadi.test.sarb.listeners.StatArbHandler;
import vadi.test.sarb.listeners.OldSimulator;

/**
 * Hello world!
 *
 */
public class App {
		
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Utility util = Utility.getInstance();
      feedEvents();
        util.info("...finished in " + (System.currentTimeMillis() - startTime) + " ms."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    
    //private static void feedEvents() {
      
    public static  void feedEvents() {
    	
    	Utility.getInstance().info("Feeding events");
    	String sb = "select * from StartEODQuote";
    	Utility.getInstance();
		Utility.registerEventListener(sb, new StartEOD());
    	StartEODQuote evt = new StartEODQuote("SSO"); //$NON-NLS-1$
    	evt.enqueue();
    	evt = new StartEODQuote("GLD");
    	evt.enqueue();
    	
    	
    	sb = "select * from StatArb";
    	Utility.getInstance();
		Utility.registerEventListener(sb, new StatArbHandler());
    	
    	StatArb evt1 = new StatArb("CSCO","MSFT");
    	//evt1.enqueue();
    	
    	evt1 = new StatArb("SSO","GLD");
    	evt1.enqueue();
    	evt1 = new StatArb("MSFT","CSCO");
    	//evt1.enqueue();
    	   	
    	sb = "select * from EODQuote";
    	//Utility.getInstance().registerEventListenet(sb, new EODHandler());
    	//Utility.getInstance().registerEventListenet(sb, new MacdListerner());
    	
    	sb = "select * from TradeSignal";
    	
    	Utility.getInstance();
		Utility.registerEventListener(sb, new OldSimulator());
        	System.out.println("One batch sent"); //$NON-NLS-1$
  
        	System.out.println(Runtime.getRuntime().totalMemory()/1024);
        	System.out.println(Runtime.getRuntime().freeMemory()/1024);
        	
            //OrderEvent event = new OrderEvent("shirt_" + i, i);
           // ePRuntime.sendEvent(event);
    //	}
        }
 
    
}
