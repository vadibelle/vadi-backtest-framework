package vadi.test.sarb.event;

public class StatArb extends Event {
    private static final long serialVersionUID = 4782076499173158101L;
    public String tick1;
    public String tick2;
    public StatArb(String tick1, String tick2) {
		super();
		this.tick1 = tick1;
		this.tick2 = tick2;
	}

	    
    /*
     * No argument constructor used by the Apama Java framework on
     * application loading
     */
    public StatArb() {
        this("","");
    }
}
