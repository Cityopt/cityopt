package eu.cityopt.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
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
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.UserSession;

/**
 * @author Olli Stenlund
 *
 */
@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession"})
public class ScenarioController {
	
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
	
	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String getCreateScenario(Map<String, Object> model) {
		ScenarioDTO scenario = new ScenarioDTO();
		model.put("scenario", scenario);
		return "createscenario";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.POST)
	@Transactional
	public String getCreateScenarioPost(ScenarioDTO formScenario, Map<String, Object> model) {

		if (model.containsKey("project") && formScenario != null)
		{
			ProjectDTO project = (ProjectDTO) model.get("project");
			
			try {
				project = projectService.findByID(project.getPrjid());
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			
			
			model.put("project", project);
			
			ScenarioDTO scenario = new ScenarioDTO();
			
			
			//Fix #10457 By:Markus Turunen Checking no other entries.			
			List<ScenarioDTO> elements = scenarioService.findByName(formScenario.getName());
			if (elements.size()==0){			
				scenario.setName(formScenario.getName());
				scenario.setDescription(formScenario.getDescription());
				scenario.getScenid();
				
				try {
					scenario = scenarioService.saveWithDefaultInputValues(scenario, project.getPrjid());
				} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
				}
				
				
			}
			else
			{
				model.put("scenario", formScenario);
				model.put("errorMessage", "This Scenario allready exist, please create another name.");
				return "createscenario";
				
				
			}
			
			
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);

			// Create input param vals for all input params
//			for (int i = 0; i < components.size(); i++)
//			{
//				ComponentDTO component = components.get(i);
//				//List<ComponentInputParamDTO> listComponentInputParams = componentInputParamService.findAllByComponentId(component.getComponentid());
//				
//				Set<InputParameterDTO> setInputParams = componentService.getInputParameters(component.getComponentid());
//				Iterator<InputParameterDTO> iter = setInputParams.iterator();
//				
//				while(iter.hasNext())
//				{
//					InputParameterDTO inputParam = iter.next();
//					
//					//InputParameterDTO inputParam = inputParamService.findByID(setInputParams.get(j).getInputid());
//					InputParamValDTO inputParamVal = new InputParamValDTO();
//					inputParamVal.setInputparameter(inputParam);
//					inputParamVal.setValue(inputParam.getDefaultvalue());
//					inputParamVal.setScenario(scenario);
//					inputParamVal = inputParamValService.save(inputParamVal);
//				}
//			}
			
			Set<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			model.put("inputParamVals", inputParamVals);

//			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			InputParamValDTO simStart = inputParamValService.findByNameAndScenario("simulation_start", scenario.getScenid());
			InputParamValDTO simEnd = inputParamValService.findByNameAndScenario("simulation_start", scenario.getScenid());
			model.put("simStart", simStart.getValue());
			model.put("simEnd", simEnd.getValue());
//			// Get simulation start and end times
//			while(iter.hasNext())
//			{
//				InputParamValDTO inputParamVal = iter.next();
//				String inputName = inputParamVal.getInputparameter().getName();
//				
//				if (inputName.equals("simulation_start"))
//				{
//					model.put("simStart", inputParamVal.getValue());
//				}
//				else if (inputName.equals("simulation_end"))
//				{
//					model.put("simEnd", inputParamVal.getValue());
//				}
//			}

			UserSession userSession = (UserSession) model.get("usersession");
			userSession = new UserSession();
			model.put("usersession", userSession);

			model.put("scenario", scenario);
			return "editscenario";
		}
		else
		{
			//project null
			return "error";
		}
	}
	
	@RequestMapping(value="openscenario",method=RequestMethod.GET)
	public String getOpenScenario (Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid)
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "createproject";
		}
		
		if (scenarioid != null)
		{
			ScenarioDTO scenario = null;
			
			try {
				scenario = scenarioService.findByID(Integer.parseInt(scenarioid));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("scenario", scenario);
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);
			Set<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			model.put("inputParamVals", inputParamVals);
		
			UserSession userSession = (UserSession) model.get("usersession");
			userSession = new UserSession();
			model.put("usersession", userSession);
			
			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			
			// Get simulatino start and end times
			while(iter.hasNext())
			{
				InputParamValDTO inputParamVal = iter.next();
				String inputName = inputParamVal.getInputparameter().getName();
				
				if (inputName.equals("simulation_start"))
				{
					model.put("simStart", inputParamVal.getValue());
				}
				else if (inputName.equals("simulation_end"))
				{
					model.put("simEnd", inputParamVal.getValue());
				}
			}
			
			return "editscenario";
		}
		else
		{
			Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
			model.put("scenarios", scenarios);
		}

		return "openscenario";
	}

	@RequestMapping(value="showscenarios",method=RequestMethod.GET)
	public String getShowScenarios (Map<String, Object> model)
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);

		return "showscenarios";
	}
	
	@RequestMapping(value="clonescenario",method=RequestMethod.GET)
	public String getCloneScenario (Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid)
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		
		if (scenarioid != null)
		{
			ScenarioDTO scenario = null;
			int nScenarioId = Integer.parseInt(scenarioid);
			
			try {						
				//This function will keep created clone names intact.
				//author@: Markus Turunen
				//date: 24.6.2015
				//ToDO: test&improve
				//Implement cloneNamer								
				scenario = scenarioService.findByID(nScenarioId);					
				String name = scenario.getName();
				List<ScenarioDTO> list =scenarioService.findByName(name);	
				String clonename=null;
					for(int i=0;list.size()>i;i++){		
						 clonename= name+"("+i+")";			
					}
				ScenarioDTO cloneScenario = copyService.copyScenario(nScenarioId, clonename, true, false, true, false);
				
				/*
				scenario = scenarioService.findByID(nScenarioId);
				String clonename= scenario.getName()+"(copy)";
				List<ScenarioDTO> clonelist =scenarioService.findByName(clonename);
				if (clonelist.isEmpty()){				
				ScenarioDTO cloneScenario = copyService.copyScenario(nScenarioId, clonename, true, false, true, false);		
				}
				*/
				
				//original
				//ScenarioDTO cloneScenario = copyService.copyScenario(nScenarioId, scenario.getName() + " clone", true, false, true, false);
				
				
				scenarioService.save(cloneScenario, project.getPrjid());
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			} catch(ObjectOptimisticLockingFailureException e){
				model.put("errorMessage", "This scenario has been updated in the meantime, please reload.");
			}
		}
			
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);

		return "openscenario";
	}
	
	
	@RequestMapping(value="editscenario", method=RequestMethod.GET)
	public String getEditScenario (Map<String, Object> model) {
		if (!model.containsKey("project"))
		{
			return "error";
		}

		ProjectDTO project = (ProjectDTO) model.get("project");
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (scenario != null && scenario.getScenid() > 0)
		{
			try {
				scenario = scenarioService.findByID(scenario.getScenid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			Set<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			
			while(iter.hasNext())
			{
				InputParamValDTO inputParamVal = iter.next();
				String inputName = inputParamVal.getInputparameter().getName();
				
				if (inputName.equals("simulation_start"))
				{
					model.put("simStart", inputParamVal.getValue());
				}
				else if (inputName.equals("simulation_end"))
				{
					model.put("simEnd", inputParamVal.getValue());
				}
			}
			
			model.put("scenario", scenario);
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);
			model.put("inputParamVals", inputParamVals);
		
			return "editscenario";
		}
		else
		{
			scenario = new ScenarioDTO();
			model.put("scenario", scenario);
			return "createscenario";
		}
	}

	@RequestMapping(value="editscenario",method=RequestMethod.POST)
	public String getEditScenarioPost(ScenarioDTO formScenario, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {

		if (model.containsKey("project") && formScenario != null)
		{
			ProjectDTO project = (ProjectDTO) model.get("project");
			try {
				project = projectService.findByID(project.getPrjid());
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
			
			scenario.setName(formScenario.getName());
			scenario.setDescription(formScenario.getDescription());
			
			try {
				scenario = scenarioService.save(scenario, project.getPrjid());
			} catch(ObjectOptimisticLockingFailureException e) {
				model.put("errorMessage", "This scenario has been updated in the meantime, please reload.");
			}
			
			Set<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			
			while(iter.hasNext())
			{
				InputParamValDTO inputParamVal = iter.next();
				String inputName = inputParamVal.getInputparameter().getName();
				
				if (inputName.equals("simulation_start"))
				{
					model.put("simStart", inputParamVal.getValue());
				}
				else if (inputName.equals("simulation_end"))
				{
					model.put("simEnd", inputParamVal.getValue());
				}
			}
			
			model.put("scenario", scenario);
			
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);
			
			model.put("inputParamVals", inputParamVals);
		}
		else
		{
			//project null
			return "error";
		}
			
		return "editscenario";
	}
	
	@RequestMapping(value="setmultiscenario", method=RequestMethod.GET)
	public String getSetMultiScenario (Map<String, Object> model) {
		if (!model.containsKey("project"))
		{
			return "error";
		}

		ProjectDTO project = (ProjectDTO) model.get("project");
		
		/*try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (scenario != null && scenario.getScenid() > 0)
		{
			try {
				scenario = scenarioService.findByID(scenario.getScenid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			Set<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			
			while(iter.hasNext())
			{
				InputParamValDTO inputParamVal = iter.next();
				String inputName = inputParamVal.getInputparameter().getName();
				
				if (inputName.equals("simulation_start"))
				{
					model.put("simStart", inputParamVal.getValue());
				}
				else if (inputName.equals("simulation_end"))
				{
					model.put("simEnd", inputParamVal.getValue());
				}
			}
			
			model.put("scenario", scenario);
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);
			model.put("inputParamVals", inputParamVals);
		
			return "editscenario";
		}
		else
		{
			scenario = new ScenarioDTO();
			model.put("scenario", scenario);
			return "createscenario";
		}*/
		
		return "setmultiscenario";
	}
	
	@RequestMapping(value = "setsimulationdate", method = RequestMethod.POST)
	public String setSimulationDatePost(Map<String, Object> model, 
		@RequestParam(value="simstart", required=true) String simStart,
		@RequestParam(value="simend", required=true) String simEnd) {

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
		
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");

		if (scenario == null)
		{
			return "error";
		}
		
		try {
			scenario = scenarioService.findByID(scenario.getScenid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		Set<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
		Iterator<InputParamValDTO> iter = inputParamVals.iterator();
		
		while(iter.hasNext())
		{
			InputParamValDTO inputParamVal = iter.next();
			String inputName = inputParamVal.getInputparameter().getName();
			
			if (inputName.equals("simulation_start"))
			{
				inputParamVal.setValue(simStart);
				inputParamVal = inputParamValService.save(inputParamVal);
				
				model.put("simStart", inputParamVal.getValue());
			}
			else if (inputName.equals("simulation_end"))
			{
				inputParamVal.setValue(simEnd);
				inputParamVal = inputParamValService.save(inputParamVal);

				model.put("simEnd", inputParamVal.getValue());
			}
		}
		
		model.put("scenario", scenario);
		
		model.put("simStart", simStart);
		model.put("simEnd", simEnd);
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		model.put("inputParamVals", inputParamVals);
		
		return "editscenario";
	}
	
	@RequestMapping(value="deletescenario",method=RequestMethod.GET)
	public String getDeleteScenario(Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid){
		//List<Scenario> scenarios = scenarioService.findAllScenarios();
		//model.addAttribute("scenarios",scenarios);
	
		if (scenarioid != null)
		{
			ScenarioDTO tempScenario = null;
			
			try {
				tempScenario = scenarioService.findByID(Integer.parseInt(scenarioid));
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (tempScenario != null)
			{
				try {
					scenarioService.delete(tempScenario.getScenid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				} catch(ObjectOptimisticLockingFailureException e) {
					model.put("errorMessage", "This scenario has been updated in the meantime, please reload.");
				}
			}
		}

		List<ScenarioDTO> scenarios = scenarioService.findAll();
		model.put("scenarios", scenarios);

		return "deletescenario";
	}

	@RequestMapping(value="scenarioparameters", method=RequestMethod.GET)
	public String getScenarioParameters(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId){
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
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		ComponentDTO selectedComponent = null;
		int nSelectedCompId = 0;
		
		if (selectedCompId != null)
		{
			nSelectedCompId = Integer.parseInt(selectedCompId);
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			//model.put("selectedcompid", selectedCompId);
			model.put("selectedComponent",  selectedComponent);

			List<InputParamValDTO> inputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, scenario.getScenid());
			//List<ComponentInputParamDTO> componentInputParamVals = componentInputParamService.findAllByComponentId(nSelectedCompId);
			model.put("inputParamVals", inputParamVals);
		}

		model.put("project", project);

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
				
		return "scenarioparameters";
	}
	
	@RequestMapping(value="scenariovariables",method=RequestMethod.GET)
	public String getScenarioVariables(Map<String, Object> model,
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
		
		model.put("project", project);
		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		return "scenariovariables";
	}
		
	@RequestMapping(value="outputvariables",method=RequestMethod.GET)
	public String getOutputVariables(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		if (selectedCompId != null)
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			ComponentDTO selectedComponent = null;
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Hibernate.initialize(selectedComponent.getInputparameters());
			//model.put("inputParams", selectedComponent.getInputparameters());
			model.put("selectedComponent",  selectedComponent);
			model.put("selectedcompid", selectedCompId);
		}

		model.put("project", project);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "outputvariables";
	}
	
	@RequestMapping(value="runmultiscenario",method=RequestMethod.GET)
	public String getRunMultiScenario(Map<String, Object> model){
		ProjectDTO project = (ProjectDTO) model.get("project");
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (project == null)
		{
			return "error";
		}
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);
		
		return "runmultiscenario";
	}
	
	@RequestMapping(value="editinputparamvalue", method=RequestMethod.GET)
	public String getEditInputParameterValue(Map<String, Object> model, 
		@RequestParam(value="inputparamvalid", required=true) String inputvalid) {
		int nInputValId = Integer.parseInt(inputvalid);
		InputParamValDTO inputParamVal = null;
		
		try {
			inputParamVal = inputParamValService.findByID(nInputValId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("inputParamVal", inputParamVal);
		
		return "editinputparamvalue";
	}

	@RequestMapping(value="editinputparamvalue", method=RequestMethod.POST)
	public String getEditInputParamValPost(InputParamValDTO inputParamVal, Map<String, Object> model,
		@RequestParam(value="inputparamvalid", required=true) String inputParamValId){
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}
		
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
		
		int nInputParamValId = Integer.parseInt(inputParamValId);
		InputParamValDTO updatedInputParamVal = null;
		
		try {
			updatedInputParamVal = inputParamValService.findByID(nInputParamValId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		updatedInputParamVal.setValue(inputParamVal.getValue());
		updatedInputParamVal.setScenario(scenario);
		inputParamValService.save(updatedInputParamVal);
				
		int componentID =  inputParamService.getComponentId(updatedInputParamVal.getInputparameter().getInputid());
		model.put("selectedcompid", componentID);
		
		try {
			model.put("selectedComponent", componentService.findByID(componentID));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		List<InputParamValDTO> inputParamVals = inputParamValService.findByComponentAndScenario(componentID, scenario.getScenid());
		model.put("inputParamVals", inputParamVals);
		
		model.put("project", project);
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		return "scenarioparameters";
	}
		
	@RequestMapping(value="runscenario", method=RequestMethod.GET)
	public String getRunScenario(Map<String, Object> model)
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
		
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		String errorMsg = "";
		String statusMsg = "";
		
		if (scenario != null && scenario.getScenid() > 0)
		{
			try {
				simService.startSimulation(scenario.getScenid());
			} catch (ParseException e) {
				e.printStackTrace();
				errorMsg = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				errorMsg = e.getMessage();
			} catch (ConfigurationException e) {
				e.printStackTrace();
				errorMsg = e.getMessage();
			} catch (ScriptException e) {
				e.printStackTrace();
				errorMsg = e.getMessage();
			}
		}

		try {
			scenario = scenarioService.findByID(scenario.getScenid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		model.put("scenario", scenario);
		statusMsg = scenario.getStatus();
		model.put("status", statusMsg);
		
		model.put("error", errorMsg);
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);
		
		return "viewchart";
	}	
}
