/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vadi.test.sarb.listeners;


import vadi.test.sarb.esper.portfolio.PFManager;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 *
 * @author tomp
 */
public class MyListener implements UpdateListener {
	 volatile int count =0;
	 PFManager pfm = PFManager.getInstance();
	 java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
    	try{
    	
     //   EventBean event = newEvents[0];
      // OrderEvent evt =  (OrderEvent)event.getUnderlying();
    //   System.out.println("Match found");
       if ( newEvents != null ){ 
       System.out.println("Count="+count++);
     //  System.out.println(newEvents.length);
    /*   System.out.println(newEvents[0].getEventType());
       System.out.println(newEvents[0].toString());
      
       MapEventBean mb = (MapEventBean) newEvents[0];
       System.out.println(mb.getProperties());*/
      // log.info("ts="+mb.get("ts"));
      // log.info("eq="+mb.get("average"));
       System.out.println("mylistener "+newEvents[0].getUnderlying().toString());
       System.out.println(pfm.positionValue());
       }
    	}
       catch(Throwable e){
    	   e.printStackTrace();
    	   
       }
     /*  try {
		Thread.sleep(1);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
      /* for (int i=0;i < newEvents.length;i++ )
       {
    	   System.out.println(newEvents[i].toString());
    	   System.out.println(newEvents[i].get("a"));
    	   System.out.println(newEvents[i].get("b"));
    	   
       }*/
 //      System.out.println("new "+newEvents[0].toString());
     //  System.out.println("old "+oldEvents[0].toString());
       
      //  EventBean old = oldEvents[0];
       // System.out.println("avg=" + event.get("avg(price)"));
       // System.out.println("Bean "+event.toString());
        //System.out.println("itemName " + event.get("itemName"));
       //System.out.println("b " + event.get("b"));
     //  System.out.println("a " + event.get("a"));
      //  System.out.println("Event "+event.toString());
         
     //    System.out.println("count(*) " + event.get("count(*)"));
         //    System.out.println("Old event "+old.toString());
   
    }
}
