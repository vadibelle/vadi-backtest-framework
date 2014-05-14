package vadi.test.sarb.esper.util;

import org.jfree.util.Log;

import vadi.test.sarb.esper.Messages;
import vadi.test.sarb.esper.portfolio.PFManager;

public class SingleRowFunction {
	final static java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper.util");
	static Utility u = Utility.getInstance();
	
	public static double toDouble(String str){
		try {
			if (str == null)
				return -1;
			Double d = Double.parseDouble(str);
			if (d.isNaN())
				return -1;
			return d;
		
	}
		catch(Throwable e){
			e.printStackTrace();
			return -1;
		}
	
	}
	
	public static double diff(String c, String o){
		try {
			return Double.parseDouble(c) - Double.parseDouble(o);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1L;
		}
	}
	
	public static double pnl(String c, String p) {
		try {
			u.trace("close="+c+" prv="+p);
			if ( c == null || p == null ){
				return -999;
			}
			double cl = Double.parseDouble(c);
			double pr = Double.parseDouble(p);
			//double pnl = 100*((cl-pr)/cl);
			double pnl = 100*Math.log(cl)/Math.log(pr);
		
			u.trace("close="+cl+" prv="+pr+" pnl="+pnl);
			
			return pnl;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1L;
		
	}
	
	public static double pnld(double c, double p) {
		try {
			return 100*((c-p)/p);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1L;
		
	}
	
	public static double atr(String h,String l,String c) {
		double high = Double.parseDouble(h);
		double low = Double.parseDouble(l);
		double close = Double.parseDouble(c);
		return (( high+low+close/3))
		;
	}
	
	public static double longPosition(String symbol)
	{
		double pos = PFManager.getInstance().longPosition(symbol);
		log.info("long position "+symbol+" "+pos);
		return pos;
		
		}
	
}
