package eu.cityopt.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.config.AppMetadata;
import eu.cityopt.repository.ProjectRepository;
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
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;



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
    ControllerService controlerService;
          
       
    @PreAuthorize("hasRole('ROLE_Administrator') or ("
		    +" hasRole('ROLE_Expert') and ("
		    	+" hasPermission(#model.get('project'),'ROLE_Administrator') or"
		    	+" hasPermission(#model.get('project'),'ROLE_Expert')))")
    
    @RequestMapping(value="projectparameters", method=RequestMethod.GET)
    public String getProjectParameters(Map<String, Object> model, 
            @RequestParam(value="selectedcompid", required=false) String selectedCompId) {
     
    	ProjectDTO project = (ProjectDTO) model.get("project");
    	if (controlerService.NullCheck(project)){return "error";}
    	controlerService.SetUpSelectedComponent(model, selectedCompId);
        model.put("project", project);
        controlerService.SetProjectExternalParameterValues(model,project);        
        controlerService.SetComponentAndExternalParamValues(model,project);        
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

            List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
            model.put("components", components);

            Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
            model.put("extParams", extParams);
            
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
        component.setName(componentForm.getName().trim());
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
        List<UnitDTO> units = unitService.findAll();
        model.addAttribute("units", units);
        
        
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
        updatedInputParam.setUnit(unit);
        int componentId = updatedInputParam.getComponentComponentid();//inputParamService.getComponentId(updatedInputParam.getInputid());

        try {
			inputParamService.update(updatedInputParam, componentId, unit.getUnitid());
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
        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
        model.put("extParams", extParams);
        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);
        List<InputParameterDTO> inputParams = componentService.getInputParameters(componentId);
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
            Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
            model.put("extParams", extParams);
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
        inputParam.setName(inputParamForm.getName().trim());
        inputParam.setDefaultvalue(inputParamForm.getDefaultvalue());
        inputParam.setType(typeService.findByName(eu.cityopt.sim.eval.Type.DOUBLE.name));
        UnitDTO unit = unitService.save(new UnitDTO());
        inputParamService.save(inputParam, component.getComponentid(), unit.getUnitid());

        model.put("selectedcompid", nSelectedCompId);
        model.put("selectedComponent",  component);
        model.put("project", project);

        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
        model.put("extParams", extParams);
        
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
        newExtParam.setName(extParam.getName().trim());
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

    @RequestMapping(value="deleteextparam", method=RequestMethod.GET)
    public String getDeleteExtParam(Map<String, Object> model,
        @RequestParam(value="extparamid", required=true) String strExtParamId) {
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

        try {
			extParamService.delete(Integer.parseInt(strExtParamId));
		} catch (NumberFormatException | EntityNotFoundException e) {
			e.printStackTrace();
		}
        
        ExtParamDTO extParam = new ExtParamDTO();
        model.put("extParam", extParam);

        List<ExtParamValDTO> extParamVals = null;

        int defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
        if (defaultExtParamValSetId != 0)
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

        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
        model.put("extParams", extParams);
        
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

        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
        model.put("extParams", extParams);
        
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

        extParamValSet.setName("New set");
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

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
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

        model.put("extParamValSet", extParamValSet);
        model.put("extParamVals", listExtParamVals);
        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
        model.put("extParams", extParams);
        
        return "projectparameters";
    }
    
    ///------- Help Methods-------////	
	// Finds project By model and project id;
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
    
    public void SetProjectExternalParameterValues(Map<String,Object> model, ProjectDTO project ){
    	 List<ExtParamValDTO> extParamVals = null;
         int defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
         if (defaultExtParamValSetId != 0)
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
    public void SetComponentAndExternalParamValues(Map<String,Object> model, ProjectDTO project ){
    	List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);
        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
        model.put("extParams", extParams);
    }    
}