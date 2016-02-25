package eu.cityopt.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValService;
import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;
import eu.cityopt.service.MetricValService;
import eu.cityopt.service.OptimizationSetService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.SearchOptimizationResults;
import eu.cityopt.service.SimulationModelService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.service.ScenarioGenerationService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;
import eu.cityopt.sim.service.ScenarioGenerationService.RunInfo;
import eu.cityopt.web.OptimizationRun;
import eu.cityopt.web.ParamForm;
import eu.cityopt.web.ScenarioForm;
import eu.cityopt.web.UserSession;


@Controller
@SessionAttributes({
    "project", "scenario", "optimizationset", "scengenerator", "optresults",
    "usersession", "user", "version"})
public class ControllerService {
	
	 	@Autowired
	    ProjectService projectService; 
	 	
	 	@Autowired
	 	OptimizationSetService optSetService;
	
	 	@Autowired
	    ScenarioService scenarioService; 
	
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
	    UnitService unitService;
	    
	    @Autowired
	    TypeService typeService;
		
	    @Autowired
	    AppUserService userService;

	    @Autowired
	    SimulationService simService;

	    @Autowired
	    SimulationModelService simModelService;

	    @Autowired
	    ScenarioGenerationService scenarioGenerationService;

	    @Autowired
	    MetricValService metricValService;
	    
		// Finds project By model and project id;
	     
	    public AppUserDTO FindAuthenticatedUser(Authentication authentication) throws Exception{
			
			String authenticatedUserName = authentication.getName();			
			AppUserDTO appuserdto;
			try {
				appuserdto = userService.findByName(authenticatedUserName);
			} catch (EntityNotFoundException e) {
				throw new Exception("User dosen't exist in database or being authorized");			
			}
			return appuserdto;
		}
	    	    
	   
	    public static ProjectDTO GetProject(Map<String,Object> model){    	
	    	ProjectDTO project =(ProjectDTO) model.get("project");	    	  	
	    	return project;
	    }
	    
	    public void updateProject(Map<String,Object> model, ProjectDTO project) {    	
	        
	        try {
				project = projectService.findByID(project.getPrjid());
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
	        model.put("project", project);
	    }
	    
	    // Check if any object is null
	    public boolean NullCheck(Object obj){
	    	if (obj==null){
	    		return true;
	    	}
	    	else{
	    		return false;
	    	}
	    }
	    
	    //Set up SelectedComponent
	    public void SetUpSelectedComponent(Map<String,Object> model, String selectedCompId) {
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
	             List<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
	             model.put("inputParameters", inputParams);
	         }
	    }
	    
	    // Set up the project External Parameter values.
	    public void getProjectExternalParameterValues(Map<String,Object> model, ProjectDTO project) {
	    	 List<ExtParamValDTO> extParamVals = null;
	         Integer defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
	         
	         if (defaultExtParamValSetId != null)
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
	    }
	    
	    // Set up component and External parameter Values according to project and model attributes.  
	    public void getComponentAndExternalParamValues(Map<String,Object> model, ProjectDTO project ){
	    	List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
	        model.put("components", components);
	        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
	        model.put("extParams", extParams);
	    }   
	    
