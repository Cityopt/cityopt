package eu.cityopt.web;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class ScatterPlotVisualization {
/**
* A demonstration application showing how to create a simple time series
* chart. This example uses monthly data.
*
* @param title the frame title.
*/
	
	public ScatterPlotVisualization (String title, XYDataset dataset, String xAxisLabel, String yAxisLabel, boolean isXDateAxis) {
		JFreeChart chart = createChart(dataset, title, xAxisLabel, yAxisLabel, isXDateAxis);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		
		JFrame f = new JFrame(title);
        f.setTitle(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout(0, 5));
        f.add(chartPanel, BorderLayout.CENTER);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setHorizontalAxisTrace(true);
        chartPanel.setVerticalAxisTrace(true);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //panel.add(createTrace());
        //panel.add(createDate());
        //panel.add(createZoom());
        f.add(panel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(ApplicationFrame.HIDE_ON_CLOSE);
        f.setVisible(true);
	}
	
	/**
	* Creates a chart.
	*
	* @param dataset a dataset.
	*
	* @return A chart.
	*/
	public static JFreeChart createChart(XYDataset dataset, String title, String xAxisLabel, String yAxisLabel, boolean isXDateAxis) 
	{
		boolean bShowLegend = true;
		
		if (dataset.getSeriesCount() > 30)
		{
			bShowLegend = false;
		}
		
		JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, bShowLegend, true, true);
	
		chart.setBackgroundPaint(Color.WHITE);
		//chart.setBackgroundPaint(new Color(230, 230, 230));
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.LIGHT_GRAY);
		plot.setBackgroundPaint(new Color(230, 230, 230));
		plot.setDomainGridlinePaint(Color.WHITE);
		plot.setDomainGridlinePaint(new Color(230, 230, 230));
		plot.setRangeGridlinePaint(Color.WHITE);
		plot.setRangeGridlinePaint(new Color(230, 230, 230));
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		XYItemRenderer r = plot.getRenderer();
		
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		
		if (isXDateAxis)
		{
			DateAxis xAxis = new DateAxis("Date");
	        xAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
			xAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));//MMM-yyyy"));
	        plot.setDomainAxis(xAxis);
		}
        //plot.setRangeAxis(new DateAxis("Time"));
		//ValueAxis axis = (ValueAxis) plot.getDomainAxis();
		
        //ValueAxis axis = (ValueAxis) plot.getRangeAxis();//getDomainAxis();
		return chart;
	}
		
	/**
	* Creates a panel for the demo (used by SuperDemo.java).
	*
	* @return A panel.
	*/
	/*public static JPanel createDemoPanel() {
		JFreeChart chart = createChart(createDataset());
		return new ChartPanel(chart);
	}*/
}