package eu.cityopt.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.DefaultXYDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import eu.cityopt.DTO.AlgoParamValDTO;
import eu.cityopt.DTO.AlgorithmDTO;
import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.service.AlgoParamValService;
import eu.cityopt.service.AlgorithmService;
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
import eu.cityopt.service.ModelParameterGrouping;
import eu.cityopt.service.ObjectiveFunctionService;
import eu.cityopt.service.OptConstraintService;
import eu.cityopt.service.OptSearchConstService;
import eu.cityopt.service.OptimizationSetService;
import eu.cityopt.service.OutputVariableService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioGeneratorService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.SearchOptimizationResults;
import eu.cityopt.service.SimulationResultService;
import eu.cityopt.service.TimeSeriesService;
import eu.cityopt.service.TimeSeriesValService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;
import eu.cityopt.sim.service.ScenarioGenerationService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.AlgoParamValForm;
import eu.cityopt.web.ExtParamValSetForm;
import eu.cityopt.web.ModelParamForm;
import eu.cityopt.web.UserSession;

/**
 * @author Olli Stenlund, Hannu Rummukainen
 *
 */
@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession", "user"})
public class OptimizationController {
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
	ComponentInputParamDTOService componentInputParamService;
	
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
	AlgorithmService algorithmService;

	@Autowired
	AlgoParamValService algoParamValService;

	@Autowired
	SimulationService simulationService;

	@Autowired
	ScenarioGenerationService scenarioGenerationService;

	@RequestMapping(value="createobjfunction",method=RequestMethod.GET)
	public String getCreateObjFunction(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	
		OptimizationSetDTO optSet = (OptimizationSetDTO) model.get("optimizationset");
		
		if (optSet == null)
		{
			optSet = new OptimizationSetDTO();
		}
		
		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}
		model.put("usersession", userSession);
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
				
				Set<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
				model.put("inputParameters", inputParams);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		List<ExtParamValDTO> extParamVals = null;
		int defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
		
