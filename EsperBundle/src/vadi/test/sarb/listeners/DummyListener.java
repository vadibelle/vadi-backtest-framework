package vadi.test.sarb.listeners;

import vadi.test.sarb.esper.util.GenericChart;
import vadi.test.sarb.esper.util.Utility;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class DummyListener implements UpdateListener {

	private GenericChart c = null;
	public DummyListener()
	{ 	try {
		if ( Utility.getInstance().doPrint())
			System.out.println("Dummy listener created");
		c = Utility.addChart("crossover");
		
	}
	catch(Throwable e){
		e.printStackTrace();
	}
	}
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try {
		if (Utility.getInstance().doPrint())
		System.out.println(" dummy Event recived "+arg0[0].getUnderlying());
		String sym = arg0[0].get("symbol").toString();
		String sr = sym + ".close";
		c.addSeries(sr);
		c.addData(sr,Double.parseDouble(arg0[0].get("q.close").toString()));
		sr = sym+".esema";
		c.addSeries(sr);
		c.addData(sr,Double.parseDouble(arg0[0].get("es.ema").toString()));
		sr = sym+".elema";
		c.addSeries(sr);
		c.addData(sr,Double.parseDouble(arg0[0].get("el.ema").toString()));
		
		}
		catch(Throwable e){
			e.printStackTrace();
		}
//		
	}

}
