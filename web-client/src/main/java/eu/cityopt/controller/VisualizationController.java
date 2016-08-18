package eu.cityopt.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.ObjectiveFunctionResultDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.DTO.TimeSeriesDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.ComponentInputParamDTOService;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.CopyService;
import eu.cityopt.service.DatabaseSearchOptimizationService;
import eu.cityopt.service.DecisionVariableService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValService;
import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;
import eu.cityopt.service.MetricService;
import eu.cityopt.service.MetricValService;
import eu.cityopt.service.ObjectiveFunctionService;
import eu.cityopt.service.OptConstraintService;
import eu.cityopt.service.OptSearchConstService;
import eu.cityopt.service.OptimizationSetService;
import eu.cityopt.service.OutputVariableService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioGeneratorService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.SimulationResultService;
import eu.cityopt.service.TimeSeriesService;
import eu.cityopt.service.TimeSeriesValService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.BarChartVisualization;
import eu.cityopt.web.PieChartVisualization;
import eu.cityopt.web.ScatterPlotVisualization;
import eu.cityopt.web.TimeSeriesVisualization;
import eu.cityopt.web.UserSession;

@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession", "user"})
public class VisualizationController {
	
	@Autowired
	ProjectService projectService; 
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	ScenarioService scenarioService;
	
	@Autowired
	AppUserService userService;
	
	@Autowired
	ComponentService componentService;
	
	@Autowired
	InputParameterService inputParamService;

	@Autowired
	InputParamValService inputParamValService;

	@Autowired
	ExtParamService extParamService;

	@Autowired
	ExtParamValService extParamValService;

	@Autowired
	ExtParamValSetService extParamValSetService;
	
	@Autowired
	MetricService metricService;

	@Autowired
	MetricValService metricValService;

	@Autowired
	UnitService unitService;
	
	@Autowired
	SimulationService simService;

	@Autowired
	SimulationResultService simResultService;

	@Autowired
	TimeSeriesService timeSeriesService;

	@Autowired
	TimeSeriesValService timeSeriesValService;
	
	@Autowired
	OutputVariableService outputVarService;
	
	@Autowired
	TypeService typeService;

	@Autowired
	CopyService copyService;
	
	@Autowired
	OptimizationSetService optSetService;
	
	@Autowired
	ObjectiveFunctionService objFuncService;
	
	@Autowired
	OptConstraintService optConstraintService;
	
	@Autowired
	OptSearchConstService optSearchService;
	
	@Autowired
	ScenarioGeneratorService scenGenService;
	
	@Autowired
	DecisionVariableService decisionVarService;
	
	@Autowired
	DatabaseSearchOptimizationService dbOptService;

	@Autowired
	ImportExportService importExportService;

    @Autowired
    SecurityAuthorization securityAuthorization;

    @Autowired
    ControllerService controllerService;
    
