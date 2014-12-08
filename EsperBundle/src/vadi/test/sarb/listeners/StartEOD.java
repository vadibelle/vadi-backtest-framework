package vadi.test.sarb.listeners;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import vadi.test.sarb.esper.DownloadTask;
import vadi.test.sarb.esper.Messages;
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.EODQuote;
import vadi.test.sarb.event.LastEOD;
import vadi.test.sarb.event.OptionPrice;
import vadi.test.sarb.event.StartEODQuote;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;



public class StartEOD implements UpdateListener,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4076557417437457642L;
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.listeners"); //$NON-NLS-1$
	private HashMap<String,OptionPrice> map ;
	LinkedBlockingQueue<Future<String>> queue ;
	public StartEOD()
	{
		map = new HashMap<String,OptionPrice>();
		queue = new LinkedBlockingQueue<Future<String>>();
	}

	 @Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {

		// TODO Auto-generated method stub
		 if (newEvents == null || newEvents.length == 0)
		 {
			 log.info("No Event returned during a match");
		 }
	//	 System.out.println("start quote received ");
		//log.info("StartEOD "+arg0.toString());
	//	Event evt = arg0.getMatchingEvent("op"); //$NON-NLS-1$
		StartEODQuote evt = (StartEODQuote)newEvents[0].getUnderlying();
					
		//	StartEODQuote op = (StartEODQuote)evt;
		 System.out.println("start quote received "+evt.getSymbol());
			EODQuoteThread eod = new EODQuoteThread();
			eod.setSymbol(evt.getSymbol());
			eod.stDate = evt.stDate;
			eod.endDate = evt.endDate;
			Utility.getInstance().getExecutor().execute(eod);
			//Utility.getInstance().addToSymboList(evt.getSymbol());
		}
				
	
	 public synchronized void put(String str, OptionPrice op)
	{
		map.put(str, op);
	}
	
	class EODQuoteThread implements Runnable {
		
		String tick;  
		String stDate , endDate;
		
		
		public String getSymbol() {
			return tick;
		}


		public void setSymbol(String symbol) {
			this.tick = symbol;
		}


		@Override
		public void run()
		{
			try {
			
			DownloadTask dt = new DownloadTask();
			if ( stDate.equals(""))
				stDate = Messages.getString("start.date");
			if ( endDate.equals(""))
				endDate = Messages.getString("end.date");
			String url = Messages.getString("StartEOD.historic.data.url")+getSymbol();
			url = url.replace("stDate", stDate);
			url = url.replace("endDate",endDate);
				
			dt.setUrl(url);
			
			Future<String> ft = Utility.getInstance().getExecutor().submit(dt);
			//queue.add(ft);
			SendEODThread sd = new SendEODThread();
			sd.setTick(getSymbol());
			sd.setData(ft);
			Utility.getInstance().getExecutor().submit(sd);
			
			
		//	sd.setTick(getSymbol());
			//GoogleDownload.getExecutor().execute(sd);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				return;
			}
		}
	}
	
	class SendEODThread implements Runnable {
	//class SendEODThread implements Runnable {
		String tick;
		Future<String> data;
		public String getTick() {
			return tick;
		}
		public void setTick(String tick) {
			this.tick = tick;
		}
		public void setData(Future<String> ft) {
			this.data = ft;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
		//	Future<String> ft;
			SimpleDateFormat df = new SimpleDateFormat(Messages.getString("StartEOD.date.format"));
			boolean error = false;
			try {
			//	ft = queue.take();
				//String str = ft.get();
				String str = this.data.get();
				String arr[] = str.split("\n");
				for(int i=arr.length-1 ;i > -1;i-- ){
					try{
					String line = arr[i];
				//	log.info(arr[i]);
				//for (String line:arr){
					//log.info(getTick()+line);
					if (line.contains("High") )
						continue;
					String []rec = line.split(Messages.getString("StartEOD.field.seperator"));
					boolean adjust = Boolean.parseBoolean(Messages.getString("Adjust.close"));
					float af = 1;
					if ( adjust)
					{
						af = Float.parseFloat(rec[6])/Float.parseFloat(rec[4]);
					}
					
					EODQuote q = new EODQuote();
					
					q.setSymbol(getTick());
					q.setOpen(Float.toString(Float.parseFloat(rec[1])*af));
					q.setHigh(Float.toString(Float.parseFloat(rec[2])*af));
					q.setLow(Float.toString(Float.parseFloat(rec[3])*af));
					q.setClose(Float.toString(Float.parseFloat(rec[4])*af));
					try {
						q.setVolume(Long.parseLong(rec[5]));	
					}
					catch(Throwable e)
					{
						q.setVolume(-1);
					}
					q.setVolume(Long.parseLong(rec[5]));
					Date dt = df.parse(rec[0]);
					q.setTimestamp(dt.getTime());
					q.enqueue();
					Thread.sleep(10);
					//log.info(q.toString());
				} catch(Throwable e)
				{
					if (Utility.getInstance().doPrint())
					log.fine("Error pasing..continue");
					
				}
			}
				LastEOD last = new LastEOD(getTick());
				Thread.sleep(1000);
				last.enqueue();
				
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("interprted Error downloading");
				error = true;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("execution Error downloading");
				error = true;
			}
			catch (Throwable e)
			{
				log.info(e.getMessage()+"Error downloading");
				error = true;
			}
			if ( error) {
			LastEOD last = new LastEOD(tick);
			last.enqueue();
			}
			
		}
		
	}
	
}
