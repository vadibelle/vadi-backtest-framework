package vadi.test.sarb.esper.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OHLCAverage implements Serializable {
	
	Double o, h, l, c, v, n;
	public Double getN() {
		return n;
	}
	public void setN(Double n) {
		this.n = n;
	}
	String symbol;
	
	List<Double> oList = Collections.synchronizedList(new ArrayList<Double>());
	List<Double> hList = Collections.synchronizedList(new ArrayList<Double>());
	List<Double> lList = Collections.synchronizedList(new ArrayList<Double>());
	List<Double> cList = Collections.synchronizedList(new ArrayList<Double>());
	List<Double> vList = Collections.synchronizedList(new ArrayList<Double>());
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public OHLCAverage(Double o, Double h, Double l, Double c, Double v,
			 String symbol) {
		super();
		this.o = o;
		this.h = h;
		this.l = l;
		this.c = c;
		this.v = v;
		this.n = 1.0;
		oList.add(o);
		hList.add(h);
		lList.add(l);
		cList.add(c);
		vList.add(v);
		this.symbol = symbol;
	}
	
	public void update(Double o, Double h, Double l, Double c, Double v
			) {
		
		oList.add(o);
		hList.add(h);
		lList.add(l);
		cList.add(c);
		vList.add(v);
		this.o = (n*this.o+o)/(n+1);
		this.h = (n*this.h+h)/(n+1);
		this.c =(n*this.c+c)/(n+1);
		this.v = (n*this.v+v)/(n+1);
		n ++;
		
}
	
	public List<Double> getoList() {
		return oList;
	}
	public void setoList(List<Double> oList) {
		this.oList = oList;
	}
	public List<Double> gethList() {
		return hList;
	}
	public void sethList(List<Double> hList) {
		this.hList = hList;
	}
	public List<Double> getlList() {
		return lList;
	}
	public void setlList(List<Double> lList) {
		this.lList = lList;
	}
	public List<Double> getcList() {
		return cList;
	}
	public void setcList(List<Double> cList) {
		this.cList = cList;
	}
	public List<Double> getvList() {
		return vList;
	}
	public void setvList(List<Double> vList) {
		this.vList = vList;
	}
	@Override
	public String toString() {
		return "OHLCAverage [c=" + c + ", h=" + h + ", l=" + l + ", n=" + n
				+ ", o=" + o + ", symbol=" + symbol + ", v=" + v + "]";
	}
	public double[] getPremitive(List<Double> dArr)
	{
		double []retArr = new double[dArr.size()];
		for(int i=0;i<dArr.size();i++)
				retArr[i] = dArr.get(i);
		return retArr;
	}
	
}
