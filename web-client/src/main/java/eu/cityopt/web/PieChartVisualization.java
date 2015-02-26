package eu.cityopt.web;

import java.awt.Color;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class PieChartVisualization extends ApplicationFrame {
/**
* A demonstration application showing how to create a simple time series
* chart. This example uses monthly data.
*
* @param title the frame title.
*/
	
	public PieChartVisualization (String title, TimeSeriesCollection timeSeriesCollection, String xAxisLabel, String yAxisLabel) {
		super(title);
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Category 1", 40);
		dataset.setValue("Category 2", 20);
		dataset.setValue("Category 3", 40);
				
		JFreeChart chart = createChart(dataset, title, xAxisLabel, yAxisLabel);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
	}
	
	/**
	* Creates a chart.
	*
	* @param dataset a dataset.
	*
	* @return A chart.
	*/
	private static JFreeChart createChart(PieDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
		JFreeChart chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
	
		chart.setBackgroundPaint(Color.white);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		XYItemRenderer r = plot.getRenderer();
		
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
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