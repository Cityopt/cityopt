package eu.cityopt.controller;

import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import eu.cityopt.DTO.AlgorithmDTO;
import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.ModelParameterDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.repository.ProjectRepository;
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
import eu.cityopt.service.ImportService;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;
import eu.cityopt.service.MetricService;
import eu.cityopt.service.MetricValService;
import eu.cityopt.service.ModelParameterGrouping;
import eu.cityopt.service.ModelParameterService;
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
import eu.cityopt.service.impl.ImportServiceImpl;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.ScenarioGenerationService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.ModelParamForm;
import eu.cityopt.web.ScenarioParamForm;
import eu.cityopt.web.UserSession;

/**
 * @author Olli Stenlund
 *
 */
@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession", "user"})
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
	ScenarioGenerationService scenGenSimService;

	@Autowired
	DecisionVariableService decisionVarService;
	
	@Autowired
	DatabaseSearchOptimizationService dbOptService;

	@Autowired
	ImportExportService importExportService;
	
	@Autowired
	ImportServiceImpl importService;
	
	@Autowired
	AlgorithmService algorithmService;

	@Autowired
	ModelParameterService modelParamService;
	
	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String getCreateScenario(Map<String, Object> model) {
		
		AppUserDTO user = (AppUserDTO) model.get("user");
		ProjectDTO project = (ProjectDTO) model.get("project");

		// TODO
		if (user != null && project != null)
		{
			//if (hasStandardRights(user.getUserid()))
			{
			
			}
		}
		
		ScenarioDTO scenario = new ScenarioDTO();
		model.put("newScenario", scenario);
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
			
			//Fix #10457 By:Markus Turunen Checking no other entries. Too wide			
			//List<ScenarioDTO> elements = scenarioService.findByNameContaining(formScenario.getName());
			
			if (scenarioService.findByName(formScenario.getName())==null){			
				scenario.setName(formScenario.getName().trim());
				scenario.setDescription(formScenario.getDescription().trim());
				scenario.getScenid();
				model.put("successful", "Scenario succesfully created.");
				List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
				model.put("components", components);
				try {
					scenario = scenarioService.saveWithDefaultInputValues(scenario, project.getPrjid());
				} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.put("scenario",  scenario);
			} else {
				model.put("newScenario", formScenario);
				model.put("errorMessage", "This Scenario already exists, please create another name.");
				return "createscenario";				
			}			
				
				
			//Bugfix #10543 author: Markus Turunen Check if energy models exist
			if(scenarioService.getInputParamVals(scenario.getScenid()) != null){
				List<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
				model.put("inputParamVals", inputParamVals);
	//			Iterator<InputParamValDTO> iter = inputParamVals.iterator();			
				InputParamValDTO simStart = inputParamValService.findByNameAndScenario("simulation_start", scenario.getScenid());
				InputParamValDTO simEnd = inputParamValService.findByNameAndScenario("simulation_end", scenario.getScenid());			
				
				if(simStart != null && simEnd != null){				
					model.put("simStart", simStart.getValue());
					model.put("simEnd", simEnd.getValue());//						
					
					UserSession userSession = (UserSession) model.get("usersession");
					
					if (userSession == null) {
						userSession = new UserSession();
					}
					
					model.put("usersession", userSession);
					model.put("Succesful", "Scenario succesfully created");
					model.put("newScenario", scenario);
					model.put("success",true);
					return "createscenario";				
				} else {
					model.put("newScenario", formScenario);				
					model.put("errorMessage", "Project lack simulation definition, please define them.");
					return "createscenario";
				}			
			}else{	
				model.put("newScenario", formScenario);				
				model.put("errorMessage", "Project lack input parameter values can't create a scenario,  please define them.");
				return "createscenario";
			}
		}
		else
			{
			model.put("newScenario", formScenario);				
			model.put("errorMessage", "There's no project selected please select one.");
			return "createscenario";
			//project null
			//return "error";
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
			
			String statusMsg = scenario.getStatus();

			if (simService.getRunningSimulations().contains(scenario.getScenid())) {
				statusMsg = "RUNNING";
			}
			
			model.put("status", statusMsg);
			
			model.put("scenario", scenario);
			List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
			model.put("components", components);
			List<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
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

	@RequestMapping(value = "createmultiscenario", method=RequestMethod.GET)
    public String getCreateMultiScenario(Map<String, Object> model) {
        
		ScenarioGeneratorDTO scenGen = new ScenarioGeneratorDTO();
        model.put("multiscenario", scenGen);

        return "createmultiscenario";
    }

    @RequestMapping(value = "createmultiscenario", method = RequestMethod.POST)
    public String getCreateMultiScenarioPost(Map<String, Object> model,
            @Validated @ModelAttribute("multiscenario") ScenarioGeneratorDTO newScenGen, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "createmultiscenario";
        } else {
            List<AlgorithmDTO> algorithms = (List<AlgorithmDTO>) algorithmService.findAll();
            ProjectDTO project = (ProjectDTO) model.get("project");
    		
    		if (project == null)
    		{
    			return "error";
    		}

        	ScenarioGeneratorDTO scenGen = scenGenService.create(project.getPrjid(), newScenGen.getName().trim());

            // TODO write something smarter?
            scenGen.setAlgorithm(algorithms.get(0));

            int nDefaultExtParamSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
            ExtParamValSetDTO extParamSet = null;
			
            try {
				extParamSet = extParamValSetService.findByID(nDefaultExtParamSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
            scenGen.setExtparamvalset(extParamSet);

            scenGen = scenGenService.save(scenGen);
            
            /*String expression = "";
            ModelParameterDTO modelParam = new ModelParameterDTO();
            modelParam.setExpression(expression);*/
            
            if (true) {//projectService.findByName(project.getName()) == null) {
                
                model.put("success", true);   
                
                List<ScenarioGeneratorDTO> listScenGens = scenGenService.findAll();
        		model.put("scenGens", listScenGens);
        		
                return "setmultiscenario";
            } else {
                model.put("success",false);                           
                return "createmultiscenario";
            }
        }
    }
    
    @RequestMapping(value="deletemultiscenario",method=RequestMethod.GET)
	public String getDeleteMultiScenario(Map<String, Object> model, 
		@RequestParam(value="multiscenarioid", required=true) String multiScenarioId){

		if (!model.containsKey("project"))
		{
			return "error";
		}

		if (multiScenarioId != null)
		{
			ScenarioGeneratorDTO scenGen = null;
			int nDeleteMultiScenarioId = Integer.parseInt(multiScenarioId);
	
			try {
				scenGen = scenGenService.findByID(nDeleteMultiScenarioId);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (scenGen != null)
			{
				try {
					scenGenService.delete(scenGen.getScengenid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				} catch(ObjectOptimisticLockingFailureException e) {
					model.put("errorMessage", "This multi scenario has been updated in the meantime, please reload.");
				}
			}
		}

		List<ScenarioGeneratorDTO> listScenGens = scenGenService.findAll();
		model.put("scenGens", listScenGens);
		
		return "setmultiscenario";
	}

    @RequestMapping(value="editmultivariable", method=RequestMethod.GET)
    public String getCreateMultiVariable(Map<String, Object> model,
    	@RequestParam(value="multiscenarioid", required=true) String multiScenarioId,
    	@RequestParam(value="multivariableid", required=true) String multiVariableId) {

    	int nMultiVariableId = Integer.parseInt(multiVariableId);

    	ModelParameterDTO modelParameter = new ModelParameterDTO();
    	model.put("multivariable", modelParameter);

    	ModelParameterDTO modelParam = null;
    	
    	try {
			 modelParam = modelParamService.findByID(nMultiVariableId);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
    	
    	model.put("multivariable", modelParam);
    	
    	UserSession userSession = (UserSession) model.get("usersession");
		
		if (userSession == null) {
			userSession = new UserSession();
		}
		
		userSession.setMultiScenarioId(multiScenarioId);
		userSession.setMultiVariableId(multiVariableId);
		
		model.put("usersession", userSession);
		model.put("multiscenarioid", multiScenarioId);
		
        AppUserDTO user = (AppUserDTO) model.get("user");
        model.put("user", user);

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
        
        return "editmultivariable";
    }

    @RequestMapping(value = "editmultivariable", method = RequestMethod.POST)
    public String getCreateMultiVariablePost(Map<String, Object> model,
        @Validated @ModelAttribute("multivariable") ModelParameterDTO newModelParameter,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "editmultivariable";
        } else {
        	UserSession userSession = (UserSession) model.get("usersession");
        	int nMultiScenarioId = 0;
        	
    		if (userSession != null) {
    			nMultiScenarioId = Integer.parseInt(userSession.getMultiScenarioId());
    		} else {
    			return "error";
    		}
    		
    		int nMultiVariableId = Integer.parseInt(userSession.getMultiVariableId());
    		model.put("usersession", userSession);

        	ModelParameterDTO modelParameter = null;
        	
			try {
				modelParameter = modelParamService.findByID(nMultiVariableId);
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
        	
			modelParameter.setExpression(newModelParameter.getExpression());

			try {
				modelParamService.update(modelParameter);
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
        	/*ScenarioGeneratorDTO scenGen = null;

        	try {
				scenGen = scenGenService.findByID(nMultiScenarioId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
        	
        	List<ModelParameterDTO> modelParams = null;
			
        	try {
				modelParams = scenGenService.getModelParameters(nMultiScenarioId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}

        	Iterator<ModelParameterDTO> iter = modelParams.iterator();
        	
        	while (iter.hasNext()) {
        		ModelParameterDTO modelParam = (ModelParameterDTO) iter.next();
        		
        		if (modelParam.getModelparamid() == newModelParameter.getModelparamid())
        		{
        			modelParam.setExpression(newModelParameter.getExpression());
        			break;
        		}
        	}
        	
        	try {
				scenGenService.setModelParameters(scenGen.getScengenid(), modelParams);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}*/
        	
        	//scenGen = scenGenService.save(scenGen);
			
        	if (true) {
        		List<ScenarioGeneratorDTO> listScenGens = scenGenService.findAll();
        		model.put("scenGens", listScenGens);
        		
                return "setmultiscenario";
            } else {
                model.put("success",false);                           
                return "createmultivariable";
            }
        }
    }
    
    /*@RequestMapping(value="deletemultivariable",method=RequestMethod.GET)
	public String getDeleteMultiVariable(Map<String, Object> model, 
		@RequestParam(value="multiscenarioid", required=true) String multiScenarioId,
		@RequestParam(value="multivariableid", required=true) String multiVariableId) {

		if (!model.containsKey("project"))
		{
			return "error";
		}

		if (multiVariableId != null)
		{
			ModelParameterDTO modelParam = null;
			int nMultiScenarioId = Integer.parseInt(multiScenarioId);
			int nDeleteMultiVarId = Integer.parseInt(multiVariableId);
			
			List<ModelParameterDTO> modelParams = null;
			
        	try {
				modelParams = scenGenService.getModelParameters(nMultiScenarioId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
        	
        	modelParams.remove(arg0).add(modelParameter);
        	
        	try {
				scenGenService.setModelParameters(scenGen.getScengenid(), modelParams);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
        	
        	
        	
        	
			try {
				modelParam = scenGenService.findByID(nDeleteMultiVarId);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (scenGen != null)
			{
				try {
					scenGenService.delete(scenGen.getScengenid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				} catch(ObjectOptimisticLockingFailureException e) {
					model.put("errorMessage", "This multi scenario has been updated in the meantime, please reload.");
				}
			}
		}

		List<ScenarioGeneratorDTO> listScenGens = scenGenService.findAll();
		model.put("scenGens", listScenGens);
		
		return "setmultiscenario";
	}*/
    
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
				List<ScenarioDTO> list =scenarioService.findByNameContaining(name);	
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
			
			String statusMsg = scenario.getStatus();

			if (simService.getRunningSimulations().contains(scenario.getScenid())) {
				statusMsg = "RUNNING";
			}
			
			model.put("status", statusMsg);
			
			List<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
			Iterator<InputParamValDTO> iter = inputParamVals.iterator();
			
			while(iter.hasNext())
			{
				InputParamValDTO inputParamVal = iter.next();
				String inputName = inputParamVal.getInputparameter().getName();
				
				if (inputName.equals("simulation_start"))
				{
					model.put("simStart", inputParamVal.getValue().trim());
				}
				else if (inputName.equals("simulation_end"))
				{
					model.put("simEnd", inputParamVal.getValue().trim());
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
			model.put("newScenario", scenario);
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
			
			scenario.setName(formScenario.getName().trim());
			scenario.setDescription(formScenario.getDescription().trim());
			
			try {
				scenario = scenarioService.save(scenario, project.getPrjid());
			} catch(ObjectOptimisticLockingFailureException e) {
				model.put("errorMessage", "This scenario has been updated in the meantime, please reload.");
			}
			
			List<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
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
	
	@RequestMapping(value = "importscenarios", method = RequestMethod.POST)
	public String uploadCSVFileHandler(Map<String, Object> model, 
		@RequestParam("file") MultipartFile file,
		@RequestParam("timeSeriesFile") MultipartFile timeSeriesMPFile) {
	
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
				
				TempDir dir = new TempDir("temp");

				File scenarioFile = new File("temp_scenario");
				byte[] bytes = file.getBytes();
				Files.write(bytes, scenarioFile);
				
				File timeSeriesFile = new File("temp_timeseries");
				byte[] timeSeriesBytes = timeSeriesMPFile.getBytes();
				Files.write(timeSeriesBytes, timeSeriesFile);
				
				List<File> listTSFiles = new ArrayList<File>();
				listTSFiles.add(timeSeriesFile);
				
				importService.importScenarioData(project.getPrjid(), scenarioFile, listTSFiles);
	        } catch (Exception e) {
	            e.printStackTrace();
	        	return "You failed to upload => " + e.getMessage();
	        }
	    } else {
	    }
		return "importdata";
	}

	@RequestMapping(value = "exportscenarios", method = RequestMethod.GET)
	public String exportScenarios(Map<String, Object> model) {
		return "importdata";
	}
	
	@RequestMapping(value = "exportscenarios", method = RequestMethod.POST)
	public String exportScenariosPost(Map<String, Object> model) {
	
	    /*try {
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
			
			TempDir dir = new TempDir("temp");

			File scenarioFile = new File("temp_scenario");
			byte[] bytes = file.getBytes();
			Files.write(bytes, scenarioFile);
			
			File timeSeriesFile = new File("temp_timeseries");
			byte[] timeSeriesBytes = timeSeriesMPFile.getBytes();
			Files.write(timeSeriesBytes, timeSeriesFile);
			
			List<File> listTSFiles = new ArrayList<File>();
			listTSFiles.add(timeSeriesFile);
			
			//importService..importScenarioData(project.getPrjid(), scenarioFile, listTSFiles);
        } catch (Exception e) {
            e.printStackTrace();
        	return "You failed to upload => " + e.getMessage();
        }*/

	    return "importdata";
	}

	@RequestMapping(value="setmultiscenario", method=RequestMethod.GET)
	public String getSetMultiScenario (Map<String, Object> model,
		@RequestParam(value="multiscenarioid", required=false) String scenGenId) {
		if (!model.containsKey("project"))
		{
			return "error";
		}

		List<ScenarioGeneratorDTO> listScenGens = scenGenService.findAll();
		model.put("scenGens", listScenGens);
		
		if (scenGenId != null && !scenGenId.isEmpty())
		{
			int nScenGenId = Integer.parseInt(scenGenId);
			model.put("multiscenarioid", scenGenId);
			ScenarioGeneratorDTO scenGen = null;
			try {
				scenGen = scenGenService.findByID(nScenGenId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}

			model.put("modelparameters", scenGen.getModelparameters());
		}
		
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
		
		List<InputParamValDTO> inputParamVals = scenarioService.getInputParamVals(scenario.getScenid());
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
	
		if (scenarioid != null)
		{
			ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
			
			ScenarioDTO tempScenario = null;
			int nDeleteScenarioId = Integer.parseInt(scenarioid);
	
			if (scenario != null && scenario.getScenid() == nDeleteScenarioId)
			{
				// Active scenario is to be deleted
				model.remove("scenario");
			}
				
			try {
				tempScenario = scenarioService.findByID(nDeleteScenarioId);
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

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		Set<ScenarioDTO> scenarios = (Set<ScenarioDTO>) projectService.getScenarios(project.getPrjid());
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
			
			
			List<InputParamValDTO> inputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, scenario.getScenid());
			ScenarioParamForm form = new ScenarioParamForm();
			 
	            for (InputParamValDTO InputParamValue : inputParamVals) {
	                int inputId = InputParamValue.getInputparamvalid();	               
	                String value = ""; 
	                
	                value = InputParamValue.getValue();
	                
	                form.getValueByInputId().put(inputId, value);
	            }        
	            
	      
	         
	      
	        model.put("scenarioParamForm", form);			
			model.put("selectedcompid", selectedCompId);
			model.put("selectedComponent",  selectedComponent);

			
			//List<ComponentInputParamDTO> componentInputParamVals = componentInputParamService.findAllByComponentId(nSelectedCompId);
			model.put("inputParamVals", inputParamVals);
		}

		model.put("project", project);

		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
				
		return "scenarioparameters";
	}

	
	@RequestMapping(value="scenarioParam", method=RequestMethod.POST)
	public String setScenarioParam(Map<String, Object> model,
		ScenarioParamForm form,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId){
		ProjectDTO project = (ProjectDTO) model.get("project");
				
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		int nSelectedCompId = Integer.parseInt(selectedCompId);		
		List<InputParamValDTO> inputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, scenario.getScenid());
		Map<Integer, InputParamValDTO> InputParamMAP = new HashMap<>();
		 
         for (InputParamValDTO inputParameterValue : inputParamVals) {
        	 InputParamMAP.put(inputParameterValue.getInputparamvalid(), inputParameterValue);
         }
         
		for (Map.Entry<Integer, String> entry : form.getValueByInputId().entrySet()) {
            
			InputParamValDTO inputParameterValue = InputParamMAP.get(entry.getKey());
			inputParameterValue.setValue(entry.getValue());
			
			
            //inputParamValId = entry.getKey();            
            //String value = entry.getValue();
            
    		//int nInputParamValId = Integer.parseInt(inputParamValId);
    		InputParamValDTO updatedInputParamVal = null;
    		
    		try {
    			updatedInputParamVal = inputParamValService.findByID(inputParameterValue.getInputparamvalid());
    		} catch (EntityNotFoundException e) {
    			e.printStackTrace();
    		}
    		
    		updatedInputParamVal.setValue(entry.getValue().trim());
    		//updatedInputParamVal.setValue(inputParamVal.getValue());
    		updatedInputParamVal.setScenario(scenario);
    		inputParamValService.save(updatedInputParamVal);            
		}
	
            
		
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
		//int nSelectedCompId = 0;
		
		if (selectedCompId != null)
		{
			nSelectedCompId = Integer.parseInt(selectedCompId);
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			model.put("selectedcompid", selectedCompId);
			model.put("selectedComponent",  selectedComponent);

			//List<InputParamValDTO> inputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, scenario.getScenid());
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
			List<OutputVariableDTO> outputVariables=null;
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);				
				outputVariables = componentService.getOutputVariables(nSelectedCompId);
				
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}

			//Hibernate.initialize(selectedComponent.getInputparameters());
			//model.put("inputParams", selectedComponent.getInputparameters());
			model.put("selectedComponent",  selectedComponent);
			model.put("selectedcompid", selectedCompId);
			model.put("outputVariables", outputVariables);
			
		}

		model.put("project", project);
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);
		
		return "outputvariables";
	}
	
	@RequestMapping(value="runmultiscenario", method=RequestMethod.GET)
	public String getRunMultiScenario(Map<String, Object> model,
		@RequestParam(value="multiscenarioid", required=false) String multiScenarioId,
		@RequestParam(value="action", required=false) String action) {
		ProjectDTO project = (ProjectDTO) model.get("project");

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		if (project == null)
		{ 
			return "error";
		}
		
		if (multiScenarioId != null && action != null)
		{
			int nMultiScenarioId = Integer.parseInt(multiScenarioId);
			UserSession userSession = (UserSession) model.get("usersession");
			
			if (userSession == null)
			{
				userSession = new UserSession();
			}
			
			if (action.equalsIgnoreCase("add")) {
				userSession.addSelectedScenGenId(nMultiScenarioId);
			} else if (action.equalsIgnoreCase("remove")) {
				userSession.removeSelectedScenGenId(nMultiScenarioId);
			}
			model.put("usersession", userSession);
		}
		
		List<ScenarioGeneratorDTO> listScenGens = scenGenService.findAll();
		model.put("scenGens", listScenGens);
		
		return "runmultiscenario";
	}
	
	@RequestMapping(value="runmultiscenario", method=RequestMethod.POST)
	public String getRunMultiScenarioPost(Map<String, Object> model) {
			
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		UserSession userSession = (UserSession) model.get("usersession");

		if (project == null || userSession == null)
		{
			return "error";
		}

		Iterator<Integer> iter = userSession.getSelectedScenGenIds().iterator();
		
		while (iter.hasNext())
		{
			int nMultiScenarioId = iter.next(); 
		
            List<DecisionVariableDTO> decVars = null;
            
			try {
				decVars = scenGenService.getDecisionVariables(nMultiScenarioId);
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}

			List<ModelParameterDTO> modelParams = null;
			
			try {
				modelParams = scenGenService.getModelParameters(nMultiScenarioId);
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
            
			try {
				ModelParameterGrouping grouping = scenGenService.getModelParameterGrouping(nMultiScenarioId);
			} catch (EntityNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				scenGenSimService.startOptimisation(nMultiScenarioId, null);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (ScriptException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		List<ScenarioGeneratorDTO> listScenGens = scenGenService.findAll();
		model.put("scenGens", listScenGens);

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

		if (simService.getRunningSimulations().contains(scenario.getScenid())) {
			statusMsg = "RUNNING";
		}
		
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