		if (defaultExtParamValSetId != 0)
		{
			try {
				ExtParamValSetDTO extParamValSet = extParamValSetService.findByID(defaultExtParamValSetId);
				model.put("extParamValSet", extParamValSet);
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			try {
				extParamVals = extParamValSetService.getExtParamVals(defaultExtParamValSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("extParamVals", extParamVals);
		}
		
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		ObjectiveFunctionDTO function = new ObjectiveFunctionDTO();
		model.put("function", function);
		
		return "createobjfunction";
	}

	@RequestMapping(value="createobjfunction", method=RequestMethod.POST)
	public String getCreateObjFunctionPost(ObjectiveFunctionDTO function, HttpServletRequest request, Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		OptimizationSetDTO optSet = null;
		
		if (model.containsKey("optimizationset"))
		{
			optSet = (OptimizationSetDTO) model.get("optimizationset");
			
			try {
				optSet = optSetService.findByID(optSet.getOptid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		else
		{
			return "error";
		}
		
		String optSense = request.getParameter("optsense");
		boolean isMaximize = false;
		
		if (optSense.equals("1"))
		{
			isMaximize = false;
		}
		else if (optSense.equals("2"))
		{
			isMaximize = true;
		}
		
		if (function != null && function.getExpression() != null)
		{
			ObjectiveFunctionDTO newFunc = new ObjectiveFunctionDTO();
			newFunc.setName(function.getName());
			newFunc.setExpression(function.getExpression());
			newFunc.setIsmaximise(isMaximize);
			newFunc.setProject(project);
			newFunc = objFuncService.save(newFunc);
			optSet.setObjectivefunction(newFunc);
			
			try {
				optSet = optSetService.update(optSet);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}

		model.put("optimizationset", optSet);
		model.put("successful", "Optimization set succesfully created.");			

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();			
		}		
		model.put("constraints", optSearchConstraints);	
		
		List<MetricValDTO> listMetricVals = metricValService.findAll();
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		
		for (int i = 0; i < listMetricVals.size(); i++)
		{
			MetricValDTO metricVal = listMetricVals.get(i);
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);
		
		return "editoptimizationset";
	}
	
	@RequestMapping(value="editobjfunction",method=RequestMethod.GET)
	public String getEditObjFunction(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	
		OptimizationSetDTO optSet = (OptimizationSetDTO) model.get("optimizationset");
		
		if (optSet == null)
		{
			optSet = new OptimizationSetDTO();
		}
		
		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}
		model.put("usersession", userSession);
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		ObjectiveFunctionDTO function = optSet.getObjectivefunction();
		model.put("function", function);
		
		return "editobjfunction";
	}

	// @author Markus Turunen
		// date: 26.6.2015 -3.7.2015
		// This method handles Optimizer cloning.
		// 
		 
		@RequestMapping(value="cloneoptimizer", method=RequestMethod.GET)
		public String CloneOptimizer(Map<String, Object> model, @RequestParam(value = "optimizerid") String optimizerid) {
		
		ProjectDTO project = (ProjectDTO) model.get("project");
		int noptimizerid = Integer.parseInt(optimizerid);		
		OptimizationSetDTO optimizer = null;
		try {
			optimizer = (OptimizationSetDTO) optSetService.findByID(noptimizerid);
		} catch (EntityNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
		Set<OpenOptimizationSetDTO> optSets=null;
		try {			
			optSets = projectService.getSearchAndGAOptimizationSets(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}			
			String name = optimizer.getName();			
			String clonename = name+"(copy)";				
				int i=0;
				while(optSetService.findByName(clonename)!=null){					
					i++;
					clonename=name+"(copy)("+i+")";				
				}			
				try {
					//clones
					OptimizationSetDTO cloneoptimisation = copyService.copyOptimizationSet(noptimizerid, clonename, true);
					cloneoptimisation=optSetService.save(cloneoptimisation);					
				
				} catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
		model.put("openoptimizationsets", optSets);		
		return "openoptimizationset";
						
		}
		
		
		
		
		//List<OptimizationSetDTO> list = optSetService.findByNameContaining(clonename);
			/*
			ProjectDTO project = (ProjectDTO) model.get("project");
			int nProjectId = Integer.parseInt(projectid);
			try {
				project = projectService.findByID(nProjectId);
			} catch (EntityNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Random number is to make clone crash temp. solution.
			//ToDo: Fix the issue:
			String clonename = project.getName()+"(clone)"+Math.random()*1000;
			try {
				ProjectDTO ProjectCloner = copyService.copyProject(nProjectId,clonename);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<ProjectDTO> projects = projectService.findAll();
			model.put("projects", projects);
			return "openproject";
					// ;(projectid, clonename, true, false, true, false);
			}
*/
	
	
	
	@RequestMapping(value="editobjfunction", method=RequestMethod.POST)
	public String getEditObjFunctionPost(ObjectiveFunctionDTO function, HttpServletRequest request, Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		OptimizationSetDTO optSet = null;
		
		if (model.containsKey("optimizationset"))
		{
			optSet = (OptimizationSetDTO) model.get("optimizationset");
			
			try {
				optSet = optSetService.findByID(optSet.getOptid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		else
		{
			return "error";
		}
		
		if (function != null && function.getExpression() != null)
		{
			ObjectiveFunctionDTO oldFunc = optSet.getObjectivefunction();
			
			oldFunc.setExpression(function.getExpression());
			oldFunc.setName(function.getName());
			oldFunc.setProject(project);
			
			try {
				oldFunc = objFuncService.update(oldFunc);
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			try {
				optSet = optSetService.findByID(optSet.getOptid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			optSet.setObjectivefunction(oldFunc);

			try {
				optSet = optSetService.save(optSet);
			} catch(ObjectOptimisticLockingFailureException e) {
				model.put("errorMessage", "This optimization set has been updated in the meantime, please reload.");
			}
		}

		model.put("optimizationset", optSet);

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		model.put("constraints", optSearchConstraints);

		List<MetricValDTO> listMetricVals = metricValService.findAll();
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		
		for (int i = 0; i < listMetricVals.size(); i++)
		{
			MetricValDTO metricVal = listMetricVals.get(i);
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);
		
		return "editoptimizationset";
	}
	
	@RequestMapping(value="deleteconstraint", method=RequestMethod.GET)
	public String getDeleteConstraint(Map<String, Object> model, @RequestParam(value="constraintid", required=true) String constraintId) {
		OptConstraintDTO constraint = null;
		int nConstraintId = Integer.parseInt(constraintId);
		
		try {
			constraint = optConstraintService.findByID(nConstraintId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		OptimizationSetDTO optSet = null;
				
		if (model.containsKey("optimizationset"))
		{
			optSet = (OptimizationSetDTO) model.get("optimizationset");
			model.put("optimizationset", optSet);
		}
		else
		{
			return "error";
		}

		try {
			optSetService.removeOptConstraint(optSet.getOptid(), nConstraintId);
			//optConstraintService.delete(constraint.getOptconstid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("constraints", optSearchConstraints);
		
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		model.put("project", project);

		List<MetricValDTO> listMetricVals = metricValService.findAll();
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		
		for (int i = 0; i < listMetricVals.size(); i++)
		{
			MetricValDTO metricVal = listMetricVals.get(i);
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);
		
		return "editoptimizationset";
	}

	@RequestMapping(value="createoptimizationset",method=RequestMethod.GET)
	public String getCreateOptimizationSet(Map<String, Object> model) {
	
		OpenOptimizationSetDTO openOptSet = new OpenOptimizationSetDTO();
		model.put("openoptimizationset", openOptSet);
		
		Set<String> optSetTypes = new HashSet<String>();
		optSetTypes.add("Database search");
		optSetTypes.add("Genetic algorithm");
		model.put("optimizationsettypes", optSetTypes);
		
		return "createoptimizationset";
	}

	@RequestMapping(value="createoptimizationset", method=RequestMethod.POST)
	public String getCreateOptimizationSetPost(Map<String, Object> model, HttpServletRequest request, OpenOptimizationSetDTO openOptSet) {
	
		String type = request.getParameter("type");
		int nType = Integer.parseInt(type);
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null || openOptSet == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		if (openOptSet != null)
		{
			if (nType == 1)
			{
				OptimizationSetDTO optSet = new OptimizationSetDTO();
				optSet.setName(openOptSet.getName());
				// Add description?
				optSet.setProject(project);
				
				//TODO clone project's defaultExtParamValSet?
				
				int nDefaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
				
				// TODO have to be fixed
				//ExtParamValSetDTO extParamValSet = extParamValSetService.findByID(nDefaultExtParamValSetId);
				//optSet.setExtparamvalset(extParamValSet);
				
				optSet = optSetService.save(optSet);
				model.put("optimizationset", optSet);
				
				List<MetricValDTO> listMetricVals = metricValService.findAll();
				List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
				
				for (int i = 0; i < listMetricVals.size(); i++)
				{
					MetricValDTO metricVal = listMetricVals.get(i);
					
					if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
					{
						listProjectMetricVals.add(metricVal);
					}
				}
				model.put("metricVals", listProjectMetricVals);
				
				return "editoptimizationset";
			}
			else if (nType == 2)
			{
				ScenarioGeneratorDTO scenGen = scenGenService.create(project.getPrjid(), openOptSet.getName());
				model.put("scengenerator", scenGen);
				
				return "redirect:/geneticalgorithm.html";
			}
			else
			{
				return "createoptimizationset";
			}
		}
		else
		{
			return "createoptimizationset";
		}
	}

	@RequestMapping(value="editoptimizationset",method=RequestMethod.GET)
	public String getEditOptimizationSet(Map<String, Object> model, 
		@RequestParam(value="optsetid", required=false) String optsetid,
		@RequestParam(value="optsettype", required=false) String optsettype) {

		OptimizationSetDTO optSet = null;
		
		if (model.containsKey("optimizationset"))
		{
			optSet = (OptimizationSetDTO) model.get("optimizationset");
			model.put("optimizationset", optSet);
		}
		else
		{
			return "error";
		}

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("constraints", optSearchConstraints);
		
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		SearchOptimizationResults optResults = (SearchOptimizationResults) model.get("optresults");
		
		if (optResults != null)
		{
			List<ScenarioWithObjFuncValueDTO> resultScenariosWithValue = (List<ScenarioWithObjFuncValueDTO>) optResults.resultScenarios;
			model.put("resultScenariosWithValue", resultScenariosWithValue);
		}
		
		List<MetricValDTO> listMetricVals = metricValService.findAll();
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		
		for (int i = 0; i < listMetricVals.size(); i++)
		{
			MetricValDTO metricVal = listMetricVals.get(i);
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);
		
		return "editoptimizationset";
	}
	
	@RequestMapping(value = "importoptimizationset", method = RequestMethod.POST)
	public String uploadCSVFileHandler(Map<String, Object> model, @RequestParam("file") MultipartFile file) {
	
		if (!file.isEmpty()) {
	        try {
	            ProjectDTO project = (ProjectDTO) model.get("project");
				
				if (project == null)
				{
					return "error";
				}
				
				try {
					project = projectService.findByID(project.getPrjid());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				model.put("project", project);
			
				//InputStream structureStream = file.getInputStream();
				//importExportService.importOptimisationProblem(projectId, name, problemFile, algorithmId, algorithmParameterFile, timeSeriesFiles)
				//importExportService.importOptimisationSet(projectId, userId, name, problemFile, timeSeriesFiles)
				
				List<MetricValDTO> listMetricVals = metricValService.findAll();
				List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
				
				for (int i = 0; i < listMetricVals.size(); i++)
				{
					MetricValDTO metricVal = listMetricVals.get(i);
					
					if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
					{
						listProjectMetricVals.add(metricVal);
					}
				}
				model.put("metricVals", listProjectMetricVals);
	        } catch (Exception e) {
	            return "You failed to upload => " + e.getMessage();
	        }
	    } else {
	    }
		
		return "editoptimizationset";
	}
	
	@RequestMapping(value="databaseoptimization", method=RequestMethod.GET)
	public String getDatabaseOptimization(Map<String, Object> model) {

		OptimizationSetDTO optSet = null;
		
		if (model.containsKey("optimizationset"))
		{
			optSet = (OptimizationSetDTO) model.get("optimizationset");

			try {
				optSet = optSetService.findByID(optSet.getOptid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("optimizationset", optSet);
		}
		else
		{
			return "error";
		}

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		if (optSet.getObjectivefunction() == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		model.put("project", project);
		
		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("constraints", optSearchConstraints);

		SearchOptimizationResults optResults = null;
		
		try {
			optResults = dbOptService.searchConstEval(project.getPrjid(), optSet.getOptid(), 5);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}
		
		if (optResults != null)
		{
			List<ScenarioWithObjFuncValueDTO> resultScenariosWithValue = (List<ScenarioWithObjFuncValueDTO>) optResults.resultScenarios;
			model.put("resultScenariosWithValue", resultScenariosWithValue);
			model.put("optresults", optResults);

			EvaluationResults evResults = optResults.getEvaluationResult();
			userSession.setOptResultString(evResults.toString());
		}
		
		model.put("usersession", userSession);

		List<MetricValDTO> listMetricVals = metricValService.findAll();
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		
		for (int i = 0; i < listMetricVals.size(); i++)
		{
			MetricValDTO metricVal = listMetricVals.get(i);
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);

		return "editoptimizationset";
	}
	
	@RequestMapping(value="openoptimizationset",method=RequestMethod.GET)
	public String getOpenOptimizationSet(Map<String, Object> model,
		@RequestParam(value="optsetid", required=false) String optsetid,
		@RequestParam(value="optsettype", required=false) String optsettype) {

		AppUserDTO user = (AppUserDTO) model.get("user");
		ProjectDTO project = (ProjectDTO) model.get("project");

		// TODO
		if (user != null && project != null)
		{
			//if (hasStandardRights(user.getUserid()))
			{
			
			}
		}

		if (optsettype != null)
		{
			if (optsettype.equals("db"))
			{
				OptimizationSetDTO optSet = null;
		
				if (optsetid != null)
				{
					int nOptSetId = Integer.parseInt(optsetid);
					
					try {
						optSet = optSetService.findByID(nOptSetId);
					} catch (NumberFormatException | EntityNotFoundException e) {
						e.printStackTrace();
					}
					model.put("optimizationset", optSet);
				}
				else if (model.containsKey("optimizationset"))
				{
					optSet = (OptimizationSetDTO) model.get("optimizationset");
					
					try {
						optSet = optSetService.findByID(optSet.getOptid());
					} catch (EntityNotFoundException e) {
						e.printStackTrace();
					}
					
					model.put("optimizationset", optSet);
				}
				else
				{
					return "error";
				}
				
				List<OptConstraintDTO> optSearchConstraints = null;
				
				try {
					optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				model.put("constraints", optSearchConstraints);
				
				project = (ProjectDTO) model.get("project");
		
				if (project == null)
				{
					return "error";
				}
				
				try {
					project = projectService.findByID(project.getPrjid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				List<MetricValDTO> listMetricVals = metricValService.findAll();
				List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
				
				for (int i = 0; i < listMetricVals.size(); i++)
				{
					MetricValDTO metricVal = listMetricVals.get(i);
					
					if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
					{
						listProjectMetricVals.add(metricVal);
					}
				}
				model.put("metricVals", listProjectMetricVals);
		
				return "editoptimizationset";
			}
			else
			{
				ScenarioGeneratorDTO scenGen = null;
				
				if (optsetid != null)
				{
					int nOptSetId = Integer.parseInt(optsetid);
					
					try {
						scenGen = (ScenarioGeneratorDTO) scenGenService.findByID(nOptSetId);
					} catch (EntityNotFoundException e) {
						e.printStackTrace();
					}
					
					model.put("scengenerator", scenGen);
				}
				else
				{
					return "error";
				}
				return "redirect:/geneticalgorithm.html";
			}
		}
		else
		{
			project = (ProjectDTO) model.get("project");
	
			if (project == null)
			{
				return "error";
			}
			
			try {
				project = projectService.findByID(project.getPrjid());
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			Set<OpenOptimizationSetDTO> optSets = null;
	
			try {
				optSets = projectService.getSearchAndGAOptimizationSets(project.getPrjid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("openoptimizationsets", optSets);
					
			return "openoptimizationset";
		}
	}

	@RequestMapping(value="deleteoptimizationset",method=RequestMethod.GET)
	public String getDeleteOptimizationSet(Map<String, Object> model,
		@RequestParam(value="optsetid", required=false) String optsetid,
		@RequestParam(value="optsettype", required=false) String optsettype) {

		if (optsettype != null)
		{
			if (optsettype.equals("db"))
			{
				if (optsetid != null)
				{
					int nOptSetId = Integer.parseInt(optsetid);
					
					try {
						optSetService.delete(nOptSetId);
					} catch (NumberFormatException | EntityNotFoundException e) {
						e.printStackTrace();
					} catch(ObjectOptimisticLockingFailureException e){
						model.put("errorMessage", "This optimization set has been updated in the meantime, please reload.");
					}
					
				}
				else
				{
					return "error";
				}
			}
			else
			{
				if (optsetid != null)
				{
					int nOptSetId = Integer.parseInt(optsetid);
					
					try {
						scenGenService.delete(nOptSetId);
					} catch (EntityNotFoundException e) {
						e.printStackTrace();
					} catch(ObjectOptimisticLockingFailureException e){
						model.put("errorMessage", "This optimization set has been updated in the meantime, please reload.");
					}
				}
				else
				{
					return "error";
				}
			}
		}

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		Set<OpenOptimizationSetDTO> optSets = null;

		try {
			optSets = projectService.getSearchAndGAOptimizationSets(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("openoptimizationsets", optSets);

		return "deleteoptimizationset";
	}
	
	@RequestMapping(value="createconstraint",method=RequestMethod.GET)
	public String getCreateConstraint(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
	
		OptConstraintDTO constraint = new OptConstraintDTO();
		model.put("constraint", constraint);

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		model.put("usersession", userSession);

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
				
				Set<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
				model.put("inputParams", inputParams);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		return "createconstraint";
	}

	@RequestMapping(value="createconstraint", method=RequestMethod.POST)
	public String getCreateConstraintPost(OptConstraintDTO constraint, Map<String, Object> model) throws EntityNotFoundException {
		OptimizationSetDTO optSet = null;
		
		if (model.containsKey("optimizationset"))
		{
			optSet = (OptimizationSetDTO) model.get("optimizationset");
			
			try {
				optSet = optSetService.findByID(optSet.getOptid());
			} catch (NumberFormatException | EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("optimizationset", optSet);
		}
		else
		{
			return "error";
		}
		
		if (constraint != null && constraint.getExpression() != null)
		{
			OptConstraintDTO newOptConstraint = new OptConstraintDTO();
			newOptConstraint.setName(constraint.getName());
			newOptConstraint.setExpression(constraint.getExpression());
			newOptConstraint.setLowerbound(constraint.getLowerbound());
			newOptConstraint.setUpperbound(constraint.getUpperbound());
			optConstraintService.save(newOptConstraint);
			
			try {
				optSetService.addOptConstraint(optSet.getOptid(), newOptConstraint);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			//optSet = optSetService.update(optSet);
		}

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("constraints", optSearchConstraints);

		return "editoptimizationset";
	}
	
	@RequestMapping(value="editconstraint",method=RequestMethod.GET)
	public String getEditConstraint(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="constraintid", required=false) String constraintId) {
	
		int nConstraintId = Integer.parseInt(constraintId);
		
		OptConstraintDTO constraint = null;
		
		try {
			constraint = optConstraintService.findByID(nConstraintId);
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
		model.put("constraint", constraint);

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}

		model.put("usersession", userSession);

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
				
				Set<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
				model.put("inputParams", inputParams);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		return "editconstraint";
	}
	
	@RequestMapping(value="editconstraint", method=RequestMethod.POST)
	public String getEditConstraintPost(OptConstraintDTO constraint, Map<String, Object> model) throws EntityNotFoundException {
		OptimizationSetDTO optSet = null;
		
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		if (model.containsKey("optimizationset"))
		{
			optSet = (OptimizationSetDTO) model.get("optimizationset");
			
			try {
				optSet = optSetService.findByID(optSet.getOptid());
			} catch (NumberFormatException | EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("optimizationset", optSet);
		}
		else
		{
			return "error";
		}
		
		if (constraint != null && constraint.getExpression() != null)
		{
			String lowerbound = constraint.getLowerbound();
			String upperbound = constraint.getUpperbound();
			String name = constraint.getName();
			String expression = constraint.getExpression();
			
			constraint = optConstraintService.findByID(constraint.getOptconstid());
			constraint.setLowerbound(lowerbound);
			constraint.setUpperbound(upperbound);
			constraint.setName(name);
			constraint.setExpression(expression);
					
			optConstraintService.update(constraint);
		}

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("constraints", optSearchConstraints);

		List<MetricValDTO> listMetricVals = metricValService.findAll();
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		
		for (int i = 0; i < listMetricVals.size(); i++)
		{
			MetricValDTO metricVal = listMetricVals.get(i);
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		model.put("metricVals", listProjectMetricVals);
		
		return "editoptimizationset";
	}
	
	@RequestMapping(value="importobjfunction",method=RequestMethod.GET)
	public String getImportObjFunction(Map<String, Object> model,
		@RequestParam(value="objectivefunctionid", required=false) String selectedObjFuncId) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		if (selectedObjFuncId != null && !selectedObjFuncId.isEmpty())
		{
			int nSelectedObjFuncId = Integer.parseInt(selectedObjFuncId);
			ObjectiveFunctionDTO objFunc = null;
			
			try {
				objFunc = objFuncService.findByID(nSelectedObjFuncId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			OptimizationSetDTO optSet = null;
			
			if (model.containsKey("optimizationset"))
			{
				optSet = (OptimizationSetDTO) model.get("optimizationset");
				
				try {
					optSet = optSetService.findByID(optSet.getOptid());
				} catch (NumberFormatException | EntityNotFoundException e) {
					e.printStackTrace();
				}
			}
			else
			{
				return "error";
			}
			
			if (optSet != null && objFunc != null)
			{
				optSet.setObjectivefunction(objFunc);

				try {
					optSet = optSetService.update(optSet);
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				model.put("optimizationset", optSet);
			}
			
			List<OptConstraintDTO> optSearchConstraints = null;
			
			try {
				optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("constraints", optSearchConstraints);
			
			List<MetricValDTO> listMetricVals = metricValService.findAll();
			List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
			
			for (int i = 0; i < listMetricVals.size(); i++)
			{
				MetricValDTO metricVal = listMetricVals.get(i);
				
				if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
				{
					listProjectMetricVals.add(metricVal);
				}
			}
			model.put("metricVals", listProjectMetricVals);
			
			return "editoptimizationset";
		}
		//TODO should use projectService.getObjectiveFunctions
		List<ObjectiveFunctionDTO> objFuncs = objFuncService.findAll();
		model.put("objFuncs", objFuncs);
		
		return "importobjfunction";
	}

	@RequestMapping(value="importsearchconstraint",method=RequestMethod.GET)
	public String getImportSearchConstraint(Map<String, Object> model,
		@RequestParam(value="constraintid", required=false) String selectedConstraintId) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		if (selectedConstraintId != null && !selectedConstraintId.isEmpty())
		{
			int nSelectedConstraintId = Integer.parseInt(selectedConstraintId);
			OptConstraintDTO constraint = null;
			
			try {
				constraint = optConstraintService.findByID(nSelectedConstraintId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			OptimizationSetDTO optSet = null;
			
			if (model.containsKey("optimizationset"))
			{
				optSet = (OptimizationSetDTO) model.get("optimizationset");
				
				try {
					optSet = optSetService.findByID(optSet.getOptid());
				} catch (NumberFormatException | EntityNotFoundException e) {
					e.printStackTrace();
				}
			}
			else
			{
				return "error";
			}
			
			if (optSet != null && constraint != null)
			{
				try {
					optSetService.addOptConstraint(optSet.getOptid(), constraint);
				} catch (EntityNotFoundException e1) {
					e1.printStackTrace();
				}

				try {
					optSet = optSetService.update(optSet);
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				model.put("optimizationset", optSet);
			}
			
			List<OptConstraintDTO> optSearchConstraints = null;
			
			try {
				optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("constraints", optSearchConstraints);
			
			List<MetricValDTO> listMetricVals = metricValService.findAll();
			List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
			
			for (int i = 0; i < listMetricVals.size(); i++)
			{
				MetricValDTO metricVal = listMetricVals.get(i);
				
				if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
				{
					listProjectMetricVals.add(metricVal);
				}
			}
			model.put("metricVals", listProjectMetricVals);
			
			return "editoptimizationset";
		}
		
		List<OptConstraintDTO> optSearchConstraints = optConstraintService.findAll();
		model.put("constraints", optSearchConstraints);
		
		return "importsearchconstraint";
	}

	@RequestMapping(value="showresults",method=RequestMethod.GET)
	public String getShowResults(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="scenarioid", required=false) String scenarioId) {
			
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			return "error";
		}

		SearchOptimizationResults optResults = (SearchOptimizationResults) model.get("optresults");
		model.put("optresults", optResults);

		int nResultScenarioId = Integer.parseInt(scenarioId);
		ScenarioDTO resultScenario = null;
		
		try {
			resultScenario = scenarioService.findByID(nResultScenarioId);
		} catch (EntityNotFoundException e) {
			System.out.println("Entity " + nResultScenarioId + " not found");
			e.printStackTrace();
		}
		
		model.put("scenario", resultScenario);

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			if (nSelectedCompId > 0)
			{
				userSession.setComponentId(nSelectedCompId);
				Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
				
				List<InputParamValDTO> listInputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, nResultScenarioId);
				model.put("inputParamVals", listInputParamVals);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		//metricService.getMetricVals(metricId, scenId)
		
		/*MetricDTO metric1 = metricService.findByID(metric1Id);
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
		}*/
		
		List<MetricValDTO> listMetricVals = metricValService.findAll();
		List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
		
		Iterator<MetricValDTO> metricValIter = listMetricVals.iterator();
		
		while (metricValIter.hasNext())
		{
			MetricValDTO metricVal = metricValIter.next();
			
			if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid()
				&& metricVal.getScenariometrics().getScenario().getScenid() == resultScenario.getScenid())
			{
				listProjectMetricVals.add(metricVal);
			}
		}
		
		model.put("metricVals", listProjectMetricVals);
		
		return "showresults";
	}
	
	@RequestMapping(value="runmultioptimizationset",method=RequestMethod.GET)
	public String getRunMultiOptimizationSet(Map<String, Object> model,
		@RequestParam(value="optsetid", required=false) String optsetid,
		@RequestParam(value="optsettype", required=false) String optsettype,
		@RequestParam(value="action", required=false) String action) {

		if (optsettype != null && action != null)
		{
			if (optsettype.equals("db"))
			{
				UserSession userSession = (UserSession) model.get("usersession");
				
				if (userSession == null)
				{
					userSession = new UserSession();
				}
				
				int nOptSetID = Integer.parseInt(optsetid);
				
				if (action.equals("add"))
				{				
					userSession.addSelectedOptSetId(nOptSetID);
				}
				else if (action.equals("remove"))
				{
					userSession.removeSelectedOptSetId(nOptSetID);
				}
				
				model.put("usersession", userSession);
			}
			else
			{
				UserSession userSession = (UserSession) model.get("usersession");
				
				if (userSession == null)
				{
					userSession = new UserSession();
				}

				int nScenGenId = Integer.parseInt(optsetid);
				
				if (action.equals("add"))
				{				
					userSession.addSelectedScenGenId(nScenGenId);
				}
				else if (action.equals("remove"))
				{
					userSession.removeSelectedScenGenId(nScenGenId);
				}
				
				model.put("usersession", userSession);

			}
		}

		ProjectDTO project = (ProjectDTO) model.get("project");
	
		if (project == null)
		{
			return "error";
		}
		
		Set<OpenOptimizationSetDTO> optSets = null;

		try {
			optSets = projectService.getSearchAndGAOptimizationSets(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("openoptimizationsets", optSets);
			
		return "runmultioptimizationset";
	}
	
	@RequestMapping(value="editsgobjfunction", method=RequestMethod.GET)
	public String getEditSGObjFunction(ModelMap model,
			@RequestParam(value="objid", required=false) Integer objid,
			@RequestParam(value="selectedcompid", required=false) Integer selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		ObjectiveFunctionDTO function = null;
		if (objid != null) {
			try {
				function = (ObjectiveFunctionDTO) objFuncService.findByID(objid);
				if (function.getProject().getPrjid() != project.getPrjid()) {
					return "error";
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "error";
			}
		} else {
			function = new ObjectiveFunctionDTO();
		}
		return getEditSGObjFunction(project, function, model, selectedCompId);
	}

	private String getEditSGObjFunction(
			ProjectDTO project, ObjectiveFunctionDTO function,
			ModelMap model, Integer selectedCompId) {
		UserSession userSession = getUserSession(model);

		model.put("function", function);

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		if (selectedCompId != null && selectedCompId > 0) {
			userSession.setComponentId(selectedCompId);
			model.put("selectedcompid", selectedCompId);
			model.put("outputVars", componentService.getOutputVariables(selectedCompId));
			model.put("inputParams", componentService.getInputParameters(selectedCompId));
		}
		model.put("metrics", projectService.getMetrics(project.getPrjid()));
		model.put("extParams", projectService.getExtParams(project.getPrjid()));

		return "editsgobjfunction";
	}

	@RequestMapping(value="editsgobjfunction", method=RequestMethod.POST)
	public String postEditSGObjFunction(
			ObjectiveFunctionDTO function, ModelMap model,
			@RequestParam("obtfunctionid") int objid,
			@RequestParam("optsense") String optSense) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		// TODO validate function expression, name uniqueness
		if (StringUtils.isBlank(function.getExpression())) {
			return getEditSGObjFunction(project, function, model, null);
		}
		function.setIsmaximise("max".equals(optSense));
		function.setProject(project);
		try {
			if (objid > 0) {
				// TODO: clone if referenced from elsewhere
				objFuncService.update(function);
			} else {
				scenGenService.addObjectiveFunction(scenGen.getScengenid(), function);
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}

		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="deletesgobjfunction", method=RequestMethod.POST)
	public String postDeleteSGObjFunction(
			ModelMap model, @RequestParam("objid") int objid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			// TODO: This only unlinks.  Should probably delete the actual ObjectiveFunction too, if it's not used elsewhere?
			scenGenService.removeObjectiveFunction(scenGen.getScengenid(), objid);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}

		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="addsgobjfunction", method=RequestMethod.GET)
	public String getAddSGObjFunction(ModelMap model) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			model.put("objFuncs", sortBy(ObjectiveFunctionDTO::getName,
					projectService.getObjectiveFunctions(project.getPrjid())));
			// TODO filter out functions that are already in scenGen
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}
		return "addsgobjfunction";
	}

	@RequestMapping(value="addsgobjfunction", method=RequestMethod.POST)
	public String postAddSGObjFunction(ModelMap model,
			@RequestParam("obtfunctionid") int obtfunctionid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			ObjectiveFunctionDTO objFunc = objFuncService.findByID(obtfunctionid);
			if (objFunc.getProject().getPrjid() != project.getPrjid()) {
				return "error";
			}
			scenGenService.addObjectiveFunction(scenGen.getScengenid(), objFunc);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}
		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="editsgconstraint", method=RequestMethod.GET)
	public String getEditSGConstraint(ModelMap model,
			@RequestParam(value="constrid", required=false) Integer constrid,
			@RequestParam(value="selectedcompid", required=false) Integer selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		OptConstraintDTO constraint = null;
		if (constrid != null) {
			try {
				constraint = (OptConstraintDTO) optConstraintService.findByID(constrid);
				if (constraint.getProject().getPrjid() != project.getPrjid()) {
					return "error";
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "error";
			}
		} else {
			constraint = new OptConstraintDTO();
		}
		return getEditSGConstraint(project, constraint, model, selectedCompId);
	}

	private String getEditSGConstraint(
			ProjectDTO project, OptConstraintDTO constraint,
			ModelMap model, Integer selectedCompId) {
		UserSession userSession = getUserSession(model);

		model.put("constraint", constraint);

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		if (selectedCompId != null && selectedCompId > 0) {
			userSession.setComponentId(selectedCompId);
			model.put("selectedcompid", selectedCompId);
			model.put("outputVars", componentService.getOutputVariables(selectedCompId));
			model.put("inputParams", componentService.getInputParameters(selectedCompId));
		}
		model.put("metrics", projectService.getMetrics(project.getPrjid()));
		model.put("extParams", projectService.getExtParams(project.getPrjid()));

		return "editsgconstraint";
	}

	@RequestMapping(value="editsgconstraint", method=RequestMethod.POST)
	public String postEditSGConstraint(
			OptConstraintDTO constraint, ModelMap model,
			@RequestParam("optconstid") int constrid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		if (StringUtils.isBlank(constraint.getLowerbound())) {
			constraint.setLowerbound(null);
		}
		if (StringUtils.isBlank(constraint.getUpperbound())) {
			constraint.setUpperbound(null);
		}
		// TODO validate expression & bounds syntax, check for name clashes
		if ((constraint.getLowerbound() == null && constraint.getUpperbound() == null)
				|| StringUtils.isBlank(constraint.getName())
				|| StringUtils.isBlank(constraint.getExpression())) {
			return getEditSGConstraint(project, constraint, model, null);
		}

		constraint.setProject(project);
		try {
			if (constrid > 0) {
				// TODO: clone if referenced from elsewhere
				optConstraintService.update(constraint);
			} else {
				scenGenService.addOptConstraint(scenGen.getScengenid(), constraint);
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}

		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="deletesgconstraint", method=RequestMethod.POST)
	public String postDeleteSGConstraint(
			ModelMap model, @RequestParam("constrid") Integer constrid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			// TODO: This only unlinks.  Should probably delete the actual ObjectiveFunction too, if it's not used elsewhere?
			scenGenService.removeOptConstraint(scenGen.getScengenid(), constrid);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}

		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="addsgconstraint", method=RequestMethod.GET)
	public String getAddSGConstraint(ModelMap model) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			model.put("constraints", sortBy(OptConstraintDTO::getName,
					projectService.getOptConstraints(project.getPrjid())));
			// TODO filter out constraints that are already in scenGen
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}
		return "addsgconstraint";
	}

	@RequestMapping(value="addsgconstraint", method=RequestMethod.POST)
	public String postAddSGConstraint(ModelMap model,
			@RequestParam("constrid") int constrid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			OptConstraintDTO constraint = optConstraintService.findByID(constrid);
			if (constraint.getProject().getPrjid() != project.getPrjid()) {
				return "error";
			}
			scenGenService.addOptConstraint(scenGen.getScengenid(), constraint);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}
		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="editsgdecisionvariable", method=RequestMethod.GET)
	public String getEditSGDecisionVariable(ModelMap model,
			@RequestParam(value="decisionvarid", required=false) Integer decisionvarid,
			@RequestParam(value="selectedcompid", required=false) Integer selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		DecisionVariableDTO decVar = null;
		if (decisionvarid != null) {
			try {
				decVar = decisionVarService.findByID(decisionvarid);
				if (decVar.getScenariogenerator().getScengenid() != scenGen.getScengenid()) {
					return "error";
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "error";
			}
		} else {
			decVar = new DecisionVariableDTO();
		}
		return getEditSGDecisionVariable(project, decVar, model, selectedCompId);
	}

	private String getEditSGDecisionVariable(ProjectDTO project,
			DecisionVariableDTO decVar, ModelMap model, Integer selectedCompId) {
		model.put("decVar", decVar);
		List<TypeDTO> typechoices = typeService.findAll().stream().filter(
				t -> (t.getName().equalsIgnoreCase("Integer") 
						|| t.getName().equalsIgnoreCase("Double")))
				.collect(Collectors.toList());
		model.put("typechoices", typechoices);
		return "editsgdecisionvariable";
	}

	@RequestMapping(value="editsgdecisionvariable", method=RequestMethod.POST)
	public String postEditSGDecisionVariable(
			DecisionVariableDTO decVar, ModelMap model,
			@RequestParam("decisionvarid") int decisionvarid,
			@RequestParam("typeid") int typeid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		if (StringUtils.isBlank(decVar.getLowerbound())) {
			decVar.setLowerbound(null);
		}
		if (StringUtils.isBlank(decVar.getUpperbound())) {
			decVar.setUpperbound(null);
		}
		if (StringUtils.isBlank(decVar.getName())) {
			return getEditSGDecisionVariable(project, decVar, model, null);
		}
		// TODO validate that the bounds are ordered, allowing for expression bounds
		try {
			decVar.setType(typeService.findByID(typeid));
			decVar.setScenariogenerator(scenGen);
			//TODO: implement tying to input parameter as an alternative to specifying a name
			decVar.setInputparameter(null);
			if (decisionvarid > 0) {
				decisionVarService.update(decVar);
			} else {
				scenGenService.addDecisionVariable(scenGen.getScengenid(), decVar);
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}
		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="deletesgdecisionvariable", method=RequestMethod.POST)
	public String postDeleteSGDecisionVariable(
			ModelMap model, @RequestParam("decisionvarid") Integer decisionvarid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			scenGenService.removeDecisionVariable(scenGen.getScengenid(), decisionvarid);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}

		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="geneticalgorithm", method=RequestMethod.GET)
	public String getGeneticAlgorithm(ModelMap model) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		return getGeneticAlgorithm(project, scenGen, model);
	}

	private String getGeneticAlgorithm(ProjectDTO project, ScenarioGeneratorDTO scenGen, Map<String, Object> model) {
		model.put("scengenerator", scenGen);
		UserSession userSession = (UserSession) model.get("usersession");
		if (userSession == null) {
			userSession = new UserSession();
			model.put("usersession", userSession);
		}
		try {
			List<ComponentDTO> components = sortComponentsByName(
					projectService.getComponents(project.getPrjid()));
			model.put("components", components);
			model.put("objFuncs", sortBy(ObjectiveFunctionDTO::getName,
					scenGenService.getObjectiveFunctions(scenGen.getScengenid())));
			model.put("constraints", sortBy(OptConstraintDTO::getName,
					scenGenService.getOptConstraints(scenGen.getScengenid())));
			List<DecisionVariableDTO> decVars = scenGenService.getDecisionVariables(scenGen.getScengenid());
			model.put("decVars", sortBy(DecisionVariableDTO::getName, decVars));
			Integer extParamValSetId = (scenGen.getExtparamvalset() == null) ? null
					: scenGen.getExtparamvalset().getExtparamvalsetid();
			model.put("extparamvals", (extParamValSetId == null) ? new ArrayList<ExtParamValDTO>()
					: sortBy(epv -> epv.getExtparam().getName(),
							extParamValSetService.getExtParamVals(extParamValSetId)));
			model.put("extparamvalsetid", extParamValSetId);
			List<ModelParameterDTO> modelParams = scenGenService.getModelParameters(scenGen.getScengenid());
			model.put("modelparams", sortBy(mp -> mp.getInputparameter().getQualifiedName(), modelParams));
			model.put("paramgrouping", new ModelParameterGrouping(modelParams, decVars));
			model.put("algorithms", algorithmService.findAll());
			model.put("algoparamvals", scenGenService.getOrCreateAlgoParamVals(scenGen.getScengenid()));

			List<ComponentDTO> inputComponents = pickInputComponents(modelParams, components);
			model.put("inputcomponents", inputComponents);
			if (userSession.getComponentId() == 0 && !inputComponents.isEmpty()) {
				userSession.setComponentId(inputComponents.iterator().next().getComponentid());
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "error";
		}

		return "geneticalgorithm";
	}

	private List<ComponentDTO> pickInputComponents(
			List<ModelParameterDTO> modelParams, List<ComponentDTO> components) {
		Set<Integer> componentIds = new HashSet<>();
		for (ModelParameterDTO mp : modelParams) {
			componentIds.add(mp.getInputparameter().getComponentComponentid());
		}
		return components.stream().filter(
				c -> componentIds.contains(c.getComponentid()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value="geneticalgorithm", method=RequestMethod.POST)
	public String postGeneticAlgorithm(ModelMap model,
			@ModelAttribute("scengenerator") ScenarioGeneratorDTO scenGenForm,
			@RequestParam("algorithmid") int algorithmId,
			@RequestParam(value="run", required=false) String run) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		if (StringUtils.isBlank(scenGenForm.getName())) {
			return getGeneticAlgorithm(project, scenGen, model);
		}
		try {
			scenGen = scenGenService.update(scenGen.getScengenid(), scenGenForm.getName(), algorithmId);
			model.put("scengenerator", scenGen);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return getGeneticAlgorithm(project, scenGen, model);
		}

		if ( ! StringUtils.isBlank(run)) {
			try {
				//TODO userid
				scenarioGenerationService.startOptimisation(scenGen.getScengenid(), null);
			} catch (ConfigurationException | ParseException | ScriptException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return getGeneticAlgorithm(project, scenGen, model);
			}
		}

		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="editextparamvalset", method=RequestMethod.GET)
	private String getEditExtParamValSet(ModelMap model,
			@RequestParam("extparamvalsetid") Integer extParamValSetId,
			@RequestParam("context") String context) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";

		if (extParamValSetId == null) {
			return "redirect:/" + context;
		}
		try {
			ExtParamValSetForm form = new ExtParamValSetForm();
			List<ExtParamValDTO> vals = sortBy(epv -> epv.getExtparam().getName(),
					extParamValSetService.getExtParamVals(extParamValSetId));
			form.setExtParamValSet(extParamValSetService.findByID(extParamValSetId));
			for (ExtParamValDTO val : vals) {
				int extParamId = val.getExtparam().getExtparamid();
				form.getValueByParamId().put(extParamId, val.getValue());
				form.getCommentByParamId().put(extParamId, val.getComment());
			}
			model.put("extparamvals", vals);
			model.put("extparamvalsetform", form);
			model.put("context", context);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "redirect:/" + context;
		}
		return "editextparamvalset";
	}

	@RequestMapping(value="editextparamvalset", method=RequestMethod.POST)
	public String postEditExtParamValSet(ModelMap model,
			ExtParamValSetForm form,
			@RequestParam("context") String context) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";

		try {
			int extParamValSetId = form.getExtParamValSet().getExtparamvalsetid();
			List<ExtParamValDTO> extParamVals = extParamValSetService.getExtParamVals(extParamValSetId);
			Map<Integer, TimeSeriesDTOX> timeSeriesByParamId = new HashMap<>();
			for (ExtParamValDTO epv : extParamVals) {
				int extParamId = epv.getExtparam().getExtparamid();
				// TODO validate values
				// TODO import time series as an alternative to scalar value
				epv.setValue(form.getValueByParamId().get(extParamId));
				epv.setComment(form.getCommentByParamId().get(extParamId));
			}
			extParamValSetService.updateOrClone(form.getExtParamValSet(), extParamVals, timeSeriesByParamId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		//TODO could support other sources...
		return "redirect:/" + context;
	}

	@RequestMapping(value="editsgalgoparamval", method=RequestMethod.GET)
	public String getEditSGAlgoParamVal(ModelMap model) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			List<AlgoParamValDTO> algoParamVals = scenGenService.getOrCreateAlgoParamVals(scenGen.getScengenid());
			model.put("algoparamvals", algoParamVals);

			AlgoParamValForm form = new AlgoParamValForm();
			for (AlgoParamValDTO apv : algoParamVals) {
				String editValue = (StringUtils.equals(apv.getValue(), apv.getAlgoparam().getDefaultvalue()))
						? "" : apv.getValue();
				form.getValueByParamId().put(apv.getAlgoparam().getAparamsid(), editValue);
			}
			model.put("algoparamvalform", form);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "redirect:/geneticalgorithm.html";
		}
		return "editsgalgoparamval";
	}

	@RequestMapping(value="editsgalgoparamval", method=RequestMethod.POST)
	public String postEditSGAlgoParamVal(ModelMap model,
			AlgoParamValForm form) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			List<AlgoParamValDTO> algoParamVals = scenGenService.getAlgoParamVals(scenGen.getScengenid());
			Map<Integer, AlgoParamValDTO> algoParamValMap = new HashMap<>();
			for (AlgoParamValDTO apv : algoParamVals) {
				algoParamValMap.put(apv.getAlgoparam().getAparamsid(), apv);
			}
			for (Map.Entry<Integer, String> entry : form.getValueByParamId().entrySet()) {
				if (StringUtils.isBlank(entry.getValue())) {
					AlgoParamValDTO apv = algoParamValMap.get(entry.getKey());
					if (apv != null) {
						entry.setValue(apv.getAlgoparam().getDefaultvalue());
					}
				}
				//TODO check if the values are numbers etc.
			}
			scenGenService.setAlgoParamVals(scenGen.getScengenid(), form.getValueByParamId());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return "redirect:/geneticalgorithm.html";
	}

	@RequestMapping(value="editsgmodelparams", method=RequestMethod.GET)
	public String getEditSGModelParams(ModelMap model) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		UserSession userSession = getUserSession(model);
		try {
			List<ComponentDTO> components = sortComponentsByName(
					projectService.getComponents(project.getPrjid()));
			List<DecisionVariableDTO> decVars = scenGenService.getDecisionVariables(scenGen.getScengenid());
			List<ModelParameterDTO> modelParams = sortBy(mp -> mp.getInputparameter().getQualifiedName(),
					scenGenService.getModelParameters(scenGen.getScengenid()));
			ModelParameterGrouping grouping = new ModelParameterGrouping(modelParams, decVars);

			ModelParamForm form = new ModelParamForm();
			for (ModelParameterDTO mp : modelParams) {
				int inputId = mp.getInputparameter().getInputid();
				ModelParameterGrouping.MultiValue multivalue = grouping.getMultiValued().get(inputId);
				String value = "";
				String group = "";
				if (multivalue != null) {
					value = multivalue.getValues();
					group = multivalue.getGroupName();
				} else if (grouping.getExpressionValued().contains(inputId)) {
					value = mp.getExpression();
				} else if (grouping.getDecisionValued().contains(inputId)) {
					value = mp.getExpression();
				} else {
					value = mp.getValue();
				}
				form.getValueByInputId().put(inputId, value);
				form.getGroupByInputId().put(inputId, group);
			}
			model.put("modelparamform", form);
			model.put("modelparams", modelParams);
			model.put("groups", sortBy(s -> s, grouping.getGroupsByName().keySet()));

			List<ComponentDTO> inputComponents = pickInputComponents(modelParams, components);
			model.put("inputcomponents", inputComponents);
			if (userSession.getComponentId() == 0 && !inputComponents.isEmpty()) {
				userSession.setComponentId(inputComponents.iterator().next().getComponentid());
			}

		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "redirect:/geneticalgorithm.html";
		}
		return "editsgmodelparams";
	}

	@RequestMapping(value="editsgmodelparams", method=RequestMethod.POST)
	public String postEditSGModelParams(ModelMap model,
			ModelParamForm form) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		if (project == null) return "redirect:/openproject.html";
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
		if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

		try {
			List<DecisionVariableDTO> decVars = scenGenService.getDecisionVariables(scenGen.getScengenid());
			List<ModelParameterDTO> modelParams = scenGenService.getModelParameters(scenGen.getScengenid());
			ModelParameterGrouping grouping = new ModelParameterGrouping(modelParams, decVars);
			Map<Integer, ModelParameterDTO> modelParamMap = new HashMap<>();
			for (ModelParameterDTO mp : modelParams) {
				modelParamMap.put(mp.getInputparameter().getInputid(), mp);
			}
			for (Map.Entry<Integer, String> entry : form.getValueByInputId().entrySet()) {
				ModelParameterDTO mp = modelParamMap.get(entry.getKey());
				String value = entry.getValue();
				String group = form.getGroupByInputId().get(entry.getKey());
				if (StringUtils.isBlank(group)) {
					String error = validateTypedValue(mp.getInputparameter().getType(), value);
					if (error == null) {
						mp.setValue(value);
						mp.setExpression(null);
					} else {
						mp.setValue(null);
						mp.setExpression(value);
					}
				} else {
					mp.setValue(null);
					mp.setExpression(grouping.makeMultiValueExpr(entry.getValue(), group));
				}
			}
			scenGenService.setModelParameters(scenGen.getScengenid(), modelParams);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return "redirect:/geneticalgorithm.html";
	}

	private String validateTypedValue(TypeDTO type, String value) {
		Type simType = Type.getByName((type != null) ? type.getName() : null);
		try {
			simType.parse(value, new EvaluationSetup(simulationService.getEvaluator(), Instant.EPOCH));
			return null;
		} catch (ParseException e) {
			// Assume it's an exception.  TODO: use SyntaxChecker to validate
			return e.getMessage();
		}
	}

	@RequestMapping(value="selectcomponent", method=RequestMethod.POST)
	public @ResponseBody String postSelectComponent(ModelMap model,
			@RequestParam("selectedcompid") int selectedCompId) {
		UserSession userSession = getUserSession(model);
		userSession.setComponentId(selectedCompId);
		return "";
	}

	private UserSession getUserSession(ModelMap model) {
		UserSession userSession = (UserSession) model.get("usersession");
		if (userSession == null) {
			userSession = new UserSession();
			model.put("usersession", userSession);
		}
		return userSession;
	}

	private static <T> List<T> sortBy(Function<T, String> key, Collection<T> collection) {
		List<T> list = new ArrayList<>(collection);
		list.sort(Comparator.comparing(key,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
		return list;
	}

	/// Sorts components by name, leaving the special CITYOPT component last.
	private List<ComponentDTO> sortComponentsByName(List<ComponentDTO> components) {
		List<ComponentDTO> list = new ArrayList<>(components);
		list.sort(Comparator.comparing(ComponentDTO::getName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
		for (Iterator<ComponentDTO> it = list.iterator(); it.hasNext();) {
			ComponentDTO comp = it.next();
			if (Namespace.CONFIG_COMPONENT.equals(comp.getName())) {
				it.remove();
				list.add(comp);
				break;
			}
		}
		return list;
	}
}