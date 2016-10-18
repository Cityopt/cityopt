package eu.cityopt.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.MetricValDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.ObjectiveFunctionResultDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.DTO.SimulationModelDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.repository.CustomQueryRepository;
import eu.cityopt.service.AlgorithmService;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.ComponentService;
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
import eu.cityopt.service.OptimizationSetService;
import eu.cityopt.service.OutputVariableService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioGeneratorService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.SearchOptimizationResults;
import eu.cityopt.service.SimulationModelService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.service.ScenarioGenerationService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.sim.service.TimeEstimatorService;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;
import eu.cityopt.sim.service.ScenarioGenerationJobInfo;
import eu.cityopt.web.ModelParamForm;
import eu.cityopt.web.OptimizationRun;
import eu.cityopt.web.Pair;
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
	    ObjectiveFunctionService objFuncService;

	    @Autowired
	    ScenarioGeneratorService scenGenService;

	    @Autowired
	    SimulationService simService;

	    @Autowired
	    TimeEstimatorService timeEstimatorService;

	    @Autowired
	    SimulationModelService simModelService;

	    @Autowired
	    ScenarioGenerationService scenarioGenerationService;

	    @Autowired
	    MetricService metricService;

	    @Autowired
	    MetricValService metricValService;

	    @Autowired
	    OutputVariableService outVarService;
	    
	    @Autowired  
	    private MessageSource messageSource;

	    @Autowired
	    private CustomQueryRepository customQueryRepository;

	    @Autowired
	    private AlgorithmService algorithmService;
	    
	    public String getMessage(String code, HttpServletRequest request) {
	    	Locale locale = RequestContextUtils.getLocale(request);
	        return messageSource.getMessage(code, null, locale);
	    }

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
	    public void getProjectExternalParameterValues(Map<String, Object> model, ProjectDTO project) {
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

	    public void getSelectedOutAndExtParameters(Map<String, Object> model, UserSession userSession) 
	    {
		    ArrayList<String> selectedParams = new ArrayList<String>();
			Iterator<Integer> iterator = userSession.getSelectedChartOutputVarIds().iterator();
	
			while (iterator.hasNext())
			{
				OutputVariableDTO outVar = null;
	
				try {
					outVar = outVarService.findByID(iterator.next());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				if (outVar != null)
				{
					selectedParams.add(outVar.getQualifiedName());
				}
			}
	
			iterator = userSession.getSelectedChartExtVarIds().iterator();
	
			while (iterator.hasNext())
			{
				ExtParamValDTO extParamVal = null;
	
				try {
					extParamVal = extParamValService.findByID(iterator.next());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				if (extParamVal != null)
				{
					selectedParams.add(extParamVal.getExtparam().getName());
				}
			}
	
			model.put("selectedParams", selectedParams);
		}

	    public void getSelectedScenariosAndMetrics(Map<String, Object> model, UserSession userSession) 
	    {
		    ArrayList<String> selectedParams = new ArrayList<String>();
			Iterator<Integer> iterator = userSession.getScenarioIds().iterator();
	
			while (iterator.hasNext())
			{
				ScenarioDTO scenario = null;
	
				try {
					scenario = scenarioService.findByID(iterator.next());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				if (scenario != null)
				{
					selectedParams.add(scenario.getName());
				}
			}
	
			iterator = userSession.getSelectedChartMetricIds().iterator();
	
			while (iterator.hasNext())
			{
				MetricDTO metric = null;
	
				try {
					metric = metricService.findByID(iterator.next());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				if (metric != null)
				{
					selectedParams.add(metric.getName());
				}
			}
	
			model.put("selectedParams", selectedParams);
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
	    
	    public void getEnergyModelInfo(Map<String, Object> model, int prjId, HttpServletRequest request) {
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
            
                SimulationModelDTO simModel = null;
			
                try {
					simModel = simModelService.findByID(nSimulationModelId);
				} catch (EntityNotFoundException e1) {
					e1.printStackTrace();
				}

                if (simModel != null)
                {
                	byte[] imageBlob = simModel.getImageblob();

                	if (imageBlob != null)
                	{
		    			String imgPath = request.getSession().getServletContext().getRealPath("/") + "assets\\img\\";
						String imgFileName = "simulationmodel_" + System.currentTimeMillis() + ".png";
						File file = new File(imgPath + imgFileName);
						System.out.println(file.getAbsolutePath());
		
		                FileOutputStream fos;
						try {
							fos = new FileOutputStream(file);
							fos.write(imageBlob);
			                fos.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
		         
		    	        UserSession session = (UserSession) model.get("usersession");
		    	        
		    	        if (session == null)
		    	        {
		    	        	session = new UserSession();
		    	        }
		            	model.put("usersession", session);

			            session.setSimModelFile(imgFileName);
                	}
            	}
            }
	    }
	    
	    public String getScenarioStatus(ScenarioDTO scenario) {
	    	String statusMsg = scenario.getStatus();

			if (simService.getRunningSimulations().containsKey(scenario.getScenid())) {
				statusMsg = "RUNNING";
			}
			
			return statusMsg;
	    }
	    
	    public void clearSession(Map<String, Object> model, HttpServletRequest request, boolean bKeepLanguage)
	    {
	    	String language = "";
	    	
	    	if (bKeepLanguage && model.get("usersession") != null)
	    	{
	    		UserSession session = (UserSession) model.get("usersession");
	    		language = session.getLanguage();
	    	}
	    	
	    	// Clear all except version
	        model.remove("project");
	        model.remove("scenario");
	        model.remove("optimizationset");
	        model.remove("scengenerator");
	        model.remove("optresults");
	        model.remove("usersession");
	        model.remove("user");

	        if (language != null && !language.isEmpty())
	        {
	        	UserSession session = new UserSession();
	        	session.setLanguage(language);
	        	model.put("usersession", session);
	        }
	        
	        // This resets the language setting also and causes problems when updating project info
	        if (request != null && request.getSession() != null)
	        {
	        	//request.getSession().invalidate();
	        	//request.getSession(true);
	        }
	    }

	    public void clearOptResults(Map<String, Object> model)
	    {
	    	SearchOptimizationResults emptyResults = new SearchOptimizationResults();
	        model.put("optresults", emptyResults);
	        
	        model.remove("bestScenarioWithValue");
	        model.remove("resultScenariosWithValue");
	        
	        UserSession session = (UserSession) model.get("usersession");
        	session.setOptResultString("");
        	model.put("usersession", session);
	    }
	    
	    public void updateGARuns(Map<String, Object> model) {
		    Map<Integer, ScenarioGenerationJobInfo> mapRuns =
		            scenarioGenerationService.getRunningOptimisations();
			List<OptimizationRun> listOptRuns = new ArrayList<OptimizationRun>();
			for (Map.Entry<Integer, ScenarioGenerationJobInfo> pair : mapRuns.entrySet()) {
			    ScenarioGenerationJobInfo runInfo = pair.getValue();
		        OptimizationRun optRun = new OptimizationRun();
		        optRun.setId(pair.getKey());
		        optRun.setStarted(runInfo.started.toString());
		        optRun.setEstimated(runInfo.estimatedCompletionTime.toString());
		        optRun.setDeadline(runInfo.deadline.toString());
		        optRun.setStatus(runInfo.formatEvaluationStatus());
		        
		        listOptRuns.add(optRun);
		    }
	
			model.put("optRuns", listOptRuns);
	    }
	    
	    public void getDefaultExtParamVals(Map<String, Object> model, int projectId)
	    {
	        List<ExtParamValDTO> extParamVals = null;
	
	        Integer defaultExtParamValSetId = projectService.getDefaultExtParamSetId(projectId);
	        
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
	    
	    public void initScenarioList(Map<String, Object> model, int projectId) {
			Set<ScenarioDTO> scenarios = projectService.getScenarios(projectId);
			Set<ScenarioForm> scenarioForms = new LinkedHashSet<ScenarioForm>();
			Set<Integer> paretoOptimal =
			        customQueryRepository.findParetoOptimalScenarios(projectId);
			
			Iterator<ScenarioDTO> iter = scenarios.iterator();
	    	while (iter.hasNext()) {
	    		ScenarioDTO scenario = (ScenarioDTO) iter.next();
	    		
	    		ScenarioForm scenarioForm = new ScenarioForm();
	    		scenarioForm.setName(scenario.getName());
	    		scenarioForm.setId(scenario.getScenid());
	    		scenarioForm.setDescription(scenario.getDescription());
	    		scenarioForm.setStatus(getScenarioStatus(scenario));
    			scenarioForm.setPareto(paretoOptimal.contains(scenario.getScenid()));
	    		
	    		scenarioForms.add(scenarioForm);
	    	}
			/*HashMap<Integer, Boolean> mapParetos = new HashMap<Integer, Boolean>();
			getParetoOptimal(scenarios, mapParetos);
			model.put("mapParetos", mapParetos);*/
			
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

			if (simService.getRunningSimulations().containsKey(scenario.getScenid())) {
				model.put("disableEdit", true);
			}
			else if (statusMsg != null && statusMsg.equals("SUCCESS"))
			{
				model.put("disableEdit", true);
			}
			
			model.put("status", statusMsg);
			
			if (scenario.getRunstart() != null 
				&& scenario.getRunend() != null)
			{
				String time = getSimulationTime(scenario);
				model.put("simulationEstimate", time);
			} else {
			    Duration d = timeEstimatorService.predictSimulationRuntime(projectId, scenarioId);
			    if (d != null) {
                    model.put("simulationEstimate", (int)(d.getSeconds() / 60 + 1) + " min");
			    }
			}
			
			List<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			
			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			
			// Get simulation start and end times
			while(iter.hasNext())
			{
				InputParamValDTO inputParamVal = iter.next();
				if (inputParamVal.getInputparameter().getComponentName().equals(Namespace.CONFIG_COMPONENT)) {
	                String inputName = inputParamVal.getInputparameter().getName();
    				if (inputName.equals(Namespace.CONFIG_SIMULATION_START))
    				{
    					model.put("simStart", inputParamVal.getValue());
    				}
    				else if (inputName.equals(Namespace.CONFIG_SIMULATION_END))
    				{
    					model.put("simEnd", inputParamVal.getValue());
    				}
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
	            
	            if (resultScenariosWithValue != null && resultScenariosWithValue.size() > 0)
	            {
	            	model.put("bestScenarioWithValue", resultScenariosWithValue.get(0));
	            }
	            
	            model.put("optresults", optResults);
	            
	            EvaluationResults evResults = optResults.getEvaluationResult();
	            
	            if (evResults != null)
	            {
	            	userSession.setOptResultString(evResults.toString());
	            }
	            
	            model.put("usersession", userSession);
            }
	        
	        model.put("usersession", userSession);

	        List<MetricValDTO> listMetricVals = new ArrayList<MetricValDTO>();

	        try {
				listMetricVals = metricService.getMetricValsByProject(projectId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
	        
	        model.put("metricVals", listMetricVals);
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
				model.put("extparamvalsetid", extParamValSetId);
				
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
	    
	    public String getGeneticAlgorithm(ProjectDTO project, ScenarioGeneratorDTO scenGen, Map<String, Object> model) {

	    	try {
				scenGen = scenGenService.findByID(scenGen.getScengenid());
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}

	    	model.put("scengenerator", scenGen);

	        ScenarioGenerationJobInfo runInfo = scenarioGenerationService.getRunningOptimisations().get(scenGen.getScengenid());
	        String strInfo = "";

	        if (runInfo != null) {
	        	strInfo += runInfo.toString();
	        }

	        String status = scenGen.getStatus();
	        boolean locked = false;

	        if ((runInfo != null && !runInfo.toString().isEmpty())
	    		|| (status != null && !status.isEmpty()))
	        {
	            if (status == null && runInfo != null) {
	                status = "RUNNING";
	            }
	        	strInfo += " Status: " + status;
	        	locked = true;
	        }

	    	model.put("runinfo", strInfo);
	        model.put("locked", locked);
	        
	        if (runInfo != null && runInfo.getEstimatedCompletionTime() != null) 
	        {
	        	Instant now = Instant.now();
	        	Instant compInstant = runInfo.getEstimatedCompletionTime();
	        	
	        	long totalSecs = Math.abs(Duration.between(compInstant, now).getSeconds());
	        	//System.out.println("Optimization completion time: " + totalSecs + " seconds");

	        	long hours = (long) Math.floor((double)(totalSecs / 3600));
	        	long minutes = (long)((totalSecs - hours * 3600) / 60);
	        	String time = "";
	        	
	        	if (hours >= 1) {
	        		time = hours + " h ";
	        	}

	        	if (minutes >= 1) {
	        		time += minutes + " min";
	        	}

	        	model.put("completionTime", time);
	        }
	        
	        UserSession userSession = getUserSession(model);

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
	            model.put("paramgrouping", scenGenService.getModelParameterGrouping(scenGen.getScengenid()));
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

	        if (scenGen.getAlgorithm().getAlgorithmid() == 1)
	        {
	        	return "gridsearch";
	        }
	        else
	        {
	        	return "geneticalgorithm";
	        }
	    }
	    
	    public String getEditSGModelParams(
	    		ProjectDTO project, ScenarioGeneratorDTO scenGen, ModelMap model,
	    		ModelParamForm form, ModelParameterGrouping grouping) {
	        UserSession userSession = getUserSession(model);
	    	try {
		        List<ComponentDTO> components = sortComponentsByName(
		                projectService.getComponents(project.getPrjid()));
		        List<ModelParameterDTO> modelParams = sortBy(mp -> mp.getInputparameter().getQualifiedName(),
		                scenGenService.getModelParameters(scenGen.getScengenid()));
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

	    public List<ComponentDTO> pickInputComponents(
	            List<ModelParameterDTO> modelParams, List<ComponentDTO> components) {
	        Set<Integer> componentIds = new HashSet<>();
	        for (ModelParameterDTO mp : modelParams) {
	            componentIds.add(mp.getInputparameter().getComponentComponentid());
	        }
	        return components.stream().filter(
	                c -> componentIds.contains(c.getComponentid()))
	                .collect(Collectors.toList());
	    }

	    public UserSession getUserSession(Map<String, Object> model) {
	        UserSession userSession = (UserSession) model.get("usersession");
	        if (userSession == null) {
	            userSession = new UserSession();
	            model.put("usersession", userSession);
	        }
	        return userSession;
	    }

	    public static <T> List<T> sortBy(Function<T, String> key, Collection<T> collection) {
	        List<T> list = new ArrayList<>(collection);
	        list.sort(Comparator.comparing(key,
	                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
	        return list;
	    }

	    /// Sorts components by name, leaving the special CITYOPT component last.
	    public List<ComponentDTO> sortComponentsByName(List<ComponentDTO> components) {
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
	    
	    public UnitDTO getDefaultUnit()
	    {
        	UnitDTO unit = new UnitDTO();
        	
        	try {
				unit = unitService.findByName(" ");
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
        	
        	return unit;
	    }
	    
	    public void getFunctions(Map<String, Object> model)
	    {
	    	ArrayList<Pair> functions = new ArrayList<Pair>();
	    	
	    	functions.add(new Pair("abs(int)", "Absolute value"));
	    	functions.add(new Pair("abs(float)", "Absolute value"));
	    	functions.add(new Pair("acos(float)", "Arc cosine in radians. Fails for arguments outside [-1, 1]."));
	    	functions.add(new Pair("all(iterable)", "Whether all elements of the iterable are true"));
	    	functions.add(new Pair("asin(float)", "Arc sine in radians.  Fails for arguments outside [-1, 1]."));
	    	functions.add(new Pair("atan(float)", "Arc tangent in radians"));
	    	functions.add(new Pair("atan2(float, float)", "Arc tangent in radians for given (y, x) coordinates"));
	    	functions.add(new Pair("bool(any)", "Boolean constructor: True for nonzero argument"));
	    	functions.add(new Pair("ceil(float)", "Ceiling function"));
	    	functions.add(new Pair("cos(float)", "Cosine"));
	    	functions.add(new Pair("cosh(float)", "Hyperbolic cosine"));
	    	functions.add(new Pair("datetime(int, int, int, …)", "datetime constructor from given year, month, day, hour, minutes, seconds. Omitted hours, minutes or seconds are interpreted as 0. Example: datetime(2014,3,31, 23,59,59)"));
	    	functions.add(new Pair("dict(…)", "Dictionary constructor"));
	    	functions.add(new Pair("enumerate(iterable)", "Pairs indices 0, 1, … with iterable elements. Returns a sequence of tuples (index, element)."));
	    	functions.add(new Pair("exp(float)", "Exponential function, equivalent to e**x"));
	    	functions.add(new Pair("float(any)", "Float constructor from numerical or string argument."));
	    	functions.add(new Pair("floor(float)", "Floor function"));
	    	functions.add(new Pair("hypot(float, float)", "Hypotenuse of right triangle, equals sqrt(x**2 + y**2)"));
	    	functions.add(new Pair("int(any)", "Integer constructor from numerical or string argument"));
	    	functions.add(new Pair("integrate(TimeSeries, float, float, float)", "Integration of time series over an interval"));
	    	functions.add(new Pair("len(iterable)", "Length of a sequence"));
	    	functions.add(new Pair("list(iterable)", "List constructor"));
	    	functions.add(new Pair("log(float)", "Natural logarithm"));
	    	functions.add(new Pair("log(float, float)", "Logarithm in the base given by the second argument"));
	    	functions.add(new Pair("map(function, iterable)", "Mapping of a sequence through a function"));
	    	functions.add(new Pair("max(TimeSeries)", "Maximum of a time series over its domain"));
	    	functions.add(new Pair("max(iterable)", "Maximum element of a sequence"));
	    	functions.add(new Pair("max(number, …)", "Maximum of the given arguments"));
	    	functions.add(new Pair("mean(TimeSeries)", "Mean value of time series over its domain"));
	    	functions.add(new Pair("mean(iterable)", "Mean value of a sequence"));
	    	functions.add(new Pair("min(TimeSeries)", "Minimum value of a time series over its domain"));
	    	functions.add(new Pair("min(iterable)", "Minimum element of a sequence"));
	    	functions.add(new Pair("min(number, …)", "Minimum of the given arguments"));
	    	functions.add(new Pair("pow(number, number)", "Power function.  Equivalent to x**y"));
	    	functions.add(new Pair("range(int)", "List from 0 up to argument–1"));
	    	functions.add(new Pair("reduce(function, iterable, any)", "Iterates a two-argument function over a sequence. Example: reduce(f, [1,2,3], -1) returns f(f(f(-1, 1), 2), 3). Example: reduce(f, [1,2,3]) returns f(f(1, 2), 3)."));
	    	functions.add(new Pair("reversed(iterable)", "Returns a sequence in reverse order"));
	    	functions.add(new Pair("round(float)", "Rounds a number to the closest integer"));
	    	functions.add(new Pair("round(float, int)", "Rounds a number to the given number of decimal places after the decimal point"));
	    	functions.add(new Pair("set(iterable)", "Set constructor"));
	    	functions.add(new Pair("sin(float)", "Sine"));
	    	functions.add(new Pair("sinh(float)", "Hyperbolic sine"));
	    	functions.add(new Pair("sorted(iterable)", "Returns a sequence in sorted order"));
	    	functions.add(new Pair("sqrt(float)", "Square root.  Fails for negative arguments"));
	    	functions.add(new Pair("stdev(TimeSeries)", "Standard deviation of a time series"));
	    	functions.add(new Pair("stdev(iterable)", "Sample standard deviation of a sequence"));
	    	functions.add(new Pair("str(any)", "String constructor"));
	    	functions.add(new Pair("sum(iterable)", "Sum of sequence elements"));
	    	functions.add(new Pair("tan(float)", "Tangent"));
	    	functions.add(new Pair("tanh(float)", "Hyperbolic tangent"));
	    	functions.add(new Pair("timedelta(float, float)", "timedelta constructor from number of days, and optionally seconds."));
	    	functions.add(new Pair("todatetime(float)", "Converts a simulation time value into a datetime object.  Inverse of tosimtime(datetime)."));
	    	functions.add(new Pair("tosimtime(datetime)", "Converts datetime object to a simulation time  value.  Inverse of todatetime(float)."));
	    	functions.add(new Pair("tosimtime(str)", "Converts ISO-8601 formatted string such as “2015-06-19T21:30:00” to a simulation time  value. "));
	    	functions.add(new Pair("tuple(any, …)", "Tuple constructor"));
	    	functions.add(new Pair("var(TimeSeries)", "Variance of a time series"));
	    	functions.add(new Pair("var(iterable)", "Sample variance of a sequence"));
	    	functions.add(new Pair("xrange(int)", "Sequence from 0 up to argument – 1."));
	    	functions.add(new Pair("zip(iterable, …)", "Combines given iterables into one iterable of tuples. Example: zip([1,2,3], [9,8,7]) returns [(1,9), (2,8), (3,7)]"));
	    	model.put("functions", functions);
	    }
	
	    public boolean isParetoOptimal(int nScenId, int nScenGenId)
	    {
	    	List<ObjectiveFunctionDTO> objFuncs = null;
			
			try {
				objFuncs = scenGenService.getObjectiveFunctions(nScenGenId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}

			if (objFuncs.size() == 0)
			{
				return false;
			}
			
		    ObjectiveFunctionDTO objFuncFirst = objFuncs.get(0);
	
			ArrayList<ObjectiveFunctionResultDTO> listResults = (ArrayList<ObjectiveFunctionResultDTO>) objFuncService.findResultsByScenarioGenerator(nScenGenId, objFuncFirst.getObtfunctionid());
			Iterator<ObjectiveFunctionResultDTO> resultIter = listResults.iterator();
			
			while (resultIter.hasNext())
			{
				ObjectiveFunctionResultDTO result = (ObjectiveFunctionResultDTO) resultIter.next();
				
				if (result.getScenID() == nScenId && result.isScengenresultParetooptimal())
				{
					return true;
				}
			}	
			return false;
	    }
	    
	    public String getSimulationTime(ScenarioDTO scenario)
	    {
			long simSeconds = (long) (scenario.getRunend().getTime() - scenario.getRunstart().getTime()) / 1000;
			long simMinutes = (long)(simSeconds / 60 + 1);
			
			String strTime = simMinutes + " min";
			return strTime;
	    }
	}

