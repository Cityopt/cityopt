package eu.cityopt.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ComponentInputParamDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptSearchConstDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.SimulationResultDTO;
import eu.cityopt.DTO.TimeSeriesDTO;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.model.Type;
import eu.cityopt.service.AppUserServiceImpl;
import eu.cityopt.service.AprosService;
import eu.cityopt.service.ComponentInputParamDTOServiceImpl;
import eu.cityopt.service.ComponentServiceImpl;
import eu.cityopt.service.CopyServiceImpl;
import eu.cityopt.service.DecisionVariableServiceImpl;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamServiceImpl;
import eu.cityopt.service.ExtParamValServiceImpl;
import eu.cityopt.service.InputParamValServiceImpl;
import eu.cityopt.service.InputParameterServiceImpl;
import eu.cityopt.service.MetricServiceImpl;
import eu.cityopt.service.MetricValServiceImpl;
import eu.cityopt.service.ObjectiveFunctionServiceImpl;
import eu.cityopt.service.OptSearchConstServiceImpl;
import eu.cityopt.service.OptSetScenariosImpl;
import eu.cityopt.service.OptimizationSetServiceImpl;
import eu.cityopt.service.OutputVariableServiceImpl;
import eu.cityopt.service.ProjectServiceImpl;
import eu.cityopt.service.ScenarioServiceImpl;
import eu.cityopt.service.SimulationResultServiceImpl;
import eu.cityopt.service.TimeSeriesServiceImpl;
import eu.cityopt.service.TypeServiceImpl;
import eu.cityopt.service.UnitServiceImpl;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.BarChartVisualization;
import eu.cityopt.web.ScatterPlotVisualization;
import eu.cityopt.web.TimeSeriesVisualization;
import eu.cityopt.web.UnitForm;
import eu.cityopt.web.UserSession;

@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "usersession"})
public class ProjectController {
	
	@Autowired
	ProjectServiceImpl projectService; 
	
	@Autowired
	ScenarioServiceImpl scenarioService;
	
	@Autowired
	AppUserServiceImpl userService;
	
	@Autowired
	ComponentServiceImpl componentService;

	@Autowired
	ComponentInputParamDTOServiceImpl componentInputParamService;
	
	@Autowired
	InputParameterServiceImpl inputParamService;

	@Autowired
	InputParamValServiceImpl inputParamValService;

	@Autowired
	ExtParamServiceImpl extParamService;

	@Autowired
	ExtParamValServiceImpl extParamValService;

	@Autowired
	MetricServiceImpl metricService;

	@Autowired
	MetricValServiceImpl metricValService;

	@Autowired
	UnitServiceImpl unitService;
	
	@Autowired
	SimulationService simService;

	@Autowired
	SimulationResultServiceImpl simResultService;

	@Autowired
	TimeSeriesServiceImpl timeSeriesService;

	@Autowired
	OutputVariableServiceImpl outputVarService;
	
	@Autowired
	TypeServiceImpl typeService;

	@Autowired
	CopyServiceImpl copyService;
	
	@Autowired
	OptimizationSetServiceImpl optSetService;
	
	@Autowired
	ObjectiveFunctionServiceImpl objFuncService;
	
	@Autowired
	OptSearchConstServiceImpl optSearchService;
	
	@Autowired
	OptSetScenariosImpl optSetScenarioService;
	
	@Autowired
	DecisionVariableServiceImpl decisionVarService;
	
	@RequestMapping(value="createproject", method=RequestMethod.GET)
	public String getCreateProject(Map<String, Object> model) {
		ProjectDTO newProject = new ProjectDTO();
		model.put("project", newProject);
		return "createproject";
	}

	@RequestMapping(value="createproject", method=RequestMethod.POST)
	public String getCreateProjectPost(Map<String, Object> model, ProjectDTO projectForm) {
		projectService.save(projectForm);
		model.put("project", projectForm);
		return "editproject";
	}

	@RequestMapping(value="openproject", method=RequestMethod.GET)
	public String getStringProjects(Map<String, Object> model)
	{
		List<ProjectDTO> projects = projectService.findAll();
		model.put("projects", projects);
	
		return "openproject";
	}	

