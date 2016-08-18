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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class BarChartVisualization {
/**
* A demonstration application showing how to create a simple time series
* chart. This example uses monthly data.
*
* @param title the frame title.
*/
	
	public BarChartVisualization (String title, TimeSeriesCollection timeSeriesCollection, String xAxisLabel, String yAxisLabel) {
		DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
		categoryDataset.addValue(1.0, "Row 1", "Column 1");
		categoryDataset.addValue(5.0, "Row 1", "Column 2");
		categoryDataset.addValue(3.0, "Row 1", "Column 3");
		categoryDataset.addValue(2.0, "Row 2", "Column 1");
		categoryDataset.addValue(3.0, "Row 2", "Column 2");
		categoryDataset.addValue(2.0, "Row 2", "Column 3");
				
		JFreeChart chart = createChart(categoryDataset, title, xAxisLabel, yAxisLabel);

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
	public static JFreeChart createChart(CategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) 
	{
		boolean bShowLegend = true;
		
		if (dataset.getColumnCount() > 30 || dataset.getRowCount() > 30)
		{
			bShowLegend = false;
		}
		
		JFreeChart chart = ChartFactory.createBarChart3D(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, bShowLegend, true, false);
	
		chart.setBackgroundPaint(Color.white);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		/*XYItemRenderer r = plot.getRenderer();
		
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
		}
		
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));*/
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