	    // Finds an External Parameter Value DTO object with it's id.
	    public ExtParamValSetDTO FindExternalParameterValSetByID(int id){
	    	ExtParamValSetDTO selectedExtParamValSet = null;
	    	try {
				selectedExtParamValSet = extParamValSetService.findByID(id);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return selectedExtParamValSet;
	    }
	    
	    // Finds an ExternalParameter id with String formatted data.
	    public int ParseExternalParameterValIDFromString(String selectedExtParamSetId){  	
	    
		    int nSelectedExtParamSetId = Integer.parseInt(selectedExtParamSetId);
		    return nSelectedExtParamSetId;		            
	    }    	
	    	
	    // Find External Parameter Value Set by it's id.
	    public List<ExtParamValDTO> FindExtParamVals(int nSelectedExtParamSetId)
	    {
		    List<ExtParamValDTO> extParamVals = null;
	        try {
	            extParamVals = extParamValSetService.getExtParamVals(nSelectedExtParamSetId);
	        } catch (EntityNotFoundException e) {
	            e.printStackTrace();
	        }
	        return extParamVals;
	    }
	    
	    // finds External Parameter Value set DTO by it's raw String id.	    
	    public ExtParamValSetDTO ParseExternalParameterValSet(String selectedExtParamSetId){
	    	
	    	ExtParamValSetDTO selectedExtParamValSet = null;
	        if (selectedExtParamSetId != null)
	        {
	            int nSelectedExtParamSetId = Integer.parseInt(selectedExtParamSetId);
	            try {
	                selectedExtParamValSet = extParamValSetService.findByID(nSelectedExtParamSetId);
	            } catch (EntityNotFoundException e) {
	                e.printStackTrace();
	            }
	            return selectedExtParamValSet;	    	
	        }
	        return selectedExtParamValSet;
	    }
	    
	    // finds Input Parameter DTO by it's raw input String id.
	    public InputParameterDTO ParseInputParameterFormStringID(String inputid){
	    	int nInputId = Integer.parseInt(inputid);
	        InputParameterDTO inputParam = null;
	        try {
	            inputParam = inputParamService.findByID(nInputId);
	        } catch (EntityNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return inputParam;
	    }

	    // finds component by it's raw input String id.
	    public ComponentDTO FindComponentByRawID(String componentid){
	    	
	    	int nCompId = Integer.parseInt(componentid);
	    	 ComponentDTO component = null;
	         try {
	             component = componentService.findByID(nCompId);
	         } catch (EntityNotFoundException e) {
	             e.printStackTrace();
	         }
			return component;
	    }
	    
	    public int ParseComponentID(String componentid){
	    	int nCompId = Integer.parseInt(componentid);
	    	return nCompId;
	    }
	    
	    public ComponentDTO FindComponentByID(int nCompId){
	    	ComponentDTO component = null;
	         try {
	             component = componentService.findByID(nCompId);
	         } catch (EntityNotFoundException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	         }
			return component;
	    }
	  
	    public AppUserDTO FindUserbyUserID(int userID){
	    	AppUserDTO user = null;
            try {
                user = userService.findByID(userID);
            } catch (EntityNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
			return user;
	    	
	    }
	    
	    public ExtParamValSetDTO SetExtParamValsetName(ExtParamValSetDTO extParamValSet, ProjectDTO project){
	    	String newName = extParamValSet.getName();
	        try {
	            extParamValSet = extParamValSetService.findByID(projectService.getDefaultExtParamSetId(project.getPrjid()));
	        } catch (EntityNotFoundException e2) {
	            e2.printStackTrace();
	        }
	        extParamValSet.setName(newName);
	       return extParamValSet;
	    }
	    
	    public void SetupModelUnits(Map<String,Object> model){	    	
	    	List<UnitDTO> Units = unitService.findAll();
	    	model.put("units", Units);	    	
	    }
	    
	    public void getEnergyModelInfo(Map<String, Object> model, int prjId) {
    		Integer nSimulationModelId = projectService.getSimulationmodelId(prjId);
            
            if (nSimulationModelId != null)
            {
            	model.put("showInfo", true);
            	String description = "";
            	
				try {
					description = simModelService.findByID(nSimulationModelId).getDescription();
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
                
				if (description != null)
				{
					int index = description.indexOf(Character.LINE_SEPARATOR);
					
					if (index > 0)
					{
						String modelName = description.substring(0, index);
			            model.put("loadedEnergyModel", modelName);
					}
				}
				
				model.put("title", "Energy model description");
                model.put("infotext", description);
            }
	    }
	    
	    public String getScenarioStatus(ScenarioDTO scenario) {
	    	String statusMsg = scenario.getStatus();

			if (simService.getRunningSimulations().contains(scenario.getScenid())) {
				statusMsg = "RUNNING";
			}
			
			return statusMsg;
	    }
	    
	    public void clearSession(Map<String, Object> model, HttpServletRequest request)
	    {
	    	// Clear all except version
	        model.remove("project");
	        model.remove("scenario");
	        model.remove("optimizationset");
	        model.remove("scengenerator");
	        model.remove("optresults");
	        model.remove("usersession");
	        model.remove("user");
	        	        
	        // This resets the language setting also and causes problems when updating project info
	        if (request != null && request.getSession() != null)
	        {
	        	//request.getSession().invalidate();
	        	//request.getSession(true);
	        }
	    }

	    public void updateGARuns(Map<String, Object> model) {
		    Map<Integer, RunInfo> mapRuns = scenarioGenerationService.getRunningOptimisations();
			List<OptimizationRun> listOptRuns = new ArrayList<OptimizationRun>();
			Iterator iter = mapRuns.entrySet().iterator();
	        
			while (iter.hasNext()) {
		        Map.Entry pair = (Map.Entry) iter.next();
		        RunInfo runInfo = new RunInfo(); 
		        runInfo = (RunInfo) pair.getValue();

		        OptimizationRun optRun = new OptimizationRun();
		        optRun.setId((int)pair.getKey());
		        optRun.setStarted(runInfo.getStarted());
		        optRun.setDeadline(runInfo.getDeadline());
		        optRun.setStatus(runInfo.getStatus());
		        
		        listOptRuns.add(optRun);
		    }
	
			model.put("optRuns", listOptRuns);
	    }
	    
	    public void getProjectMetricVals(Map<String, Object> model, int projectId) {
			
	    	List<MetricValDTO> listMetricVals = metricValService.findAll();
	    	List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();

	        for (int i = 0; i < listMetricVals.size(); i++)
	        {
	            MetricValDTO metricVal = listMetricVals.get(i);
	
	            if (metricVal.getMetric().getProject().getPrjid() == projectId)
	            {
	                listProjectMetricVals.add(metricVal);
	            }
	        }
	        model.put("metricVals", listProjectMetricVals);
	    }

	    public void initScenarioList(Map<String, Object> model, int projectId) {
			Set<ScenarioDTO> scenarios = projectService.getScenarios(projectId);
			Set<ScenarioForm> scenarioForms = new HashSet<ScenarioForm>();
			
			Iterator<ScenarioDTO> iter = scenarios.iterator();
	    	
	    	while (iter.hasNext()) {
	    		ScenarioDTO scenario = (ScenarioDTO) iter.next();
	    		
	    		ScenarioForm scenarioForm = new ScenarioForm();
	    		scenarioForm.setName(scenario.getName());
	    		scenarioForm.setId(scenario.getScenid());
	    		scenarioForm.setDescription(scenario.getDescription());
	    		scenarioForm.setStatus(getScenarioStatus(scenario));
	    		scenarioForms.add(scenarioForm);
	    	}
	    	
	    	model.put("scenarioForms", scenarioForms);
	    }
	    
	    public void initEditScenario(Map<String, Object> model, int projectId, int scenarioId) {
	    	ScenarioDTO scenario = null;
			
			try {
				scenario = scenarioService.findByID(scenarioId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			UserSession session = (UserSession) model.get("usersession");
			session.setActiveScenario(scenario.getName());
			model.put("usersession", session);
			
			String statusMsg = getScenarioStatus(scenario);

			if (simService.getRunningSimulations().contains(scenario.getScenid())) {
				model.put("disableEdit", true);
			}
			else if (statusMsg != null && statusMsg.equals("SUCCESS"))
			{
				model.put("disableEdit", true);
			}
			
			model.put("status", statusMsg);
			
			List<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			
			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			
			// Get simulation start and end times
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
		
	    	List<ComponentDTO> components = projectService.getComponents(projectId);
	    	model.put("components", components);
		
	    	model.put("inputParamVals", inputParamVals);
	    }
	    
	    public void initEditOptSet(Map<String, Object> model, int projectId, int optSetId) 
	    {
		    List<OptConstraintDTO> optSearchConstraints = null;
	
	        try {
	            optSearchConstraints = optSetService.getOptConstraints(optSetId);
	        } catch (EntityNotFoundException e) {
	            e.printStackTrace();
	        }
	        model.put("constraints", optSearchConstraints);
	
	        SearchOptimizationResults optResults = (SearchOptimizationResults) model.get("optresults");
	        UserSession userSession = (UserSession) model.get("usersession");
	        
	        if (userSession == null) {
	        	userSession = new UserSession();
	        }
	        
	        if (optResults != null)
	        {
	            List<ScenarioWithObjFuncValueDTO> resultScenariosWithValue = (List<ScenarioWithObjFuncValueDTO>) optResults.resultScenarios;
	            model.put("resultScenariosWithValue", resultScenariosWithValue);
	            model.put("optresults", optResults);
	            
	            EvaluationResults evResults = optResults.getEvaluationResult();
	            userSession.setOptResultString(evResults.toString());
	            model.put("usersession", userSession);
            }
	        
	        List<MetricValDTO> listMetricVals = metricValService.findAll();
	        List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();
	
	        for (int i = 0; i < listMetricVals.size(); i++)
	        {
	            MetricValDTO metricVal = listMetricVals.get(i);
	            if (metricVal.getMetric().getProject().getPrjid() == projectId)
	            {
	                listProjectMetricVals.add(metricVal);
	            }
	        }
	        model.put("metricVals", listProjectMetricVals);
	    }
	    
	    public void initEditObjFunc(Map<String, Object> model, int projectId, String selectedCompId, UserSession userSession) 
	    {
	    	List<ComponentDTO> components = projectService.getComponents(projectId);
	        model.put("components", components);
	
	        if (selectedCompId != null && !selectedCompId.isEmpty())
	        {
	            int nSelectedCompId = Integer.parseInt(selectedCompId);
	
	            if (nSelectedCompId > 0)
	            {
	            	if (userSession != null)
	            	{
	            		userSession.setComponentId(nSelectedCompId);
	            	}
	                List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
	                model.put("outputVars", outputVars);
	            }
	            model.put("selectedcompid", nSelectedCompId);
	        }
	
	        Set<MetricDTO> metrics = projectService.getMetrics(projectId);
	        model.put("metrics", metrics);
	    }	
	    
	    public void initExtParamSets(Map<String, Object> model, String strExtParamSetId, int projectId) 
	    {
	    	List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(projectId);
			model.put("extParamValSets", extParamValSets);
			Integer extParamValSetId = projectService.getDefaultExtParamSetId(projectId);
			
			if (strExtParamSetId != null) {
				model.put("extparamvalsetid", strExtParamSetId);
				extParamValSetId = Integer.parseInt(strExtParamSetId);
			}
			
			if (extParamValSetId != null) {
				List<ExtParamValDTO> extParamVals = null;
				try {
					extParamVals = extParamValSetService.getExtParamVals(extParamValSetId);
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				model.put("extParamVals", extParamVals);
			}
			
			model.put("postpage", "extparamsets.html");
			model.put("backpage", "editoptimizationset.html");
		}
	    
	    public void initUpdateMetric(Map<String, Object> model, int projectId) 
	    {	    
		    UserSession userSession = (UserSession) model.get("usersession");
	
	        if (userSession == null)
	        {
	            userSession = new UserSession();
	        }
	
	        model.put("usersession", userSession);
	
	        MetricDTO metric = new MetricDTO();
	        metric.setExpression(userSession.getExpression());
	        
	        ParamForm paramForm = new ParamForm();
	        paramForm.setName(metric.getName());
	        paramForm.setValue(metric.getExpression());
	        
	        if (metric.getUnit() != null) {
	        	paramForm.setUnit(metric.getUnit().getName());
	        }
	        
	        model.put("paramForm", paramForm);
	        model.put("action", "create");
	
	        List<UnitDTO> units = unitService.findAll();
	        model.put("units", units);
	    }
	    
	    public void changeLanguage(Map<String, Object> model, String language)
	    {
	    	UserSession session = (UserSession) model.get("usersession");
	    	if (session == null)
	    	{
	    		session = new UserSession();
	    	}
	    	session.setLanguage(language);
	    	model.put("usersession", session);

	    }
	}

