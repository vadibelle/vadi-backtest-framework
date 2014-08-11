package vadi.test.sarb.esper.util;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPServiceDestroyedException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.deploy.DeploymentOptions;
import com.espertech.esper.client.deploy.DeploymentResult;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.deploy.Module;
import com.espertech.esper.client.deploy.ParseException;

import vadi.test.sarb.esper.Messages;
import vadi.test.sarb.esper.db.DbUtil;
import vadi.test.sarb.event.StartEODQuote;
import vadi.test.sarb.listeners.DummyListener;

public class Utility {

	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper.util");
	static final int NUM_THREADS = 10;
	private static volatile Utility instance = null;
	private ExecutorService executor = null;
	private  DbUtil dbUtil = null;
	private   EPServiceProvider epService ;
	private ConcurrentHashMap<Object,HashMap> map = null;
	//static final JFrame container ;
	private static boolean simulationMode = false;
	private long currentTime = 0;
	private ArrayList<String> symbolList;
	private boolean print = false;
	private boolean trace = false;
	private ArrayList<StartEODQuote> quoteList;
	int maxExecutions = 1;
	private Semaphore doneSemaphore;
	static {
		System.out.println("Initializing esper engine");
			
		getInstance();
		/*
		container = new JFrame("Dashboard");
		container.setLayout(new FlowLayout());
		container.setSize(700, 700);
		*/
		//container.getContentPane().setLayout(new GridLayout());
		//container.getContentPane().setLayout(new BorderLayout());
	//container.getContentPane().setLayout(new GridLayout());
		//container.getContentPane().setSize(400, 150);
		//container.getContentPane().setLayout(new GridBagLayout());
		//container.getContentPane().setLayout(new SpringLayout());

		/*
		container.pack();
		container.setVisible(true);
		container.addWindowListener(new java.awt.event.WindowAdapter(){		
		 @Override
		public void windowClosing(java.awt.event.WindowEvent e){
             System.exit(0);
     }});
     */
	}
	
	public synchronized ConcurrentHashMap getMap() {
				if ( map == null )
					map = new ConcurrentHashMap<Object,HashMap>();
		return map;
	}

	
	
	public void info(String message)
	{
		//String print = vadi.test.sarb.esper.Messages.getString("do.print");
		if ( print)
			log.info(message);
	}
	public void trace(String message)
	{
		//String print = vadi.test.sarb.esper.Messages.getString("do.print");
		if ( trace)
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
		symbolList = new ArrayList<String>();
		quoteList = new ArrayList<StartEODQuote>();
		//String logLevel = vadi.test.sarb.esper.Messages.getString("log.level");
		Configuration config = new Configuration();
		/*config.getEngineDefaults().getThreading().setThreadPoolInbound(true);
		config.getEngineDefaults().getThreading().setThreadPoolInboundNumThreads(5);
		config.getEngineDefaults().getThreading().setThreadPoolOutbound(true);
		config.getEngineDefaults().getThreading().setThreadPoolOutboundNumThreads(5);*/
		
		doneSemaphore = new Semaphore(maxExecutions);
		if ( mode != null && mode.equals("true"))
		{
			simulationMode = true;
			config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
			}
		
		String pr = vadi.test.sarb.esper.Messages.getString("do.print");
		if ( pr.equals("true"))
			print = true;
		
		if ( Messages.getString("do.trace").equals("true"))
			trace = true;
		
		//config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
       config.addEventTypeAutoName("vadi.test.sarb.event");
       config.addEventType(vadi.test.sarb.event.StartEODQuote.class);
       //config.addImport();
       // config.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(false);
       // config.getEngineDefaults().getThreading().setInsertIntoDispatchPreserveOrder(false);
       // config.getEngineDefaults().getThreading().setEngineFairlock(false);
       
        epService = EPServiceProviderManager.getDefaultProvider(config);

        // epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction(arg0, arg1)
      //  init();
        
              
	}
	public void createIntVar(String name, int val){
		
		epService.getEPAdministrator().getConfiguration().removeVariable(name, true);
		epService.getEPAdministrator().getConfiguration().
		addVariable(name, Integer.class, val);
		
	}
	
