package vadi.test.sarb.event;


//@EventType(description = "Find correlation b/w 2 events", name = "vadi.test.event.Correlate")
public class Correlate extends Event {
    private static final long serialVersionUID = -2640240457389882262L;
    public String tick1;
    public String tick2;

    /**
     * No argument constructor used by the Apama Java framework on
     * application loading
     */
    public Correlate() {
      
        this("","");
    }

	public Correlate(String tick1, String tick2) {
		super();
		this.tick1 = tick1;
		this.tick2 = tick2;
	}

	public String getTick1() {
		return tick1;
	}

	public void setTick1(String tick1) {
		this.tick1 = tick1;
	}

	public String getTick2() {
		return tick2;
	}

	public void setTick2(String tick2) {
		this.tick2 = tick2;
	}
    
    
}
