package vadi.test.sarb.esper.util;

import java.util.logging.Level;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

public class EMAFactory implements AggregationFunctionFactory{
	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	private  Level level = Level.FINEST;
	private  String fName = "";
	
	public Class getValueType() {
		// TODO Auto-generated method stub
		return Double.class;
	}

	public AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
	log.log(level,"Creating EsperEMA");
		return new EsperEMA();
	}


	public void setFunctionName(String arg0) {
		// TODO Auto-generated method stub
		fName = arg0;
		
	}

	public void validate(AggregationValidationContext arg0) {
		// TODO Auto-generated method stub
		log.log(level,"validated");
		if ((arg0.getParameterTypes().length != 1) ||
			    (arg0.getParameterTypes()[0] != Double.class)) {
			    throw new IllegalArgumentException("Concat aggregation requires a single parameter of type String");
			  
		}
		
	}

}