	@RequestMapping(value="editproject", method=RequestMethod.GET)
	public String getEditProject(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid) {
		if (prjid != null)
		{
			ProjectDTO project = null;
			try {
				project = projectService.findByID(Integer.parseInt(prjid));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			model.put("project", project);

			//projectForm = new ProjectForm();
			//projectForm.setProjectName(project.getName());
			//projectForm.setProjectCreator("" + project.getCreatedby());
			//projectForm.setLocation(project.getLocation());
			//projectForm.setDate(project.getCreatedon().toString());
			//projectForm.setDescription(project.getName());
		}
		else if (!model.containsKey("project"))
		{
			ProjectDTO newProject = new ProjectDTO();
			model.put("project", newProject);
			return "createproject";
		}

		return "editproject";
	}

	@RequestMapping(value="editproject", method=RequestMethod.POST)
	public String getEditProjectPost(ProjectDTO projectForm, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {
	
		if (projectForm != null && action != null)
		{
			if (action.equals("create"))
			{
			}
			else if (action.equals("update"))
			{
			}
			
			/*if (true) //project.getAprosFileName() != null)
			{
				AprosService aprosService = new AprosService();
				String strFileName = "";//project.getAprosFileName();
				aprosService.readDiagramFile(strFileName);
			}*/
			
			ProjectDTO project = new ProjectDTO();
			project.setName(projectForm.getName());
			project.getPrjid();
			projectService.save(project);
			model.put("project", project);
		}
		return "editproject";
	}

	@RequestMapping(value="closeproject", method=RequestMethod.GET)
	public String getCloseProjects(Map<String, Object> model, HttpServletRequest request)
	{
		model.remove("project");
		request.getSession().removeAttribute("project");
		request.getSession().invalidate();
		return "start";
	}	

	@RequestMapping(value="index",method=RequestMethod.GET)
	public String getIndex(Model model) {
	
		return "index";
	}

	@RequestMapping(value="start",method=RequestMethod.GET)
	public String getStart(Model model){
	
		return "start";
	}

	@RequestMapping(value="deleteproject",method=RequestMethod.GET)
	public String getDeleteProject(Model model, @RequestParam(value="prjid", required=false) String prjid){
		if (prjid != null)
		{
			ProjectDTO tempProject = null;
			
			try {
				tempProject = projectService.findByID(Integer.parseInt(prjid));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try {
				projectService.delete(tempProject.getPrjid());
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<ProjectDTO> projects = projectService.findAll();
		model.addAttribute("projects",projects);

		return "deleteproject";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String getCreateScenario(Map<String, Object> model) {
		ScenarioDTO scenario = new ScenarioDTO();
		model.put("scenario", scenario);
		return "createscenario";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.POST)
	public String getCreateScenarioPost(ScenarioDTO formScenario, Map<String, Object> model) {

		if (model.containsKey("project") && formScenario != null)
		{
			ProjectDTO project = (ProjectDTO) model.get("project");
			model.put("project", project);
			ScenarioDTO scenario = new ScenarioDTO();
			scenario.setName(formScenario.getName());
			scenario.setDescription(formScenario.getDescription());
			scenario.getScenid();
			scenarioService.save(scenario, project.getPrjid());
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
		
			return "editscenario";
		}
		else
		{
			Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
			model.put("scenarios", scenarios);
		}

		return "openscenario";
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
				scenario = scenarioService.findByID(nScenarioId);

				ScenarioDTO cloneScenario = copyService.copyScenario(nScenarioId, scenario.getName() + " clone", true, false, true, false);
				scenarioService.save(cloneScenario, project.getPrjid());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
			
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);

		return "openscenario";
	}
	
	@RequestMapping(value="editscenario",method=RequestMethod.GET)
	public String getEditScenario (Map<String, Object> model) {
		if (!model.containsKey("project"))
		{
			return "error";
		}

		ProjectDTO project = (ProjectDTO) model.get("project");
		project = projectService.findByID(project.getPrjid());
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (scenario != null && scenario.getScenid() > 0)
		{
			model.put("scenario", scenario);
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);
			Set<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
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
			project = projectService.findByID(project.getPrjid());
			
			ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
			
			scenario.setName(formScenario.getName());
			scenario.setDescription(formScenario.getDescription());
			
			scenarioService.save(scenario, project.getPrjid());
			model.put("scenario", scenario);
		}
		else
		{
			//project null
			return "error";
		}
			
		return "editscenario";
	}
	
	@RequestMapping(value="deletescenario",method=RequestMethod.GET)
	public String getDeleteScenario(Model model, @RequestParam(value="scenarioid", required=false) String scenarioid){
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
				}
			}
		}

		List<ScenarioDTO> scenarios = scenarioService.findAll();
		model.addAttribute("scenarios", scenarios);

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

		project = projectService.findByID(project.getPrjid());
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
		}

		model.put("project", project);

		List<ComponentInputParamDTO> inputParamVals = componentInputParamService.findAllByPrjAndScenId(project.getPrjid(), scenario.getScenid());
		List<ComponentInputParamDTO> componentInputParamVals = new ArrayList<ComponentInputParamDTO>();
		
 		for (int i = 0; i < inputParamVals.size(); i++)
		{
			if (inputParamVals.get(i).getComponentid() == nSelectedCompId)
			{
				componentInputParamVals.add(inputParamVals.get(i));
			}
		}
		
		model.put("componentInputParamVals", componentInputParamVals);
		
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
		
		project = projectService.findByID(project.getPrjid());
		
		model.put("project", project);
		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		return "scenariovariables";
	}
	
	@RequestMapping(value="usermanagement", method=RequestMethod.GET)
	public String getUserManagement(Model model){
		List<AppUserDTO> users = userService.findAll();
		model.addAttribute("users", users);
	
		return "usermanagement";
	}

	@RequestMapping(value="units", method=RequestMethod.GET)
	public String getUnits(Model model){
		List<UnitDTO> units = unitService.findAll();
		model.addAttribute("units", units);
	
		return "units";
	}

	@RequestMapping(value="createunit", method=RequestMethod.GET)
	public String getCreateUnit(Model model) {

		UnitForm unitForm = new UnitForm();
		model.addAttribute("unitForm", unitForm);
		
		List<TypeDTO> types = typeService.findAll();
		List<String> typeStrings = new ArrayList<String>();
		
		for (int i = 0; i < types.size(); i++)
		{
			typeStrings.add(types.get(i).getName());
		}
		
		model.addAttribute("types", typeStrings);
		
		return "createunit";
	}

	@RequestMapping(value="createunit", method=RequestMethod.POST)
	public String getCreateUnitPost(UnitForm unitForm, Model model) {

		if (unitForm != null)
		{
			if (unitForm.getName() != null && unitForm.getType() != null && !unitForm.getName().isEmpty())
			{
				UnitDTO newUnit = new UnitDTO();
				newUnit.setName(unitForm.getName());
				List<TypeDTO> types = typeService.findAll();

				// Find the type
				for (int i = 0; i < types.size(); i++)
				{
					if (types.get(i).getName().equals(unitForm.getType()))
					{
						newUnit.setType(types.get(i));
						break;
					}
				}
				
				System.out.println("unit " + newUnit.getName() + " type " + newUnit.getType());
				unitService.save(newUnit);
			}
		}
		
		List<UnitDTO> units = unitService.findAll();
		model.addAttribute("units", units);
		
		return "units";
	}
	
	@RequestMapping(value="paramreliability", method=RequestMethod.GET)
	public String getParamReliability(Model model){
	
		return "paramreliability";
	}

	@RequestMapping(value="importdata", method=RequestMethod.GET)
	public String getImportData(Model model){
	
		return "importdata";
	}
	
	@RequestMapping(value="createuser",method=RequestMethod.GET)
	public String getCreateUser(Map<String, Object> model) {
		UserForm userForm = new UserForm();
		model.put("userForm", userForm);
	
		return "createuser";
	}

