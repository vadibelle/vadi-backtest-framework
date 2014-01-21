package vadi.test.sarb.esper.util;

public class SingleRowFunction {
	
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
			double cl = Double.parseDouble(c);
			double pr = Double.parseDouble(p);
			return 100*((cl-pr)/pr);
		} catch (NumberFormatException e) {
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
	
}
