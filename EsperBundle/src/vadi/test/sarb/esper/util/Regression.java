package vadi.test.sarb.esper.util;

import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

public class Regression  implements AggregationFunctionFactory  {

	
	String name = "";
		
	
	public Regression()
	{
	}
	

	public AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
		//return new Regression();
		return new Slope();
	}

	
	//
	public void setFunctionName(String arg0) {
		// TODO Auto-generated method stub
		name = arg0;
		
	}

	//
	public void validate(AggregationValidationContext arg0) {
		/*if ((arg0.getParameterTypes().length != 1) ||
			    (arg0.getParameterTypes()[0] != Double.class)) {
			    throw new IllegalArgumentException("Concat aggregation requires a single parameter of type double");
			  
		}*/
	}
		public Class getValueType() {
			// TODO Auto-generated method stub
			return Double.class;
		}	
		

	
	class  Slope implements AggregationMethod {

		private SimpleRegression reg ;
		int counter = 0;
		String name = "";
		int max=0;
		LinkedList<Double> queue ;
		boolean start = false;
		//
		
		public Slope()
		{
			reg = new SimpleRegression();
			queue = new LinkedList();
		}
		public void clear() {
			// TODO Auto-generated method stub
			
		}

		//
		public void enter(Object arg0) {
			// TODO Auto-generated method stub
			if (arg0 == null )
				return;
			double d = Double.parseDouble(arg0.toString());
			if (Double.isNaN(d))
				return;
			queue.add((Double)arg0);
			
		}

		//
		public Object getValue() {
			// TODO Auto-generated method stub
			counter++;
			if ( !start)
				return -999;
			reg = new SimpleRegression();
			Iterator i = queue.iterator();
			//System.out.println("size "+queue.size());
			int j = 0;
			while ( i.hasNext()){
			//	String obj = i.next().toString();
				Double d = (Double)i.next();
			//	System.out.println("d is "+d);
			//	Double d = Double.parseDouble(obj);
				if ( Double.isNaN(d)){
					continue;
				}	
			//	System.out.println("adding  "+d+" at"+j);
				reg.addData(j++,d);
				
				//System.out.println(" items "+obj);
			}
			//System.out.println("Total "+j);
			queue.remove();
		//	System.out.println("Slope is "+reg.getSlope());
			return reg.getSlope();
		}

		
		public void leave(Object arg0) {
			// TODO Auto-generated method stub
			counter --;
			reg = null;
			//System.out.println("inside leave "+queue.size());
			start = true;
			
		}
		
		public Class getValueType() {
			// TODO Auto-generated method stub
			return Double.class;
		}
		
	}
		
}
