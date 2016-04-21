package eu.cityopt.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.TimeSeriesDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.DTO.TimeSeriesValDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.config.AppMetadata;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Type;
import eu.cityopt.repository.OutputVariableRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.TypeRepository;
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
import eu.cityopt.service.UserGroupProjectService;
import eu.cityopt.service.UserGroupService;
import eu.cityopt.sim.eval.SyntaxChecker;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.sim.service.SyntaxCheckerService;
import eu.cityopt.web.ParamForm;


@Controller
@SessionAttributes({
    "project", "scenario", "optimizationset", "scengenerator", "optresults",
    "usersession", "user", "version"})
public class ParameterController {

    @Autowired
    ProjectService projectService; 
    
    @Autowired
    ComponentService componentService;
    
    @Autowired
    InputParameterService inputParamService;

    @Autowired
    InputParamValService inputParamValService;

    @Autowired
    OutputVariableService outputVarService;
    
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
    ControllerService controllerService;
    
    @Autowired
    SecurityAuthorization securityAuthorization;

    @Autowired 
	private ModelMapper modelMapper;
	
    @Autowired 
	private TypeRepository typeRepository;

    @Autowired 
	private OutputVariableRepository outVarRepository;

    @Autowired
	SyntaxCheckerService syntaxCheckerService;

    @Autowired
    ImportExportService importExportService;

	@Autowired
	TimeSeriesValService timeSeriesValService;

    @RequestMapping(value="projectparameters", method=RequestMethod.GET)
    public String getProjectParameters(Map<String, Object> model, 
            @RequestParam(value="selectedcompid", required=false) String selectedCompId) {
     	
    	ProjectDTO project = (ProjectDTO) model.get("project");
		
    	if (controllerService.NullCheck(project)){return "error";}
		
    	securityAuthorization.atLeastGuest_guest(project);
		model.put("project", project);

		controllerService.SetUpSelectedComponent(model, selectedCompId);
        controllerService.getComponentAndExternalParamValues(model,project);        
        return "projectparameters";
    }

    @RequestMapping(value="extparams", method=RequestMethod.GET)
    public String getExtParams(Map<String, Object> model) {
     	
    	ProjectDTO project = (ProjectDTO) model.get("project");
		
    	if (controllerService.NullCheck(project)) {return "error";}
		
    	securityAuthorization.atLeastGuest_guest(project);
		model.put("project", project);

        controllerService.getProjectExternalParameterValues(model,project);        
        controllerService.getComponentAndExternalParamValues(model,project);        
        return "extparams";
    }

