package vadi.test.sarb.esper.util;

import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

public class Regression  implements AggregationFunctionFactory,com.espertech.esper.epl.agg.aggregator.AggregationMethod  {

	private SimpleRegression reg ;
	int counter = 0;
	String name = "";
	int max=0;
	LinkedList queue ;
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	public Regression()
	{
		reg = new SimpleRegression();
		queue = new LinkedList();
	}
	

	@Override
	public void enter(Object arg0) {
	//	counter ++;
		if (arg0 == null )
			return;
		queue.add(arg0);
//		if ( max < counter)
//			max = counter;
//		else {
//			counter = counter % max;
//			counter ++;
//		}
	//	System.out.println("counter "+counter+" "+max);
	//	double d = Double.parseDouble(arg0.toString());
	//	reg.addData(counter, d);
		
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		reg = new SimpleRegression();
		Iterator i = queue.iterator();
		int j = 0;
		while ( i.hasNext()){
			String obj = i.next().toString();
			Double d = Double.parseDouble(obj);
			if ( Double.isNaN(d))
				continue;
				
			reg.addData(j++,d);
			//System.out.println(" items "+obj);
		}
		//System.out.println("Total "+j);
		return reg.getSlope();
		}

	@Override
	public void leave(Object arg0) {
		// TODO Auto-generated method stub
		//counter --;
		reg = null;
		queue.remove();
		
	}

	@Override
	public Class getValueType() {
		// TODO Auto-generated method stub
		return Double.class;
	}

	
	@Override
	public AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
		return new Regression();
	}

	
	@Override
	public void setFunctionName(String arg0) {
		// TODO Auto-generated method stub
		name = arg0;
		
	}

	@Override
	public void validate(AggregationValidationContext arg0) {
		if ((arg0.getParameterTypes().length != 1) ||
			    (arg0.getParameterTypes()[0] != Double.class)) {
			    throw new IllegalArgumentException("Concat aggregation requires a single parameter of type String");
			  
		}
		
			
		
	}

}
