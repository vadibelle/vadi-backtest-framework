package vadi.test.sarb.esper;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;



public class StartApp implements BundleActivator {

	
	App myApp;
	public StartApp() {
		System.out.println("Created StartApp");
		myApp = new App();
		
	}

	public void start(BundleContext arg0) throws Exception {
			// TODO Auto-generated method stub
		try {
		//String [] args = {};
		//vadi.test.sarb.esper.App.main(args);
			myApp.feedEvents();
		
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
	}

	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Ending Esperdemo");
		
	}

}

