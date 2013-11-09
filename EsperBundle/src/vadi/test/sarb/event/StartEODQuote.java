package vadi.test.sarb.event;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;


public class StartEODQuote extends Event implements Serializable, EventHandler{
    private static final long serialVersionUID = 6745510857395053984L;

    /**
     * No argument constructor used by the Apama Java framework on
     * application loading
     */
    public StartEODQuote() {
        this("");
    }
    public String symbol;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public StartEODQuote(String symbol) {
		super();
		this.symbol = symbol;
	}


	@Override
	public void handle(ConcurrentHashMap<String,Object> state) {
		// TODO Auto-generated method stub
		
	}
	
      
}
