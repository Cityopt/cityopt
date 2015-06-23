package eu.cityopt.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.AprosService;
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
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.UnitForm;

/**
 * @author Olli Stenlund
 *
 */
@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession"})
public class ProjectController {
	
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
	
	@RequestMapping(value="createproject", method=RequestMethod.GET)
	public String getCreateProject(Map<String, Object> model) {
		ProjectDTO newProject = new ProjectDTO();
		model.put("project", newProject);
		return "createproject";
	}

	@RequestMapping(value="createproject", method=RequestMethod.POST)
	public String getCreateProjectPost(Map<String, Object> model, ProjectDTO projectForm) {
		if (projectForm.getName() != null)
		{
			ProjectDTO project = new ProjectDTO();
			project.setName(projectForm.getName());
			String desc = projectForm.getDescription();
			
			if (desc != null && !desc.isEmpty())
			{
				project.setDescription(projectForm.getDescription());
			}
			
			// Create default ext param val set
			/*ExtParamValSetDTO extParamValSet = new ExtParamValSetDTO();
			extParamValSet.setName("default set");
			extParamValSet = extParamValSetService.save(extParamValSet);
			
			project.setExtparamvalset(extParamValSet);*/
			project = projectService.save(project, 0, 0);
			
			model.put("project", project);
			model.remove("scenario");
			
			return "editproject";
		}
		else
		{
			return "error";
		}
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
				// TODO Auto-generated catch block
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
		else if (model.containsKey("project"))
		{
			ProjectDTO project = (ProjectDTO) model.get("project");
			
			try {
				project = projectService.findByID(project.getPrjid());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("project", project);
		}
		else if (!model.containsKey("project"))
		{
			ProjectDTO newProject = new ProjectDTO();
			model.put("project", newProject);
			model.remove("scenario");
			return "createproject";
		}

		return "editproject";
	}
                            
	@RequestMapping(value = "uploadFile", method = RequestMethod.POST)
	public String uploadFileHandler(Map<String, Object> model, @RequestParam(value="detailLevel", required=false) String detailLevel,
	    @RequestParam("file") MultipartFile file) {
	
		if (!file.isEmpty()) {
	        try {
	            byte[] bytes = file.getBytes();

	            // Creating the directory to store file
	            /*String rootPath = "~" + File.separator;//System.getProperty("java.home");
	            File dir = new File(rootPath);// + File.separator + "modelFiles");
	            
	            //if (!dir.exists())
	            //    dir.mkdirs();

	            // Create the file on server
	            File serverFile = new File(dir.getAbsolutePath()
	                    + File.separator + file.getName());
	            BufferedOutputStream stream = new BufferedOutputStream(
	                    new FileOutputStream(serverFile));
	            stream.write(bytes);
	            stream.close();*/

	            //logger.info("Server File Location="
	            //        + serverFile.getAbsolutePath());

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
			
				Set<String> simulatorNames = SimulatorManagers.getSimulatorNames();
				String simulatorName = simulatorNames.iterator().next();
				Instant timeOrigin =  Instant.parse("2015-01-01T00:00:00Z");

				importExportService.importSimulationModel(project.getPrjid(), 0, "Imported evergy model " + Instant.now(), bytes, simulatorName, timeOrigin);
				importExportService.importModelInputsAndOutputs(project.getPrjid(), 0);
				
				//Path path = new Path(file.getOriginalFilename());
				//importExportService.importSimulationStructure(project.getPrjid(), path);
	        } catch (Exception e) {
	            return "You failed to upload => " + e.getMessage();
	        }
	    } else {
	    }
		return "editproject";
	}
	
	@RequestMapping(value = "importcomponents", method = RequestMethod.POST)
	public String uploadComponentsFileHandler(Map<String, Object> model, 
	    @RequestParam("file") MultipartFile file) {
	
		if (!file.isEmpty()) {
	        try {
	            byte[] bytes = file.getBytes();

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
			
				Set<String> simulatorNames = SimulatorManagers.getSimulatorNames();
				String simulatorName = simulatorNames.iterator().next();
				Instant timeOrigin =  Instant.parse("2015-01-01T00:00:00Z");

				importExportService.importSimulationModel(project.getPrjid(), 0, "Imported evergy model " + Instant.now(), bytes, simulatorName, timeOrigin);
				importExportService.importModelInputsAndOutputs(project.getPrjid(), 0);
				
				//Path path = new Path(file.getOriginalFilename());
				//importExportService.importSimulationStructure(project.getPrjid(), path);
	        } catch (Exception e) {
	            return "You failed to upload => " + e.getMessage();
	        }
	    } else {
	    }
		return "editproject";
	}
	
	@RequestMapping(value="editproject", method=RequestMethod.POST)
	public String getEditProjectPost(ProjectDTO projectForm, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {
		//@RequestParam(value="name", required=false) String name,
        //@RequestParam(value="file", required=false) MultipartFile file) {

		if (projectForm != null && action != null)
		{
			if (action.equals("create"))
			{
				ProjectDTO project = new ProjectDTO();
				project.setName(projectForm.getName());
				project = projectService.save(project,0,0);
				model.put("project", project);
			}
			else if (action.equals("update"))
			{
				ProjectDTO project = (ProjectDTO) model.get("project");
				
				if (project == null)
				{
					return "error";
				}
				
				try {
					//project = projectService.findByID(project.getPrjid());

					project.setName(projectForm.getName());
					project.setDescription(projectForm.getDescription());
					project.setLocation(projectForm.getLocation());
					project.setDesigntarget(projectForm.getDesigntarget());
					
					project = projectService.save(project, projectService.getSimulationmodelId(project.getPrjid()),
							projectService.getDefaultExtParamSetId(project.getPrjid()));
					
				} catch(ObjectOptimisticLockingFailureException e) {
					model.put("errorMessage", "This project has been updated in the meantime, please reload.");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				model.put("project", project);
			}
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
	public String getStart(Model model, HttpServletRequest request) {
	
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if (true)
		{
			return "start";
		}
		else
		{
			return "index";
		}
	}

	@RequestMapping(value="deleteproject",method=RequestMethod.GET)
	public String getDeleteProject(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid){
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
				e.printStackTrace();
			} catch(ObjectOptimisticLockingFailureException e) {
				model.put("errorMessage", "This project has been updated in the meantime, please reload.");
			}
		}

		List<ProjectDTO> projects = projectService.findAll();
		model.put("projects",projects);

		return "deleteproject";
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
						//newUnit.setType(types.get(i));
						break;
					}
				}
				
				//System.out.println("unit " + newUnit.getName() + " type " + newUnit.getType());
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
	
	@RequestMapping(value="projectdata", method=RequestMethod.GET)
	public String getProjectData(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="selectedextparamvalsetid", required=false) String selectedExtParamValSetId) {
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
		
		ComponentDTO selectedComponent = null;
		
		if (selectedCompId != null)
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("selectedcompid", selectedCompId);
			model.put("selectedComponent",  selectedComponent);
			Set<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
			model.put("inputParameters", inputParams);
		}

		model.put("project", project);
		
		List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
		model.put("extParamValSets", extParamValSets);
		
		List<ExtParamValDTO> extParamVals = null;
		
		if (selectedExtParamValSetId != null)
		{
			try {
				extParamVals = extParamValSetService.getExtParamVals(projectService.getDefaultExtParamSetId(project.getPrjid()));
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("extParamVals", extParamVals);
		}
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectdata";
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
		
	@RequestMapping(value="extparamsets",method=RequestMethod.GET)
	public String getExtParamSets(Model model) {
	
		return "extparamsets";
	}
	
	@RequestMapping(value="projectparameters", method=RequestMethod.GET)
	public String getProjectParameters(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {
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
		
		ComponentDTO selectedComponent = null;
		
		if (selectedCompId != null)
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("selectedcompid", selectedCompId);
			model.put("selectedComponent",  selectedComponent);
			Set<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
			model.put("inputParameters", inputParams);
		}

		model.put("project", project);
		List<ExtParamValDTO> extParamVals = null;
		
		int defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
		if (defaultExtParamValSetId != 0)
		{
			try {
				extParamVals = extParamValSetService.getExtParamVals(defaultExtParamValSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("extParamVals", extParamVals);
		}
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectparameters";
	}
	
	@RequestMapping(value="selectextparamset", method=RequestMethod.GET)
	public String getSelectExtParamSet(Map<String, Object> model, 
		@RequestParam(value="selectedextparamsetid", required=false) String selectedExtParamSetId) {

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
		ExtParamValSetDTO selectedExtParamSet = null;
		
		if (selectedExtParamSetId != null)
		{
			int nSelectedExtParamSetId = Integer.parseInt(selectedExtParamSetId);
			
			try {
				selectedExtParamSet = extParamValSetService.findByID(nSelectedExtParamSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}

//			project.setDefaultextparamvalset(selectedExtParamSet);
			project = projectService.save(project, projectService.getSimulationmodelId(project.getPrjid()), 
					selectedExtParamSet.getExtparamvalsetid());
			
			model.put("selectedextparamsetid", nSelectedExtParamSetId);
			model.put("selectedExtParamSet",  selectedExtParamSet);
			List<ExtParamValDTO> extParamVals = null;

			try {
				extParamVals = extParamValSetService.getExtParamVals(nSelectedExtParamSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("extParamVals", extParamVals);
			model.put("project", project);
			
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);
			
			return "projectparameters";
		}

		List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
		model.put("extParamValSets", extParamValSets);
		model.put("project", project);
		
		return "selectextparamset";
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
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		int componentId = inputParamService.getComponentId(updatedInputParam.getInputid());
		inputParamService.save(updatedInputParam, componentId, unit.getUnitid());
				
		model.put("selectedcompid", componentId);
		
		try {
			model.put("selectedComponent", componentService.findByID(componentId));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		model.put("project", project);
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		model.put("extParams", extParams);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		Set<InputParameterDTO> inputParams = componentService.getInputParameters(componentId);
		model.put("inputParameters", inputParams);

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
//		newInputParameter.setComponent(component);
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
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
		ExtParamDTO newExtParam = new ExtParamDTO();
		newExtParam.setName(extParam.getName());
		newExtParam.setProject(project);
		newExtParam = extParamService.save(newExtParam, project.getPrjid());
		
		List<ExtParamValSetDTO> extParamSets = projectService.getExtParamValSets(project.getPrjid());

		// Add ext param val to all ext param val sets
		for (int i = 0; i < extParamSets.size(); i++)
		{
			ExtParamValSetDTO extParamValSet = extParamSets.get(i);

			ExtParamValDTO extParamVal = new ExtParamValDTO();
			extParamVal.setValue("");
			extParamVal.setExtparam(newExtParam);
			
			HashSet<ExtParamValDTO> setExtVals = new HashSet<ExtParamValDTO>();
			setExtVals.add(extParamVal);
			
			try {
				extParamValSetService.addExtParamVals(extParamValSet.getExtparamvalsetid(), setExtVals);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}

		List<ExtParamValDTO> extParamVals = null;
		int defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
		if (defaultExtParamValSetId != 0)
		{
			try {
				extParamVals = extParamValSetService.getExtParamVals(defaultExtParamValSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("extParamVals", extParamVals);
		}
		
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
			e1.printStackTrace();
		}
		model.put("project", project);

		int nExtParamId = Integer.parseInt(extparamid);
		ExtParamDTO extParam = null;
		try {
			extParam = extParamService.findByID(nExtParamId);
		} catch (EntityNotFoundException e) {
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
			e1.printStackTrace();
		}
		
		int nExtParamId = Integer.parseInt(extParamId);
		ExtParamDTO updatedExtParam = null;
		try {
			updatedExtParam = extParamService.findByID(nExtParamId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		updatedExtParam.setName(extParam.getName());
		
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
			// TODO Auto-generated catch block
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

		List<ExtParamValDTO> extParamVals = null;
		
		int defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
		if (defaultExtParamValSetId != 0)
		{
			try {
				extParamVals = extParamValSetService.getExtParamVals(defaultExtParamValSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			model.put("extParamVals", extParamVals);
		}
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectparameters";
	}

	@RequestMapping(value="createextparamset", method=RequestMethod.GET)
	public String getCreateExtParamSet(Map<String, Object> model) {
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
		
		ExtParamValSetDTO extParamValSet = new ExtParamValSetDTO();
		
		Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
		Iterator<ExtParamDTO> iter = extParams.iterator();
		Set<ExtParamValDTO> extParamVals = new HashSet<ExtParamValDTO>();
		
		while (iter.hasNext())
		{
			ExtParamDTO extParam = iter.next();
			
			ExtParamValDTO extParamVal = new ExtParamValDTO();
			extParamVal.setExtparam(extParam);
			extParamVal.setValue("");
			extParamVal = extParamValService.save(extParamVal);
			
			extParamVals.add(extParamVal);
		}

		extParamValSet.setName("temp");
		extParamValSet = extParamValSetService.save(extParamValSet);
		
		try {
			extParamValSetService.addExtParamVals(extParamValSet.getExtparamvalsetid(), extParamVals);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		extParamValSet = extParamValSetService.save(extParamValSet);

		//Namespace namespace = simService.makeProjectNamespace(project.getPrjid());
		/*ExternalParameters contExtParams = null;
		
		try {
			contExtParams = simService.loadExternalParameters(project.getPrjid(), project.getExtparamvalset().getExtparamvalsetid());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		while (iter.hasNext())
		{
			ExtParamValDTO extParamVal = new ExtParamValDTO();
			ExtParamDTO extParam = iter.next();
			String defaultValue = (String) contExtParams.get(extParam.getName());
			extParamVal.setValue(defaultValue);
			extParamVals.add(extParamVal);
		}
		
		extParamValSet = extParamValSetService.save(extParamValSet);
		
		try {
			extParamValSetService.addExtParamVals(extParamValSet.getExtparamvalsetid(), extParamVals);
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}*/
		
//		project.setDefaultextparamvalset(extParamValSet);
		
		int simModelId = projectService.getSimulationmodelId(project.getPrjid());
		project = projectService.save(project, simModelId, extParamValSet.getExtparamvalsetid());
		model.put("project", project);
		
		model.put("extParamValSet", extParamValSet);
		model.put("extParamVals", extParamVals);
		
		return "createextparamset";
	}

	@RequestMapping(value="createextparamset", method=RequestMethod.POST)
	public String getCreateExtParamSetPost(ExtParamValSetDTO extParamValSet, Map<String, Object> model) {
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

		String newName = extParamValSet.getName();
		
		try {
			extParamValSet = extParamValSetService.findByID(projectService.getDefaultExtParamSetId(project.getPrjid()));
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}
		extParamValSet.setName(newName);
		extParamValSet = extParamValSetService.save(extParamValSet);
		
		model.put("project", project);
		List<ExtParamValDTO> listExtParamVals = null;
		
		try {
			listExtParamVals = extParamValSetService.getExtParamVals(extParamValSet.getExtparamvalsetid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		model.put("extParamVals", listExtParamVals);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "projectparameters";
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
			// TODO Auto-generated catch block
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
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
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
	public String getCreateMetricPost(MetricDTO metricForm, Map<String, Object> model) {
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

		MetricDTO metric = new MetricDTO();
		metric.setName(metricForm.getName());
		metric.setExpression(metricForm.getExpression());
		metric.setProject (project);
		metric = metricService.save(metric);
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
		Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
		model.put("metrics", metrics);
		
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
		int userId = 0;
		//java.nio.file.Files.readAllBytes(path);
		Set<String> simulatorNames = SimulatorManagers.getSimulatorNames();
		String simulatorName = simulatorNames.iterator().next();
		//Instant timeOrigin = new Instant();
		//importExportService.importSimulationModel(project.getPrjid(), userId, formProject.getDescription(), modelData, simulatorName, overrideTimeOrigin);

		
		
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
				
				if (inputParamService.getComponentId(inputParam.getInputid()) != 0)
				{
					inputParamService.save(inputParam, inputParamService.getComponentId(inputParam.getInputid()), unit.getUnitid());
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

	@RequestMapping(value="error", method=RequestMethod.GET)
	public String getError(Map<String, Object> model)
	{
		return "error";
	}	
}
