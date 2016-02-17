package eu.cityopt.controller;

import java.awt.Desktop.Action;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
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
import eu.cityopt.web.ScenarioForm;
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
    ControllerService controllerService;

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
	
    @Autowired
    SecurityAuthorization securityAuthorization;

	@RequestMapping(value="createscenario",method=RequestMethod.GET)
	public String createScenario(Map<String, Object> model) {
		
		AppUserDTO user = (AppUserDTO) model.get("user");
		ProjectDTO project = (ProjectDTO) model.get("project");

		securityAuthorization.atLeastExpert_standard(project);
		
		ScenarioDTO scenario = new ScenarioDTO();
		model.put("newScenario", scenario);
		return "createscenario";
	}
	
	@RequestMapping(value="createscenario", method=RequestMethod.POST)
	@Transactional
	public String createScenarioPost(Map<String, Object> model, 
		@Validated @ModelAttribute ("newScenario") ScenarioDTO formScenario, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
            return "createscenario";
        }
		
		if (model.containsKey("project") && formScenario != null)
		{
			ProjectDTO project = (ProjectDTO) model.get("project");
			
			try {
				project = projectService.findByID(project.getPrjid());
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}			

			securityAuthorization.atLeastExpert_standard(project);
			
			model.put("project", project);			
			ScenarioDTO scenario = new ScenarioDTO();			
			
			ScenarioDTO scenarioTest = scenarioService.findByNameAndProject(project.getPrjid(), formScenario.getName());
			
			if (scenarioTest==null){			
				scenario.setName(formScenario.getName().trim());
				scenario.setDescription(formScenario.getDescription().trim());
				scenario.getScenid();
				
				List<ComponentDTO> components = projectService.getComponents(project.getPrjid());				
				try {
					scenario = scenarioService.saveWithDefaultInputValues(scenario, project.getPrjid());
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				model.put("components", components);
				model.put("successful", "Scenario succesfully created.");
				model.put("scenario",  scenario);
			} else {
				model.put("newScenario", formScenario);
				model.put("error", "This Scenario already exists, please create another name.");
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
					model.put("error", "Project lack simulation definition, please define them.");
					return "createscenario";
				}			
			}else{	
				model.put("newScenario", formScenario);				
				model.put("error", "Project lack input parameter values can't create a scenario,  please define them.");
				return "createscenario";
			}
		}
		else
			{
			model.put("newScenario", formScenario);				
			model.put("error", "There's no project selected please select one.");
			return "createscenario";
			//project null
			//return "error";
			}		
	}
	
	@RequestMapping(value="openscenario",method=RequestMethod.GET)
	public String openScenario (Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid)
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastGuest_guest(project);

		if (scenarioid != null)
		{
			controllerService.initEditScenario(model, project.getPrjid(), Integer.parseInt(scenarioid));			
			return "editscenario";
		}
		else
		{
			controllerService.initScenarioList(model, project.getPrjid());
		}

		return "openscenario";
	}
	    
	@RequestMapping(value="showscenarios",method=RequestMethod.GET)
	public String showScenarios (Map<String, Object> model)
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastGuest_guest(project);

		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);

		return "showscenarios";
	}
	
	@RequestMapping(value="clonescenario",method=RequestMethod.GET)
	public String cloneScenario (Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid)
	{
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

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
				model.put("error", "This scenario has been updated in the meantime, please reload.");
			}
		}
			
		controllerService.initScenarioList(model, project.getPrjid());

		return "openscenario";
	}
	
	@RequestMapping(value="editscenario", method=RequestMethod.GET)
	public String editScenario (Map<String, Object> model) {
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

		securityAuthorization.atLeastGuest_guest(project);

		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		if (scenario != null && scenario.getScenid() > 0)
		{
			controllerService.initEditScenario(model, project.getPrjid(), scenario.getScenid());			
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
	public String editScenarioPost(ScenarioDTO formScenario, Map<String, Object> model, 
		@RequestParam(value="action", required=false) String action) {

		if (model.containsKey("project") && formScenario != null)
		{
			ProjectDTO project = (ProjectDTO) model.get("project");
			try {
				project = projectService.findByID(project.getPrjid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			securityAuthorization.atLeastStandard_standard(project);

			ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
			
			scenario.setName(formScenario.getName().trim());
			scenario.setDescription(formScenario.getDescription().trim());
			
			try {
				scenario = scenarioService.save(scenario, project.getPrjid());
			} catch(ObjectOptimisticLockingFailureException e) {
				model.put("error", "This scenario has been updated in the meantime, please reload.");
			}
			
			controllerService.initEditScenario(model, project.getPrjid(), scenario.getScenid());			
		}
		else
		{
			//project null
			return "error";
		}
			
		return "editscenario";
	}
	
	@RequestMapping(value = "importscenarios", method = RequestMethod.POST)
	public String importScenarios(
        Map<String, Object> model,
        @RequestParam("file") MultipartFile file,
        @RequestParam("timeSeriesFile1") MultipartFile timeSeriesMPFile1) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

		securityAuthorization.atLeastExpert_standard(project);

	    if (!file.isEmpty()) {
	        try (InputStream scenarios = file.getInputStream();
	             InputStream timeSeries
	                 = (timeSeriesMPFile1 != null
	                    ? timeSeriesMPFile1.getInputStream() : null)) {

	            try {
	                project = projectService.findByID(project.getPrjid());
	            } catch (Exception e1) {
	                e1.printStackTrace();
	            }
	            model.put("project", project);

	            importExportService.importScenarioData(project.getPrjid(), scenarios, "Imported from " + file.getOriginalFilename(), timeSeries);
	        } catch (Exception e) {
	            e.printStackTrace();
            	model.put("error", e.getStackTrace().toString());
                return "error";
	        }
	    } else {
	    }
	    return "importdata";
	}

	@RequestMapping(value = "exportscenarios", method = RequestMethod.GET)
	public void exportScenarios(Map<String, Object> model, HttpServletRequest request, 
		HttpServletResponse response) {

        ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return;
		}

		securityAuthorization.atLeastStandard_guest(project);

		Path timeSeriesPath = null;
		Path scenarioPath = null;
		File fileScenario = null;
		File fileTimeSeries = null;
		List<File> files = new ArrayList<File>();
		
		try (TempDir tempDir = new TempDir("export")) {
	        timeSeriesPath = tempDir.getPath().resolve("timeseries.csv");
	        scenarioPath = tempDir.getPath().resolve("scenarios.csv");
	        importExportService.exportScenarioData(project.getPrjid(), scenarioPath, timeSeriesPath);

	        fileTimeSeries = timeSeriesPath.toFile();
	        fileScenario = scenarioPath.toFile();
	        files.add(fileScenario);
	        files.add(fileTimeSeries);
		
			// Set the content type based to zip
			response.setContentType("Content-type: text/zip");
			response.setHeader("Content-Disposition", "attachment; filename=scenarios.zip");
	
			ServletOutputStream out = null;
			
			try {
				out = response.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));
	
			for (File file : files) {
		
				try {
					zos.putNextEntry(new ZipEntry(file.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
		
				// Get the file
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					// If the file does not exists, write an error entry instead of
					// file
					// contents
					
					try {
						zos.write(("ERROR could not find file " + file.getName()).getBytes());
						zos.closeEntry();
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Could find file "
							+ file.getAbsolutePath());
					continue;
				} catch (IOException e)	{
					e.printStackTrace();
				}
			
				BufferedInputStream fif = new BufferedInputStream(fis);
		
				// Write the contents of the file
				int data = 0;
				try {
					while ((data = fif.read()) != -1) {
						zos.write(data);
					}
					fif.close();
		
					zos.closeEntry();
					System.out.println("Finished file " + file.getName());
		
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			response.flushBuffer();
			zos.close();
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	@RequestMapping(value = "exportsimulationresults", method = RequestMethod.GET)
	public void exportSimulationResults(Map<String, Object> model, HttpServletResponse response) {

		System.out.println("Start");

        ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return;
		}

		securityAuthorization.atLeastStandard_guest(project);

		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");

		if (scenario == null)
		{
			return;
		}
		
		Path timeSeriesPath = null;
		Path scenarioPath = null;
		File fileScenario = null;
		File fileTimeSeries = null;
		List<File> files = new ArrayList<File>();
		
		try (TempDir tempDir = new TempDir("export")) {
	        timeSeriesPath = tempDir.getPath().resolve("timeseries.csv");
	        scenarioPath = tempDir.getPath().resolve("scenarios.csv");
	        
			//System.out.println("Starting exporting simulation results");
			importExportService.exportSimulationResults(scenarioPath, timeSeriesPath, project.getPrjid(), scenario.getScenid());

	        fileTimeSeries = timeSeriesPath.toFile();
	        fileScenario = scenarioPath.toFile();
	        files.add(fileScenario);
	        files.add(fileTimeSeries);

			// Set the content type based to zip
			response.setContentType("Content-type: text/zip");
			response.setHeader("Content-Disposition", "attachment; filename=simulation_results.zip");
	
			ServletOutputStream out = null;
			
			try {
				out = response.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));
	
			for (File file : files) {
		
				try {
					zos.putNextEntry(new ZipEntry(file.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
		
				// Get the file
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					// If the file does not exists, write an error entry instead of
					// file
					// contents
					
					try {
						zos.write(("ERROR could not find file " + file.getName()).getBytes());
						zos.closeEntry();
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Could find file "
							+ file.getAbsolutePath());
					continue;
				} catch (IOException e)	{
					e.printStackTrace();
				}
			
				BufferedInputStream fif = new BufferedInputStream(fis);
		
				// Write the contents of the file
				int data = 0;
				try {
					while ((data = fif.read()) != -1) {
						zos.write(data);
					}
					fif.close();
		
					zos.closeEntry();
					//System.out.println("Finished file " + file.getName());
		
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			response.flushBuffer();
			zos.close();
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
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

		securityAuthorization.atLeastExpert_expert(project);

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		controllerService.initEditScenario(model, project.getPrjid(), scenario.getScenid());
		
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
		
		return "editscenario";
	}
	
	@RequestMapping(value="deletescenario",method=RequestMethod.GET)
	public String deleteScenario(Map<String, Object> model, @RequestParam(value="scenarioid", required=false) String scenarioid) {
	
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}
		
		securityAuthorization.atLeastExpert_expert(project);

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
					model.put("error", "This scenario has been updated in the meantime, please reload.");
				}
			}
		}

		Set<ScenarioDTO> scenarios = (Set<ScenarioDTO>) projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);

		return "deletescenario";
	}

	@RequestMapping(value="scenarioparameters", method=RequestMethod.GET)
	public String scenarioParameters(Map<String, Object> model, 
		@RequestParam(value="selectedcompid", required=false) String selectedCompId){
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastGuest_guest(project);

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
		String statusMsg = controllerService.getScenarioStatus(scenario);

		if (statusMsg != null && statusMsg.equals("SUCCESS"))
		{
			model.put("disableEdit", true);
		}
				
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
	public String scenarioParamPost(Map<String, Object> model,
		ScenarioParamForm form,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastStandard_standard(project);

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
    		InputParamValDTO updatedInputParamVal = null;
    		
    		try {
    			updatedInputParamVal = inputParamValService.findByID(inputParameterValue.getInputparamvalid());
    		} catch (EntityNotFoundException e) {
    			e.printStackTrace();
    		}
    		
    		updatedInputParamVal.setValue(entry.getValue().trim());
    		updatedInputParamVal.setScenario(scenario);
    		inputParamValService.save(updatedInputParamVal);            
		}
		
		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		model.put("project", project);

		controllerService.initEditScenario(model, project.getPrjid(), scenario.getScenid());
		
		return "editscenario";
	}
	
	@RequestMapping(value="scenariovariables",method=RequestMethod.GET)
	public String scenarioVariables(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastGuest_guest(project);

		try {
			project = projectService.findByID(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		model.put("project", project);
		
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
        
		return "scenariovariables";
	}
		
	
	@RequestMapping(value="editinputparamvalue", method=RequestMethod.GET)
	public String editInputParameterValue(Map<String, Object> model, 
		@RequestParam(value="inputparamvalid", required=true) String inputvalid) {
		int nInputValId = Integer.parseInt(inputvalid);
		InputParamValDTO inputParamVal = null;

		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		try {
			inputParamVal = inputParamValService.findByID(nInputValId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("inputParamVal", inputParamVal);
		
		return "editinputparamvalue";
	}

	@RequestMapping(value="editinputparamvalue", method=RequestMethod.POST)
	public String editInputParamValPost(InputParamValDTO inputParamVal, Map<String, Object> model,
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
		securityAuthorization.atLeastExpert_expert(project);
		
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
	public String runScenario(Map<String, Object> model)
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
		
		securityAuthorization.atLeastStandard_standard(project);

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

		statusMsg = controllerService.getScenarioStatus(scenario);

		model.put("status", statusMsg);
		model.put("error", errorMsg);
		
		List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
		model.put("components", components);

		Set<ExtParamValDTO> extParamVals = projectService.getExtParamVals(project.getPrjid());
		model.put("extParamVals", extParamVals);
		
		Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
		model.put("scenarios", scenarios);
		
		return "timeserieschart";
	}	
	
    @RequestMapping(value="simulationinfo", method=RequestMethod.GET)
    public String simInfoPage(Map<String, Object> model)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
        if (project == null || scenario == null)
        {
        	return "error";
        }

		securityAuthorization.atLeastGuest_guest(project);

        model.put("title", "Simulation info for scenario " + scenario.getName());
		
        BufferedReader bufReader = new BufferedReader(new StringReader(scenario.getLog()));
        String line = null;
        StringBuilder result = new StringBuilder();
        
        try {
			while( (line = bufReader.readLine()) != null )
			{
				if (line.length() > 140)
				{
					result.append(line.substring(0, 140));
					result.append(System.getProperty("line.separator"));
					result.append(line.substring(140, Math.min(280, line.length() - 1)));
				}
				else
				{
					result.append(line);
				}
				result.append(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        model.put("infotext", result.toString());
        
        return "simulationinfo";
    }	

}
