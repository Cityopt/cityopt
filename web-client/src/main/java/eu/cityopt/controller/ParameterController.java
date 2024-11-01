package eu.cityopt.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
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
import eu.cityopt.security.SecurityAuthorization;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.ComponentInputParamDTOService;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.CopyService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValService;
import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;
import eu.cityopt.service.OutputVariableService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.TimeSeriesValService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.eval.SyntaxChecker;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SyntaxCheckerService;
import eu.cityopt.validators.InputParameterValidator;
import eu.cityopt.web.ParamForm;


@Controller
@SessionAttributes({
    "project", "scenario", "optimizationset", "scengenerator", "optresults",
    "usersession", "user", "version", "activeblock", "page"})
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

	@Autowired
	@Qualifier("inputParameterValidator")
    InputParameterValidator validator;

    @RequestMapping(value="projectparameters", method=RequestMethod.GET)
    public String projectParameters(Map<String, Object> model, 
        @RequestParam(value="selectedcompid", required=false) String selectedCompId,
        @RequestParam(value="comppagenum", required=false) String comppagenum,
        @RequestParam(value="inputpagenum", required=false) String inputpagenum) {
     	
    	ProjectDTO project = (ProjectDTO) model.get("project");
		
    	if (controllerService.NullCheck(project)){return "error";}
		
    	securityAuthorization.atLeastGuest_guest(project);
		model.put("project", project);

		if (comppagenum != null && !comppagenum.isEmpty())
		{
			int nCompPageNum = Integer.parseInt(comppagenum);
			controllerService.getComponents(model, project, nCompPageNum);
		}
		else
		{
			controllerService.getComponents(model, project, 1);
		}
		
		if (inputpagenum != null && !inputpagenum.isEmpty())
		{
			int nInputPageNum = Integer.parseInt(inputpagenum);
			controllerService.setUpSelectedComponent(model, selectedCompId, nInputPageNum);
		}
		else
		{
			controllerService.setUpSelectedComponent(model, selectedCompId, 1);
		}
		
        return "projectparameters";
    }

    @RequestMapping(value="extparams", method=RequestMethod.GET)
    public String extParams(Map<String, Object> model) {
     	
    	ProjectDTO project = (ProjectDTO) model.get("project");
		
    	if (controllerService.NullCheck(project)) {return "error";}
		
    	securityAuthorization.atLeastGuest_guest(project);
		model.put("project", project);

        controllerService.getProjectExternalParameterValues(model,project);        
        controllerService.getComponentAndExternalParamValues(model,project);        
        return "extparams";
    }

    @RequestMapping(value="selectextparamset", method=RequestMethod.GET)
    public String selectExtParamSet(Map<String, Object> model, 
        @RequestParam(value="selectedextparamsetid", required=false) String selectedExtParamSetId,
        HttpServletRequest request) {

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

            try {
            	project = projectService.save(project, projectService.getSimulationmodelId(project.getPrjid()), 
            		selectedExtParamValSet.getExtparamvalsetid());
            } catch (ObjectOptimisticLockingFailureException e){
				model.put("error", controllerService.getMessage("project_updated", request));
			}

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
            controllerService.clearOptResults(model);
            
            return "extparams";
        }

        List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
        model.put("extParamValSets", extParamValSets);
        model.put("project", project);

        return "selectextparamset";
    }
        
    @RequestMapping(value="createcomponent", method=RequestMethod.GET)
    public String createComponent(Model model) {
    	securityAuthorization.atLeastExpert_expert(model);
        ComponentDTO newComponent = new ComponentDTO();
        model.addAttribute("component", newComponent);

        return "createcomponent";
    }

    @RequestMapping(value="createcomponent", method=RequestMethod.POST)
    public String createComponentPost(ComponentDTO componentForm, Map<String, Object> model,
    	@RequestParam(value="cancel", required=false) String cancel,
    	HttpServletRequest request) {
    		 
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        
        if (cancel != null)
        {
        	 controllerService.getComponents(model, project, 1);        
             return "projectparameters";
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
        
        try {
        	componentService.save(component, project.getPrjid());
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

        try {
            model.put("project", projectService.findByID(project.getPrjid()));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        controllerService.getComponents(model, project, 1);        
        
        return "projectparameters";
    }
   
    @RequestMapping(value="editcomponent", method=RequestMethod.GET)
    public String editComponent(Map<String, Object> model, @RequestParam(value="componentid", required=true) String componentid) {

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
    public String editComponentPost(ComponentDTO component, Map<String, Object> model,
        @RequestParam(value="componentid", required=true) String componentid,
        HttpServletRequest request) 
    {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);
        
        String name = component.getName();
        
        if (name == null || name.isEmpty()) {
        	ComponentDTO newComponent = new ComponentDTO();
            model.put("component", newComponent);
            model.put("error", controllerService.getMessage("write_component_name", request));
        	return "editcomponent";
        }
        
    	SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
     	boolean isValid = checker.isValidTopLevelName(name);

     	if (!isValid)
     	{
            model.put("component", component);
            model.put("error", controllerService.getMessage("write_another_component_name", request));
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

        try {
        	componentService.save(oldComponent, project.getPrjid());
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}
        
        model.put("selectedcompid", oldComponent.getComponentid());
        model.put("selectedComponent",  oldComponent);

        try {
            model.put("project", projectService.findByID(project.getPrjid()));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        controllerService.getComponents(model, project, 1);        

        return "projectparameters";
    }
    
    @RequestMapping(value="editinputparameter", method=RequestMethod.GET)
    public String editInputParameter(Map<String, Object> model, 
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
        
        String min = inputParam.getLowerBound() != null ? inputParam.getLowerBound() : "";
        inputParamForm.setMin(min);
        String max = inputParam.getUpperBound() != null ? inputParam.getUpperBound() : "";
        inputParamForm.setMax(max);
        model.put("inputParamForm", inputParamForm);

        System.out.println("input " + inputParam.getName() + " " + inputParam.getDefaultvalue() + " " + inputParam.getLowerBound() + " " + inputParam.getUpperBound());

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
			List<Double> values = new ArrayList<Double>();
			List<String> times = new ArrayList<String>();
			
			while(timeSeriesIter.hasNext()) {
				TimeSeriesValDTO timeSeriesVal = timeSeriesIter.next();
				times.add(timeSeriesVal.getTime().toString());
				values.add(timeSeriesVal.getValue());
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
			// It's a value
		}

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);
        
        return "editinputparameter";
    }

    @RequestMapping(value="editinputparameter", method=RequestMethod.POST)
    public String editInputParameterPost(Map<String, Object> model, 
		ParamForm inputParamForm,
        @RequestParam(value="inputparamid", required=false) String inputParamId,
    	@RequestParam(value="cancel", required=false) String cancel,
    	BindingResult result, HttpServletRequest request) {
    	        
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

        int nInputParamId = Integer.parseInt(inputParamId);
        InputParameterDTO updatedInputParam = null;
        
        try {
            updatedInputParam = inputParamService.findByID(nInputParamId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        
        String selectedCompId = "" + updatedInputParam.getComponentComponentid();
        
        if (cancel != null) {
        	controllerService.getComponents(model, project, 1);
        	controllerService.setUpSelectedComponent(model, selectedCompId, 1);
            return "projectparameters";
        }
        
        updatedInputParam.setName(inputParamForm.getName());
        updatedInputParam.setDefaultvalue(inputParamForm.getValue());
        updatedInputParam.setLowerBound(inputParamForm.getMin());
        updatedInputParam.setUpperBound(inputParamForm.getMax());

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
        int componentId = updatedInputParam.getComponentComponentid();
        
        InputParameterDTO inputParam = null;

        try {
			inputParam = inputParamService.findByID(nInputParamId);
		} catch (EntityNotFoundException e2) {
			e2.printStackTrace();
		}
		
        if (inputParamForm.getName() == null || inputParamForm.getName().isEmpty() 
    		|| strUnit == null || strUnit.isEmpty())
        {
        	model.put("inputParam", inputParam);
            
            inputParamForm = new ParamForm();
            inputParamForm.setName(inputParam.getName());
            inputParamForm.setValue(inputParam.getDefaultvalue());
            inputParamForm.setMin(inputParam.getLowerBound());
            inputParamForm.setMax(inputParam.getUpperBound());
            model.put("inputParamForm", inputParamForm);

            model.put("selectedcompid", componentId);

            List<UnitDTO> units = unitService.findAll();
            model.put("units", units);
            model.put("error", controllerService.getMessage("write_input_parameter_and_select_unit", request));
        	return "editinputparameter";
        }
        
        InputParameterDTO testInput = inputParamService.findByNameAndComponent(inputParamForm.getName(), componentId);
        SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
     	boolean isValid = checker.isValidAttributeName(inputParamForm.getName());

        boolean bValueEdited = true;
        
        if (testInput == null) {
        	bValueEdited = false;
        }
        else
        {
	     	/*System.out.println("testinput " + testInput.getName() + " " + testInput.getDefaultvalue() + " " + testInput.getLowerBound()
	     		+ " " + testInput.getUpperBound() + " " + testInput.getUnit());
	        System.out.println("updatedInputParam " + updatedInputParam.getName() + " " + updatedInputParam.getDefaultvalue() + " " + updatedInputParam.getLowerBound()
	     		+ " " + updatedInputParam.getUpperBound() + " " + updatedInputParam.getUnit());
	
	        if ((controllerService.areStringsNotSet(testInput.getDefaultvalue(), updatedInputParam.getDefaultvalue()) || (testInput.getDefaultvalue() != null && testInput.getDefaultvalue().equals(updatedInputParam.getDefaultvalue())))
	         		&& (controllerService.areStringsNotSet(testInput.getLowerBound(), updatedInputParam.getLowerBound()) || (testInput.getLowerBound() != null && testInput.getLowerBound().equals(updatedInputParam.getLowerBound()))) 
	         		&& (controllerService.areStringsNotSet(testInput.getUpperBound(), updatedInputParam.getUpperBound()) || (testInput.getUpperBound() != null && testInput.getUpperBound().equals(updatedInputParam.getUpperBound())))
	         		&& (testInput.getUnit() != null && testInput.getUnit().getName() != null && updatedInputParam.getUnit() != null && testInput.getUnit().getName().equals(updatedInputParam.getUnit().getName())))
	        {
	     		bValueEdited = false; 
	        }*/
        }
        
   		if (!isValid || 
 			(testInput != null && !bValueEdited))
     	{
            model.put("error", controllerService.getMessage("write_another_input_parameter_name", request));
        	model.put("inputParam", inputParam);
            
        	inputParamForm = new ParamForm();
            inputParamForm.setName(inputParam.getName());
            inputParamForm.setValue(inputParam.getDefaultvalue());
            inputParamForm.setMin(inputParam.getLowerBound());
            inputParamForm.setMax(inputParam.getUpperBound());
            model.put("inputParamForm", inputParamForm);
            model.put("selectedcompid", componentId);

            List<UnitDTO> units = unitService.findAll();
            model.put("units", units);
            return "editinputparameter";
        }
         	
        validator.validate(updatedInputParam, result);
        
        if (result.hasErrors()) {
        	try {
				inputParam = inputParamService.findByID(nInputParamId);
				model.put("inputParam", inputParam);
	            
	            inputParamForm = new ParamForm();
	            inputParamForm.setName(inputParam.getName());
	            inputParamForm.setValue(inputParam.getDefaultvalue());
	            inputParamForm.setMin(inputParam.getLowerBound());
	            inputParamForm.setMax(inputParam.getUpperBound());
	            model.put("inputParamForm", inputParamForm);

	            List<UnitDTO> units = unitService.findAll();
	            model.put("units", units);
	        	
	        	model.put("error", result.getGlobalError().getCode());  
	        	//System.out.println("Error " + result.getGlobalError().toString());
	    	} 
			catch (EntityNotFoundException e) 
			{
				e.printStackTrace();
			}        	
	        return "editinputparameter";
		} else {
        	//System.out.println("Input param " + updatedInputParam.getDefaultvalue() + " " + updatedInputParam.getLowerBound());

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
	        
	        controllerService.getComponents(model, project, 1);        
	        controllerService.setUpSelectedComponent(model, selectedCompId, 1);
        	
	        return "projectparameters";
        }
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

                int nInputId = Integer.parseInt(inputId);
                InputParameterDTO inputParam = inputParamService.findByID(nInputId);

                if (cancel != null)
            	{
                	controllerService.getComponents(model, project, 1);
                	 
                	int componentId = inputParam.getComponentComponentid();
                	controllerService.setUpSelectedComponent(model, "" + componentId, 1);
                    return "projectparameters";
            	}

                InputStream stream = file.getInputStream();
                
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

            	if (inputParam.getName().equals(inputParamName))
            	{
            		inputParam = inputParamService.update(inputParam, inputParam.getComponentComponentid(), unit.getUnitid(), ts);
            	}
                else
                {
                	String error = controllerService.getMessage("name_conflict", request);
                	model.put("error", error);
                }

                stream.close();
                
                int componentId = inputParam.getComponentComponentid();
            	model.put("selectedcompid", componentId);
            	List<InputParameterDTO> inputParams = componentService.getInputParameters(componentId);
                model.put("inputParameters", inputParams);
                model.put("successText", controllerService.getMessage("file_imported", request));
            } catch (Exception e) {
            	e.printStackTrace();
            }
        } else {
        }
    	
        controllerService.getComponents(model, project, 1);
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
		@RequestParam(value="selectedcompid", required=false) String selectedCompId,
		@RequestParam(value="comppagenum", required=false) String comppagenum,
	    @RequestParam(value="outputpagenum", required=false) String outputpagenum) {
	     	
    	ProjectDTO project = (ProjectDTO) model.get("project");
		
    	if (controllerService.NullCheck(project)){return "error";}
			
    	securityAuthorization.atLeastGuest_guest(project);
		model.put("project", project);

		if (comppagenum != null && !comppagenum.isEmpty())
		{
			int nCompPageNum = Integer.parseInt(comppagenum);
			controllerService.getComponents(model, project, nCompPageNum);
		}
		else
		{
			controllerService.getComponents(model, project, 1);
		}

		if (selectedCompId != null && !selectedCompId.isEmpty())
		{
			int nSelectedCompId = Integer.parseInt(selectedCompId);
			
			ComponentDTO selectedComponent = null;
			List<OutputVariableDTO> outputVariables = null;
			
			try {
				selectedComponent = componentService.findByID(nSelectedCompId);
				outputVariables = componentService.getOutputVariables(nSelectedCompId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}

            model.put("outputpages", (int)Math.ceil((double)outputVariables.size() / 10));

            if (outputpagenum != null && !outputpagenum.isEmpty())
    		{
    			int nOutputPageNum = Integer.parseInt(outputpagenum);
    			
    		    if (nOutputPageNum > 0)
    		    {
    		    	outputVariables = outputVariables.subList((nOutputPageNum - 1) * 10, Math.min(nOutputPageNum * 10, outputVariables.size()));
    	            model.put("outputpagenum", nOutputPageNum);
    	        }
    	        else
    	        {
    	        	outputVariables = outputVariables.subList(0, Math.min(10, outputVariables.size()));
    	            model.put("outputpagenum", "1");
    	        }
    		}
    		else
    		{
    			outputVariables = outputVariables.subList(0, Math.min(10, outputVariables.size()));
	            model.put("outputpagenum", "1");
    		}
    	    
	    	model.put("selectedComponent",  selectedComponent);
			model.put("selectedcompid", selectedCompId);
			model.put("outputVariables", outputVariables);
		}

		model.put("project", project);
		
		return "outputvariables";
	}

    @RequestMapping(value="editoutputvariable", method=RequestMethod.POST)
    public String editOutputParameterPost(Map<String, Object> model, 
		ParamForm paramForm,
        @RequestParam(value="outputvarid", required=false) String outputVarId,
    	@RequestParam(value="cancel", required=false) String cancel,
    	HttpServletRequest request) {
            
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        if (cancel != null) {
   			controllerService.getComponents(model, project, 1);
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
        
        try {
        	outputVarService.save(updatedOutputVar);
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

        model.put("selectedcompid", componentId);

        try {
            model.put("selectedComponent", componentService.findByID(componentId));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        model.put("project", project);
        
        controllerService.getComponents(model, project, 1);
        
        List<OutputVariableDTO> outputVars = componentService.getOutputVariables(componentId);
        model.put("outputVariables", outputVars);
        
        return "outputvariables";
    }
    
    @RequestMapping(value="createinputparameter", method=RequestMethod.GET)
    public String createInputParameter(Map<String, Object> model,
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
            controllerService.getComponents(model, project, 1);        
            return "projectparameters";
        }

        int nSelectedCompId = Integer.parseInt(strSelectedCompId);
        
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
    public String createInputParamPost(ParamForm inputParamForm, Map<String, Object> model,
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
            controllerService.getComponents(model, project, 1);
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
        
        InputParameterDTO testInput = inputParamService.findByNameAndComponent(inputParamForm.getName(), nSelectedCompId);
        SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
     	boolean isValid = checker.isValidAttributeName(inputParamForm.getName());

     	if (!isValid || testInput != null)
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
        
        try {
        	inputParamService.save(inputParam, component.getComponentid(), unit.getUnitid(), null);
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

        controllerService.setUpSelectedComponent(model, strSelectedCompId, 1);
        controllerService.getProjectExternalParameterValues(model, project);
        controllerService.getComponents(model, project, 1);        
        
        return "projectparameters";
    }

    @RequestMapping(value="deleteinputparameter", method=RequestMethod.GET)
    public String deleteInputParam(Map<String, Object> model,
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
        controllerService.getComponents(model, project, 1);
        
        return "projectparameters";
    }

    @RequestMapping(value="createextparam", method=RequestMethod.GET)
    public String createExtParam(Map<String, Object> model) {
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
    public String createExtParamPost(ExtParamDTO extParam, Map<String, Object> model,
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

        try {
        	newExtParam = extParamService.save(newExtParam, project.getPrjid());
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

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

        model.put("info", controllerService.getMessage("ext_parameter_created", request));
        model.put("project", project);
       
        controllerService.getProjectExternalParameterValues(model,project);        
        controllerService.getComponentAndExternalParamValues(model, project);
        controllerService.clearOptResults(model);
        
        return "extparams";
    }

    @RequestMapping(value="deleteextparam", method=RequestMethod.GET)
    public String deleteExtParam(Map<String, Object> model,
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
        controllerService.clearOptResults(model);
        
        return "extparams";
    }
    
    @RequestMapping(value="editextparam", method=RequestMethod.GET)
    public String editExtParam(Map<String, Object> model,
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
    public String editExtParamPost(ParamForm paramForm, Map<String, Object> model,
        @RequestParam(value="extparamid", required=true) String extParamId,
        HttpServletRequest request) {
    
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
        
		try {
			extParamService.save(updatedExtParam, project.getPrjid());
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

        model.put("project", project);

        return "editproject";
    }

    @RequestMapping(value="editextparamvalue", method=RequestMethod.GET)
    public String editExtParamVal(Map<String, Object> model,
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
        paramForm.setComment(extParamVal.getComment());
        
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
    public String editExtParamValPost(ParamForm paramForm, Map<String, Object> model,
        @RequestParam(value="extparamvalid", required=true) String extParamValId,
        @RequestParam(value="cancel", required=false) String cancel,
        HttpServletRequest request) 
    {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_standard(project);

        if (cancel != null) {
            controllerService.getProjectExternalParameterValues(model,project);        
            controllerService.getComponentAndExternalParamValues(model,project);        
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

        if (updatedExtParamVal.getExtparam().getType().getTypeid() < 4)
        {
        	updatedExtParamVal.setValue(paramForm.getValue());
        }

        updatedExtParamVal.setComment(paramForm.getComment());

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
			
			try {
				extParamService.save(extParam, project.getPrjid());
	        } catch (ObjectOptimisticLockingFailureException e){
				model.put("error", controllerService.getMessage("project_updated", request));
			}
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
        controllerService.clearOptResults(model);
        
        return "extparams";
    }

    @RequestMapping(value="createextparamset", method=RequestMethod.GET)
    public String createExtParamSet(Map<String, Object> model,
		HttpServletRequest request) {
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

        if (extParams.isEmpty())
        {
        	model.put("error", controllerService.getMessage("external_parameters_missing", request));
        	List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
            model.put("extParamValSets", extParamValSets);
            model.put("project", project);
        	return "selectextparamset";
        }
        
        while (iter.hasNext())
        {
            ExtParamDTO extParam = iter.next();

            ExtParamValDTO extParamVal = new ExtParamValDTO();
            extParamVal.setExtparam(extParam);
            extParamVal.setValue("0");
            
            try {
            	extParamVal = extParamValService.save(extParamVal);
            } catch (ObjectOptimisticLockingFailureException e){
    			model.put("error", controllerService.getMessage("project_updated", request));
    		}

            extParamVals.add(extParamVal);
        }

        extParamValSet.setName("New set");
        
        try {
        	extParamValSet = extParamValSetService.save(extParamValSet);
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

        try {
            extParamValSetService.addExtParamVals(extParamValSet.getExtparamvalsetid(), extParamVals);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        try {
        	extParamValSet = extParamValSetService.save(extParamValSet);
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

        Integer intSimModelId = projectService.getSimulationmodelId(project.getPrjid());
        int nSimModelId = 0;
        
        if (intSimModelId != null)
        {
        	nSimModelId = Integer.parseInt("" + intSimModelId);
        }
        
        try {
        	project = projectService.save(project, nSimModelId, extParamValSet.getExtparamvalsetid());
        } catch (ObjectOptimisticLockingFailureException e){
			model.put("error", controllerService.getMessage("project_updated", request));
		}

        model.put("project", project);

        model.put("extParamValSet", extParamValSet);
        model.put("extParamVals", extParamVals);

        return "createextparamset";
    }

    @RequestMapping(value="createextparamset", method=RequestMethod.POST)
    public String createExtParamSetPost(ExtParamValSetDTO extParamValSet, Map<String, Object> model,
		HttpServletRequest request) {
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
	        
	        try {
	        	extParamValSet = extParamValSetService.save(extParamValSet);
	        } catch (ObjectOptimisticLockingFailureException e){
				model.put("error", controllerService.getMessage("project_updated", request));
			}
	
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
        controllerService.clearOptResults(model);
        
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
