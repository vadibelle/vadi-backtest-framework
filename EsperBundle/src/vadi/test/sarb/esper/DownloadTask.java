package vadi.test.sarb.esper;

import java.util.concurrent.Callable;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class DownloadTask implements Callable<String> {
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper");
	public String getUrl() {
		return url;
	}

	String symbol;
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	String url;
	
	
	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		 HttpClient httpclient = new DefaultHttpClient();
		 //httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		 try {
	            HttpGet httpget = new HttpGet(url);
	          //  System.setProperty("http.proxyHost", "proxy.statestr.com");
	            
	            log.info("executing request " + httpget.getURI());

	            // Create a response handler
	            ResponseHandler<String> responseHandler = new BasicResponseHandler();
	            String responseBody = httpclient.execute(httpget, responseHandler);
	           
	            	  //log.info(responseBody);
	            	   return responseBody;
	           

	        }
		 catch(Throwable e){
			 e.printStackTrace();
			 return "";
		 }
		 finally {
	            // When HttpClient instance is no longer needed,
	            // shut down the connection manager to ensure
	            // immediate deallocation of all system resources
	            httpclient.getConnectionManager().shutdown();
	        }
	  	
	}
	

}

/*
http://www.google.com/finance/getprices?q={Code}&x={Exchange}&i={Interval}&p={Period}&f={Fields}. The meaning of the parameters is:

    Code. The code of the security. For example, GOOG for Google or EURUSD for the Euro/Dollar currency pair. This parameter is case sensitive and must be capitalized to be recognized.
    Exchange. The exchange where the security is listed. For example, NASDAQ for GOOG or CURRENCY for EURUSD. The exchange must be in upper case and can be left blank for American exchanges.
    Interval. Google groups the data into intervals whose length in seconds is defined by this parameter. Its minimum value is 60 seconds.
    Period. The period of time from which data will be returned. Google always returns the most recent data. Examples of this parameter are 1d (one day), 1w (one week), 1m (one month), or 1y (one year).
    Fields. The fields to return. This parameter seems to be ignored by Google, as it always returns the date, open, high, low, close, and volume of every interval.

As an example, the URL http://www.google.com/finance/getprices?q=LHA&x=ETR&i=60&p=1d&f=d,c,h,l,o,v means: Download the fields date, close, high, low, open, and volume (f=d,c,h,l,o,v) for the last day (p=1d) grouping the data into 60 second intervals (i=60) for the security LHA (q=LHA) belonging to the exchange "ETR" (x=ETR).

Upon invoking that URL, something similar to this would be downloaded:
Collapse | Copy Code

EXCHANGE=ETR
MARKET_OPEN_MINUTE=540
MARKET_CLOSE_MINUTE=1050
INTERVAL=60
COLUMNS=DATE,CLOSE,HIGH,LOW,OPEN,VOLUME
DATA=
TIMEZONE_OFFSET=120
a1306998060,14.84,14.95,14.83,14.93,54359
2,14.84,14.84,14.84,14.84,97
3,14.865,14.865,14.84,14.84,5584
4,14.875,14.875,14.875,14.875,1230
5,14.865,14.885,14.85,14.88,14962
6,14.845,14.86,14.84,14.86,7596
7,14.855,14.855,14.84,14.845,20912
8,14.845,14.85,14.845,14.85,9833
9,14.85,14.85,14.85,14.85,2358

...*/