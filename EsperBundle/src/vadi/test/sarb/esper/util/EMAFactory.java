package vadi.test.sarb.esper.util;

import java.util.logging.Level;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

public class EMAFactory implements AggregationFunctionFactory{
	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper.util");
	private  Level level = Level.FINEST;
	private  String fName = "";
	
	@Override
	public Class getValueType() {
		// TODO Auto-generated method stub
		return Double.class;
	}

	@Override
	public AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
	log.log(level,"Creating EsperEMA");
		return new EsperEMA();
	}


	@Override
	public void setFunctionName(String arg0) {
		// TODO Auto-generated method stub
		fName = arg0;
		
	}

	@Override
	public void validate(AggregationValidationContext arg0) {
		// TODO Auto-generated method stub
		log.log(level,"validated");
	//	if ((arg0.getParameterTypes().length != 1) ||
			//    (arg0.getParameterTypes()[0] != Double.class)) {
			 //   throw new IllegalArgumentException(" EMA requires a single parameter of type double "+arg0.getParameterTypes()[0]);
			  
	//	}
		
	}

}
