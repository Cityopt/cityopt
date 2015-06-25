package eu.cityopt.controller;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
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
import eu.cityopt.service.SearchOptimizationResults;
import eu.cityopt.service.SimulationResultService;
import eu.cityopt.service.TimeSeriesService;
import eu.cityopt.service.TimeSeriesValService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.UserSession;

/**
 * @author Olli Stenlund
 *
 */
@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession"})
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
			// TODO Auto-generated catch block
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

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		model.put("constraints", optSearchConstraints);

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

		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		return "editoptimizationset";
	}

	@RequestMapping(value="creategaobjfunction",method=RequestMethod.GET)
	public String getCreateGAObjFunction(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		ScenarioGeneratorDTO scenGenerator = (ScenarioGeneratorDTO) model.get("scengenerator");
		
		if (scenGenerator == null)
		{
			scenGenerator = new ScenarioGeneratorDTO();
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

		ObjectiveFunctionDTO function = new ObjectiveFunctionDTO();
		model.put("function", function);
		
		return "creategaobjfunction";
	}

	@RequestMapping(value="creategaobjfunction", method=RequestMethod.POST)
	public String getCreateGAObjFunctionPost(ObjectiveFunctionDTO function, Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ScenarioGeneratorDTO scenGenerator = null;
		
		if (model.containsKey("scengenerator"))
		{
			scenGenerator = (ScenarioGeneratorDTO) model.get("scengenerator");
		}
		else
		{
			return "error";
		}
		
		if (function != null && function.getExpression() != null)
		{
			ObjectiveFunctionDTO func = new ObjectiveFunctionDTO();
			func.setName(function.getName());
			func.setExpression(function.getExpression());
			func.setIsmaximise(function.getIsmaximise());
			func.setProject(project);
			
			//ScenGenObjectiveFunctionDTO scenGenFunc = new ScenGenObjectiveFunctionDTO();
			//scenGenFunc.setObjectivefunction(func);
			
			// Needed?
			//scenGenFuncService.save(scenGenFunc);
			
			try {
				scenGenService.addObjectiveFunction(scenGenerator.getScengenid(), func);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}

			scenGenerator = scenGenService.save(scenGenerator);
		}

		//TODO optSense

		model.put("scengenerator", scenGenerator);

                try {
			List<ObjectiveFunctionDTO> gaFuncs = scenGenService.getObjectiveFunctions(scenGenerator.getScengenid());
                        model.put("objFuncs",  gaFuncs);

                        List<OptConstraintDTO> gaConstraints = scenGenService.getOptConstraints(scenGenerator.getScengenid());
                        model.put("constraints", gaConstraints);

                        project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);
		
		//List<OptSearchConstDTO> optSearchConstraints = optSearchService.findAll();
		//model.put("constraints", optSearchConstraints);

		return "geneticalgorithm";
	}

	@RequestMapping(value="createdecisionvariable", method=RequestMethod.GET)
	public String getCreateDecisionVariables(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		ScenarioGeneratorDTO scenGenerator = (ScenarioGeneratorDTO) model.get("scengenerator");
		
		if (scenGenerator == null)
		{
			scenGenerator = new ScenarioGeneratorDTO();
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

		DecisionVariableDTO decVar = new DecisionVariableDTO();
		model.put("decVar", decVar);
		
		return "creategaobjfunction";
	}

	@RequestMapping(value="createdecisionvariable", method=RequestMethod.POST)
	public String getCreateDecisionVariablesPost(DecisionVariableDTO decVar, Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScenarioGeneratorDTO scenGenerator = null;
		
		if (model.containsKey("scengenerator"))
		{
			scenGenerator = (ScenarioGeneratorDTO) model.get("scengenerator");
		}
		else
		{
			return "error";
		}
		
		if (decVar != null && decVar.getExpression() != null)
		{
			DecisionVariableDTO newDecVar = new DecisionVariableDTO();
			newDecVar.setName(decVar.getName());
			newDecVar.setExpression(decVar.getExpression());
			newDecVar.setLowerbound(decVar.getLowerbound());
			newDecVar.setUpperbound(decVar.getUpperbound());
			newDecVar.setScenariogenerator(scenGenerator);
			
			decisionVarService.save(newDecVar);

			scenGenerator = scenGenService.save(scenGenerator);
		}

		model.put("scengenerator", scenGenerator);

		try {

			List<ObjectiveFunctionDTO> gaFuncs = scenGenService.getObjectiveFunctions(scenGenerator.getScengenid());
                        model.put("objFuncs",  gaFuncs);

                        List<OptConstraintDTO> gaConstraints = scenGenService.getOptConstraints(scenGenerator.getScengenid());
                        model.put("constraints", gaConstraints);

                        project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);
		
		return "geneticalgorithm";
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

	@RequestMapping(value="createoptimizationset",method=RequestMethod.POST)
	public String getCreateOptimizationSetPost(Map<String, Object> model, HttpServletRequest request, OpenOptimizationSetDTO openOptSet) {
	
		String type = request.getParameter("type");
		int nType = Integer.parseInt(type);
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
		
		if (openOptSet != null)
		{
			if (nType == 1)
			{
				OptimizationSetDTO optSet = new OptimizationSetDTO();
				optSet.setName(openOptSet.getName());
				// Add description?
				optSet.setProject(project);
				
				optSet = optSetService.save(optSet);
				model.put("optimizationset", optSet);
				return "editoptimizationset";
			}
			else if (nType == 2)
			{
				ScenarioGeneratorDTO scenGenerator = new ScenarioGeneratorDTO();
				scenGenerator.setName(openOptSet.getName());
				scenGenerator.setProject(project);
				scenGenerator = scenGenService.save(scenGenerator);
				model.put("scengenerator", scenGenerator);
				return "geneticalgorithm";
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

		SearchOptimizationResults optResults = (SearchOptimizationResults) model.get("optresults");
		model.put("optresults", optResults);
		
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
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

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

		model.put("optresults", optResults);
		
		UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null)
		{
			userSession = new UserSession();
		}
		
		List<ScenarioWithObjFuncValueDTO> resultScenariosWithValue = (List<ScenarioWithObjFuncValueDTO>) optResults.resultScenarios;
		model.put("resultScenariosWithValue", resultScenariosWithValue);
		
		EvaluationResults evResults = optResults.getEvaluationResult();
		userSession.setOptResultString(evResults.toString());
		
		model.put("usersession", userSession);

		return "editoptimizationset";
	}
	
	@RequestMapping(value="openoptimizationset",method=RequestMethod.GET)
	public String getOpenOptimizationSet(Map<String, Object> model,
		@RequestParam(value="optsetid", required=false) String optsetid,
		@RequestParam(value="optsettype", required=false) String optsettype) {

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
				Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
				model.put("metrics", metrics);
		
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
				//TODO model parameters, decision variables

				ProjectDTO project = (ProjectDTO) model.get("project");

				if (project == null)
				{
					return "error";
				}
				
				try {
		                        List<ObjectiveFunctionDTO> gaFuncs = scenGenService.getObjectiveFunctions(scenGen.getScengenid());
		                        model.put("objFuncs",  gaFuncs);

		                        List<OptConstraintDTO> gaConstraints = scenGenService.getOptConstraints(scenGen.getScengenid());
		                        model.put("constraints", gaConstraints);

		                        project = projectService.findByID(project.getPrjid());
				} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
				model.put("metrics", metrics);

				return "geneticalgorithm";
			}
		}
		else
		{
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

		return "editoptimizationset";
	}
	
	@RequestMapping(value="importobjfunction",method=RequestMethod.GET)
	public String getImportObjFunction(Map<String, Object> model,
		@RequestParam(value="objectivefunctionid", required=false) String selectedObjFuncId) {

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
			
			return "editoptimizationset";
		}
		List<ObjectiveFunctionDTO> objFuncs = objFuncService.findAll();
		model.put("objFuncs", objFuncs);
		
		return "importobjfunction";
	}

	@RequestMapping(value="importsearchconstraint",method=RequestMethod.GET)
	public String getImportSearchConstraint(Map<String, Object> model,
		@RequestParam(value="constraintid", required=false) String selectedConstraintId) {

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
				/*Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);*/
				
				List<InputParamValDTO> listInputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, nResultScenarioId);
				model.put("inputParamVals", listInputParamVals);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		//metricService.getMetricVals(metricId, scenId)
		
		//Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		//model.put("metrics", metrics);
			
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
	
	@RequestMapping(value="geneticalgorithm", method=RequestMethod.GET)
	public String getGeneticAlgorithm(Map<String, Object> model,
		@RequestParam(value="optsetid", required=false) String optsetid,
		@RequestParam(value="optsettype", required=false) String optsettype) {

		ScenarioGeneratorDTO scenGen = null;
		
		if (model.containsKey("scengenerator"))
		{
			scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
			model.put("scengenerator", scenGen);
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
		
		try {
			List<ObjectiveFunctionDTO> gaFuncs = scenGenService.getObjectiveFunctions(scenGen.getScengenid());
			model.put("objFuncs",  gaFuncs);

			List<OptConstraintDTO> gaConstraints = scenGenService.getOptConstraints(scenGen.getScengenid());
			model.put("constraints", gaConstraints);

                        project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		return "geneticalgorithm";
	}	
}
