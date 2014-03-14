package vadi.test.sarb.esper.util;

import java.util.ArrayList;

import vadi.test.sarb.esper.Messages;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

public class RSICalculator implements AggregationFunctionFactory,com.espertech.esper.epl.agg.aggregator.AggregationMethod{
			String  name = "";
		boolean start = false;
		ArrayList list = new ArrayList();
		double emaU = 0 ;
		double emaD = 0;
		int max = 0;
		int counter = 0;
		double prev = 0;
		double  k = 0.75;
		double rsi=0;
		
		
		public RSICalculator()
		{
			
		}
		public  void print(String str){
			//if ( Messages.getString("do.print").equals("true"))
			
			//System.out.println( str);
			
		}
		
		public void clear() {
			
			start = false;
			emaU=emaD=0;
			
		}
		
		
		public void enter(Object arg0) {
			// TODO Auto-generated method stub
			counter ++;
			if ( max < counter )
			max = counter;
			print("counter="+counter+"max="+max);
			k = (2/(1+counter));
			
			double c = Double.parseDouble(arg0.toString());
			if ( prev > c)
				emaD = (prev-c)*k + (1-k)*emaD;
			if ( c > prev)
				emaU = (c-prev)*k + (1-k)*emaU;
			
			if( c == prev )
			{
				emaD = (1-k)*emaD;
				emaU = (1-k)*emaU;
			}
			prev = c;
			if ( emaD !=0 )
				rsi = emaU/emaD;
			
			if ( rsi != 0 )
				rsi = 100 - (100/(1+rsi));
			print("rsi="+rsi);
			list.add(arg0);
				
		}

		
		public Object getValue() {
			if ( !start )
			return 0;
			else
			return rsi;
			
			
		}

		
		public void leave(Object arg0) {
			start = true;
			
			counter--;
			list.remove(arg0);
					
		}

		
		public Class getValueType() {
			// TODO Auto-generated method stub
			return Double.class;
		}

		
		public AggregationMethod newAggregator() {
			// TODO Auto-generated method stub
			return new RSICalculator();
		}

		
		public void setFunctionName(String arg0) {
			// TODO Auto-generated method stub
			name = arg0;
			
		}

		
		public void validate(AggregationValidationContext arg0) {
			// TODO Auto-generated method stub
			if (arg0.getParameterTypes()[0] != Float.class) {
				throw new IllegalArgumentException("Concat aggregation requires a parameter of type float");
			  
		}
			
		}
		
	}
