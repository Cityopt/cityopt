package eu.cityopt.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import org.jfree.data.time.Day;
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
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.ObjectiveFunctionResultDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.ScenarioSimpleDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.DTO.TimeSeriesDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.repository.CustomQueryRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.security.SecurityAuthorization;
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
import eu.cityopt.web.ScenarioInfo;
import eu.cityopt.web.TimeSeriesVisualization;
import eu.cityopt.web.UserSession;

@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession", "user",
	"activeblock", "page"})
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

    @Autowired
    CustomQueryRepository customQueryRepository;
    
	@RequestMapping(value="timeserieschart", method=RequestMethod.GET)
	public String timeSeriesChart(Map<String, Object> model, 
		@RequestParam(value="scenarioid", required=false) String scenarioId,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid,
		@RequestParam(value="metricid", required=false) String metricid,
		@RequestParam(value="action", required=false) String action,
		@RequestParam(value="charttype", required=false) String charttype) {

		model.put("activeblock", "visualization");
    	model.put("page", "timeserieschart");

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
		
		if (simService.getRunningSimulations().containsKey(scenario.getScenid())) {
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

		model.put("usersession", userSession);
		
		controllerService.getSelectedOutAndExtParameters(model, userSession);
		
		controllerService.getProjectExternalParameterValues(model, project);
		
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

		model.put("activeblock", "visualization");
    	model.put("page", "summarychart");

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		model.put("usersession", userSession);

		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		securityAuthorization.atLeastGuest_guest(project);
		
		if (charttype != null)
		{
			if (charttype.equals("0"))
			{
				charttype = "1";
			}
			
			userSession.setSummaryChartType(Integer.parseInt(charttype));
			model.put("charttype", Integer.parseInt(charttype));
		}
		else
		{
			int nChartType = userSession.getSummaryChartType();
			
			if (nChartType == 0)
			{
				nChartType = 1;
			}

			model.put("charttype", nChartType);
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
		
		model.put("usersession", userSession);
		controllerService.getSelectedScenariosAndMetrics(model, userSession);
		
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
		
		if (userSession.getTimeSeriesChartType() >= 0)
		{
			model.put("charttype", userSession.getTimeSeriesChartType());
		}
		
		if (userSession.getSelectedChartOutputVarIds().size() == 0 && userSession.getSelectedChartExtVarIds().size() == 0)
	    {
	    	model.put("error", controllerService.getMessage("not_enough_selections", request));
	    }
	    
		int nScenId = scenario.getScenid();
		String status = scenario.getStatus();

		if (simService.getRunningSimulations().containsKey(scenario.getScenid())) {
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
				chart = TimeSeriesVisualization.createChart(timeSeriesCollection, 
					controllerService.getMessage("time_series", request), 
					controllerService.getMessage("date", request), 
					controllerService.getMessage("value", request));
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

		controllerService.getSelectedOutAndExtParameters(model, userSession);
		controllerService.getProjectExternalParameterValues(model, project);

		model.put("usersession", userSession);
		
		return "timeserieschart";
	}

	@RequestMapping("drawsummarychart")
	public String drawSummaryChart(Map<String, Object> model, HttpServletRequest request) throws Exception {
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

		if (userSession.getSummaryChartType() > 0)
		{
			model.put("charttype", userSession.getSummaryChartType());
		}
		else
		{
			model.put("charttype", 1);
		}
		
		ChartRenderingInfo chartInfo = new ChartRenderingInfo(new StandardEntityCollection());
		Iterator<Integer> iterator = userSession.getSelectedChartMetricIds().iterator();
		   
	    if (userSession.getSelectedChartMetricIds().size() == 0)
	    {
	    	model.put("error", controllerService.getMessage("not_enough_metric_selections", request));
	    }
	    
	    if (userSession.getScenarioIds().size() == 0)
	    {
	    	model.put("error", controllerService.getMessage("not_enough_scenario_selections", request));
	    }

		// Get metrics time series (max 2 metrics)
		if (userSession.getSelectedChartMetricIds().size() == 1)
		{
			DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
			DefaultPieDataset pieDataset = new DefaultPieDataset();

			int metric1Id = iterator.next(); 
		    
			try {
				MetricDTO metric1 = metricService.findByID(metric1Id);
				
				Set<Integer> scenarioIds = userSession.getScenarioIds();
				Iterator<Integer> scenIter = scenarioIds.iterator();
				
				while (scenIter.hasNext())
				{
					Integer scenarioId = (Integer) scenIter.next();
					int nScenarioId = (int)scenarioId;
					ScenarioDTO scenarioTemp = scenarioService.findByID(nScenarioId);
					List<MetricValDTO> listMetricVals = metricService.getMetricVals(metric1Id, nScenarioId);
					
					if (listMetricVals.size() == 0)
					{
						String error = controllerService.getMessage("metric_values_missing", request) + metric1.getName();
						System.out.println(error);
						model.put("error", error);
						continue;
					}
					
					MetricValDTO metricVal1 = listMetricVals.get(0);
					
					if (userSession.getSummaryChartType() == 3 && Double.parseDouble(metricVal1.getValue()) < 0)
					{
						model.put("error", controllerService.getMessage("cant_create_pie_chart_with_negative_values", request));
					}
					
					if (userSession.getSummaryChartType() == 0 || userSession.getSummaryChartType() == 1)
					{
						userSession.setSummaryChartType(1);

				    	model.put("error", controllerService.getMessage("not_enough_metric_selections", request));
					}
					else if (userSession.getSummaryChartType() == 2) 
					{
						categoryDataset.addValue(Double.parseDouble(metricVal1.getValue()), scenarioTemp.getName(), metric1.getName());
					} 
					else if (userSession.getSummaryChartType() == 3) 
					{
						pieDataset.setValue(scenarioTemp.getName(), Double.parseDouble(metricVal1.getValue()));
					}
				}				
			
				JFreeChart chart = null;
				int nChartType = userSession.getSummaryChartType();
				
				if (nChartType == 0) {
					// No time series type for metrics
					userSession.setSummaryChartType(1);
					nChartType = 1;
				}
				
				if (nChartType == 2) {
					chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("bar_chart", request), "", "", true);
				} else if (nChartType == 3) {
					chart = PieChartVisualization.createChart(pieDataset, controllerService.getMessage("pie_chart", request) + " " + metric1.getName(), "", "");
				}

				if (chart != null)
				{
					String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
					String imgFileName = "summarychart_" + System.currentTimeMillis() + ".png";
					File file = new File(imgPath + imgFileName);
					System.out.println("Wrote file " + file.getAbsolutePath());
					
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
					chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("bar_chart", request), "", "", true);
				}

				if (chart != null)
				{
					String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
					String imgFileName = "summarychart_" + System.currentTimeMillis() + ".png";
					File file = new File(imgPath + imgFileName);
					System.out.println("Wrote file " + file.getAbsolutePath());
					
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
			
			chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("bar_chart", request), "", "", true);

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
		}
		
		controllerService.getSelectedScenariosAndMetrics(model, userSession);
		
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
		
		if (userSession.getSelectedGAObjFuncIds().size() == 0)
	    {
	    	model.put("error", controllerService.getMessage("not_enough_selections", request));
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
			JFreeChart chart = BarChartVisualization.createChart(categoryDataset, controllerService.getMessage("genetic_optimization_scenario_results", request), "", "", true);
			
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
		else if (userSession.getSelectedGAObjFuncIds().size() > 2)
		{
			iterator = userSession.getSelectedGAObjFuncIds().iterator();
			ArrayList<ArrayList<Double>> allObjFuncValues = new ArrayList<ArrayList<Double>>();
			ArrayList<String> scenarioNames = new ArrayList<String>();
			ArrayList<String> functionNames = new ArrayList<String>();

			// Go through objective functions
			while (iterator.hasNext())
			{
				int objFuncId = iterator.next();
				ArrayList<Double> objFuncValues = new ArrayList<Double>();
				ObjectiveFunctionDTO objFunc = objFuncService.findByID(objFuncId);
				
				functionNames.add(objFunc.getName());
				
				try {
					ArrayList<ObjectiveFunctionResultDTO> listResults = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFuncId);
					
					Iterator<ObjectiveFunctionResultDTO> resultIter = listResults.iterator();
					
					while (resultIter.hasNext())
					{
						ObjectiveFunctionResultDTO result = (ObjectiveFunctionResultDTO) resultIter.next();
						
						if (!userSession.hasSelectedGAScenarioId(result.getScenID()))
						{
							continue;
						}
						
						// TODO fix long list
						ScenarioDTO scenarioTemp = scenarioService.findByID(result.getScenID());
						scenarioNames.add(scenarioTemp.getName());
						
						String value = result.getValue();
						objFuncValues.add(Double.parseDouble(value));
					}				
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				// Define start and end of scale
				double min = controllerService.getMinValue(objFuncValues);
				double max = controllerService.getMaxValue(objFuncValues);
				double length = max - min;
				double start = min - length / 10;

				ArrayList<Double> scaledValues = new ArrayList<Double>();
				
				// Scale values
				for (Double d : objFuncValues)
				{
					double scaledValue = (d - start) / (length * 1.2);
					scaledValues.add(scaledValue);
				}
				allObjFuncValues.add(scaledValues);
			}

			DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
			
			for (int i = 0; i < allObjFuncValues.get(0).size(); i++)
			{
				int j = 0;

				for (ArrayList<Double> list : allObjFuncValues)
				{
					double value = (double) list.get(i);
					categoryDataset.addValue(value, scenarioNames.get(i), functionNames.get(j));
					j++;
				}
			}
				
			JFreeChart chart = null;
			ChartRenderingInfo chartInfo = new ChartRenderingInfo(new StandardEntityCollection());
			
			chart = BarChartVisualization.createChart(categoryDataset, 
				controllerService.getMessage("genetic_optimization_scenario_results", request), 
				controllerService.getMessage("objective_function", request), 
				controllerService.getMessage("value", request), false);

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
	                    String address = "";
	                    return address;
	                }
	            };

	            String map = ChartUtilities.getImageMap("chart", chartInfo, tooltipConstructor, urlConstructor);
	            userSession.setGAChartImageMap(map);
	            userSession.setGAChartFile(imgFileName);
			}
		}
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		Set<ScenarioInfo> scenarioInfos = new HashSet<ScenarioInfo>();
        Set<Integer> paretoOptimal =
                customQueryRepository.findParetoOptimalScenarios(
                        project.getPrjid(), scenGen.getScengenid());
		
		for (ScenarioDTO scenario : scenarios)
		{
			ScenarioInfo scenarioInfo = new ScenarioInfo();
			scenarioInfo.setName(scenario.getName());
			scenarioInfo.setId(scenario.getScenid());
			scenarioInfo.setPareto(paretoOptimal.contains(scenario.getScenid()));
			
			scenarioInfos.add(scenarioInfo);
		}
		
		model.put("scenarioInfos", scenarioInfos);

		List<ObjectiveFunctionDTO> objFuncs = null;
		
		try {
			objFuncs = scenGenService.getObjectiveFunctions(scenGen.getScengenid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		model.put("objFuncs", objFuncs);
		
		return "gachart";//"redirect:/gachart.html";
	}

	@RequestMapping(value="viewtable", method=RequestMethod.GET)
	public String viewTable(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid) {

		model.put("activeblock", "visualization");
    	model.put("page", "viewtable");

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

		if (simService.getRunningSimulations().containsKey(scenario.getScenid())) {
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

		if (simService.getRunningSimulations().containsKey(scenario.getScenid())) {
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

		model.put("activeblock", "visualization");
    	model.put("page", "gachart");

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

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

		if (scenGen == null)
		{
			return "error";
		}

		Set<ScenarioSimpleDTO> scenarios = scenGen.getScenarios();
		Set<ScenarioInfo> scenarioInfos = new HashSet<ScenarioInfo>();
        Set<Integer> paretoOptimal =
                customQueryRepository.findParetoOptimalScenarios(
                        project.getPrjid(), scenGen.getScengenid());
		
		for (ScenarioSimpleDTO scenario : scenarios)
		{
			ScenarioInfo scenarioInfo = new ScenarioInfo();
			scenarioInfo.setName(scenario.getName());
			scenarioInfo.setId(scenario.getScenid());
			scenarioInfo.setPareto(paretoOptimal.contains(scenario.getScenid()));
			
			scenarioInfos.add(scenarioInfo);
		}
		
		model.put("scenarioInfos", scenarioInfos);
		
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

		if (action != null)
		{
			if (action.equals("selectall"))
			{
				userSession.removeAllSelectedGAScenarioIds();
				
				for (ScenarioSimpleDTO scenario : scenarios)
				{
					userSession.addSelectedGAScenarioId(scenario.getScenid());
				}

				userSession.removeAllSelectedGAObjFuncIds();
				
				for (ObjectiveFunctionDTO objFunc : objFuncs)
				{
					userSession.addSelectedGAObjFuncId(objFunc.getObtfunctionid());
				}
			}
			else if (action.equals("selectpareto"))
			{
				reset = "true";
			}
			else if (action.equals("removeall"))
			{
				userSession.removeAllSelectedGAObjFuncIds();
				userSession.removeAllSelectedGAScenarioIds();
			}
		}
		
		// Reset selections
		if (reset != null && reset.equalsIgnoreCase("true"))
		{
			userSession.removeAllSelectedGAScenarioIds();

			if (objFuncs.size() > 0)
			{
				ObjectiveFunctionDTO objFuncFirst = objFuncs.get(0);
	
				ArrayList<ObjectiveFunctionResultDTO> listResults = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(scenGen.getScengenid(), objFuncFirst.getObtfunctionid());
				Iterator<ObjectiveFunctionResultDTO> resultIter = listResults.iterator();
				
				while (resultIter.hasNext())
				{
					ObjectiveFunctionResultDTO result = (ObjectiveFunctionResultDTO) resultIter.next();
					
					if (result.isScengenresultParetooptimal())
					{
						userSession.addSelectedGAScenarioId(result.getScenID());
					}
				}
			
				userSession.removeAllSelectedGAObjFuncIds();
				
				for (ObjectiveFunctionDTO objFunc : objFuncs)
				{
					userSession.addSelectedGAObjFuncId(objFunc.getObtfunctionid());
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