	@RequestMapping(value="timeserieschart", method=RequestMethod.GET)
	public String timeSeriesChart(Map<String, Object> model, 
		@RequestParam(value="scenarioid", required=false) String scenarioId,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid,
		@RequestParam(value="metricid", required=false) String metricid,
		@RequestParam(value="action", required=false) String action,
		@RequestParam(value="charttype", required=false) String charttype) {

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		model.put("usersession", userSession);

		ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (project == null || scenario == null)
		{
			return "error";
		}

		securityAuthorization.atLeastGuest_guest(project);
		
		try {
			scenario = scenarioService.findByID(scenario.getScenid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
		model.put("scenario", scenario);
		String status = scenario.getStatus();
		
		if (simService.getRunningSimulations().contains(scenario.getScenid())) {
			status = "RUNNING";
		}
		
		model.put("status", status);
		
		if (charttype != null)
		{
			userSession.setTimeSeriesChartType(Integer.parseInt(charttype));
			model.put("charttype", Integer.parseInt(charttype));
		}
		else
		{
			model.put("charttype", userSession.getTimeSeriesChartType());
		}
		
		if (action != null)
		{
			if (action.equals("removeall"))
			{
				userSession.removeAllExtVarIds();
				userSession.removeAllOutputVarIds();
			}
		}

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		else if (userSession.getComponentId() > 0)
		{
			model.put("selectedcompid", userSession.getComponentId());
			List<OutputVariableDTO> outputVars = componentService.getOutputVariables(userSession.getComponentId());
			model.put("outputVars", outputVars);
		}
		
		if (outputvarid != null && action != null)
		{
			if (action.equals("add"))
			{
				userSession.addOutputVarId(Integer.parseInt(outputvarid));
			}
			else if (action.equals("remove"))
			{
				userSession.removeOutputVarId(Integer.parseInt(outputvarid));
			}
		}
		
		if (extparamid != null && action != null)
		{
			if (action.equals("add"))
			{
				userSession.addExtVarId(Integer.parseInt(extparamid));
			}
			else if (action.equals("remove"))
			{
				userSession.removeExtVarId(Integer.parseInt(extparamid));
			}
		}

		if (action != null && action.equals("openwindow") && status != null && !status.isEmpty())
		{
			Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
		    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
			
		    // Get output variable results
			while(iterator.hasNext()) {
				int outputVarId = iterator.next(); 
		    
				try {
					OutputVariableDTO outputVar = outputVarService.findByID(outputVarId);
					SimulationResultDTO simResult = simResultService.findByOutVarIdScenId(outputVarId, scenario.getScenid());
						
					if (simResult != null)
					{
						List<TimeSeriesValDTO> timeSeriesVals = simResultService.getTimeSeriesValsOrderedByTime(simResult.getSimresid());
						TimeSeries timeSeries = new TimeSeries(outputVar.getComponent().getName() + "." + outputVar.getName());
	
						for (int i = 0; i < timeSeriesVals.size(); i++)
						{
							TimeSeriesValDTO timeSeriesVal = timeSeriesVals.get(i);
							timeSeries.add(new Minute(timeSeriesVal.getTime()),timeSeriesVal.getValue());
						}
						
						timeSeriesCollection.addSeries(timeSeries);
					}
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
		    }

			iterator = userSession.getSelectedChartExtVarIds().iterator();
			   
			// Get external parameter time series
			while(iterator.hasNext()) {
				int extVarId = iterator.next(); 
		    
				try {
					ExtParamValDTO extVarVal = extParamValService.findByID(extVarId);
					TimeSeriesDTO timeSeriesDTO = extVarVal.getTimeseries();
					
					if (timeSeriesDTO != null)
					{
						List<TimeSeriesValDTO> timeSeriesVals = timeSeriesValService.findByTimeSeriesIdOrderedByTime(timeSeriesDTO.getTseriesid());
						TimeSeries timeSeries = new TimeSeries(extVarVal.getExtparam().getName());
						Iterator<TimeSeriesValDTO> timeSeriesIter = timeSeriesVals.iterator();
						
						while(timeSeriesIter.hasNext()) {
							TimeSeriesValDTO timeSeriesVal = timeSeriesIter.next();
							timeSeries.add(new Minute(timeSeriesVal.getTime()), timeSeriesVal.getValue());
						}
		
						timeSeriesCollection.addSeries(timeSeries);
					}
					else
					{
						TimeSeries timeSeries = new TimeSeries(extVarVal.getExtparam().getName());
						timeSeries.add(new Minute(new Date()), Double.parseDouble(extVarVal.getValue()));
						
						timeSeriesCollection.addSeries(timeSeries);
					}
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
		    }

			if (timeSeriesCollection.getSeriesCount() > 0)
			{
				if (userSession.getTimeSeriesChartType() == 0) {
					TimeSeriesVisualization demo = new TimeSeriesVisualization(project.getName(), timeSeriesCollection, "Time", "");
				} else if (userSession.getTimeSeriesChartType() == 1) {
					ScatterPlotVisualization demo = new ScatterPlotVisualization(project.getName(), timeSeriesCollection, "Date", "Value", true);
				}
			}
		}
		
		model.put("usersession", userSession);
		
		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		return "timeserieschart";
	}

	@RequestMapping(value="summarychart", method=RequestMethod.GET)
	public String summaryChart(Map<String, Object> model, 
		@RequestParam(value="scenarioid", required=false) String scenarioId,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid,
		@RequestParam(value="metricid", required=false) String metricid,
		@RequestParam(value="action", required=false) String action,
		@RequestParam(value="charttype", required=false) String charttype) {

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		model.put("usersession", userSession);

		ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (project == null || scenario == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);
		
		String status = scenario.getStatus();
		
		if (simService.getRunningSimulations().contains(scenario.getScenid())) {
			status = "RUNNING";
		}
		
		model.put("status", status);
	
		if (charttype != null)
		{
			userSession.setSummaryChartType(Integer.parseInt(charttype));
			model.put("charttype", Integer.parseInt(charttype));
		}
		else
		{
			model.put("charttype", userSession.getSummaryChartType());
		}
		
		if (action != null)
		{
			if (action.equals("removeall"))
			{
				userSession.removeAllMetricIds();
				userSession.removeAllScenarioIds();
			}
		}

		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);
		
		if (scenarioId != null && action != null)
		{
			int nScenarioId = Integer.parseInt(scenarioId);
			
			if (action.equals("add"))
			{
				userSession.addScenarioId(nScenarioId);
			}
			else if (action.equals("remove"))
			{
				userSession.removeScenarioId(nScenarioId);
			}
		}
		
		if (metricid != null && action != null)
		{
			if (action.equals("add"))
			{
				userSession.addMetricId(Integer.parseInt(metricid));
			}
			else if (action.equals("remove"))
			{
				userSession.removeMetricId(Integer.parseInt(metricid));
			}
		}

		if (action != null && action.equals("openwindow"))
		{
			TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
			Iterator<Integer> iterator = userSession.getSelectedChartMetricIds().iterator();
			
			// Get metrics time series (max 2 metrics)
			if (userSession.getSelectedChartMetricIds().size() == 2)
			{
				XYSeriesCollection collection = new XYSeriesCollection();
				DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
				DefaultPieDataset pieDataset = new DefaultPieDataset();
				timeSeriesCollection.removeAllSeries();
				
				int metric1Id = iterator.next(); 
				int metric2Id = iterator.next(); 
			    
				try {
					MetricDTO metric1 = metricService.findByID(metric1Id);
					MetricDTO metric2 = metricService.findByID(metric2Id);
					//Set<MetricValDTO> metricVals1 = metricService.getMetricVals(metric1Id);
					//Set<MetricValDTO> metricVals2 = metricService.getMetricVals(metric2Id);
					TimeSeries timeSeries = new TimeSeries("Scenario metric values");

					Set<Integer> scenarioIds = userSession.getScenarioIds();
					Iterator<Integer> scenIter = scenarioIds.iterator();
					DefaultXYDataset dataset = new DefaultXYDataset();

					while (scenIter.hasNext())
					{
						Integer integerScenarioId = (Integer) scenIter.next();
						int nScenarioId = (int)integerScenarioId;
						ScenarioDTO scenarioTemp = scenarioService.findByID(nScenarioId);
						MetricValDTO metricVal1 = metricService.getMetricVals(metric1Id, nScenarioId).get(0);
						MetricValDTO metricVal2 = metricService.getMetricVals(metric2Id, nScenarioId).get(0);
					
						//timeSeries.add(new Minute((int)Double.parseDouble(metricVal1.getValue()), new Hour()), Double.parseDouble(metricVal2.getValue()));
						
						
						if (userSession.getSummaryChartType() == 0) 
						{
							/*TimeSeries timeSeries = new TimeSeries(scenarioTemp.getName());
							timeSeries.add(new Minute((int)Double.parseDouble(metricVal1.getValue()), new Hour()), Double.parseDouble(metricVal2.getValue()));
							System.out.println("time series point " + metricVal1.getValue() + ", " + metricVal2.getValue() );
							timeSeriesCollection.addSeries(timeSeries);*/
						} 
						else if (userSession.getSummaryChartType() == 1) 
						{
							XYSeries series = new XYSeries(scenarioTemp.getName());
							series.add(Double.parseDouble(metricVal1.getValue()), Double.parseDouble(metricVal2.getValue()));
							/*double[][] data = new double[2][1];
							data[0][0] = Double.parseDouble(metricVal1.getValue());
							data[1][0] = Double.parseDouble(metricVal2.getValue());
						    dataset.addSeries(scenario.getName(), data);*/
						    //System.out.println("time series point " + metricVal1.getValue() + ", " + metricVal2.getValue() );
							collection.addSeries(series);						
						} 
						else if (userSession.getSummaryChartType() == 2) 
						{
							categoryDataset.addValue(Double.parseDouble(metricVal1.getValue()), scenarioTemp.getName(), metric1.getName());
							categoryDataset.addValue(Double.parseDouble(metricVal2.getValue()), scenarioTemp.getName(), metric2.getName());
						} 
						else if (userSession.getSummaryChartType() == 3) 
						{
							pieDataset.setValue(scenarioTemp.getName(), Double.parseDouble(metricVal1.getValue()));
						}
						
						//index++;
					}				
															
					timeSeriesCollection.addSeries(timeSeries);
				
					//JFreeChart chart = null;
				
					if (userSession.getSummaryChartType() == 0) {
						userSession.setSummaryChartType(1);
					}
					
					// TODO
					if (userSession.getSummaryChartType() == 1) {
						//ScatterPlotVisualization demo = new ScatterPlotVisualization("Scatter plot", dataset, "", "", false);
						//chart = TimeSeriesVisualization.createChart(timeSeriesCollection, "Time series", metric1.getName(), metric2.getName());
					} else if (userSession.getSummaryChartType() == 2) {
						//chart = ScatterPlotVisualization.createChart(timeSeriesCollection, "Scatter plot", metric1.getName(), metric2.getName(), false);
					}

					if (timeSeriesCollection.getSeriesCount() > 0)
					{
						TimeSeriesVisualization demo = new TimeSeriesVisualization(project.getName(), timeSeriesCollection, "Time", "");
					}
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
		model.put("usersession", userSession);
		
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);
		
		return "summarychart";
	}

	@RequestMapping("drawtimeserieschart")
	public String drawTimeSeriesChart(Map<String, Object> model, HttpServletRequest request) throws Exception 
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (project == null || scenario == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);
		
		UserSession userSession = (UserSession) model.get("usersession");

		if (userSession == null)
		{
			userSession = new UserSession();
		}
		
		int nScenId = scenario.getScenid();
		String status = scenario.getStatus();

		if (simService.getRunningSimulations().contains(scenario.getScenid())) {
			status = "RUNNING";
		}

		if (status == null || status.isEmpty())
		{
			return "error";
		}

		model.put("status", status);

		Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
	    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		String unit = "";
		
	    // Get output variable results
		while(iterator.hasNext()) {
			int outputVarId = iterator.next(); 
	    
			// TODO Add values from all selected scenarios?
			
			try {
				OutputVariableDTO outputVar = outputVarService.findByID(outputVarId);
				SimulationResultDTO simResult = simResultService.findByOutVarIdScenId(outputVarId, nScenId);
				
				if (outputVar.getUnit() != null)
				{
					unit = "(" + outputVar.getUnit().getName() + ")";
				}
				
				if (simResult != null)
				{
					List<TimeSeriesValDTO> timeSeriesVals = simResultService.getTimeSeriesValsOrderedByTime(simResult.getSimresid());
					TimeSeries timeSeries = new TimeSeries(outputVar.getComponent().getName() + "." + outputVar.getName() + " " + unit);
	
					for (int i = 0; i < timeSeriesVals.size(); i++)
					{
						TimeSeriesValDTO timeSeriesVal = timeSeriesVals.get(i);
						timeSeries.add(new Minute(timeSeriesVal.getTime()), timeSeriesVal.getValue());
					}
					
					timeSeriesCollection.addSeries(timeSeries);
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
	    }

		iterator = userSession.getSelectedChartExtVarIds().iterator();
		   
		// Get external parameter time series
		while(iterator.hasNext()) {
			int extVarId = iterator.next(); 
	    
			try {
				ExtParamValDTO extVarVal = extParamValService.findByID(extVarId);
				TimeSeriesDTO timeSeriesDTO = extVarVal.getTimeseries();
				
				if (extVarVal.getExtparam().getUnit() != null)
				{
					unit = "(" + extVarVal.getExtparam().getUnit().getName() + ")";
				}

				if (timeSeriesDTO != null)
				{
					List<TimeSeriesValDTO> timeSeriesVals = timeSeriesValService.findByTimeSeriesIdOrderedByTime(timeSeriesDTO.getTseriesid());
					TimeSeries timeSeries = new TimeSeries(extVarVal.getExtparam().getName() + " " + unit);
					Iterator<TimeSeriesValDTO> timeSeriesIter = timeSeriesVals.iterator();
					
					while(timeSeriesIter.hasNext()) {
						TimeSeriesValDTO timeSeriesVal = timeSeriesIter.next();
						timeSeries.add(new Minute(timeSeriesVal.getTime()), timeSeriesVal.getValue());
					}
	
					timeSeriesCollection.addSeries(timeSeries);
				}
				else
				{
					TimeSeries timeSeries = new TimeSeries(extVarVal.getExtparam().getName() + " " + unit);
					double value = 0;
					
					if (!extVarVal.getValue().isEmpty()) {
						value = Double.parseDouble(extVarVal.getValue());
					}
					
					timeSeries.add(new Minute(new Date()), value);
					
					timeSeriesCollection.addSeries(timeSeries);
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
	    }
		
		iterator = userSession.getSelectedChartMetricIds().iterator();
		   
		if (timeSeriesCollection.getSeriesCount() > 0)
		{
			JFreeChart chart = null;
			ChartRenderingInfo chartInfo = new ChartRenderingInfo(new StandardEntityCollection());
			
			if (userSession.getTimeSeriesChartType() > 1)
			{
				userSession.setTimeSeriesChartType(0);
			}
			
			if (userSession.getTimeSeriesChartType() == 0) {
				chart = TimeSeriesVisualization.createChart(timeSeriesCollection, controllerService.getMessage("time_series", request), controllerService.getMessage("date", request), controllerService.getMessage("value", request));
			} else if (userSession.getTimeSeriesChartType() == 1) {
				chart = ScatterPlotVisualization.createChart(timeSeriesCollection, controllerService.getMessage("scatter_plot", request), controllerService.getMessage("date", request), controllerService.getMessage("value", request), true);
			} 

			if (chart != null)
			{
				String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
				String imgFileName = "timeserieschart_" + System.currentTimeMillis() + ".png";
				File file = new File(imgPath + imgFileName);
				System.out.println(file.getAbsolutePath());
				
				ChartUtilities.saveChartAsPNG(file, chart, 750, 400, chartInfo);
				
	            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
	                public String generateToolTipFragment(String arg0) {
	                    String toolTip = " title = \"" + arg0 + "\"";
	                    return (toolTip);
	                }
	            };

	            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
	                public String generateURLFragment(String arg0) {
	                    String address = "";
	                    return address;
	                }
	            };

	            String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
	            userSession.setTimeSeriesImageMap(map);
	            userSession.setTimeSeriesFile(imgFileName);
			}
		}

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);

		return "timeserieschart";
	}

	@RequestMapping("drawsummarychart")
	public String drawSummaryChart(Map<String, Object> model, HttpServletRequest request) throws Exception {
		ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (project == null || scenario == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);
		
		UserSession userSession = (UserSession) model.get("usersession");

		if (userSession == null)
		{
			userSession = new UserSession();
		}
		
		int nScenId = scenario.getScenid();
		String status = scenario.getStatus();

		if (simService.getRunningSimulations().contains(scenario.getScenid())) {
			status = "RUNNING";
		}

		if (status == null || status.isEmpty())
		{
			return "error";
		}

		model.put("status", status);
		
		Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
	    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		ChartRenderingInfo chartInfo = new ChartRenderingInfo(new StandardEntityCollection());
		
	    iterator = userSession.getSelectedChartMetricIds().iterator();
		   
		// Get metrics time series (max 2 metrics)
		if (userSession.getSelectedChartMetricIds().size() == 1)
		{
			timeSeriesCollection.removeAllSeries();
			XYSeriesCollection collection = new XYSeriesCollection();
			DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
			DefaultPieDataset pieDataset = new DefaultPieDataset();

			int metric1Id = iterator.next(); 
		    int index = 0;
		    
			try {
				MetricDTO metric1 = metricService.findByID(metric1Id);
				
				Set<Integer> scenarioIds = userSession.getScenarioIds();
				Iterator<Integer> scenIter = scenarioIds.iterator();
				
				while (scenIter.hasNext())
				{
					Integer scenarioId = (Integer) scenIter.next();
					int nScenarioId = (int)scenarioId;
					ScenarioDTO scenarioTemp = scenarioService.findByID(nScenarioId);
					MetricValDTO metricVal1 = metricService.getMetricVals(metric1Id, nScenarioId).get(0);
					DefaultXYDataset dataset = new DefaultXYDataset();

					if (userSession.getSummaryChartType() == 0)
					{
						userSession.setSummaryChartType(1);
					}
					else if (userSession.getSummaryChartType() == 2) 
					{
						categoryDataset.addValue(Double.parseDouble(metricVal1.getValue()), scenarioTemp.getName(), metric1.getName());
					} 
					else if (userSession.getSummaryChartType() == 3) 
					{
						pieDataset.setValue(scenarioTemp.getName(), Double.parseDouble(metricVal1.getValue()));
					}
					
					index++;
				}				
			
				JFreeChart chart = null;
				
				if (userSession.getSummaryChartType() == 0) {
					// No time series type for metrics
					userSession.setSummaryChartType(1);
				}
				
				if (userSession.getSummaryChartType() == 2) {
					chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("bar_chart", request), "", "");
				} else if (userSession.getSummaryChartType() == 3) {
					chart = PieChartVisualization.createChart(pieDataset, controllerService.getMessage("pie_chart", request) + " " + metric1.getName(), "", "");
				}
				
				if (chart != null)
				{
					String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
					String imgFileName = "summarychart_" + System.currentTimeMillis() + ".png";
					File file = new File(imgPath + imgFileName);
					System.out.println(file.getAbsolutePath());
					
					ChartUtilities.saveChartAsPNG(file, chart, 750, 400, chartInfo);
					
		            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
		                public String generateToolTipFragment(String arg0) {
		                    String toolTip = " title = \"" + arg0 + "\"";
		                    return (toolTip);
		                }
		            };

		            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
		                public String generateURLFragment(String arg0) {
		                    String address = "";
		                    return address;
		                }
		            };

		            String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
		            userSession.setSummaryImageMap(map);
		            userSession.setSummaryFile(imgFileName);
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		else if (userSession.getSelectedChartMetricIds().size() == 2)
		{
			timeSeriesCollection.removeAllSeries();
			XYSeriesCollection collection = new XYSeriesCollection();
			DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();

			int metric1Id = iterator.next(); 
			int metric2Id = iterator.next(); 
		    
			try {
				MetricDTO metric1 = metricService.findByID(metric1Id);
				MetricDTO metric2 = metricService.findByID(metric2Id);
				
				Set<Integer> scenarioIds = userSession.getScenarioIds();
				Iterator<Integer> scenIter = scenarioIds.iterator();
				
				while (scenIter.hasNext())
				{
					Integer scenarioId = (Integer) scenIter.next();
					int nScenarioId = (int)scenarioId;
					ScenarioDTO scenarioTemp = scenarioService.findByID(nScenarioId);
					
					List<MetricValDTO> metricVals1 = metricService.getMetricVals(metric1Id, nScenarioId);
					List<MetricValDTO> metricVals2 = metricService.getMetricVals(metric2Id, nScenarioId);
					double value1 = 0;
					double value2 = 0;
					
					if (metricVals1.size() == 0)
					{
						value1 = 0;
					} else {
						value1 = Double.parseDouble(metricVals1.get(0).getValue());
					}
					
					if (metricVals2.size() == 0)
					{
						value2 = 0;
					} else {
						value2 = Double.parseDouble(metricVals2.get(0).getValue());
					}
					
					if (userSession.getSummaryChartType() == 1) 
					{
						XYSeries series = new XYSeries(scenarioTemp.getName());
						series.add(value1, value2);
						collection.addSeries(series);						
					} 
					else if (userSession.getSummaryChartType() == 2) 
					{
						categoryDataset.addValue(value1, scenarioTemp.getName(), metric1.getName());
						categoryDataset.addValue(value2, scenarioTemp.getName(), metric2.getName());
					}
				}				
			
				JFreeChart chart = null;
				
				if (userSession.getSummaryChartType() == 0) {
					// No time series type for metrics
					userSession.setSummaryChartType(1);
				}
				
				if (userSession.getSummaryChartType() == 1) {
					String unit1 = "";
					String unit2 = "";
					
					if (metric1.getUnit() != null)
					{
						unit1 = "(" + metric1.getUnit().getName() + ")";
					}
					
					if (metric2.getUnit() != null)
					{
						unit2 = "(" + metric2.getUnit().getName() + ")";
					}

					chart = ScatterPlotVisualization.createChart(collection, controllerService.getMessage("scatter_plot", request), metric1.getName() + " " + unit1, metric2.getName() + " " + unit2, false);
				} else if (userSession.getSummaryChartType() == 2) {
					chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("bar_chart", request), "", "");
				}

				if (chart != null)
				{
					String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
					String imgFileName = "summary_" + System.currentTimeMillis() + ".png";
					File file = new File(imgPath + imgFileName);
					System.out.println(file.getAbsolutePath());
					
					ChartUtilities.saveChartAsPNG(file, chart, 750, 400, chartInfo);
					
		            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
		                public String generateToolTipFragment(String arg0) {
		                    String toolTip = " title = \"" + arg0 + "\"";
		                    return (toolTip);
		                }
		            };

		            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
		                public String generateURLFragment(String arg0) {
		                    String address = "";
		                    return address;
		                }
		            };

		            String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
		            userSession.setSummaryImageMap(map);
		            userSession.setSummaryFile(imgFileName);
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		else if (userSession.getSelectedChartMetricIds().size() > 2)
		{
			timeSeriesCollection.removeAllSeries();
			DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();

			while (iterator.hasNext()) 
			{
				int metricId = iterator.next(); 
			    
				try {
					MetricDTO metric = metricService.findByID(metricId);
					
					Set<Integer> scenarioIds = userSession.getScenarioIds();
					Iterator<Integer> scenIter = scenarioIds.iterator();
					
					while (scenIter.hasNext())
					{
						Integer scenarioId = (Integer) scenIter.next();
						int nScenarioId = (int)scenarioId;
						ScenarioDTO scenarioTemp = scenarioService.findByID(nScenarioId);
						List<MetricValDTO> metricVals = metricService.getMetricVals(metricId, nScenarioId);
						double value;
						
						if (metricVals.size() == 0)
						{
							value = 0;
						}
						else
						{
							value = Double.parseDouble(metricVals.get(0).getValue());
						}
	
						categoryDataset.addValue(value, scenarioTemp.getName(), metric.getName());
					}				
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			JFreeChart chart = null;
			
			if (userSession.getSummaryChartType() != 2) {
				// Only bar chart for more than 2 metrics
				userSession.setSummaryChartType(2);
			}
			
			chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("bar_chart", request), "", "");

			if (chart != null)
			{
				String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
				String imgFileName = "summary_" + System.currentTimeMillis() + ".png";
				File file = new File(imgPath + imgFileName);
				System.out.println(file.getAbsolutePath());
				
				ChartUtilities.saveChartAsPNG(file, chart, 750, 400, chartInfo);
				
	            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
	                public String generateToolTipFragment(String arg0) {
	                    String toolTip = " title = \"" + arg0 + "\"";
	                    return (toolTip);
	                }
	            };

	            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
	                public String generateURLFragment(String arg0) {
	                    String address = "";
	                    return address;
	                }
	            };

	            String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
	            userSession.setSummaryImageMap(map);
	            userSession.setSummaryFile(imgFileName);
			}
		}
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);

		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		return "summarychart";
	}

	@RequestMapping(value = "drawgachart", method = RequestMethod.GET)
	public String drawGAChart(Map<String, Object> model, HttpServletRequest request) throws Exception 
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);
		
		UserSession userSession = (UserSession) model.get("usersession");

		if (userSession == null)
		{
			userSession = new UserSession();
		}
		
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

		if (scenGen == null)
		{
			return "error";
		}
		
		if (userSession.getSelectedGAObjFuncIds().size() > 2
			|| userSession.getSelectedGAObjFuncIds().size() == 0)
		{
			return "error";
		}
		
	    Iterator<Integer> iterator = userSession.getSelectedGAObjFuncIds().iterator();
		int objFunc1Id = iterator.next();
		
		ObjectiveFunctionDTO objFunc1 = objFuncService.findByID(objFunc1Id);
		
		if (userSession.getSelectedGAObjFuncIds().size() == 1)
		{
			ArrayList<ObjectiveFunctionResultDTO> listResults1 = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFunc1Id);
			Iterator<ObjectiveFunctionResultDTO> resultIter = listResults1.iterator();
			DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
			
			while (resultIter.hasNext())
			{
				ObjectiveFunctionResultDTO result = (ObjectiveFunctionResultDTO) resultIter.next();
				
				if (!userSession.hasSelectedGAScenarioId(result.getScenID()))
				{
					continue;
				}
				
				ScenarioDTO scenarioTemp = scenarioService.findByID(result.getScenID());
				String value1 = result.getValue();
				
				ObjectiveFunctionDTO objFunc = objFuncService.findByID(result.getObtfunctionid());
				categoryDataset.addValue(Double.parseDouble(value1), scenarioTemp.getName(), objFunc.getName());
			}
			
			ChartRenderingInfo chartInfo = new ChartRenderingInfo(new StandardEntityCollection());
			JFreeChart chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("genetic_optimization_scenario_results", request), "", "");
			
			if (chart != null)
			{
				String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
				String imgFileName = "gachart_" + System.currentTimeMillis() + ".png";
				File file = new File(imgPath + imgFileName);
				System.out.println(file.getAbsolutePath());
				
				ChartUtilities.saveChartAsPNG(file, chart, 750, 400, chartInfo);
				
	            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
	                public String generateToolTipFragment(String arg0) {
	                    String toolTip = " title = \"" + arg0 + "\"";
	                    return (toolTip);
	                }
	            };

	            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
	                public String generateURLFragment(String arg0) {
	                    String address = "openscenario.html";
	                    return (address);
	                }
	            };

