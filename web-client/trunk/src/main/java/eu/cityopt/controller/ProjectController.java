package eu.cityopt.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.swing.JFileChooser;

import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.model.AppUser;
import eu.cityopt.model.Component;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.Unit;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.AppUserServiceImpl;
import eu.cityopt.service.AprosService;
import eu.cityopt.service.ComponentServiceImpl;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.InputParameterServiceImpl;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ProjectServiceImpl;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.ScenarioServiceImpl;

@Controller
@SessionAttributes({"project", "scenario"})
public class ProjectController {

	
	@Autowired
	ProjectServiceImpl projectService; 

	@Autowired
	AppUserServiceImpl userService;
	
	@Autowired
	ScenarioServiceImpl scenarioService; 

	@Autowired
	ComponentServiceImpl componentService;
	
	@Autowired
	InputParameterServiceImpl inputParamService;
	
	@RequestMapping(value="getProjects",method=RequestMethod.GET)
	public String getGoalReports(Model model) {
		List<Project> projects = projectService.findAll();
		model.addAttribute("projects",projects);
		
		return "getProjects";
	}	

	@RequestMapping(value="createproject", method=RequestMethod.GET)
	public String getCreateProject(Map<String, Object> model) {
		Project newProject = new Project();
		model.put("project", newProject);
		return "createproject";
	}

	@RequestMapping(value="openproject", method=RequestMethod.GET)
	public String getStringProjects(Map<String, Object> model)
	{
		List<Project> projects = projectService.findAll();
		model.put("projects", projects);
	
		return "openproject";
	}	

	@RequestMapping(value="editproject", method=RequestMethod.GET)
	public String getEditProject(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid) {
		if (prjid != null)
		{
			Project project = projectService.findByID(Integer.parseInt(prjid));
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
			Project newProject = new Project();
			model.put("project", newProject);
			return "createproject";
		}

		return "editproject";
	}