    @RequestMapping(value="selectextparamset", method=RequestMethod.GET)
    public String getSelectExtParamSet(Map<String, Object> model, 
            @RequestParam(value="selectedextparamsetid", required=false) String selectedExtParamSetId) {

        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null)
        {
            return "error";
        }        
        securityAuthorization.atLeastExpert_expert(project);

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }
        ExtParamValSetDTO selectedExtParamValSet = null;

        if (selectedExtParamSetId != null)
        {
            int nSelectedExtParamSetId = Integer.parseInt(selectedExtParamSetId);

            try {
                selectedExtParamValSet = extParamValSetService.findByID(nSelectedExtParamSetId);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }

            //			project.setDefaultextparamvalset(selectedExtParamSet);
            project = projectService.save(project, projectService.getSimulationmodelId(project.getPrjid()), 
                    selectedExtParamValSet.getExtparamvalsetid());

            model.put("selectedextparamsetid", nSelectedExtParamSetId);
            model.put("extParamValSet", selectedExtParamValSet);
            List<ExtParamValDTO> extParamVals = null;

            try {
                extParamVals = extParamValSetService.getExtParamVals(nSelectedExtParamSetId);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
            model.put("extParamVals", extParamVals);
            model.put("project", project);
            
            controllerService.getComponentAndExternalParamValues(model,project);   
                        
            return "extparams";
        }

        List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
        model.put("extParamValSets", extParamValSets);
        model.put("project", project);

        return "selectextparamset";
    }
        
    @RequestMapping(value="createcomponent", method=RequestMethod.GET)
    public String getCreateComponent(Model model) {
    	securityAuthorization.atLeastExpert_expert(model);
        ComponentDTO newComponent = new ComponentDTO();
        model.addAttribute("component", newComponent);

        return "createcomponent";
    }

    @RequestMapping(value="createcomponent", method=RequestMethod.POST)
    public String getCreateComponentPost(ComponentDTO componentForm, Map<String, Object> model,
    	@RequestParam(value="cancel", required=false) String cancel,
    	HttpServletRequest request) {
    		 
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        
        if (cancel != null)
        {
        	return "editproject";
        }
        
        securityAuthorization.atLeastExpert_expert(project);
        
        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        String name = componentForm.getName();
        
        if (name == null || name.isEmpty()) {
        	ComponentDTO newComponent = new ComponentDTO();
            model.put("component", newComponent);
            model.put("error", controllerService.getMessage("write_component_name", request));
        	return "createcomponent";
        }
        
    	SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
     	boolean isValid = checker.isValidTopLevelName(name);

     	if (!isValid)
     	{
     		ComponentDTO newComponent = new ComponentDTO();
            model.put("component", newComponent);
            model.put("error", controllerService.getMessage("write_another_component_name", request));
        	return "createcomponent";
        }
     	
     	ComponentDTO component = new ComponentDTO();
        component.setName(componentForm.getName().trim());
        componentService.save(component, project.getPrjid());

        try {
            model.put("project", projectService.findByID(project.getPrjid()));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        controllerService.getComponentAndExternalParamValues(model, project);        
        
        return "projectparameters";
    }
   
    @RequestMapping(value="editcomponent", method=RequestMethod.GET)
    public String getEditComponent(Map<String, Object> model, @RequestParam(value="componentid", required=true) String componentid) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
    	securityAuthorization.atLeastStandard_standard(project);

    	int nCompId = Integer.parseInt(componentid);
        ComponentDTO component = null;

        try {
            component = componentService.findByID(nCompId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        model.put("component", component);

        return "editcomponent";
    }

    @RequestMapping(value="editcomponent", method=RequestMethod.POST)
    public String getEditComponentPost(ComponentDTO component, Map<String, Object> model,
        @RequestParam(value="componentid", required=true) String componentid,
        HttpServletRequest request) 
    {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);
        
        if (component.getName().isEmpty())
        {
        	model.put("error", controllerService.getMessage("write_name", request));
        	model.put("component", component);
        	return "editcomponent";
        }
        
        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        int nCompId = Integer.parseInt(componentid);
        ComponentDTO oldComponent = null;
        try {
            oldComponent = componentService.findByID(nCompId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        oldComponent.setName(component.getName());

        componentService.save(oldComponent, project.getPrjid());
        model.put("selectedcompid", oldComponent.getComponentid());
        model.put("selectedComponent",  oldComponent);

        try {
            model.put("project", projectService.findByID(project.getPrjid()));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        controllerService.getComponentAndExternalParamValues(model, project);        

        return "projectparameters";
    }
    
    @RequestMapping(value="editinputparameter", method=RequestMethod.GET)
    public String getEditInputParameter(Map<String, Object> model, 
		@RequestParam(value="inputparamid", required=true) String inputid) {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
    	securityAuthorization.atLeastStandard_standard(project);

    	int nInputId = Integer.parseInt(inputid);
        InputParameterDTO inputParam = null;
        
        try {
            inputParam = inputParamService.findByID(nInputId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        
        model.put("inputParam", inputParam);
        
        ParamForm inputParamForm = new ParamForm();
        inputParamForm.setName(inputParam.getName());
        inputParamForm.setValue(inputParam.getDefaultvalue());
        model.put("inputParamForm", inputParamForm);

		TimeSeriesDTO timeSeriesDTO = inputParam.getTimeseries();
		
		if (timeSeriesDTO != null)
		{
			List<TimeSeriesValDTO> timeSeriesVals = null;
			
			try {
				timeSeriesVals = timeSeriesValService.findByTimeSeriesIdOrderedByTime(timeSeriesDTO.getTseriesid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
			
			Iterator<TimeSeriesValDTO> timeSeriesIter = timeSeriesVals.iterator();
			List<String> values = new ArrayList<String>();
			List<String> times = new ArrayList<String>();
			
			while(timeSeriesIter.hasNext()) {
				TimeSeriesValDTO timeSeriesVal = timeSeriesIter.next();
				times.add(timeSeriesVal.getTime().toString());
				values.add(timeSeriesVal.getValue().toString());
			}
			System.out.println("Times: " + times.toString());
			System.out.println("Values: " + values.toString());
			
			if (times.size() > 100)
			{
				times = times.subList(0, 100);
			}

			if (values.size() > 100)
			{
				values = values.subList(0, 100);
			}

			model.put("times", times);
			model.put("values", values);
		}
		else
		{
			System.out.println("Time series empty");
		}

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);
        
        return "editinputparameter";
    }

    @RequestMapping(value="editinputparameter", method=RequestMethod.POST)
    public String editInputParameterPost(Map<String, Object> model, 
		ParamForm inputParamForm,
        @RequestParam(value="inputparamid", required=false) String inputParamId,
    	@RequestParam(value="cancel", required=false) String cancel) {
    	        
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        
        if (cancel != null) {
        	controllerService.getComponentAndExternalParamValues(model, project);
        	return "projectparameters";
        }
        
        securityAuthorization.atLeastExpert_expert(project);
        
        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        int nInputParamId = Integer.parseInt(inputParamId);
        InputParameterDTO updatedInputParam = null;
        
        try {
            updatedInputParam = inputParamService.findByID(nInputParamId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        
        updatedInputParam.setName(inputParamForm.getName());
        updatedInputParam.setDefaultvalue(inputParamForm.getValue());
        TypeDTO type = typeService.findByName(eu.cityopt.sim.eval.Type.DOUBLE.name);
        updatedInputParam.setType(type);
        
        String strUnit = inputParamForm.getUnit();
        UnitDTO unit = null;
        
		try {
			unit = unitService.findByName(strUnit);
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}
        
        updatedInputParam.setUnit(unit);
        int componentId = updatedInputParam.getComponentComponentid();//inputParamService.getComponentId(updatedInputParam.getInputid());

        try {
			inputParamService.update(updatedInputParam, componentId, unit.getUnitid(), null);
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
        
        model.put("selectedcompid", componentId);

        try {
            model.put("selectedComponent", componentService.findByID(componentId));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        model.put("project", project);
        
        controllerService.getComponentAndExternalParamValues(model, project);        
        
        List<InputParameterDTO> inputParams = componentService.getInputParameters(componentId);
        model.put("inputParameters", inputParams);
        
        return "projectparameters";
    }

    @RequestMapping(value = "importinputtimeseries", method = RequestMethod.POST)
    public String importInputTimeSeries(Map<String, Object> model, 
		@RequestParam("file") MultipartFile file, 
    	@RequestParam(value="inputid", required=true) String inputId,
    	@RequestParam(value="cancel", required=false) String cancel,
    	HttpServletRequest request)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (!file.isEmpty()) {
            try {
                if (project == null)
                {
                    return "error";
                }
                securityAuthorization.atLeastExpert_standard(project);

                try {
                    project = projectService.findByID(project.getPrjid());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                model.put("project", project);

                if (cancel != null)
            	{
                	controllerService.getComponentAndExternalParamValues(model, project);
                	return "projectparameters";
            	}
            	
                int nInputId = Integer.parseInt(inputId);
                InputParameterDTO inputParam = inputParamService.findByID(nInputId);
                
                InputStream stream = file.getInputStream();
                System.out.println("Starting import time series");
                
                Map<String, TimeSeriesDTOX> tsData = importExportService.readTimeSeriesCsv(project.getPrjid(), stream);
                Set<String> keys = tsData.keySet();
                Iterator<String> iter = keys.iterator();
                
                // Take only the first one
                String inputParamName = iter.next();
            	System.out.println("param name: " + inputParamName);
                TimeSeriesDTOX ts = tsData.get(inputParamName);
            	System.out.println("param values length: " + ts.getValues().length);
            	System.out.println("param times length: " + ts.getTimes().length);
                
                TypeDTO type = typeService.findByName(eu.cityopt.sim.eval.Type.TIMESERIES_STEP.name);
                inputParam.setType(type);
                
                UnitDTO unit = inputParam.getUnit();

                if (unit == null)
                {
                	unit = controllerService.getDefaultUnit();
                }

                List<InputParameterDTO> testInputs = inputParamService.findByName(inputParamName);
                System.out.println("test inputs size " + testInputs.size() + " name: " + inputParamName);
                
                if (testInputs.size() > 0
                	&& inputParam.getName().equals(inputParamName))
                {
                	// Update
                	inputParam = inputParamService.update(inputParam, inputParam.getComponentComponentid(), unit.getUnitid(), ts);
                } 
                else if (testInputs.size() == 0)
                {
                    inputParam.setName(inputParamName);
                	inputParam = inputParamService.save(inputParam, inputParam.getComponentComponentid(), unit.getUnitid(), ts);
                }
                else
                {
                	String error = controllerService.getMessage("imported_input_parameter_exists", request);
                	model.put("error", error);
                }

                /*InputParamValDTO inputParamVal = new InputParamValDTO();
                inputParamVal.setInputparam(inputParam);
                inputParamVal = inputParamValService.save(inputParamVal);*/
                
                stream.close();
                System.out.println("Finished importing input time series");

                List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
	            model.put("components", components);
	            Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
	            model.put("extParams", extParams);

            } catch (Exception e) {
            	e.printStackTrace();
            }
        } else {
        }
    	
        controllerService.getComponentAndExternalParamValues(model, project);
        return "projectparameters";
    }
    
    @RequestMapping(value="editoutputvariable", method=RequestMethod.GET)
    public String editOutputVariable(Map<String, Object> model, 
		@RequestParam(value="outputvarid", required=true) String outputvarid) {
        int nOutputId = Integer.parseInt(outputvarid);
        OutputVariableDTO outputVar = null;
        
        try {
        	outputVar = outputVarService.findByID(nOutputId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        
        ProjectDTO project = (ProjectDTO) model.get("project");
        securityAuthorization.atLeastExpert_expert(project);

        model.put("outputVar", outputVar);
        
        ParamForm paramForm = new ParamForm();
        paramForm.setName(outputVar.getName());
        model.put("paramForm", paramForm);

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);
        
        return "editoutputvariable";
    }

	@RequestMapping(value="outputvariables",method=RequestMethod.GET)
	public String outputVariables(Map<String, Object> model,
		@RequestParam(value="selectedcompid", required=false) String selectedCompId) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastGuest_guest(project);

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

    @RequestMapping(value="editoutputvariable", method=RequestMethod.POST)
    public String editOutputParameterPost(Map<String, Object> model, 
		ParamForm paramForm,
        @RequestParam(value="outputvarid", required=false) String outputVarId,
    	@RequestParam(value="cancel", required=false) String cancel) {
            
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        if (cancel != null) {
            controllerService.getComponentAndExternalParamValues(model, project);        
        	return "outputvariables";
        }
        
        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        int nOutputVarId = Integer.parseInt(outputVarId);
        OutputVariableDTO updatedOutputVar = null;
        
        try {
        	updatedOutputVar = outputVarService.findByID(nOutputVarId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        
        String strUnit = paramForm.getUnit();
        UnitDTO unit = null;
        
		try {
			unit = unitService.findByName(strUnit);
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}
        
		updatedOutputVar.setUnit(unit);
        int componentId = updatedOutputVar.getComponent().getComponentid();
        outputVarService.save(updatedOutputVar);

        /*try {
        	outputVarService.update(updatedOutputVar);
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}*/
        
        model.put("selectedcompid", componentId);

        try {
            model.put("selectedComponent", componentService.findByID(componentId));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        model.put("project", project);
        
        controllerService.getComponentAndExternalParamValues(model, project);        
        
        List<OutputVariableDTO> outputVars = componentService.getOutputVariables(componentId);
        model.put("outputVariables", outputVars);
        
        return "outputvariables";
    }
    
    @RequestMapping(value="createinputparameter", method=RequestMethod.GET)
    public String getCreateInputParameter(Map<String, Object> model,
            @RequestParam(value="selectedcompid", required=true) String strSelectedCompId) {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        if (strSelectedCompId == null || strSelectedCompId.isEmpty())
        {
            model.put("project", project);
            controllerService.getComponentAndExternalParamValues(model, project);        
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
        model.put("inputParam", newInputParameter);
        model.put("selectedcompid", nSelectedCompId);

        ParamForm inputParamForm = new ParamForm();
        model.put("inputParamForm", inputParamForm);

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);
        
        return "createinputparameter";
    }

    @RequestMapping(value="createinputparameter", method=RequestMethod.POST)
    public String getCreateInputParamPost(ParamForm inputParamForm, Map<String, Object> model,
        @RequestParam(value="selectedcompid", required=true) String strSelectedCompId,
    	@RequestParam(required=false, value="cancel") String cancel,
    	HttpServletRequest request) 
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        
        controllerService.updateProject(model, project);
        securityAuthorization.atLeastExpert_expert(project);

        if (cancel != null)
        {
            controllerService.getComponentAndExternalParamValues(model, project);
        	return "projectparameters";
        }
        
        int nSelectedCompId = Integer.parseInt(strSelectedCompId);
        String strUnit = inputParamForm.getUnit();

        if (inputParamForm.getName() == null || inputParamForm.getName().isEmpty() 
    		|| strUnit == null || strUnit.isEmpty())
        {
            InputParameterDTO newInputParameter = new InputParameterDTO();
            model.put("inputParam", newInputParameter);
            
            inputParamForm = new ParamForm();
            model.put("inputParamForm", inputParamForm);
            model.put("selectedcompid", nSelectedCompId);

            List<UnitDTO> units = unitService.findAll();
            model.put("units", units);
            model.put("error", controllerService.getMessage("write_input_parameter_and_select_unit", request));
        	return "createinputparameter";
        }
        
        SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
     	boolean isValid = checker.isValidAttributeName(inputParamForm.getName());

     	if (!isValid)
     	{
            model.put("error", controllerService.getMessage("write_another_input_parameter_name", request));
            InputParameterDTO newInputParameter = new InputParameterDTO();
            model.put("inputParam", newInputParameter);
            
            inputParamForm = new ParamForm();
            model.put("inputParamForm", inputParamForm);
            model.put("selectedcompid", nSelectedCompId);

            List<UnitDTO> units = unitService.findAll();
            model.put("units", units);
            return "createinputparameter";
        }

        ComponentDTO component = null;
        try {
            component = componentService.findByID(nSelectedCompId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        InputParameterDTO inputParam = new InputParameterDTO();
        inputParam.setName(inputParamForm.getName());
        inputParam.setDefaultvalue(inputParamForm.getValue());
        UnitDTO unit = null;
        
        if (strUnit == null || strUnit.isEmpty()) {
        	strUnit = "-";
        }
        
		try {
			unit = unitService.findByName(strUnit);
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}
        
        inputParam.setUnit(unit);
        inputParam.setType(typeService.findByName(eu.cityopt.sim.eval.Type.DOUBLE.name));
        
    	inputParamService.save(inputParam, component.getComponentid(), unit.getUnitid(), null);

        controllerService.SetUpSelectedComponent(model, strSelectedCompId);
        controllerService.getProjectExternalParameterValues(model, project);
        controllerService.getComponentAndExternalParamValues(model, project);        
        
        return "projectparameters";
    }

    @RequestMapping(value="deleteinputparameter", method=RequestMethod.GET)
    public String getDeleteInputParam(Map<String, Object> model,
        @RequestParam(value="inputparamid", required=true) String strInputParamId) {
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
        model.put("project", project);

        try {
			inputParamService.delete(Integer.parseInt(strInputParamId));
		} catch (NumberFormatException | EntityNotFoundException e) {
			e.printStackTrace();
		}
        controllerService.getDefaultExtParamVals(model, project.getPrjid());
        controllerService.getComponentAndExternalParamValues(model, project);
        
        return "projectparameters";
    }

    @RequestMapping(value="createextparam", method=RequestMethod.GET)
    public String getCreateExtParam(Map<String, Object> model) {
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
        model.put("project", project);

        ExtParamDTO extParam = new ExtParamDTO();
        model.put("extParam", extParam);

        return "createextparam";
    }

    @RequestMapping(value="createextparam", method=RequestMethod.POST)
    public String getCreateExtParamPost(ExtParamDTO extParam, Map<String, Object> model,
    	@RequestParam(required=false, value="cancel") String cancel,
    	HttpServletRequest request) {
        
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        if (cancel != null)
        {
            controllerService.getProjectExternalParameterValues(model,project);        
            controllerService.getComponentAndExternalParamValues(model, project);
        	return "extparams";
        }

        String name = extParam.getName().trim();
        
        if (name.isEmpty())
        {
            model.put("error", controllerService.getMessage("write_parameter_name", request));
            model.put("extParam", new ExtParamDTO());
            return "createextparam";
        }
        
        SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
     	boolean isValid = checker.isValidTopLevelName(name);

     	if (!isValid)
     	{
            model.put("error", controllerService.getMessage("write_another_parameter_name", request));
            model.put("extParam", new ExtParamDTO());
            return "createextparam";
        }
     	
        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        ExtParamDTO newExtParam = new ExtParamDTO();
        newExtParam.setName(name);
        newExtParam.setProject(project);
        
        TypeDTO typeDTO = new TypeDTO();
		typeDTO = typeService.findByName(eu.cityopt.sim.eval.Type.DOUBLE.name);
    	
        if (typeDTO != null)
        {
        	newExtParam.setType(typeDTO);
        }

        newExtParam = extParamService.save(newExtParam, project.getPrjid());

        List<ExtParamValSetDTO> extParamSets = projectService.getExtParamValSets(project.getPrjid());

        // Add ext param val to all ext param val sets
        for (int i = 0; i < extParamSets.size(); i++)
        {
            ExtParamValSetDTO extParamValSet = extParamSets.get(i);

            ExtParamValDTO extParamVal = new ExtParamValDTO();
            extParamVal.setValue("0");
            extParamVal.setExtparam(newExtParam);

            HashSet<ExtParamValDTO> setExtVals = new HashSet<ExtParamValDTO>();
            setExtVals.add(extParamVal);

            try {
                extParamValSetService.addExtParamVals(extParamValSet.getExtparamvalsetid(), setExtVals);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
        }

        model.put("project", project);
       
        controllerService.getProjectExternalParameterValues(model,project);        
        controllerService.getComponentAndExternalParamValues(model, project);

        return "extparams";
    }

    @RequestMapping(value="deleteextparam", method=RequestMethod.GET)
    public String getDeleteExtParam(Map<String, Object> model,
        @RequestParam(value="extparamid", required=true) String strExtParamId) {
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
        model.put("project", project);

        try {
			extParamService.delete(Integer.parseInt(strExtParamId));
		} catch (NumberFormatException | EntityNotFoundException e) {
			e.printStackTrace();
		}
        
        ExtParamDTO extParam = new ExtParamDTO();
        model.put("extParam", extParam);

        controllerService.getDefaultExtParamVals(model, project.getPrjid());
        controllerService.getComponentAndExternalParamValues(model, project);
        
        return "extparams";
    }
    
    @RequestMapping(value="editextparam", method=RequestMethod.GET)
    public String getEditExtParam(Map<String, Object> model,
            @RequestParam(value="extparamid", required=true) String extparamid) {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_standard(project);

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

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);
        
        return "editextparam";
    }

    @RequestMapping(value="editextparam", method=RequestMethod.POST)
    public String getEditExtParamPost(ParamForm paramForm, Map<String, Object> model,
            @RequestParam(value="extparamid", required=true) String extParamId){
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_standard(project);

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
        updatedExtParam.setName(paramForm.getName());

        String strUnit = paramForm.getUnit();
        UnitDTO unit = null;
        
		try {
			unit = unitService.findByName(strUnit);
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}
        
		updatedExtParam.setUnit(unit);
        
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
        securityAuthorization.atLeastStandard_standard(project);

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
        
        ParamForm paramForm = new ParamForm();
        paramForm.setName(extParamVal.getExtparam().getName());
        paramForm.setValue(extParamVal.getValue());
        
        if (extParamVal.getExtparam().getUnit() != null) {
        	paramForm.setUnit(extParamVal.getExtparam().getUnit().getName());
        }
        
        model.put("extParamVal", extParamVal);
        model.put("paramForm", paramForm);

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);
        
        return "editextparamvalue";
    }

    @RequestMapping(value="editextparamvalue", method=RequestMethod.POST)
    public String getEditExtParamValPost(ParamForm paramForm, Map<String, Object> model,
        @RequestParam(value="extparamvalid", required=true) String extParamValId,
        @RequestParam(value="cancel", required=false) String cancel){
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_standard(project);

        if (cancel != null) {
            controllerService.getComponentAndExternalParamValues(model, project);
        	return "extparams";
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

        updatedExtParamVal.setValue(paramForm.getValue());

        // Commented because done later other way
        //extParamValService.save(updatedExtParamVal);

        ExtParamDTO extParam = updatedExtParamVal.getExtparam();
        try {
			extParam = extParamService.findByID(extParam.getExtparamid());
		} catch (EntityNotFoundException e3) {
			e3.printStackTrace();
		}
        
        String strUnit = paramForm.getUnit();
        
        if (strUnit != null && !strUnit.isEmpty())
        {
	        UnitDTO unit = null;
	        
			try {
				unit = unitService.findByName(strUnit);
			} catch (EntityNotFoundException e2) {
				e2.printStackTrace();
			}
	        
			extParam.setUnit(unit);
			extParamService.save(extParam, project.getPrjid());
        }
        
		int nDefaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());

		try {
			extParamValSetService.updateExtParamValInSet(nDefaultExtParamValSetId, updatedExtParamVal, null, false);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
        model.put("project", project);

        controllerService.getDefaultExtParamVals(model, project.getPrjid());
        controllerService.getComponentAndExternalParamValues(model, project);
        
        return "extparams";
    }

    @RequestMapping(value="createextparamset", method=RequestMethod.GET)
    public String getCreateExtParamSet(Map<String, Object> model) {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

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
            extParamVal.setValue("0");
            extParamVal = extParamValService.save(extParamVal);

            extParamVals.add(extParamVal);
        }

        extParamValSet.setName("New set");
        extParamValSet = extParamValSetService.save(extParamValSet);

        try {
            extParamValSetService.addExtParamVals(extParamValSet.getExtparamvalsetid(), extParamVals);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        extParamValSet = extParamValSetService.save(extParamValSet);

        Integer intSimModelId = projectService.getSimulationmodelId(project.getPrjid());
        int nSimModelId = 0;
        
        if (intSimModelId != null)
        {
        	nSimModelId = Integer.parseInt("" + intSimModelId);
        }
        
        project = projectService.save(project, nSimModelId, extParamValSet.getExtparamvalsetid());
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
        securityAuthorization.atLeastExpert_expert(project);

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        String newName = extParamValSet.getName();
        Integer defaultExtSetId = projectService.getDefaultExtParamSetId(project.getPrjid());

        if (defaultExtSetId != null)
        {
	        try {
	            extParamValSet = extParamValSetService.findByID(defaultExtSetId);
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
	
	        model.put("extParamValSet", extParamValSet);
	        model.put("extParamVals", listExtParamVals);
        }
        
        controllerService.getComponentAndExternalParamValues(model, project);
        
        return "extparams";
    }
    
    public ProjectDTO GetProject(Map<String,Object> model){    	
    	ProjectDTO project =(ProjectDTO) model.get("project");       
    	try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }    	
    	return project;
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
    public void SetUpSelectedComponent(Map<String,Object> model,String selectedCompId){
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
}
