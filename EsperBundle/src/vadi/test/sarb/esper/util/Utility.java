package vadi.test.sarb.esper.util;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPServiceDestroyedException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.deploy.DeploymentException;
import com.espertech.esper.client.deploy.DeploymentOptions;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.deploy.Module;
import com.espertech.esper.client.deploy.ParseException;
import com.espertech.esper.client.time.CurrentTimeEvent;

import vadi.test.sarb.esper.db.DbUtil;
import vadi.test.sarb.listeners.DummyListener;
import vadi.test.sarb.listeners.MyListener;

public class Utility {

	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	static final int NUM_THREADS = 40;
	private static volatile Utility instance = null;
	private ExecutorService executor = null;
	private  DbUtil dbUtil = null;
	private   EPServiceProvider epService ;
	private ConcurrentHashMap<Object,HashMap> map = null;
	static final JFrame container ;
	private static boolean simulationMode = false;
	private long currentTime = 0;

	static {
		System.out.println("Initializing esper engine");
		getInstance();
		container = new JFrame("Dashboard");
		container.setLayout(new FlowLayout());
		container.setSize(700, 700);
		//container.getContentPane().setLayout(new GridLayout());
		//container.getContentPane().setLayout(new BorderLayout());
	//container.getContentPane().setLayout(new GridLayout());
		//container.getContentPane().setSize(400, 150);
		//container.getContentPane().setLayout(new GridBagLayout());
		//container.getContentPane().setLayout(new SpringLayout());

		container.pack();
		container.setVisible(true);
		container.addWindowListener(new java.awt.event.WindowAdapter(){		
		 @Override
		public void windowClosing(java.awt.event.WindowEvent e){
             System.exit(0);
     }});
	}
	
	public synchronized ConcurrentHashMap getMap() {
				if ( map == null )
					map = new ConcurrentHashMap<Object,HashMap>();
		return map;
	}

	
	public void info(String message)
	{
		log.info(message);
	}
			 
	public  synchronized ExecutorService getExecutor() {
		
		if (executor == null )
		{
			executor = Executors
			.newFixedThreadPool(NUM_THREADS);
		}
		
		return executor;
	}
	
	
	private Utility(){
		String mode = vadi.test.sarb.esper.Messages.getString("sim.mode");
		Configuration config = new Configuration();
		if ( mode != null && mode.equals("true"))
		{
			simulationMode = true;
			config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
			
		}
		
        config.addEventTypeAutoName("vadi.test.sarb.event");
        epService = EPServiceProviderManager.getDefaultProvider(config);
       // epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction(arg0, arg1)
      //  init();

              
	}
	
	public static boolean isSimMode()
	{
		return simulationMode;
	}
	
	public void init() {
		  String expression = "select * from StockQuote.win:time(10 sec) as a , StockQuote.win:time(10 sec) as b where a.price=20.0 and b.price=10.0";
		     //   + " and a.itemName = b.itemName";
		        	
		    EPStatement statement = epService.getEPAdministrator().createEPL(expression);
		    
		    statement.addListener(new DummyListener());
	}
	
    public static Utility getInstance() {
	       synchronized (Utility.class){
                if (instance == null) {
                        instance = new Utility();
                }
        	}

			return instance;
}

    public synchronized DbUtil getDbUtil()
    {
    	if ( dbUtil == null )
			try {
				dbUtil = new DbUtil();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	return dbUtil;
    }

	public EPServiceProvider getEpService() {
		return epService;
	}

	public void setEpService(EPServiceProvider epService) {
		this.epService = epService;
	}

public static void createStmt(String eventExpr){
		System.out.println("Creating statement "+eventExpr);
		getInstance().getEpService().getEPAdministrator().createEPL(eventExpr).addListener(new DummyListener());
		
	}

	public static void registerEventListener(String eventExpr, UpdateListener listener){
		try {
			System.out.println("Registering listener for  "+eventExpr+" "+listener);
			getInstance().getEpService().getEPAdministrator().createEPL(eventExpr).addListener(listener);
		} catch (EPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EPServiceDestroyedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	
	public static void deployModule(String file)
	{
		EPDeploymentAdmin deployAdmin = getInstance().getEpService().
				getEPAdministrator().getDeploymentAdmin();
		try {
			Module module =  deployAdmin.read(new File(file));
			deployAdmin.deploy(module, new DeploymentOptions());
			 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addModuleListener(String name, UpdateListener listener ) {
		try {
			System.out.println("Register listner for module "+name);
			EPStatement ep = getInstance().getEpService().
				getEPAdministrator().getStatement(name);
			if ( ep == null )
				throw new RuntimeException("EP statement null");
			if ( listener == null)
				throw new RuntimeException("Listener statement null");
				ep.addListener(listener);
		} catch (EPServiceDestroyedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void addEPLFunction(String name, String cls){
		getInstance().getEpService().getEPAdministrator().getConfiguration()
		.addPlugInAggregationFunction(name, cls);
	}
	
	public static void addEPLFactory(String name,String cls) {
		try {
			getInstance().getEpService().getEPAdministrator().getConfiguration()
			.addPlugInAggregationFunctionFactory(name, cls);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EPServiceDestroyedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
			
	
	public static JFrame getContainer() {
		return container;
	}

	public static GenericChart addChart(String name)
	{
		GenericChart c = new GenericChart();
		c.setTitle(name);
		c.setOrientation("V");
		getContainer().getContentPane().add(c.getChart(name));
		return c;
		 
	}


	public long getCurrentTime() {
		return currentTime;
	}


	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	
	
}