	@RequestMapping(value="editproject", method=RequestMethod.POST)
	public String getEditProjectPost(Project project, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {
	
		if (project != null && action != null)
		{
			if (action.equals("create"))
			{
			}
			else if (action.equals("update"))
			{
			}
			
			if (true) //project.getAprosFileName() != null)
			{
				AprosService aprosService = new AprosService();
				String strFileName = "";//project.getAprosFileName();
				aprosService.readDiagramFile(strFileName);
			}
			
			//project.setName(projectForm.getProjectName());
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
			Project tempProject = projectService.findByID(Integer.parseInt(prjid));
			try {
				projectService.delete(tempProject);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Project> projects = projectService.findAll();
		model.addAttribute("projects",projects);

		return "deleteproject";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String getCreateScenario(Map<String, Object> model) {
		Scenario scenario = new Scenario();
		model.put("scenario", scenario);
		return "createscenario";
	}

	@RequestMapping(value="createscenario",method=RequestMethod.POST)
	public String getCreateScenarioPost(Scenario formScenario, Map<String, Object> model) {

		if (model.containsKey("project") && formScenario != null)
		{
			Project project = (Project) model.get("project");
			model.put("project", project);
			Scenario scenario = new Scenario();
			scenario.setName(formScenario.getName());
			scenario.setDescription(formScenario.getDescription());
			scenario.setProject(project);
			scenario.getScenid();
			scenarioService.save(scenario);
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
		Project project = (Project) model.get("project");
		
		if (project == null)
		{
			return "createproject";
		}
		
		if (scenarioid != null)
		{
			Scenario scenario = scenarioService.findByID(Integer.parseInt(scenarioid));
			model.put("scenario", scenario);
			return "editscenario";
		}

		return "openscenario";
	}

	@RequestMapping(value="editscenario",method=RequestMethod.GET)
	public String getEditScenario (Map<String, Object> model) {
		Scenario scenario = (Scenario) model.get("scenario");
		
		if (scenario != null)
		{
			model.put("scenario", scenario);
			return "editscenario";
		}
		else
		{
			scenario = new Scenario();
			model.put("scenario", scenario);
			return "createscenario";
		}
	}

	@RequestMapping(value="editscenario",method=RequestMethod.POST)
	public String getEditScenarioPost(Scenario formScenario, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {

		if (model.containsKey("project") && formScenario != null)
		{
			Project project = (Project) model.get("project");
			Scenario scenario = (Scenario) model.get("scenario");
			
			scenario.setProject(project);
			scenario.setName(formScenario.getName());
			scenario.setDescription(formScenario.getDescription());
			
			scenarioService.save(scenario);
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
			Scenario tempScenario = scenarioService.findByID(Integer.parseInt(scenarioid));
			try {
				scenarioService.delete(tempScenario);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<Scenario> scenarios = scenarioService.findAll();
		model.addAttribute("scenarios", scenarios);

		return "deletescenario";
	}

	@RequestMapping(value="scenarioparameters", method=RequestMethod.GET)
	public String getScenarioParameters(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId){
		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		Component selectedComponent = null;
		
		if (selectedCompId != null)
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			selectedComponent = componentService.findByID(nSelectedCompId);
			model.put("selectedcompid", selectedCompId);
			model.put("selectedComponent",  selectedComponent);
		}

		model.put("project", project);
		
		return "scenarioparameters";
	}
	
	@RequestMapping(value="scenariovariables",method=RequestMethod.GET)
	public String getScenarioVariables(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {

		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		model.put("project", project);
		
		return "scenariovariables";
	}
	
	@RequestMapping(value="usermanagement", method=RequestMethod.GET)
	public String getUserManagement(Model model){
		List<AppUser> users = userService.findAll();
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
			AppUser user = new AppUser();
			user.setName(userForm.getName());
			user.getUserid();
			userService.save(user);
		}

		List<AppUser> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
	}

	@RequestMapping(value="edituser",method=RequestMethod.GET)
	public String getEditUser(Model model, @RequestParam(value="userid", required=true) String userid) {
		int nUserId = Integer.parseInt(userid);
		
		AppUser user = userService.findByID(nUserId);
		UserForm userForm = new UserForm();
		userForm.setName(user.getName());
		model.addAttribute("userForm", userForm);

		return "edituser";
	}

	@RequestMapping(value="edituser", method=RequestMethod.POST)
	public String getEditUserPost(UserForm userForm, Map<String, Object> model,
		@RequestParam(value="userid", required=true) String userId) {

		AppUser user = (AppUser) userService.findByID(Integer.parseInt(userId));
		
		if (userForm.getName() != null)
		{
			user.setName(userForm.getName());
			userService.save(user);
		}

		List<AppUser> users = userService.findAll();
		model.put("users", users);

		return "usermanagement";
	}
	
	@RequestMapping(value="deleteuser", method=RequestMethod.GET)
	public String getDeleteUser(Model model, @RequestParam(value="userid") String userid){
		int nUserId = Integer.parseInt(userid);
		
		if (nUserId >= 0)
		{
			AppUser user = userService.findByID(nUserId);
			try {
				userService.delete(user);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<AppUser> users = userService.findAll();
		model.addAttribute("users", users);

		return "usermanagement";
	}
	
	@RequestMapping(value="viewchart",method=RequestMethod.GET)
	public String getViewChart(Model model){
	
		return "viewchart";
	}
	
	@RequestMapping(value="viewtable",method=RequestMethod.GET)
	public String getViewTable(Model model){
	
		return "viewtable";
	}
	
	@RequestMapping(value="coordinates",method=RequestMethod.GET)
	public String getCoordinates(Model model){
	
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

		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		if (selectedCompId != null)
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			Component selectedComponent = componentService.findByID(nSelectedCompId);
			model.put("selectedcompid", selectedCompId);
			//Hibernate.initialize(selectedComponent.getInputparameters());
			//model.put("inputParams", selectedComponent.getInputparameters());
			model.put("selectedComponent",  selectedComponent);
		}

		model.put("project", project);
		
		return "outputvariables";
	}
	
	@RequestMapping(value="runmultiscenario",method=RequestMethod.GET)
	public String getRunMultiScenario(Model model){
	
		return "runmultiscenario";
	}
	
	@RequestMapping(value="runmultioptimizationset",method=RequestMethod.GET)
	public String getRunMultiOptimizationSet(Model model){
	
		return "runmultioptimizationset";
	}

	@RequestMapping(value="metricdefinition",method=RequestMethod.GET)
	public String getMetricDefinition(Model model){
	
		return "metricdefinition";
	}

	@RequestMapping(value="projectparameters", method=RequestMethod.GET)
	public String getProjectParameters(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId){
		Project project = (Project) model.get("project");

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

		Component selectedComponent = null;
		
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
			selectedComponent = componentService.findByID(nSelectedCompId);
			model.put("selectedcompid", selectedCompId);
			//Hibernate.initialize(selectedComponent.getInputparameters());
			//model.put("inputParams", selectedComponent.getInputparameters());
			model.put("selectedComponent",  selectedComponent);
		}

		model.put("project", project);
		
		return "projectparameters";
	}
	
	@RequestMapping(value="createcomponent", method=RequestMethod.GET)
	public String getCreateComponent(Model model){

		Component newComponent = new Component();
		model.addAttribute("component", newComponent);
		
		return "createcomponent";
	}

	@RequestMapping(value="createcomponent", method=RequestMethod.POST)
	public String getCreateComponentPost(Component component, Map<String, Object> model){
		Project project = (Project) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		component.setComponentid(1);
		component.setProject(project);
		componentService.save(component);
		
		model.put("project", projectService.findByID(project.getPrjid()));
		
		return "projectparameters";
	}

	@RequestMapping(value="editcomponent", method=RequestMethod.GET)
	public String getEditComponent(Model model, @RequestParam(value="componentid", required=true) String componentid) {
		int nCompId = Integer.parseInt(componentid);
		Component  component = componentService.findByID(nCompId);
		model.addAttribute("component", component);
		
		return "editcomponent";
	}

	@RequestMapping(value="editcomponent", method=RequestMethod.POST)
	public String getEditComponentPost(Component component, Map<String, Object> model,
		@RequestParam(value="componentid", required=true) String componentid) {
		Project project = (Project) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		int nCompId = Integer.parseInt(componentid);
		Component oldComponent = componentService.findByID(nCompId);
		oldComponent.setName(component.getName());
		
		componentService.save(oldComponent);
		model.put("selectedcompid", oldComponent.getComponentid());
		model.put("selectedComponent",  oldComponent);

		model.put("project", projectService.findByID(project.getPrjid()));
		
		return "projectparameters";
	}

	@RequestMapping(value="editinputparameter", method=RequestMethod.GET)
	public String getEditInputParameter(Model model, @RequestParam(value="inputparameterid", required=true) String inputid) {
		int nInputId = Integer.parseInt(inputid);
		InputParameter inputParam = inputParamService.findByID(nInputId);
		model.addAttribute("inputParam", inputParam);
		
		return "editinputparameter";
	}

	@RequestMapping(value="editinputparameter", method=RequestMethod.POST)
	public String getEditInputParameterPost(InputParameter inputParam, Map<String, Object> model,
		@RequestParam(value="inputparamid", required=true) String inputParamId){
		Project project = (Project) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		int nInputParamId = Integer.parseInt(inputParamId);
		InputParameter updatedInputParam = inputParamService.findByID(nInputParamId);
		updatedInputParam.setName(inputParam.getName());
		updatedInputParam.setDefaultvalue(inputParam.getDefaultvalue());
		inputParamService.save(updatedInputParam);
				
		model.put("selectedcompid", updatedInputParam.getComponent().getComponentid());
		model.put("selectedComponent",  updatedInputParam.getComponent());
		model.put("project", project);

		return "projectparameters";
	}

	@RequestMapping(value="createinputparameter", method=RequestMethod.GET)
	public String getCreateInputParameter(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=true) String strSelectedCompId) {
		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}

		int nSelectedCompId = Integer.parseInt(strSelectedCompId);
		Component component = componentService.findByID(nSelectedCompId);

		InputParameter newInputParameter = new InputParameter();
		newInputParameter.setUnit(new Unit(0));
		newInputParameter.setComponent(component);
		model.put("inputParam", newInputParameter);
		model.put("selectedcompid", nSelectedCompId);
		
		return "createinputparameter";
	}

	@RequestMapping(value="createinputparameter", method=RequestMethod.POST)
	public String getCreateInputParamPost(InputParameter inputParam, Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=true) String strSelectedCompId) {
		Project project = (Project) model.get("project");

		if (project == null)
		{
			return "error";
		}

		int nSelectedCompId = Integer.parseInt(strSelectedCompId);
		Component component = componentService.findByID(nSelectedCompId);
		inputParam.setComponent(component);
		inputParamService.save(inputParam);
				
		model.put("selectedcompid", nSelectedCompId);
		model.put("selectedComponent",  component);
	
		model.put("project", project);
	
		return "projectparameters";
	}
	
	@RequestMapping(value="uploaddiagram", method=RequestMethod.GET)
	public String getUploadDiagram(HttpServletRequest request, Map<String, Object> model){
		Project project = (Project) model.get("project");

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
		aprosService.readDiagramFile(strFileName);
		String strTest = "";
		
		for (int i = 0; i < aprosService.listNewComponents.size(); i++)
		{
			Component component = aprosService.listNewComponents.get(i);
			component.setProject(project);
			componentService.save(component);
			strTest += component.getName() + " ";
		}

		for (int i = 0; i < aprosService.listNewInputParams.size(); i++)
		{
			InputParameter inputParam = aprosService.listNewInputParams.get(i);
			//inputParamService.save(inputParam);
			strTest += inputParam.getName() + " ";
		}
		
		if (project == null)
		{
			return "error";
		}
		
		return "editproject";
	}	
}
