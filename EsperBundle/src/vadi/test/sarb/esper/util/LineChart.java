package vadi.test.sarb.esper.util;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart extends JFrame {
	JFreeChart chart = null;
	ConcurrentHashMap<String,XYSeries> series;
	ConcurrentHashMap<String,Integer> seriesCount;
	XYPlot plot;
	int index;
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	String title ;
	String xLabel ;
	String yLabel ;
	PlotOrientation orientation ;
	Color[] color = { Color.blue,Color.red,Color.blue };
	XYSeriesCollection dataset = new XYSeriesCollection();
	String chartType = "";
	JPanel content = null;
	public String getChartype() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	public String getxLabel() {
		return xLabel;
	}
	public void setxLabel(String xLabel) {
		this.xLabel = xLabel;
	}
	public String getyLabel() {
		return yLabel;
	}
	public void setyLabel(String yLabel) {
		this.yLabel = yLabel;
	}
	public PlotOrientation getOrientation() {
		return this.orientation;
	}
	public void setOrientation(String str) {
		if ( str.equals("V")){
			log.info("Setting vertical");
			this.orientation = PlotOrientation.VERTICAL;
		}
	}

		public LineChart() throws HeadlessException {
		super();
		this.series = new ConcurrentHashMap<String,XYSeries>();
		this.seriesCount = new ConcurrentHashMap<String,Integer>();
		index=0;
		 title = "Title";
		 xLabel = "xaxis";
		 yLabel = "yaxis";
		 orientation = PlotOrientation.HORIZONTAL;
		
	}
	public void addSeries(String name){
		if ( series.containsKey(name))
			return;
			
		XYSeries xy = new XYSeries(name);
		series.put(name, xy);
		seriesCount.put(name, 0);
		if (chart == null)
		{
			log.info("Creating a new chart "+index+" "+name);
			createChart(xy);
			index++;
		}
		else {
			log.info("Adding a series "+index+" "+name);
			//XYDataset dataset = new XYSeriesCollection(xy);
			dataset.addSeries(xy);
			
			//plot.setDataset(index,dataset);
			/*log.info("Setting color for series "+index+" "+color[index%color.length]);
			plot.getRenderer().setSeriesPaint(index, color[index%color.length]);
			plot.setDataset(index++,dataset)*/;
		}
		// plot.getRenderer().setSeriesPaint(index-1, color[(index-1)%color.length]);
	}
	public void addData(String xy,Double key,Double val )
	{
		series.get(xy).add(key, val);
		int num = seriesCount.get(xy);
		num++;
		seriesCount.put(xy,num);
		//series.get(xy).a
	}
	public void addData(String xy,Double val )
	{
		int num = seriesCount.get(xy);
		num++;
		seriesCount.put(xy,num);
		//series.get(xy).a
		series.get(xy).add(num, val);
	}
	public void createChart(XYSeries xy)
	{
		
		
	 dataset = new XYSeriesCollection(xy);
		//CategoryDataset xyDataset = new DefaultCategoryDataset();
      //xyDataset.
        
      //chart = ChartFactory.createXYLineChart
	 if ( chartType.equals("scatter") )
	 {
		//chart= ChartFactory.createXYAreaChart
		 chart = ChartFactory.createScatterPlot
			//chart = ChartFactory.createLineChart
			(this.getTitle(),  // Title
			this.getyLabel(),           // Y-Axis label
	         this.getxLabel(),           // X-Axis label
	      //   getyLabel(),           // Y-Axis label
	         dataset,          // Dataset
	         this.getOrientation(),
	        //PlotOrientation.HORIZONTAL,
	        // PlotOrientation.VERTICAL,
	         true,
	         true,
	         true                // Show legend
	        );
		 
	 }
	 else{
	//chart = ChartFactory.createScatterPlot
		chart = ChartFactory.createXYLineChart
		(this.getTitle(),  // Title
		this.getyLabel(),           // Y-Axis label
         this.getxLabel(),           // X-Axis label
      //   getyLabel(),           // Y-Axis label
         dataset,          // Dataset
         this.getOrientation(),
        //PlotOrientation.HORIZONTAL,
        // PlotOrientation.VERTICAL,
         true,
         true,
         true                // Show legend
        );
	 }
       //ChartFactory.
     //   this.plot = chart.getXYPlot();
        
      /*plot.getRenderer().setSeriesPaint(0,color[0]);
      plot.getRenderer().setSeriesPaint(1,color[1]);
      plot.getRenderer().setSeriesPaint(2,color[2]);*/
       // this.plot.setRenderer(new XYSplineRenderer());
      //  this.plot.setDataset(1, xyDataset);
    //   this.plot.setRenderer(1, new StandardXYItemRenderer());
  
       /* ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
       // axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 200.0); */

        final ChartPanel chartPanel = new ChartPanel(chart);
        content = new JPanel(new BorderLayout());
        content.add(chartPanel);
         chartPanel.setPreferredSize(new java.awt.Dimension(700, 470));
        this.setContentPane(content);
        this.pack();
        this.setVisible(true);
             
        this.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
			public void windowClosing(java.awt.event.WindowEvent e){
                        System.exit(0);
                }});

	}
	
	public JPanel getChart(String name){
			this.addSeries(name);
		return content;	
	}
	
}
