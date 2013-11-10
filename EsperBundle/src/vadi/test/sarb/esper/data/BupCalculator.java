package vadi.test.sarb.esper.data;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import javax.rmi.CORBA.Util;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;


// @userless indicator
	@Deprecated
public class BupCalculator implements AggregationFunctionFactory,com.espertech.esper.epl.agg.aggregator.AggregationMethod  {
	
	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	private  Level level = Level.OFF;
	private String name;
	private Double bup ;
	private LinkedList qc;
	private LinkedList qo;
	private boolean start ;
	
	public BupCalculator(){
		name = "";
		bup = 0.0;
		qc = new LinkedList();
		qo = new LinkedList();
		start = false;
		
		
	}
	@Override
	public void clear() {
		
			
	}

	@Override
	public void enter(Object arg0) {
		// TODO Auto-generated method stub
		if (arg0 == null )
			return;
		Object[] params = (Object[]) arg0;
		String c = params[0].toString();
		String o = params[1].toString();
		qc.add(c); qo.add(o);
			
		
	}

	
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		
		if (!start )
			return 0 ;
		
		double bu = 0;
		double bd = 0;
		double pc = 0;
		double cc = 0;
		double co = 0;
		for ( int i=0; i < qc.size();i++)
		{
			if ( i == 0)
				pc = Double.parseDouble(qc.get(i).toString());
			else
				pc = Double.parseDouble(qc.get(i-1).toString());
			cc = Double.parseDouble(qc.get(i).toString());
			co = Double.parseDouble(qo.get(i).toString());
			
			double d1 = pc - co;
			double d2 = cc - co;
			
			if ( d1 > 0 && d2 > 0)
				bu += 1;
			else if ( d1 < 0 && d2 < 0)
				bd += 1;
			else if ( d1 > 0 && d2 < 0 )
				bd++;
			else bu++;
				
		}
		log.log(level, " getvalue "+(bu/(bu+bd)));
				
		return (bu/(bu+bd));

	}

	@Override
	public void leave(Object arg0) {
		// TODO Auto-generated method stub
		start = true;
		//System.out.println("Leaving");
		log.log(level,"Leaving");
		if ( arg0 == null )
			return;
		
		qc.remove();
		qo.remove();
	}

	@Override
	public Class getValueType() {
				return Double.class;
	}

	@Override
	public AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
		return new BupCalculator();
	}

	@Override
	public void setFunctionName(String arg0) {
		// TODO Auto-generated method stub
		name = arg0;
		
	}

	@Override
	public void validate(AggregationValidationContext arg0) {
		if ((arg0.getParameterTypes().length != 2) ||
			    (arg0.getParameterTypes()[0] != String.class)) {
			    throw new IllegalArgumentException("Concat aggregation requires a 2 parameter of type String");
			  
		}
		
	}

}
