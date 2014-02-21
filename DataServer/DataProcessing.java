import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;




import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;




public class DataProcessing extends ApplicationFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static List<String []> acc_values;
	static List<String []> gps_values;
	private ChartFrame frame = null;
	
	
	/*Constructor*/
	public DataProcessing(final String title) {

		super(title);

		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}


	
	/*Creating the chart for plotting Accelerometer data*/
	private JFreeChart createChart(final XYDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Accelerometer Graph for X,Y,Z axes",      // chart title
				"Time (sec) [Difference from original timestamp]",                      // x axis label
				"XYZ values",                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL,
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		//        final StandardLegend legend = (StandardLegend) chart.getLegend();
		//      legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();

		plot.getRenderer().setSeriesPaint(1, Color.BLUE);
		plot.setBackgroundPaint(Color.lightGray);

		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		renderer.setSeriesShapesVisible(2, false);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

	/*Dataset for plotting the Accelerometer XYZ values vs time*/
	private XYDataset createDataset() {

		double timestamp_ref;
		String[] temp = acc_values.get(0);
		timestamp_ref = Double.parseDouble(temp[3])/1000;
		double time;

		final XYSeries series1 = new XYSeries("X-Axis");
		final XYSeries series2 = new XYSeries("Y-Axis");
		final XYSeries series3 = new XYSeries("Z-Axis");

		for (String []tempval : acc_values)
		{
			time = Double.parseDouble(tempval[3])/1000- timestamp_ref;
			//	System.out.println("Time inst: "+time);

			series1.add(time, Double.parseDouble(tempval[0]));
			series2.add(time, Double.parseDouble(tempval[1]));
			series3.add(time, Double.parseDouble(tempval[2]));
			
			if(Double.parseDouble(tempval[0]) == 0.0 && Double.parseDouble(tempval[1]) == 0.0 && Double.parseDouble(tempval[2]) == 0.0)
				System.out.println("User not moving at time  :"+time);
			
			else
				System.out.println("USer moving at time :"+ time);
		}

		

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);

		return dataset;

	}



	/*Method to prpare the chart for GPS - User Movement wrt time*/
	private void displayChart() {

		if (this.frame == null) {

			// create a default chart based on some sample data...
			final String title = "GPS Plot - Movement wrt Time(in sec)";
			final String xAxisLabel = "Time";
			final String yAxisLabel = "Moved=1,Not Moved =0";

			final XYDataset data = createStepXYDataset();

			final JFreeChart chart = ChartFactory.createXYStepChart(
					title,
					xAxisLabel, yAxisLabel,
					data,
					PlotOrientation.VERTICAL,
					true,   // legend
					true,   // tooltips
					false   // urls
					);

			// then customise it a little...
			chart.setBackgroundPaint(new Color(216, 216, 216));
			final XYPlot plot = chart.getXYPlot();

			plot.setDomainAxis(0, new NumberAxis()); 
			NumberAxis axis = (NumberAxis) plot.getDomainAxis();
			axis.setNumberFormatOverride( new NumberFormat(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public StringBuffer format(double number, StringBuffer toAppendTo,
						FieldPosition pos) {
					// TODO Auto-generated method stub
					return new StringBuffer(String.format("%f", number));
				}

				@Override
				public StringBuffer format(long number, StringBuffer toAppendTo,
						FieldPosition pos) {
					// TODO Auto-generated method stub
					return new StringBuffer(String.format("%3.0f", number));
				}

				@Override
				public Number parse(String source, ParsePosition parsePosition) {
					// TODO Auto-generated method stub
					return null;
				}
			} );
			axis.setAutoRange(true);
			axis.setAutoRangeIncludesZero(false);

			plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
			plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));

			// and present it in a frame...
			this.frame = new ChartFrame("GPS Plot vs Time", chart);
			this.frame.pack();
			RefineryUtilities.positionFrameRandomly(this.frame);
			this.frame.setVisible(true);

		}
		else {
			this.frame.setVisible(true);
			this.frame.requestFocus();
		}

	}



	/*DataSet and calculations to plot the user movement wrt time*/
	public static XYDataset createStepXYDataset() {

		double  timestamp2, timestamp;

		double timestamp_ref;
		String[] temp = gps_values.get(0);
		timestamp_ref = Double.parseDouble(temp[2])/1000;

		final XYSeries s1 = new XYSeries("Step Plot");

		int R = 6371; // km
		double lat1, lat2, long2, long1;


		for(int i=0; i<gps_values.size(); i++)
		{
			String[] temp1= gps_values.get(i);
			lat1 = Double.parseDouble(temp1[0]);
			long1 = Double.parseDouble(temp1[1]);

			int j=i+1;

			if(j<gps_values.size())
			{
				String[] temp2 = gps_values.get(j);
				lat2 = Double.parseDouble(temp2[0]);
				long2 = Double.parseDouble(temp2[1]);
				timestamp2 = Double.parseDouble(temp2[2])/1000;

				/*Using Haversine formula for calculating distance between successive GPS co-ordinates and if difference is greater than a threashold, movement has occured*/

				double dlat = (lat2 - lat1)*Math.PI/180;
				double dlong = (long2 - long1)*Math.PI/180;
				double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
						Math.sin(dlong/2) * Math.sin(dlong/2) * Math.cos(lat1) * Math.cos(lat2); 
				double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
				double d = R * c;

				timestamp = timestamp2-timestamp_ref;


				if(d*1000 > 1.0)
					s1.add(timestamp, 1);
				else
					s1.add(timestamp, 0);
			}
		}

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);
		return dataset;
	}
	
	
	
	/*Main Function*/
	public static void main(String []args){

		BufferedReader br = null;
		Path path = Paths.get("C:\\Users\\SKANDA GURUANAND\\workspace\\Accel\\logdata.txt");
		//	System.out.println("File Path:"+path);
		String[] data_type;
		String[] values;
		acc_values = new ArrayList<String []>();
		gps_values = new ArrayList<String []>();

		try {

			String line;

			br = new BufferedReader(new FileReader(path.toString()));

			while ((line = br.readLine()) != null) {
				//	System.out.println(line);
				data_type = line.split(":");

				if(data_type[0].equalsIgnoreCase("ACC"))
				{
					values = data_type[1].split(",");
					acc_values.add(values);

				}

				if(data_type[0].equalsIgnoreCase("GPS"))
				{
					values = data_type[1].split(",");
					gps_values.add(values);

				}
			}

			/*Below lines for launching the Accelerometer Plot*/
			final DataProcessing demo = new DataProcessing("Accelerometer Data");
			demo.pack();
			RefineryUtilities.centerFrameOnScreen(demo);
			demo.setVisible(true); 

			/*Below lines for launching GPS - UserMovement vs Time Plot*/
			final DataProcessing step = new DataProcessing("1");
			step.displayChart();
			
			

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}


}