	public void createDoubleVar(String name, String val){
		epService.getEPAdministrator().getConfiguration().removeVariable(name, true);
		epService.getEPAdministrator().getConfiguration().
		addVariable(name, Double.class, Double.parseDouble(val));
		
	}

	public Object getVariable(String name){
		return epService.getEPRuntime().getVariableValue(name);
	}
	public Map getVariableValueAll() {
		return epService.getEPRuntime().getVariableValueAll();
	}
	public boolean doPrint(){
		return print;
	}
	
	
	public boolean isSymbolListEmpty() {
		return symbolList.isEmpty();
	}
	
	public ArrayList<String> getSymbolList() {
		return symbolList;
	}


	public void setSymbolList(ArrayList<String> symbolList) {
		this.symbolList = symbolList;
	}

	public void addToSymboList(String smbl)
	{
		if ( !symbolList.contains(smbl.toUpperCase()))
		symbolList.add(smbl.toUpperCase());
	}

	public void removeFromSymbolList(String smbl)
	{
		symbolList.remove(smbl.toUpperCase());
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
	
	public static String deployModule(String file)
	{
		EPDeploymentAdmin deployAdmin = getInstance().getEpService().
				getEPAdministrator().getDeploymentAdmin();
		try {
			Module module =  deployAdmin.read(new File(file));
			DeploymentResult d = deployAdmin.deploy(module, new DeploymentOptions());
			return d.getDeploymentId();
						
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
		return "";
	}
	
	public void undeploy(String did){
		EPDeploymentAdmin deployAdmin = getInstance().getEpService().
				getEPAdministrator().getDeploymentAdmin();
		try {
			deployAdmin.undeployRemove(did);
									
		} 
		catch(Throwable e)
		{
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
	
	@SuppressWarnings("deprecation")
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
		//return container;
		return null;
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
	
	public void addToPortfolio(StartEODQuote q) {
		quoteList.add( q);
	}
	
	public boolean isPortfolioEmpty()
	{
		return quoteList.isEmpty();
	}
	public void removeFromPortfolio(StartEODQuote q)
	{
		quoteList.remove(q);
		
	}
	public ArrayList<StartEODQuote> getPortfolioList()
	{
		return quoteList;
	}

	public void acquire()
	{
		try {
			System.out.println("get semaphore");
			doneSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void release()
	{	System.out.println("release semaphore");
		doneSemaphore.release();
	}
	
	public void reset()
	{
		/*
		epService.getEPAdministrator().destroyAllStatements();
		epService.removeAllServiceStateListeners();
		epService.removeAllStatementStateListeners();
		
		*/
		epService.initialize();
		String mode = vadi.test.sarb.esper.Messages.getString("sim.mode");
		symbolList = new ArrayList<String>();
		quoteList = new ArrayList<StartEODQuote>();
		//String logLevel = vadi.test.sarb.esper.Messages.getString("log.level");
		Configuration config = new Configuration();
		/*config.getEngineDefaults().getThreading().setThreadPoolInbound(true);
		config.getEngineDefaults().getThreading().setThreadPoolInboundNumThreads(5);
		config.getEngineDefaults().getThreading().setThreadPoolOutbound(true);
		config.getEngineDefaults().getThreading().setThreadPoolOutboundNumThreads(5);*/
		
		doneSemaphore = new Semaphore(maxExecutions);
		if ( mode != null && mode.equals("true"))
		{
			simulationMode = true;
			config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
			}
		
		String pr = vadi.test.sarb.esper.Messages.getString("do.print");
		if ( pr.equals("true"))
			print = true;
		
		if ( Messages.getString("do.trace").equals("true"))
			trace = true;
		
		//config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
      //  config.addEventTypeAutoName("vadi.test.sarb.event");
       // config.addImport()
       // config.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(false);
       // config.getEngineDefaults().getThreading().setInsertIntoDispatchPreserveOrder(false);
       // config.getEngineDefaults().getThreading().setEngineFairlock(false);
       
        epService = EPServiceProviderManager.getDefaultProvider(config);
        
        // epService.getEPAdministrator().getConfiguration().addPlugInAggregationFunction(arg0, arg1)
      //  init();
		
	}
	
}
