package eu.cityopt.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.hibernate.loader.plan.build.internal.returns.CollectionAttributeFetchImpl;
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
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.Project;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.AprosService;
import eu.cityopt.service.ComponentInputParamDTOServiceImpl;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValServiceImpl;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParamValServiceImpl;
import eu.cityopt.service.InputParameterService;
import eu.cityopt.service.MetricService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.eval.EvaluationException;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.apros.AprosRunner;

@Controller
@SessionAttributes({"project", "scenario"})
public class ProjectController {
	
	@Autowired
	ProjectService projectService; 
	
	@Autowired
	ScenarioService scenarioService;
	
	@Autowired
	AppUserService userService;
	
	@Autowired
	ComponentService componentService;

	@Autowired
	ComponentInputParamDTOServiceImpl componentInputParamService;
	
	@Autowired
	InputParameterService inputParamService;

	@Autowired
	InputParamValServiceImpl inputParamValService;

	@Autowired
	ExtParamService extParamService;

	@Autowired
	ExtParamValServiceImpl extParamValService;

	@Autowired
	MetricService metricService;
	
	@Autowired
	UnitService unitService;
	
	@RequestMapping(value="createproject", method=RequestMethod.GET)
	public String getCreateProject(Map<String, Object> model) {
		ProjectDTO newProject = new ProjectDTO();
		model.put("project", newProject);
		return "createproject";
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
			} catch (EntityNotFoundException e) {
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

	@RequestMapping(value="editscenario",method=RequestMethod.GET)
	public String getEditScenario (Map<String, Object> model) {
		if (!model.containsKey("project"))
		{
			return "error";
		}

		ProjectDTO project = (ProjectDTO) model.get("project");
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			try {
				project = projectService.findByID(project.getPrjid());
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		model.put("project", project);
		Set<ExtParamValDTO> extParams = null;//projectService...getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		
		return "scenariovariables";
	}
	
	@RequestMapping(value="usermanagement", method=RequestMethod.GET)
	public String getUserManagement(Model model){
		List<AppUserDTO> users = userService.findAll();
		model.addAttribute("users", users);
	
		return "usermanagement";
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
	public String getDeleteUser(Model model, @RequestParam(value="userid") String userid){
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
	
	@RequestMapping(value="viewchart",method=RequestMethod.GET)
	public String getViewChart(Model model){
	
		List<ComponentDTO> components = componentService.findAll();
		model.addAttribute("components", components);

		return "viewchart";
	}
	
	@RequestMapping(value="viewtable",method=RequestMethod.GET)
	public String getViewTable(Model model){
	
		return "viewtable";
	}
	
	@RequestMapping(value="coordinates",method=RequestMethod.GET)
	public String getCoordinates(Map<String, Object> model){
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
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "coordinates";
	}
	
	@RequestMapping(value="databaseoptimization",method=RequestMethod.GET)
	public String getDatabaseOptimization(Model model){
	
		return "databaseoptimization";
	}
	
	@RequestMapping(value="openoptimizationset",method=RequestMethod.GET)
	public String getOpenOptimizationSet(Model model){
	
		return "openoptimizationset";
	}

	@RequestMapping(value="deleteoptimizationset",method=RequestMethod.GET)
	public String getDeleteOptimizationSet(Model model){
	
		return "deleteoptimizationset";
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
	
	@RequestMapping(value="runmultioptimizationset",method=RequestMethod.GET)
	public String getRunMultiOptimizationSet(Model model){
	
		return "runmultioptimizationset";
	}

	@RequestMapping(value="projectparameters", method=RequestMethod.GET)
	public String getProjectParameters(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId){
		ProjectDTO project = (ProjectDTO) model.get("project");
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
				// TODO Auto-generated catch block
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
		
		try {
			model.put("project", projectService.findByID(project.getPrjid()));
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		try {
			model.put("project", projectService.findByID(project.getPrjid()));
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@RequestMapping(value="editinputparamvalue", method=RequestMethod.GET)
	public String getEditInputParamVal(Model model, @RequestParam(value="inputparamvalid", required=true) String inputid) {
		int nInputId = Integer.parseInt(inputid);
		ComponentInputParamDTO inputParamVal = null;

		try {
			inputParamVal = null;//componentInputParamService. inputParamValService.findByID(nInputId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("inputParamVal", inputParamVal);
		
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

		int nInputParamValId = Integer.parseInt(inputParamValId);
		InputParamValDTO updatedInputParamVal = null;
		
		try {
			updatedInputParamVal = inputParamValService.findByID(nInputParamValId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		updatedInputParamVal.setValue(inputParamVal.getValue());
		//UnitDTO unit = unitService.save(new UnitDTO());
		inputParamValService.save(updatedInputParamVal);
				
		model.put("selectedcompid", updatedInputParamVal.getInputparameter().getComponent().getComponentid());

		try {
			model.put("selectedComponent", inputParamValService.findByID(updatedInputParamVal.getInputparameter().getComponent().getComponentid()));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		model.put("project", project);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		return "projectparameters";
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
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
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

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}

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

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}

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
				cloneMetric = metricService.save(cloneMetric);
				metricService.setProject(cloneMetric.getMetid(), project.getPrjid());
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
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
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
	public String getCreateMetricPost(MetricDTO metricForm, Map<String, Object> model){
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

		MetricDTO metric = new MetricDTO();
		metric.setName(metricForm.getName());
		metric.setExpression(metricForm.getExpression());
		metric = metricService.save(metric);
		metricService.setProject(metric.getMetid(), project.getPrjid());
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
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
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
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
		
		return "metricdefinition";
	}

	@RequestMapping(value="uploaddiagram", method=RequestMethod.GET)
	public String getUploadDiagram(HttpServletRequest request, Map<String, Object> model){
		ProjectDTO project = (ProjectDTO) model.get("project");
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		List<ComponentDTO> components = componentService.findAll();
		model.put("components", components);

		return "viewchart";
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
