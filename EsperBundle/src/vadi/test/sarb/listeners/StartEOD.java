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
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("global"); //$NON-NLS-1$
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
			Utility.getInstance().getExecutor().execute(eod);
			//Utility.getInstance().addToSymboList(evt.getSymbol());
		}
				
	
	 public synchronized void put(String str, OptionPrice op)
	{
		map.put(str, op);
	}
	
	class EODQuoteThread implements Runnable {
		
		String tick;  ;
		
		
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
			dt.setUrl(Messages.getString("StartEOD.historic.data.url")+getSymbol());
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
					String line = arr[i];
				//for (String line:arr){
					//log.info(getTick()+line);
					if (line.contains("High") )
						continue;
					String []rec = line.split(Messages.getString("StartEOD.field.seperator"));
					
					EODQuote q = new EODQuote();
					q.setSymbol(getTick());
					q.setOpen(rec[1]);
					q.setHigh(rec[2]);
					q.setLow(rec[3]);
					q.setClose(rec[4]);
					q.setVolume(Long.parseLong(rec[5]));
					Date dt = df.parse(rec[0]);
					q.setTimestamp(dt.getTime());
					q.enqueue();
					Thread.sleep(20);
				}
				LastEOD last = new LastEOD(getTick());
				Thread.sleep(100);
				last.enqueue();
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("Error downloading");
				error = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("Error downloading");
				error = true;
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.info("Error downloading");
				error = true;
			}
			catch (Throwable e)
			{
				log.info("Error downloading");
				error = true;
			}
			if ( error) {
			LastEOD last = new LastEOD(tick);
			last.enqueue();
			}
			
		}
		
	}
	
}