	            String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
	            userSession.setGAChartImageMap(map);
	            userSession.setGAChartFile(imgFileName);
			}
		}
		else if (userSession.getSelectedGAObjFuncIds().size() == 2)
		{
			int objFunc2Id = iterator.next();
			ObjectiveFunctionDTO objFunc2 = objFuncService.findByID(objFunc2Id);
			XYSeriesCollection collection = new XYSeriesCollection();

			try {
				ArrayList<ObjectiveFunctionResultDTO> listResults1 = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFunc1Id);
				ArrayList<ObjectiveFunctionResultDTO> listResults2 = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFunc2Id);
				
				Iterator<ObjectiveFunctionResultDTO> resultIter = listResults1.iterator();
				
				while (resultIter.hasNext())
				{
					ObjectiveFunctionResultDTO result = (ObjectiveFunctionResultDTO) resultIter.next();
					
					if (!userSession.hasSelectedGAScenarioId(result.getScenID()))
					{
						continue;
					}
					
					ScenarioDTO scenarioTemp = scenarioService.findByID(result.getScenID());

					String value1 = result.getValue();
					String value2 = "";
					
					Iterator<ObjectiveFunctionResultDTO> resultIter2 = listResults2.iterator();
					
					while(resultIter2.hasNext())
					{
						ObjectiveFunctionResultDTO result2 = (ObjectiveFunctionResultDTO) resultIter2.next();
						
						if (result2.getScenID() == result.getScenID())
						{
							value2 = result2.getValue();
							break;
						}
					}
					
					XYSeries series = new XYSeries(scenarioTemp.getName());
					series.add(Double.parseDouble(value1), Double.parseDouble(value2));
					
					collection.addSeries(series);						
					
					/*if (result.isScengenresultParetooptimal()) {
						collection.addSeries(series);
					} else {
						collection.addSeries(series);
					}*/
				}				
			
				ChartRenderingInfo chartInfo = new ChartRenderingInfo(new StandardEntityCollection());
				JFreeChart chart = ScatterPlotVisualization.createChart(collection, controllerService.getMessage("genetic_optimization_scenario_results", request), objFunc1.getName(), objFunc2.getName(), false);
				
				if (chart != null)
				{
					String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
					String imgFileName = "gachart_" + System.currentTimeMillis() + ".png";
					File file = new File(imgPath + imgFileName);
					System.out.println(file.getAbsolutePath());
					
					ChartUtilities.saveChartAsPNG(file, chart, 750, 400, chartInfo);
					
		            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
		                public String generateToolTipFragment(String arg0) {
		                    String toolTip = " title = \"" + arg0 + "\"";
		                    return (toolTip);
		                }
		            };

		            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
		                public String generateURLFragment(String arg0) {
		                    String address = "openscenario.html";
		                    return (address);
		                }
		            };

		            String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
		            userSession.setGAChartImageMap(map);
		            userSession.setGAChartFile(imgFileName);
				}

				model.put("usersession", userSession);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);

		List<ObjectiveFunctionDTO> objFuncs = null;
		
		try {
			objFuncs = scenGenService.getObjectiveFunctions(scenGen.getScengenid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		model.put("objFuncs", objFuncs);

		return "gachart";//"redirect:/gachart.html";
	}

	@RequestMapping("gachart.png")
	public void renderGAChart(Map<String, Object> model, String variation, OutputStream stream,
		HttpServletRequest request) throws Exception {
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return;
		}
		securityAuthorization.atLeastGuest_guest(project);
		
		UserSession userSession = (UserSession) model.get("usersession");

		if (userSession == null)
		{
			userSession = new UserSession();
		}
		
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

		if (scenGen == null)
		{
			return;
		}
		
		if (userSession.getSelectedGAObjFuncIds().size() > 2
			|| userSession.getSelectedGAObjFuncIds().size() == 0)
		{
			return;
		}
		
	    Iterator<Integer> iterator = userSession.getSelectedGAObjFuncIds().iterator();
		int objFunc1Id = iterator.next();
		
		ObjectiveFunctionDTO objFunc1 = objFuncService.findByID(objFunc1Id);
		
		if (userSession.getSelectedGAObjFuncIds().size() == 1)
		{
			ArrayList<ObjectiveFunctionResultDTO> listResults1 = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFunc1Id);
			Iterator<ObjectiveFunctionResultDTO> resultIter = listResults1.iterator();
			DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
			
			while (resultIter.hasNext())
			{
				ObjectiveFunctionResultDTO result = (ObjectiveFunctionResultDTO) resultIter.next();
				
				if (!userSession.hasSelectedGAScenarioId(result.getScenID()))
				{
					continue;
				}
				
				ScenarioDTO scenarioTemp = scenarioService.findByID(result.getScenID());
				String value1 = result.getValue();
				
				ObjectiveFunctionDTO objFunc = objFuncService.findByID(result.getObtfunctionid());
				categoryDataset.addValue(Double.parseDouble(value1), scenarioTemp.getName(), objFunc.getName());
			}
			
			JFreeChart chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("genetic_optimization_scenario_results", request), "", "");
			
			if (chart != null)
			{
				CategoryPlot categoryPlot = chart.getCategoryPlot();
				BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
				br.setMaximumBarWidth(.10); 
				ChartUtilities.writeChartAsPNG(stream, chart, 750, 400);
			}
		}
		else if (userSession.getSelectedGAObjFuncIds().size() == 2)
		{
			int objFunc2Id = iterator.next();
			ObjectiveFunctionDTO objFunc2 = objFuncService.findByID(objFunc2Id);
			XYSeriesCollection collection = new XYSeriesCollection();

			try {
				ArrayList<ObjectiveFunctionResultDTO> listResults1 = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFunc1Id);
				ArrayList<ObjectiveFunctionResultDTO> listResults2 = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFunc2Id);
				
				Iterator<ObjectiveFunctionResultDTO> resultIter = listResults1.iterator();
				
				while (resultIter.hasNext())
				{
					ObjectiveFunctionResultDTO result = (ObjectiveFunctionResultDTO) resultIter.next();
					
					if (!userSession.hasSelectedGAScenarioId(result.getScenID()))
					{
						continue;
					}
					
					ScenarioDTO scenarioTemp = scenarioService.findByID(result.getScenID());

					String value1 = result.getValue();
					String value2 = "";
					
					Iterator<ObjectiveFunctionResultDTO> resultIter2 = listResults2.iterator();
					
					while(resultIter2.hasNext())
					{
						ObjectiveFunctionResultDTO result2 = (ObjectiveFunctionResultDTO) resultIter2.next();
						
						if (result2.getScenID() == result.getScenID())
						{
							value2 = result2.getValue();
							break;
						}
					}
					
					XYSeries series = new XYSeries(scenarioTemp.getName());
					series.add(Double.parseDouble(value1), Double.parseDouble(value2));
					
					collection.addSeries(series);						
					
					/*if (result.isScengenresultParetooptimal()) {
						collection.addSeries(series);
					} else {
						collection.addSeries(series);
					}*/
				}				
			
				ChartRenderingInfo chartInfo = new ChartRenderingInfo(new StandardEntityCollection());
				JFreeChart chart = ScatterPlotVisualization.createChart(collection, controllerService.getMessage("genetic_optimization_scenario_results", request), objFunc1.getName(), objFunc2.getName(), false);
				
				if (chart != null)
				{
					ChartUtilities.writeChartAsPNG(stream, chart, 750, 400, chartInfo);
					//ChartUtilities.writeImageMap(writer, name, info, useOverLibForToolTips);

		            ToolTipTagFragmentGenerator tooltipConstructor = new ToolTipTagFragmentGenerator() {
		                public String generateToolTipFragment(String arg0) {
		                    String toolTip = " title = \"value" + arg0 + "\"";
		                    return (toolTip);
		                }
		            };

		            URLTagFragmentGenerator urlConstructor = new URLTagFragmentGenerator() {
		                public String generateURLFragment(String arg0) {
		                    String address = " href=\"ControllerAddress\\methodName?"
		                        + arg0 + "\"";
		                    return (address);
		                }
		            };

		            //String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
		            //userSession.setImageMap(map);
				}

				model.put("usersession", userSession);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value="viewtable", method=RequestMethod.GET)
	public String viewTable(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid) {

		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);
		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		if (outputvarid != null)
		{
			userSession.addOutputVarId(Integer.parseInt(outputvarid));
		}
		
		if (extparamid != null)
		{
			userSession.addExtVarId(Integer.parseInt(extparamid));
		}
		
		model.put("usersession", userSession);
		
		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (scenario == null)
		{
			return "error";
		}
		
		try {
			scenario = scenarioService.findByID(scenario.getScenid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
		model.put("scenario", scenario);
		String status = scenario.getStatus();

		if (simService.getRunningSimulations().contains(scenario.getScenid())) {
			status = "RUNNING";
		}
			
		model.put("status", status);

		Set<MetricValDTO> listMetricVals = scenarioService.getMetricsValues(scenario.getScenid());
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		Iterator<MetricValDTO> metricValIter = listMetricVals.iterator();
		
		while (metricValIter.hasNext())
		{
			MetricValDTO metricVal = metricValIter.next();
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid()
				&& metricVal.getScenariometrics().getScenario().getScenid() == scenario.getScenid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);
		
		return "viewtable";
	}
	
	@RequestMapping(value="writetable", method=RequestMethod.GET)
	public String writeTable(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid) {

		int nOutputVarId = Integer.parseInt(outputvarid);
		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (scenario == null)
		{
			return "error";
		}
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		String status = scenario.getStatus();

		if (simService.getRunningSimulations().contains(scenario.getScenid())) {
			status = "RUNNING";
		}
			
		model.put("status", status);
		
		/*Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
	    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		
	    // Get output variable results
		while(iterator.hasNext()) {
			int outputVarId = iterator.next(); 
	    
			try {
				OutputVariableDTO outputVar = outputVarService.findByID(outputVarId);
				SimulationResultDTO simResult = simResultService.findByOutVarIdScenId(outputVarId, nScenId);
					
				List<TimeSeriesValDTO> timeSeriesVals = simResultService.getTimeSeriesValsOrderedByTime(simResult.getSimresid());
				TimeSeries timeSeries = new TimeSeries(outputVar.getName());

				for (int i = 0; i < timeSeriesVals.size(); i++)
				{
					TimeSeriesValDTO timeSeriesVal = timeSeriesVals.get(i);
					String value = timeSeriesVal.getValue();
					value = value.replace(",", ".");
					timeSeries.add(new Minute(timeSeriesVal.getTime()), Double.parseDouble(value));
				}
				
				timeSeriesCollection.addSeries(timeSeries);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
	    }*/
		
		Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
	    List<Double> listOutputVarVals = new ArrayList<Double>();
	    List<String> listOutputVarTimes = new ArrayList<String>();
	    OutputVariableDTO outputVar = null;
	    
	    try {
			outputVar = outputVarService.findByID(nOutputVarId);
			SimulationResultDTO simResult = simResultService.findByOutVarIdScenId(nOutputVarId, scenario.getScenid());
			List<TimeSeriesValDTO> timeSeriesVals = simResultService.getTimeSeriesValsOrderedByTime(simResult.getSimresid());
			
			for (int i = 0; i < timeSeriesVals.size(); i++)
			{
				TimeSeriesValDTO timeSeriesVal = timeSeriesVals.get(i);
				listOutputVarVals.add(timeSeriesVal.getValue());
				listOutputVarTimes.add(timeSeriesVal.getTime().toString());
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	
	    model.put("listOutputVarVals", listOutputVarVals);
	    model.put("listOutputVarTimes", listOutputVarTimes);
		model.put("selectedOutputVar", outputVar);
	    
		/*iterator = userSession.getSelectedChartExtVarIds().iterator();
		   
		// Get external parameter time series
		while(iterator.hasNext()) {
			int extVarId = iterator.next(); 
	    
			try {
				ExtParamValDTO extVarVal = extParamValService.findByID(extVarId);
				TimeSeriesDTO timeSeriesDTO = extVarVal.getTimeseries();
				
				if (timeSeriesDTO != null)
				{
					Set<TimeSeriesVal> timeSeriesVals = timeSeriesDTO.getTimeseriesvals();
					TimeSeries timeSeries = new TimeSeries(extVarVal.getExtparam().getName());
					Iterator<TimeSeriesVal> timeSeriesIter = timeSeriesVals.iterator();
					
					while(timeSeriesIter.hasNext()) {
						TimeSeriesVal timeSeriesVal = timeSeriesIter.next();
						timeSeries.add(new Minute(timeSeriesVal.getTime()), Double.parseDouble(timeSeriesVal.getValue()));
					}
	
					timeSeriesCollection.addSeries(timeSeries);
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
	    }*/
		
		model.put("usersession", userSession);
		
		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		Set<MetricValDTO> listMetricVals = scenarioService.getMetricsValues(scenario.getScenid());
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		Iterator<MetricValDTO> metricValIter = listMetricVals.iterator();
		
		while (metricValIter.hasNext())
		{
			MetricValDTO metricVal = metricValIter.next();
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid()
				&& metricVal.getScenariometrics().getScenario().getScenid() == scenario.getScenid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);
		
		return "viewtable";
	}
	
	@RequestMapping(value="gachart", method=RequestMethod.GET)
	public String makeGAChart(Map<String, Object> model, 
		@RequestParam(value="scenarioid", required=false) String scenarioId,
		@RequestParam(value="objfuncid", required=false) String objfuncid,
		@RequestParam(value="action", required=false) String action,
		@RequestParam(value="resetselections", required=false) String reset,
		@RequestParam(value="charttype", required=false) String charttype) {

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);

		if (action != null)
		{
			if (action.equals("removeall"))
			{
				userSession.removeAllSelectedGAObjFuncIds();
				userSession.removeAllSelectedGAScenarioIds();
			}
		}

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

		if (scenGen == null)
		{
			return "error";
		}

		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);
		
		if (scenarioId != null && action != null)
		{
			int nScenarioId = Integer.parseInt(scenarioId);
			
			if (action.equals("add"))
			{
				userSession.addSelectedGAScenarioId(nScenarioId);
			}
			else if (action.equals("remove"))
			{
				userSession.removeSelectedGAScenarioId(nScenarioId);
			}
		}

		List<ObjectiveFunctionDTO> objFuncs = null;
		
		try {
			objFuncs = scenGenService.getObjectiveFunctions(scenGen.getScengenid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		model.put("objFuncs", objFuncs);

		// Reset selections
		if (reset != null && reset.equalsIgnoreCase("true"))
		{
			userSession.removeAllSelectedGAScenarioIds();
			
			for (ScenarioDTO scenario : scenarios)
			{
				userSession.addSelectedGAScenarioId(scenario.getScenid());
			}

			userSession.removeAllSelectedGAObjFuncIds();
			
			for (ObjectiveFunctionDTO objFunc : objFuncs)
			{
				userSession.addSelectedGAObjFuncId(objFunc.getObtfunctionid());
				
				if (userSession.getSelectedGAObjFuncIds().size() >= 2)
				{
					break;
				}
			}
		}

		if (objfuncid != null && action != null)
		{
			if (action.equals("add"))
			{
				userSession.addSelectedGAObjFuncId(Integer.parseInt(objfuncid));
			}
			else if (action.equals("remove"))
			{
				userSession.removeSelectedGAObjFuncId(Integer.parseInt(objfuncid));
			}
		}

		model.put("usersession", userSession);
		
		return "gachart";
	}
}
