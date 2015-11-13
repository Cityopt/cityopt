package eu.cityopt.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.service.AppUserService;
import eu.cityopt.service.ComponentService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamService;
import eu.cityopt.service.ExtParamValService;
import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.service.InputParamValService;
import eu.cityopt.service.InputParameterService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;


@Controller
@SessionAttributes({
    "project", "scenario", "optimizationset", "scengenerator", "optresults",
    "usersession", "user", "version"})
public class ControllerService {
	
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
	    AppUserService userService;
	    
	   
	    
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
	    
	    // Set up the project External Parameter values.
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
	    
	    // Set up component and External parameter Values according to project and model attributes.  
	    public void SetComponentAndExternalParamValues(Map<String,Object> model, ProjectDTO project ){
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
	    public List<ExtParamValDTO> FindExtParamVals(int nSelectedExtParamSetId){
	    	
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
	             // TODO Auto-generated catch block
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
	    
	    
	    
	    
	}