	@RequestMapping(value="createuser", method=RequestMethod.POST)
	public String getCreateUserPost(UserForm userForm, Map<String, Object> model) {
		if (userForm.getName() != null)
		{
			AppUserDTO user = new AppUserDTO();
			user.setName(userForm.getName());
			user.getUserid();
			userService.save(user);
		}

		List<AppUserDTO> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
	}

	@RequestMapping(value="edituser",method=RequestMethod.GET)
	public String getEditUser(Model model, @RequestParam(value="userid", required=true) String userid) {
		int nUserId = Integer.parseInt(userid);
		
		AppUserDTO user = null;
		try {
			user = userService.findByID(nUserId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		UserForm userForm = new UserForm();
		userForm.setName(user.getName());
		model.addAttribute("userForm", userForm);

		return "edituser";
	}

	@RequestMapping(value="edituser", method=RequestMethod.POST)
	public String getEditUserPost(UserForm userForm, Map<String, Object> model,
		@RequestParam(value="userid", required=true) String userId) {

		AppUserDTO user = null;
		try {
			user = (AppUserDTO) userService.findByID(Integer.parseInt(userId));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		if (userForm.getName() != null)
		{
			user.setName(userForm.getName());
			userService.save(user);
		}

		List<AppUserDTO> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
	}
	
	@RequestMapping(value="deleteuser", method=RequestMethod.GET)
	public String getDeleteUser(Model model, @RequestParam(value="userid") String userid) {
		int nUserId = Integer.parseInt(userid);
		
		if (nUserId >= 0)
		{
			AppUserDTO user = null;
			try {
				user = userService.findByID(nUserId);
			} catch (EntityNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				userService.delete(user.getUserid());
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<AppUserDTO> users = userService.findAll();
		model.addAttribute("users", users);

		return "usermanagement";
	}
	
	@RequestMapping(value="coordinates",method=RequestMethod.GET)
	public String getCoordinates(Map<String, Object> model){
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "coordinates";
	}
	
	@RequestMapping(value="createobjfunction",method=RequestMethod.GET)
	public String getCreateObjFunction(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
	
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
	public String getCreateObjFunctionPost(ObjectiveFunctionDTO function, Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
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
			ObjectiveFunctionDTO newFunc = new ObjectiveFunctionDTO();
			newFunc.setName(function.getName());
			newFunc.setExpression(function.getExpression());
			newFunc.setProject(project);
			newFunc = objFuncService.save(newFunc);
			optSet.setObjectivefunction(newFunc);
			optSet = optSetService.save(optSet);
		}

		model.put("optimizationset", optSet);

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getSearchConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		model.put("constraints", optSearchConstraints);

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
		
		project = projectService.findByID(project.getPrjid());
	
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
		
		if (function != null && function.getExpression() != null)
		{
			ObjectiveFunctionDTO newFunc = new ObjectiveFunctionDTO();
			newFunc.setName(function.getName());
			newFunc.setExpression(function.getExpression());
			optSet.setObjectivefunction(newFunc);
			optSetService.save(optSet);
		}

		//List<OptSearchConstDTO> optSearchConstraints = optSearchService.findAll();
		//model.put("constraints", optSearchConstraints);

		return "geneticalgorithm";
	}
	
	@RequestMapping(value="createoptimizationset",method=RequestMethod.GET)
	public String getCreateOptimizationSet(Map<String, Object> model) {
	
		OptimizationSetDTO optSet = new OptimizationSetDTO();
		model.put("optimizationset", optSet);
		
		return "createoptimizationset";
	}

	@RequestMapping(value="createoptimizationset",method=RequestMethod.POST)
	public String getCreateOptimizationSetPost(Map<String, Object> model, OptimizationSetDTO optSet) {
	
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		if (optSet != null)
		{
			optSet.setProject(project);
			
			optSetService.save(optSet);
		}
		
		model.put("optimizationset", optSet);
		
		return "editoptimizationset";
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
			optSearchConstraints = optSetService.getSearchConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("constraints", optSearchConstraints);
		
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

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
					optSearchConstraints = optSetService.getSearchConstraints(optSet.getOptid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				model.put("constraints", optSearchConstraints);
				
				ProjectDTO project = (ProjectDTO) model.get("project");
		
				if (project == null)
				{
					return "error";
				}
				
				project = projectService.findByID(project.getPrjid());
				Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
				model.put("metrics", metrics);
		
				return "editoptimizationset";
			}
			else
			{
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
			
			project = projectService.findByID(project.getPrjid());
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
	public String getDeleteOptimizationSet(Model model){
	
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
		
		project = projectService.findByID(project.getPrjid());

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
			
			try {
				optSetService.addSearchConstraint(optSet.getOptid(), newOptConstraint);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			optSet = optSetService.save(optSet);
		}

		List<OptConstraintDTO> optSearchConstraints = null;
		
		try {
			optSearchConstraints = optSetService.getSearchConstraints(optSet.getOptid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("constraints", optSearchConstraints);

		return "editoptimizationset";
	}
	
	@RequestMapping(value="importobjfunction",method=RequestMethod.GET)
	public String getImportObjFunction(Model model) {
	
		return "importobjfunction";
	}

	@RequestMapping(value="importsearchconstraint",method=RequestMethod.GET)
	public String getImportSearchConstraint(Model model) {
	
		return "importsearchconstraint";
	}

	@RequestMapping(value="extparamsets",method=RequestMethod.GET)
	public String getExtParamSets(Model model) {
	
		return "extparamsets";
	}
	
	@RequestMapping(value="showresults",method=RequestMethod.GET)
	public String getShowResults(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
			
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());

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
				Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
				model.put("outputVars", outputVars);
				
				Set<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
				model.put("inputParams", inputParams);
			}
			model.put("selectedcompid", nSelectedCompId);
		}
		
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);
			
		return "showresults";
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
		project = projectService.findByID(project.getPrjid());
		
		if (project == null)
		{
			return "error";
		}
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);
		
		return "runmultiscenario";
	}
	
	@RequestMapping(value="runmultioptimizationset",method=RequestMethod.GET)
	public String getRunMultiOptimizationSet(Model model){
	
		return "runmultioptimizationset";
	}

	@RequestMapping(value="projectparameters", method=RequestMethod.GET)
	public String getProjectParameters(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId){
		ProjectDTO project = (ProjectDTO) model.get("project");
		project = projectService.findByID(project.getPrjid());
		
		if (project == null)
		{
			return "error";
		}
		
		//Hibernate.initialize(project.getComponents());
		//Set<Component> projectComponents = project.getComponents();
		
		/*if (projectComponents != null && projectComponents.size() > 0)
		{
			model.put("components", projectComponents);
		}*/
	
		/*Set<Component> setComponents = project.getComponents();
		List<Component> listComponents = new ArrayList<Component>();
        Iterator<Component> iterator = setComponents.iterator();
        
        while(iterator.hasNext())
        {
               Component cmp = iterator.next();
               listComponents.add(cmp);
               cmp.getName();
        }

		if (listComponents != null && listComponents.size() > 0)
		{
			model.put("components", listComponents);
		}*/

		ComponentDTO selectedComponent = null;
		
		// Select the first component if no component is selected
		/*if (selectedCompId == null && projectComponents != null && projectComponents.size() > 0)
		{
			selectedComponent = projectComponents.iterator().next();
			model.put("selectedCompId", selectedComponent.getComponentid());
			//Hibernate.initialize(selectedComponent.getInputparameters());
			//model.put("inputParams", selectedComponent.getInputparameters());
			model.put("selectedComponent",  selectedComponent);
		}*/
		
		if (selectedCompId != null)
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("selectedcompid", selectedCompId);
			//Hibernate.initialize(selectedComponent.getInputparameters());
			//model.put("inputParams", selectedComponent.getInputparameters());
			model.put("selectedComponent",  selectedComponent);
			Set<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
			model.put("inputParameters", inputParams);
		}

		model.put("project", project);
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectparameters";
	}
	
	@RequestMapping(value="createcomponent", method=RequestMethod.GET)
	public String getCreateComponent(Model model){

		ComponentDTO newComponent = new ComponentDTO();
		model.addAttribute("component", newComponent);
		
		return "createcomponent";
	}

	@RequestMapping(value="createcomponent", method=RequestMethod.POST)
	public String getCreateComponentPost(ComponentDTO componentForm, Map<String, Object> model){
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		ComponentDTO component = new ComponentDTO();
		component.setName(componentForm.getName());
		componentService.save(component, project.getPrjid());
		
		model.put("project", projectService.findByID(project.getPrjid()));
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectparameters";
	}

	@RequestMapping(value="editcomponent", method=RequestMethod.GET)
	public String getEditComponent(Model model, @RequestParam(value="componentid", required=true) String componentid) {
		int nCompId = Integer.parseInt(componentid);
		ComponentDTO component = null;
		try {
			component = componentService.findByID(nCompId);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("component", component);
		
		return "editcomponent";
	}

	@RequestMapping(value="editcomponent", method=RequestMethod.POST)
	public String getEditComponentPost(ComponentDTO component, Map<String, Object> model,
		@RequestParam(value="componentid", required=true) String componentid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		int nCompId = Integer.parseInt(componentid);
		ComponentDTO oldComponent = null;
		try {
			oldComponent = componentService.findByID(nCompId);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oldComponent.setName(component.getName());
		
		componentService.save(oldComponent, project.getPrjid());
		model.put("selectedcompid", oldComponent.getComponentid());
		model.put("selectedComponent",  oldComponent);

		model.put("project", projectService.findByID(project.getPrjid()));
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectparameters";
	}

	@RequestMapping(value="editinputparameter", method=RequestMethod.GET)
	public String getEditInputParameter(Model model, @RequestParam(value="inputparameterid", required=true) String inputid) {
		int nInputId = Integer.parseInt(inputid);
		InputParameterDTO inputParam = null;
		try {
			inputParam = inputParamService.findByID(nInputId);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("inputParam", inputParam);
		
		return "editinputparameter";
	}

	@RequestMapping(value="editinputparameter", method=RequestMethod.POST)
	public String getEditInputParameterPost(InputParameterDTO inputParam, Map<String, Object> model,
		@RequestParam(value="inputparamid", required=true) String inputParamId){
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		int nInputParamId = Integer.parseInt(inputParamId);
		InputParameterDTO updatedInputParam = null;
		try {
			updatedInputParam = inputParamService.findByID(nInputParamId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		updatedInputParam.setName(inputParam.getName());
		updatedInputParam.setDefaultvalue(inputParam.getDefaultvalue());
		UnitDTO unit = unitService.save(new UnitDTO());
		inputParamService.save(updatedInputParam, updatedInputParam.getComponent().getComponentid(), unit.getUnitid());
				
		model.put("selectedcompid", updatedInputParam.getComponent().getComponentid());
		try {
			model.put("selectedComponent", inputParamService.findByID(updatedInputParam.getComponent().getComponentid()));
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.put("project", project);
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		return "projectparameters";
	}

	@RequestMapping(value="editinputparametervalue", method=RequestMethod.GET)
	public String getEditInputParameterValue(Model model, @RequestParam(value="inputparamvalid", required=true) String inputvalid) {
		int nInputValId = Integer.parseInt(inputvalid);
		InputParamValDTO inputParamVal = null;
		
		try {
			inputParamVal = inputParamValService.findByID(nInputValId);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.addAttribute("inputParamVal", inputParamVal);
		
		return "editinputparametervalue";
	}

	@RequestMapping(value="editinputparametervalue", method=RequestMethod.POST)
	public String getEditInputParamValPost(InputParamValDTO inputParamVal, Map<String, Object> model,
		@RequestParam(value="inputparamvalid", required=true) String inputParamValId){
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		int nInputParamValId = Integer.parseInt(inputParamValId);
		InputParamValDTO updatedInputParamVal = null;
		
		try {
			updatedInputParamVal = inputParamValService.findByID(nInputParamValId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		updatedInputParamVal.setValue(inputParamVal.getValue());
		UnitDTO unit = unitService.save(new UnitDTO());
		inputParamValService.save(updatedInputParamVal);
				
		model.put("selectedcompid", updatedInputParamVal.getInputparameter().getComponent().getComponentid());
		
		try {
			model.put("selectedComponent", inputParamService.findByID(updatedInputParamVal.getInputparameter().getComponent().getComponentid()));
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.put("project", project);
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		return "scenarioparameters";
	}

	@RequestMapping(value="createinputparameter", method=RequestMethod.GET)
	public String getCreateInputParameter(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=true) String strSelectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		if (strSelectedCompId == null || strSelectedCompId.isEmpty())
		{
			model.put("project", project);
			return "projectparameters";
		}
		
		int nSelectedCompId = Integer.parseInt(strSelectedCompId);
		ComponentDTO component = null;
		try {
			component = componentService.findByID(nSelectedCompId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		InputParameterDTO newInputParameter = new InputParameterDTO();
		UnitDTO unit = unitService.save(new UnitDTO());
		newInputParameter.setUnit(unit);
		newInputParameter.setComponent(component);
		model.put("inputParam", newInputParameter);
		model.put("selectedcompid", nSelectedCompId);
		
		return "createinputparameter";
	}

	@RequestMapping(value="createinputparameter", method=RequestMethod.POST)
	public String getCreateInputParamPost(InputParameterDTO inputParamForm, Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=true) String strSelectedCompId) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		int nSelectedCompId = Integer.parseInt(strSelectedCompId);
		ComponentDTO component = null;
		try {
			component = componentService.findByID(nSelectedCompId);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputParameterDTO inputParam = new InputParameterDTO();
		inputParam.setName(inputParamForm.getName());
		inputParam.setDefaultvalue(inputParamForm.getDefaultvalue());
		UnitDTO unit = unitService.save(new UnitDTO());
		inputParamService.save(inputParam, component.getComponentid(), unit.getUnitid());
				
		model.put("selectedcompid", nSelectedCompId);
		model.put("selectedComponent",  component);
	
		model.put("project", project);
	
		return "projectparameters";
	}

	@RequestMapping(value="createextparam", method=RequestMethod.GET)
	public String getCreateExtParam(Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
		model.put("project", project);
		
		ExtParamDTO extParam = new ExtParamDTO();
		model.put("extParam", extParam);
		
		return "createextparam";
	}

	@RequestMapping(value="createextparam", method=RequestMethod.POST)
	public String getCreateExtParamPost(ExtParamDTO extParam, Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		project = projectService.findByID(project.getPrjid());
		
		ExtParamDTO newExtParam = new ExtParamDTO();
		newExtParam.setName(extParam.getName());
		newExtParam.setDefaultvalue(extParam.getDefaultvalue());
		
		extParamService.save(newExtParam, project.getPrjid());

		model.put("project", project);
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectparameters";
	}
	
	@RequestMapping(value="editextparam", method=RequestMethod.GET)
	public String getEditExtParam(Map<String, Object> model,
		@RequestParam(value="extparamid", required=true) String extparamid) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
		model.put("project", project);

		int nExtParamId = Integer.parseInt(extparamid);
		ExtParamDTO extParam = null;
		try {
			extParam = extParamService.findByID(nExtParamId);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model.put("extParam", extParam);

		return "editextparam";
	}

	@RequestMapping(value="editextparam", method=RequestMethod.POST)
	public String getEditExtParamPost(ExtParamDTO extParam, Map<String, Object> model,
		@RequestParam(value="extparamid", required=true) String extParamId){
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		project = projectService.findByID(project.getPrjid());
		
		int nExtParamId = Integer.parseInt(extParamId);
		ExtParamDTO updatedExtParam = null;
		try {
			updatedExtParam = extParamService.findByID(nExtParamId);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updatedExtParam.setName(extParam.getName());
		updatedExtParam.setDefaultvalue(extParam.getDefaultvalue());
		extParamService.save(updatedExtParam, project.getPrjid());

		model.put("project", project);

		return "editproject";
	}

	@RequestMapping(value="editextparamvalue", method=RequestMethod.GET)
	public String getEditExtParamVal(Map<String, Object> model,
		@RequestParam(value="extparamvalid", required=true) String extparamvalid) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
		model.put("project", project);

		int nExtParamValId = Integer.parseInt(extparamvalid);
		ExtParamValDTO extParamVal = null;

		try {
			extParamVal = extParamValService.findByID(nExtParamValId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("extParamVal", extParamVal);

		return "editextparamvalue";
	}

	@RequestMapping(value="editextparamvalue", method=RequestMethod.POST)
	public String getEditExtParamValPost(ExtParamValDTO extParamVal, Map<String, Object> model,
		@RequestParam(value="extparamvalid", required=true) String extParamValId){
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		project = projectService.findByID(project.getPrjid());
		
		int nExtParamValId = Integer.parseInt(extParamValId);
		ExtParamValDTO updatedExtParamVal = null;

		try {
			updatedExtParamVal = extParamValService.findByID(nExtParamValId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		updatedExtParamVal.setValue(extParamVal.getValue());
		extParamValService.save(updatedExtParamVal);

		model.put("project", project);

		return "editproject";
	}

	@RequestMapping(value="metricdefinition",method=RequestMethod.GET)
	public String getMetricDefinition(Map<String, Object> model,
		@RequestParam(value="metricid", required=false) String metricid,
		@RequestParam(value="action", required=false) String action) {
		
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		project = projectService.findByID(project.getPrjid());
		
		if (action != null && metricid != null)
		{
			int nMetricId = Integer.parseInt(metricid);
			
			if (action.equals("clone")) {
				MetricDTO metric = null;
				try {
					metric = metricService.findByID(nMetricId);
				} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MetricDTO cloneMetric = new MetricDTO();
				cloneMetric.setName(metric.getName() + "_new");
				cloneMetric.setExpression(metric.getExpression());
				cloneMetric.setProject(project);
				cloneMetric = metricService.save(cloneMetric);
			}
			else if (action.equals("delete")) {
				MetricDTO metric = null;
				try {
					metric = metricService.findByID(nMetricId);
				} catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					metricService.delete(metric.getMetid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
					return "error";
				}
			}
		}
		
		project = projectService.findByID(project.getPrjid());
		model.put("project", project);
	
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		return "metricdefinition";
	}

	@RequestMapping(value="createmetric", method=RequestMethod.GET)
	public String getCreateMetric(Model model) {

		MetricDTO newMetric = new MetricDTO();
		model.addAttribute("metric", newMetric);
		
		return "createmetric";
	}

	@RequestMapping(value="createmetric", method=RequestMethod.POST)
	public String getCreateMetricPost(MetricDTO metricForm, Map<String, Object> model) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		project = projectService.findByID(project.getPrjid());
		
		if (project == null)
		{
			return "error";
		}

		MetricDTO metric = new MetricDTO();
		metric.setName(metricForm.getName());
		metric.setExpression(metricForm.getExpression());
		metric.setProject (project);
		metric = metricService.save(metric);
		
		project = projectService.findByID(project.getPrjid());
		
		model.put("project", project);
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);

		return "metricdefinition";
	}

	@RequestMapping(value="editmetric", method=RequestMethod.GET)
	public String getEditMetric(Model model, @RequestParam(value="metricid", required=true) String metricid) {
		int nMetricId = Integer.parseInt(metricid);
		MetricDTO metric = null;

		try {
			metric = metricService.findByID(nMetricId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.addAttribute("metric", metric);
		
		return "editmetric";
	}

	@RequestMapping(value="editmetric", method=RequestMethod.POST)
	public String getEditMetricPost(MetricDTO metric, Map<String, Object> model,
		@RequestParam(value="metricid", required=true) String metricid) {
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		project = projectService.findByID(project.getPrjid());
		
		if (project == null)
		{
			return "error";
		}

		int nMetricId = Integer.parseInt(metricid);
		MetricDTO oldMetric = null;
		try {
			oldMetric = metricService.findByID(nMetricId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		oldMetric.setName(metric.getName());
		oldMetric.setExpression(metric.getExpression());
		
		metricService.save(oldMetric);

		model.put("project", project);
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);
		
		return "metricdefinition";
	}

	@RequestMapping(value="uploaddiagram", method=RequestMethod.GET)
	public String getUploadDiagram(HttpServletRequest request, Map<String, Object> model){
		ProjectDTO project = (ProjectDTO) model.get("project");
		project = projectService.findByID(project.getPrjid());
		
		/*File file ;
		int maxFileSize = 5000 * 1024;
		int maxMemSize = 5000 * 1024;
		ServletContext context = pageContext.getServletContext();
		String filePath = context.getInitParameter("file-upload");

		// Verify the content type
		String contentType = request.getContentType();
		if ((contentType.indexOf("multipart/form-data") >= 0)) {

			DiskFileItemFactory factory = new DiskFileItemFactory();
			// maximum size that will be stored in memory
			factory.setSizeThreshold(maxMemSize);
			// Location to save data that is larger than maxMemSize.
			factory.setRepository(new File("c:\\temp"));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// maximum file size to be uploaded.
			upload.setSizeMax( maxFileSize );
			try{ 
		         // Parse the request to get file items.
		         List fileItems = upload.parseRequest(request);

		         // Process the uploaded file items
		         Iterator i = fileItems.iterator();

		         out.println("<html>");
		         out.println("<head>");
		         out.println("<title>JSP File upload</title>");  
		         out.println("</head>");
		         out.println("<body>");
		         while ( i.hasNext () ) 
		         {
		            FileItem fi = (FileItem)i.next();
		            if ( !fi.isFormField () )	
		            {
		            // Get the uploaded file parameters
		            String fieldName = fi.getFieldName();
		            String fileName = fi.getName();
		            boolean isInMemory = fi.isInMemory();
		            long sizeInBytes = fi.getSize();
		            // Write the file
		            if( fileName.lastIndexOf("\\") >= 0 ){
		            file = new File( filePath + 
		            fileName.substring( fileName.lastIndexOf("\\"))) ;
		            }else{
		            file = new File( filePath + 
		            fileName.substring(fileName.lastIndexOf("\\")+1)) ;
		            }
		            fi.write( file ) ;
		            out.println("Uploaded Filename: " + filePath + 
		            fileName + "<br>");
		            }
		         }
		         out.println("</body>");
		         out.println("</html>");
		      }catch(Exception ex) {
		         System.out.println(ex);
		      }
		   }else{
		      out.println("<html>");
		      out.println("<head>");
		      out.println("<title>Servlet upload</title>");  
		      out.println("</head>");
		      out.println("<body>");
		      out.println("<p>No file uploaded</p>"); 
		      out.println("</body>");
		      out.println("</html>");
		   }*/
		   
		AprosService aprosService = new AprosService();
		String strFileName = request.getParameter("uploadFile");
		int maxLevel = 2;//Integer.parseInt(request.getParameter("parameterLevel"));
		aprosService.readDiagramFile(strFileName, maxLevel);
		String strTest = "";
		
		for (int i = 0; i < aprosService.listNewComponents.size(); i++)
		{
			ComponentDTO component = aprosService.listNewComponents.get(i);
			/*ComponentDTO existingComponent = null;
			
			try {
				existingComponent = componentService.findByID(component.getComponentid());
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (existingComponent != null)
			{
				try {
					componentService.update(component, project.getPrjid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
			}
			else
			{*/
			
			try {
				componentService.save(component, project.getPrjid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//}
			strTest += component.getName() + " ";
		}

		for (int i = 0; i < aprosService.listNewInputParams.size(); i++)
		{
			InputParameterDTO inputParam = aprosService.listNewInputParams.get(i);
			//inputParamService.save(inputParam);
			InputParameterDTO existingInputParam = null;
			
			/*try {
				existingInputParam = inputParamService.findByID(inputParam.getInputid());
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (existingInputParam != null)
			{
				try {
					inputParamService.supdate(inputParam, inputParam.getComponentID(), 0);
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
			}
			else
			{*/
				UnitDTO unit = unitService.save(new UnitDTO());
				
				if (inputParam.getComponent() != null)
				{
					ComponentDTO component = componentService.findByName(inputParam.getComponent().getName()).get(0);

					inputParamService.save(inputParam, component.getComponentid(), unit.getUnitid());
				}
				else
				{
					System.out.println("input param " + inputParam.getName() + " component null");
				}
			//}
						
			strTest += inputParam.getName() + " ";
		}
		
		if (project == null)
		{
			return "error";
		}
		
		return "editproject";
	}	
	
	@RequestMapping(value="runscenario", method=RequestMethod.GET)
	public String getRunScenario(Map<String, Object> model)
	{
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		project = projectService.findByID(project.getPrjid());
		
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (scenario != null && scenario.getScenid() > 0)
		{
			try {
				simService.startSimulation(scenario.getScenid());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		
		List<ComponentDTO> components = componentService.findAll();
		model.put("components", components);

		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		return "viewchart";
	}	

	@RequestMapping(value="viewchart", method=RequestMethod.GET)
	public String getViewChart(Map<String, Object> model, 
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

		ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (project == null || scenario == null)
		{
			return "error";
		}
		
		if (charttype != null)
		{
			userSession.setChartType(Integer.parseInt(charttype));
		}
		
		if (action != null)
		{
			if (action.equals("removeall"))
			{
				userSession.removeAllExtVarIds();
				userSession.removeAllOutputVarIds();
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
		
		List<ComponentDTO> components = componentService.findAll();
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
		else if (userSession.getComponentId() > 0)
		{
			model.put("selectedcompid", userSession.getComponentId());
			Set<OutputVariableDTO> outputVars = componentService.getOutputVariables(userSession.getComponentId());
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
			Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
		    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
			
		    // Get output variable results
			while(iterator.hasNext()) {
				int outputVarId = iterator.next(); 
		    
				try {
					OutputVariableDTO outputVar = outputVarService.findByID(outputVarId);
					SimulationResultDTO simResult = simResultService.findByOutVarIdScenId(outputVarId, scenario.getScenid());
						
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
						Set<TimeSeriesVal> timeSeriesVals = timeSeriesDTO.getTimeseriesvals();
						TimeSeries timeSeries = new TimeSeries(extVarVal.getExtparam().getName());
						Iterator<TimeSeriesVal> timeSeriesIter = timeSeriesVals.iterator();
						
						while(timeSeriesIter.hasNext()) {
							TimeSeriesVal timeSeriesVal = timeSeriesIter.next();
							timeSeries.add(new Minute(timeSeriesVal.getTime()), Double.parseDouble(timeSeriesVal.getValue()));
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

			iterator = userSession.getSelectedChartMetricIds().iterator();
			
			// Get metrics time series (max 2 metrics)
			if (userSession.getSelectedChartMetricIds().size() == 2)
			{
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
					Iterator scenIter = scenarioIds.iterator();
					
					while (scenIter.hasNext())
					{
						Integer integerScenarioId = (Integer) scenIter.next();
						int nScenarioId = (int)integerScenarioId;
						MetricValDTO metricVal1 = metricService.getMetricVals(metric1Id, nScenarioId).get(0);
						MetricValDTO metricVal2 = metricService.getMetricVals(metric2Id, nScenarioId).get(0);
						
						timeSeries.add(new Minute((int)Double.parseDouble(metricVal1.getValue()), new Hour()), Double.parseDouble(metricVal2.getValue()));
					}				
															
					timeSeriesCollection.addSeries(timeSeries);
				
					JFreeChart chart = null;
					
					if (userSession.getChartType() == 0) {
						chart = TimeSeriesVisualization.createChart(timeSeriesCollection, "Time series", metric1.getName(), metric2.getName());
					} else if (userSession.getChartType() == 1) {
						chart = ScatterPlotVisualization.createChart(timeSeriesCollection, "Scatter plot", metric1.getName(), metric2.getName());
					}

					if (timeSeriesCollection.getSeriesCount() > 0)
					{
						TimeSeriesVisualization demo = new TimeSeriesVisualization(project.getName() + " time series", timeSeriesCollection, "Time", "");
					}
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
			}
			else
			{
				if (timeSeriesCollection.getSeriesCount() > 0)
				{
					TimeSeriesVisualization demo = new TimeSeriesVisualization(project.getName() + " time series", timeSeriesCollection, "Time", "");
				}
			}
		}
		
		model.put("usersession", userSession);
		
		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);
		
		/*try {
			simService.updateMetricValues(project.getPrjid(), null);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}*/
		
		return "viewchart";
	}

	@RequestMapping("chart.png")
	public void renderChart(Map<String, Object> model, String variation, OutputStream stream) throws Exception {
		UserSession userSession = (UserSession) model.get("usersession");

		ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
	    TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		
	    // Get output variable results
		while(iterator.hasNext()) {
			int outputVarId = iterator.next(); 
	    
			try {
				OutputVariableDTO outputVar = outputVarService.findByID(outputVarId);
				SimulationResultDTO simResult = simResultService.findByOutVarIdScenId(outputVarId, scenario.getScenid());
					
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
					Set<TimeSeriesVal> timeSeriesVals = timeSeriesDTO.getTimeseriesvals();
					TimeSeries timeSeries = new TimeSeries(extVarVal.getExtparam().getName());
					Iterator<TimeSeriesVal> timeSeriesIter = timeSeriesVals.iterator();
					
					while(timeSeriesIter.hasNext()) {
						TimeSeriesVal timeSeriesVal = timeSeriesIter.next();
						timeSeries.add(new Minute(timeSeriesVal.getTime()), Double.parseDouble(timeSeriesVal.getValue()));
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
		
		iterator = userSession.getSelectedChartMetricIds().iterator();
		   
		// Get metrics time series (max 2 metrics)
		if (userSession.getSelectedChartMetricIds().size() == 2)
		{
			timeSeriesCollection.removeAllSeries();
			XYSeriesCollection collection = new XYSeriesCollection();
			
			int metric1Id = iterator.next(); 
			int metric2Id = iterator.next(); 
		    
			try {
				MetricDTO metric1 = metricService.findByID(metric1Id);
				MetricDTO metric2 = metricService.findByID(metric2Id);
				//Set<MetricValDTO> metricVals1 = metricService.getMetricVals(metric1Id);
				//Set<MetricValDTO> metricVals2 = metricService.getMetricVals(metric2Id);
				
				Set<Integer> scenarioIds = userSession.getScenarioIds();
				Iterator scenIter = scenarioIds.iterator();
				//double[][] data = new double[2][userSession.getScenarioIds().size()];
				int index = 0;
				
				while (scenIter.hasNext())
				{
					Integer scenarioId = (Integer) scenIter.next();
					int nScenarioId = (int)scenarioId;
					ScenarioDTO scenarioTemp = scenarioService.findByID(nScenarioId);
					MetricValDTO metricVal1 = metricService.getMetricVals(metric1Id, nScenarioId).get(0);
					MetricValDTO metricVal2 = metricService.getMetricVals(metric2Id, nScenarioId).get(0);
					DefaultXYDataset dataset = new DefaultXYDataset();

					if (userSession.getChartType() == 0) {
						TimeSeries timeSeries = new TimeSeries(scenarioTemp.getName());
						timeSeries.add(new Minute((int)Double.parseDouble(metricVal1.getValue()), new Hour()), Double.parseDouble(metricVal2.getValue()));
						System.out.println("time series point " + metricVal1.getValue() + ", " + metricVal2.getValue() );
						timeSeriesCollection.addSeries(timeSeries);
					} else if (userSession.getChartType() == 1) {
						XYSeries series = new XYSeries(scenarioTemp.getName());
						series.add(Double.parseDouble(metricVal1.getValue()), Double.parseDouble(metricVal2.getValue()));
						/*double[][] data = new double[2][1];
						data[0][0] = Double.parseDouble(metricVal1.getValue());
						data[1][0] = Double.parseDouble(metricVal2.getValue());
					    dataset.addSeries(scenario.getName(), data);*/
					    System.out.println("time series point " + metricVal1.getValue() + ", " + metricVal2.getValue() );
						collection.addSeries(series);						
					}
					
					index++;
				}				
			
				JFreeChart chart = null;
				
				if (userSession.getChartType() == 0) {
					chart = TimeSeriesVisualization.createChart(timeSeriesCollection, "Time series", metric1.getName(), metric2.getName());
				} else if (userSession.getChartType() == 1) {
					chart = ScatterPlotVisualization.createChart(collection, "Scatter plot", metric1.getName(), metric2.getName());
				}
				
				ChartUtilities.writeChartAsPNG(stream, chart, 750, 400);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		else if (timeSeriesCollection.getSeriesCount() > 0)
		{
			JFreeChart chart = null;
			
			if (userSession.getChartType() == 0) {
				chart = TimeSeriesVisualization.createChart(timeSeriesCollection, "Time series", "Time", "");
			} else if (userSession.getChartType() == 1) {
				chart = ScatterPlotVisualization.createChart(timeSeriesCollection, "Scatter plot", "Time", "");
			} else if (userSession.getChartType() == 2) {

				DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
				categoryDataset.addValue(1.0, "Row 1", "Column 1");
				categoryDataset.addValue(5.0, "Row 1", "Column 2");
				categoryDataset.addValue(3.0, "Row 1", "Column 3");
				categoryDataset.addValue(2.0, "Row 2", "Column 1");
				categoryDataset.addValue(3.0, "Row 2", "Column 2");
				categoryDataset.addValue(2.0, "Row 2", "Column 3");
						
				chart = BarChartVisualization.createChart(categoryDataset, "Bar chart", "Time", "");
			} else if (userSession.getChartType() == 3) {
				//chart = PieChartVisualization.createChart(timeSeriesCollection, "Pie chart", "Time", "");
			}
			
			ChartUtilities.writeChartAsPNG(stream, chart, 750, 400);
		}
		else
		{
			//JFreeChart chart = new JFreeChart();
		}
	}
	
	@RequestMapping(value="viewtable", method=RequestMethod.GET)
	public String getViewTable(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid) {

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
		
		List<ComponentDTO> components = componentService.findAll();
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
		
		return "viewtable";
	}
	
	@RequestMapping(value="writetable", method=RequestMethod.GET)
	public String getWriteTable(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="outputvarid", required=false) String outputvarid,
		@RequestParam(value="extparamid", required=false) String extparamid) {

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
		
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		List<ComponentDTO> components = componentService.findAll();
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
		
		Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
	    List<Double> listOutputVarVals = new ArrayList<Double>();
	    List<String> listOutputVarTime = new ArrayList<String>();
		
	    // Get output variable results
		while(iterator.hasNext()) {
			int outputVarId = iterator.next(); 
	    
			try {
				OutputVariableDTO outputVar = outputVarService.findByID(outputVarId);
				SimulationResultDTO simResult = simResultService.findByOutVarIdScenId(outputVarId, scenario.getScenid());
					
				List<TimeSeriesValDTO> timeSeriesVals = simResultService.getTimeSeriesValsOrderedByTime(simResult.getSimresid());
				
				for (int i = 0; i < timeSeriesVals.size(); i++)
				{
					TimeSeriesValDTO timeSeriesVal = timeSeriesVals.get(i);
					listOutputVarVals.add(Double.parseDouble(timeSeriesVal.getValue()));
					listOutputVarTime.add(timeSeriesVal.getTime().toString());
				}
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			break; // TODO
	    }

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
		
		return "viewtable";
	}
		
	@RequestMapping(value="geneticalgorithm", method=RequestMethod.GET)
	public String getGeneticAlgorithm(Map<String, Object> model)
	{
		return "geneticalgorithm";
	}	
	
	@RequestMapping(value="error", method=RequestMethod.GET)
	public String getError(Map<String, Object> model)
	{
		return "error";
	}	
}
