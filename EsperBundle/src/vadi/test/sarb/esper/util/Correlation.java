package vadi.test.sarb.esper.util;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import javax.rmi.CORBA.Util;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;


// @userless indicator
	
public class Correlation implements AggregationFunctionFactory,com.espertech.esper.epl.agg.aggregator.AggregationMethod  {
	
	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.sarb");
	private  Level level = Level.OFF;
	private String name;
	private Double bup ;
	private LinkedList first;
	private LinkedList second;
	private boolean start ;
	PearsonsCorrelation correl;
	public Correlation(){
		name = "";
		bup = 0.0;
		first = new LinkedList();
		second = new LinkedList();
		start = false;
		correl = new PearsonsCorrelation();
		
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
		first.add(Double.parseDouble(c)); 
		second.add(Double.parseDouble(o));
				
	}

	
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		
		if (!start )
			return 0 ;
		
		
		double []farry = new double[first.size()];
		double []sarry = new double[second.size()];

	   for(int i=0;i < first.size();i++)
	   {
		   farry[i] = (Double)first.get(i);
		   sarry[i] = (Double)second.get(i);
	   }
		
		double d = correl.correlation(farry, sarry);
				
		return d;

	}

	@Override
	public void leave(Object arg0) {
		// TODO Auto-generated method stub
		start = true;
		//System.out.println("Leaving");
	//	log.log(level,"Leaving");
		if ( arg0 == null )
			return;
		
		first.remove();
		second.remove();
	}

	@Override
	public Class getValueType() {
				return Double.class;
	}

	@Override
	public AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
		return new Correlation();
	}

	@Override
	public void setFunctionName(String arg0) {
		// TODO Auto-generated method stub
		name = arg0;
		
	}

	@Override
	public void validate(AggregationValidationContext arg0) {
		if ((arg0.getParameterTypes().length != 2) ||
			    (arg0.getParameterTypes()[0] != Double.class)) {
			    throw new IllegalArgumentException("Concat aggregation requires a 2 parameter of type String");
			  
		}
		
	}

}
