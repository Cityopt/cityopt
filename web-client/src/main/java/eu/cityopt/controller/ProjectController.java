package eu.cityopt.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.TimeSeriesDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.config.AppMetadata;
//Contains Forms for UI
import eu.cityopt.forms.ExternParamIDForm;
import eu.cityopt.sim.eval.Type;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.DTO.UserGroupDTO;
import eu.cityopt.DTO.UserGroupProjectDTO;
import eu.cityopt.config.AppMetadata;
//Contains Forms for UI
import eu.cityopt.forms.ExternParamIDForm;
import eu.cityopt.model.UserGroupProject;
import eu.cityopt.opt.io.TimeSeriesData;
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
import eu.cityopt.service.ProjectManagementService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioGeneratorService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.SimulationModelService;
import eu.cityopt.service.SimulationResultService;
import eu.cityopt.service.TimeSeriesService;
import eu.cityopt.service.TimeSeriesValService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.service.UserGroupProjectService;
import eu.cityopt.service.UserGroupService;
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.ParamForm;
import eu.cityopt.web.RoleForm;
import eu.cityopt.web.ScenarioParamForm;
import eu.cityopt.web.UnitForm;
import eu.cityopt.web.UserManagementForm;
import eu.cityopt.web.UserSession;
import eu.cityopt.service.ImportService;

/**
 * @author Olli Stenlund
 *
 */
@Controller
@SessionAttributes({
    "project", "scenario", "optimizationset", "scengenerator", "optresults",
    "usersession", "user", "version"})
public class ProjectController {

    @Autowired
    AppMetadata appMetaData;

    @Autowired
    ProjectService projectService; 

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ScenarioService scenarioService;

    @Autowired
    AppUserService userService;

    @Autowired
    UserGroupService userGroupService;

    @Autowired
    UserGroupProjectService userGroupProjectService;

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
    SimulationModelService simModelService;

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

    @Autowired
    ImportService importService;

    @Autowired
    MessageSource resource;
    
    @Autowired
    ProjectManagementService projectManagementService;
    
    @Autowired
    ControllerService controllerService;
    
    @Autowired
    SecurityAuthorization securityAuthorization;
    
    // Page where user is redirected if authorization fails.    
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String accessDenied(Map<String, Object> model) {      
    	 	return "403";
    }  
    
    @Secured({"ROLE_Administrator","ROLE_Expert"})
    @RequestMapping(value="createproject", method=RequestMethod.GET)
    public String getCreateProject(Map<String, Object> model) {
        ProjectDTO newProject = new ProjectDTO();
        model.put("newProject", newProject);

        AppUserDTO user = (AppUserDTO) model.get("user");
        
        if (user != null)
        {
        	model.put("user", user);
        }

        return "createproject";
    }

    @Secured({"ROLE_Administrator","ROLE_Expert"})
    @RequestMapping(value = "createproject", method = RequestMethod.POST)
    public String getCreateProjectPost(Map<String, Object> model,
            @Validated @ModelAttribute("newProject") ProjectDTO projectForm, 
            BindingResult bindingResult,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {

            return "createproject";
        } else {

            ProjectDTO project = new ProjectDTO();
            project.setName(projectForm.getName().trim());
            String desc = projectForm.getDescription();

            if (desc != null && !desc.isEmpty())
            {
                project.setDescription(projectForm.getDescription().trim());
            }

            project.setLocation(projectForm.getLocation().trim());
            project.setDesigntarget(projectForm.getDesigntarget().trim());

            // Create default ext param val set
            /*ExtParamValSetDTO extParamValSet = new ExtParamValSetDTO();
			extParamValSet.setName("default set");
			extParamValSet = extParamValSetService.save(extParamValSet);			
			project.setExtparamvalset(extParamValSet);*/

            if (projectService.findByName(project.getName()) == null) {
               
                /*
				AppUserDTO user = (AppUserDTO) model.get("user");				
				UserGroupProjectDTO userGroupProject = new UserGroupProjectDTO();
				userGroupProject.setAppuser(user);
				userGroupProject.setProject(project);

				UserGroupDTO userGroup = userGroupService.findByGroupName("Administrator").get(0);
				userGroupProject.setUsergroup(userGroup);
				userGroupProject = userGroupProjectService.save(userGroupProject);
                 */           
                

                //project = projectService.save(project, 0, 0);      
                
                
                // Set up the project Rights.
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String user= ((UserDetails)principal).getUsername();
                project = projectManagementService.createProjectWithAdminUser(project, user);
                
                /*
                project = projectService.save(project, 0, 0);			
                model.put("project", project);
                model.remove("scenario");
                model.remove("optimizationset");
                */
                
                controllerService.clearSession(model, request);
                model.put("project", project);
                model.put("success",true);              

                return "createproject";
                //return "editproject";
            } else {
                model.put("newProject", new ProjectDTO());                        
                model.put("success",false);                           
                return "createproject";
            }
        }
    }
    
    /*
			Locale locale = LocaleContextHolder.getLocale();			
			// TODO fix
			//@ author: Markus Turunene 8.7.2015
			// Language integration for messages.			
			//I'm calling message from resources can be language implemented:
			// 3 part message: Project+(current created project)+is succesfully created

			String projectCreation =resource.getMessage("project", new Object[1], locale)
			+" "+project.getName()+" "+resource.getMessage("succesfully_created", new Object[1], locale);			
			model.put("successful", projectCreation);

			return "createproject";			
			//return "editproject";
			}
			else{
				model.put("project", project);				
				//@ author: Markus Turunene date 8.7.2015 //Calling a locale resource for the error message == Require file resource for working.
				Locale locale = LocaleContextHolder.getLocale();
				String Errormessage= resource.getMessage("error_allready_exist", new Object[1], locale);
				model.put("errorMessage", Errormessage);
				return "createproject";
			}	
			//return "error";
     */
        
    @PreAuthorize("hasAnyRole('ROLE_Administrator','ROLE_Expert','ROLE_Standard','ROLE_Guest')")   
    // This security annotation allow access to every authorized user.
    @RequestMapping(value="openproject", method=RequestMethod.GET)
    public String getStringProjects(Map<String, Object> model)
    {
    	// Fine if Administrator
    	List<ProjectDTO> projects= new ArrayList<ProjectDTO>();    			
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ( principal != null && principal instanceof UserDetails) {
            Collection<? extends GrantedAuthority> authorites = ((UserDetails)principal).getAuthorities();
           
            
            for(int i=0;authorites.size()>i;i++){
            	GrantedAuthority accessProvided = (GrantedAuthority) authorites.toArray()[i];
            	if (accessProvided.getAuthority().equals("ROLE_Administrator")){
            		projects = projectService.findAll();               
            	}     	
            }
            if(projects.size()==0){
            	 String username= ((UserDetails)principal).getUsername();
            	 int userID;
				try {
					userID = userService.findByName(username).getUserid();
					projects = userGroupProjectService.findProjectsByUser(userID);
				} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}            	 
            }            
        } else {
        	//
        }                
        model.put("projects", projects);
        return "openproject";
    }	
   
    //@PreAuthorize("hasPermission(#prjid,'ROLE_Administrator')")     
    @RequestMapping(value="editproject", method=RequestMethod.GET)
    public String getEditProject(Map<String, Object> model, HttpServletRequest request, 
		@RequestParam(value="prjid", required=false) String prjid) {
       
    	if (prjid != null)
        {
    		// Open a new project
    		
    		securityAuthorization.atLeastGuest_guest(prjid);
    		
            AppUserDTO user = (AppUserDTO) model.get("user");
            int nProjectId = Integer.parseInt(prjid);
            ProjectDTO project = null;

            try {
                project = projectService.findByID(nProjectId);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
            
            controllerService.getEnergyModelInfo(model, project.getPrjid());

            controllerService.clearSession(model, request);
            model.put("project", project);
        }
        else if (model.containsKey("project"))
        {
            ProjectDTO project = (ProjectDTO) model.get("project");
            securityAuthorization.atLeastGuest_guest(project);

            try {
                project = projectService.findByID(project.getPrjid());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
            
            controllerService.getEnergyModelInfo(model, project.getPrjid());
                        
            model.put("project", project);
        }
        else if (!model.containsKey("project"))
        {
            ProjectDTO newProject = new ProjectDTO();
            model.put("newProject", newProject);
            model.remove("scenario");
            return "createproject";
        }

        return "editproject";
    }
    
    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    public String uploadFileHandler(Map<String, Object> model,
            @RequestParam(value="detailLevel", required=false) String detailLevel,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                ProjectDTO project = (ProjectDTO) model.get("project");
                if (project == null)
                {
                    return "error";
                }
                securityAuthorization.atLeastStandard_standard(project);

                try {
                    project = projectService.findByID(project.getPrjid());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                model.put("project", project);

                // Put in the right values if known.  If unknown, leave null.
                //Set<String> simulatorNames = SimulatorManagers.getSimulatorNames();
                String simulatorName = null;
                Instant timeOrigin = null;

                List<Locale.LanguageRange> languageList = Locale.LanguageRange.parse(
                        RequestContextUtils.getLocale(request).getLanguage() + ",en");
                importExportService.importSimulationModel(project.getPrjid(), 0, languageList, bytes, simulatorName, timeOrigin);
                importExportService.importModelInputsAndOutputs(project.getPrjid(), 0);
                System.out.println("Model imported");
                
                Integer nSimulationModelId = projectService.getSimulationmodelId(project.getPrjid());
                
                if (nSimulationModelId != null)
                {
                	model.put("showInfo", true);
                }
                
                controllerService.getEnergyModelInfo(model, project.getPrjid());
                
                model.put("success",true);
                
                //return "infopage";
            } catch (Exception e) {
            	model.put("success",false);
            	e.printStackTrace();
                return "You failed to upload => " + e.getMessage();
            }
        } else {
        }
        return "editproject";
    }

    @RequestMapping(value = "importstructurefile", method = RequestMethod.POST)
    public String importStructureFile(Map<String, Object> model, @RequestParam("file") MultipartFile file) {

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

                InputStream structureStream = file.getInputStream();
                importExportService.importSimulationStructure(project.getPrjid(), structureStream);
                structureStream.close();
            } catch (Exception e) {
            	//System.out.println("" + e.getStackTrace().toString());
            	e.printStackTrace();
                return "You failed to upload => " + e.getStackTrace().toString();
            }
        } else {
        }
        return "importdata";
    }

    @RequestMapping(value = "exportstructurefile", method = RequestMethod.GET)
    public void exportStructureFile(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        ProjectDTO project = null;

        try {
            project = (ProjectDTO) model.get("project");

            if (project == null)
            {
                return;
            }
            securityAuthorization.atLeastStandard_guest(project);
            

            try {
                project = projectService.findByID(project.getPrjid());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            model.put("project", project);
        } catch (Exception e) {
            return;
        }
        // set headers for the response
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"project.csv\"");
        response.setHeader(headerKey, headerValue);

        OutputStream outputStream = response.getOutputStream();

        try {
            importExportService.exportSimulationStructure(project.getPrjid(), outputStream);
        } catch (EntityNotFoundException | ScriptException e) {
            //FIXME This is not the right way to handle exceptions.
            e.printStackTrace();
        }

        outputStream.close();
    }

    @RequestMapping(value = "exportmetrics", method = RequestMethod.GET)
	public void exportMetrics(Map<String, Object> model, HttpServletRequest request, 
		HttpServletResponse response) {

    	//System.out.println("Export metrics");
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
	        
	        List<ExtParamValSetDTO> extValSets = projectService.getExtParamValSets(project.getPrjid());
	        Set<Integer> extParamValSetIds = new HashSet<Integer>();
	        
	        for (ExtParamValSetDTO extValSet : extValSets)
	        {
	        	extParamValSetIds.add(extValSet.getExtparamvalsetid());
	        }

	        Set<ScenarioDTO> scenarios = projectService.getScenarios(project.getPrjid());
	        Set<Integer> scenarioIds = new HashSet<Integer>();
	        
	        for (ScenarioDTO scenarioTemp : scenarios)
	        {
	        	scenarioIds.add(scenarioTemp.getScenid());
	        }

	    	//System.out.println("Starting export metrics");
	        importExportService.exportMetricValues(scenarioPath, timeSeriesPath, project.getPrjid(), extParamValSetIds, scenarioIds);

	        fileTimeSeries = timeSeriesPath.toFile();
	        fileScenario = scenarioPath.toFile();
	        files.add(fileScenario);
	        files.add(fileTimeSeries);

			// Set the content type based to zip
			response.setContentType("Content-type: text/zip");
			response.setHeader("Content-Disposition", "attachment; filename=metrics.zip");
	
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
			out.close();
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
    
    @RequestMapping(value = "importextparam", method = RequestMethod.POST)
    public String importExtParamFile(Map<String, Object> model, @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                ProjectDTO project = (ProjectDTO) model.get("project");

                if (project == null)
                {
                    return "error";
                }
                securityAuthorization.atLeastStandard_standard(project);

                try {
                    project = projectService.findByID(project.getPrjid());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                model.put("project", project);

                InputStream stream = file.getInputStream();
                System.out.println("Starting import time series");
                
                List<ExtParamValDTO> extParamVals = null;
                ExtParamValSetDTO extParamValSet = null;
                
                Map<String, TimeSeriesDTOX> tsData = importExportService.readTimeSeriesCsv(project.getPrjid(), stream);
                Set<String> keys = tsData.keySet();
                Iterator<String> iter = keys.iterator();
                
                while (iter.hasNext())
                {
                	String extParamName = iter.next();
                	String baseName = extParamName;
                    TimeSeriesDTOX ts = tsData.get(extParamName);
	                
                	ExtParamDTO extParam = new ExtParamDTO();
                	boolean bFoundName = true;
                	int i = 2;
                	
                	// Find if the name is already used and create a new name
                	while (bFoundName)
                	{
                		System.out.println("Finding name " + extParamName);
                        
	                	ExtParamDTO testExtParam = extParamService.findByName(extParamName, project.getPrjid());

	                	if (testExtParam != null)
	                	{
	                		bFoundName = true;
	                	}
	                	else
	                	{
	                		bFoundName = false;
	                		break;
	                	}

	                	extParamName = baseName + "_" + i;
	                	i++;
                	}
	                
	                extParam.setName(extParamName);
	                TypeDTO type = typeService.findByName(eu.cityopt.sim.eval.Type.TIMESERIES_STEP.name);
	                extParam.setType(type);
	                extParam = extParamService.save(extParam, project.getPrjid());
	                
	                ExtParamValDTO extParamVal = new ExtParamValDTO();
	                extParamVal.setExtparam(extParam);
	                extParamVal = extParamValService.save(extParamVal);

	                int defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
	                
	                System.out.println("Default ext param set id " + defaultExtParamValSetId);
                    
	                if (defaultExtParamValSetId != 0)
	        		{
	                	try {
		                    extParamValSet = extParamValSetService.findByID(defaultExtParamValSetId);
		                    model.put("extParamValSet", extParamValSet);
		                } catch (EntityNotFoundException e1) {
		                    e1.printStackTrace();
		                }
		    		}
	                
	                if (extParamValSet != null)
	                {
		                extParamValSetService.updateExtParamValInSetOrClone(extParamValSet.getExtparamvalsetid(), extParamVal, ts);
	                }	                
	                extParamValSet = extParamValSetService.findByID(defaultExtParamValSetId);
	                model.put("extParamValSet", extParamValSet);
	                
	                try {
	                    extParamVals = extParamValSetService.getExtParamVals(defaultExtParamValSetId);
	                } catch (EntityNotFoundException e) {
	                    e.printStackTrace();
	                }
	                model.put("extParamVals", extParamVals);
                }
                
                stream.close();
                System.out.println("Finished importing time series");

                List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
	            model.put("components", components);
	            Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
	            model.put("extParams", extParams);

            } catch (Exception e) {
            	e.printStackTrace();
            }
        } else {
        }
        return "projectparameters";
    }

    @RequestMapping(value = "exportextparam", method = RequestMethod.GET)
    public void exportExtParam(Map<String, Object> model, 
		@RequestParam(value="extparamvalid", required=true) String strExtParamValId, 
		@RequestParam(value="extparamvalsetid", required=true) String strExtParamValSetId,
   		HttpServletRequest request, HttpServletResponse response) throws IOException {

        ProjectDTO project = null;
        ExtParamValDTO extParamVal = null;
        
        try {
            project = (ProjectDTO) model.get("project");

            if (project == null)
            {
                return;
            }
            securityAuthorization.atLeastStandard_standard(project);

            try {
                project = projectService.findByID(project.getPrjid());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            model.put("project", project);
            
            extParamVal = extParamValService.findByID(Integer.parseInt(strExtParamValId));
        } catch (Exception e) {
        	e.printStackTrace();
            return;
        }
        
        // set headers for the response
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"external_parameters.csv\"");
        response.setHeader(headerKey, headerValue);

        OutputStream outputStream = response.getOutputStream();

        try {
            importExportService.exportExtParamTimeSeries(Integer.parseInt(strExtParamValSetId), outputStream, extParamVal.getExtparam());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        outputStream.close();
    }

    @Secured({"ROLE_Administrator","ROLE_Expert"})
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
                securityAuthorization.atLeastExpert_admin(project);

                try {
                    //project = projectService.findByID(project.getPrjid());                	
                	//Fix issue: Bug #10864 //Trim because we handling raw input data method .
                	//trim() - method deletes spaces end and start.
                    project.setName(projectForm.getName().trim());
                    project.setDescription(projectForm.getDescription().trim());
                    project.setLocation(projectForm.getLocation().trim());
                    project.setDesigntarget(projectForm.getDesigntarget().trim());

                    Integer defaultExtSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
                    int nDefaultExtSetId = 0;
                    
                    if (defaultExtSetId != null)
                    {
                    	nDefaultExtSetId = defaultExtSetId;
                    }
                    
                    project = projectService.save(project, projectService.getSimulationmodelId(project.getPrjid()), nDefaultExtSetId);
                    controllerService.getEnergyModelInfo(model, project.getPrjid());
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

    @Secured({"ROLE_Administrator","ROLE_Expert"})
    @RequestMapping(value="closeproject", method=RequestMethod.GET)
    public String getCloseProjects(Map<String, Object> model, HttpServletRequest request)
    {
        model.remove("project");
        model.remove("scenario");
        model.remove("optimizationset");
        model.remove("scengenerator");
        model.remove("optresults");
        return "start";
    }	

    /*
    @RequestMapping(value="logout", method=RequestMethod.GET)
    public String getLogout(Map<String, Object> model, HttpServletRequest request)
    {
        model.remove("project");
        model.remove("scenario");
        model.remove("optimizationset");
        model.remove("scengenerator");
        model.remove("optresults");
        model.remove("usersession");
        model.remove("user");
        request.getSession().invalidate();
        return "logout";
    }	
     */
    
    //@Secured({"ROLE_Administrator","ROLE_Expert"})
    @PreAuthorize("hasRole('ROLE_Administrator') or"
    	    	+" isAuthenticated() and ("
    	    	+" hasPermission(#prjid,'ROLE_Administrator') or"
    	    	+" hasPermission(#prjid,'ROLE_Expert'))")    
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

    // @author Markus Turunen
    // date: 25.6.2015
    // This method handles project cloning.	
    @Secured({"ROLE_Administrator","ROLE_Expert"})
    @RequestMapping(value="cloneproject", method=RequestMethod.GET)
    public String CloneScenario(Map<String, Object> model, @RequestParam(value = "projectid") String projectid) {
      
    	ProjectDTO project= this.ParseProjectIDtoProjectDTO(model, projectid);	
    	int nProjectId = Integer.parseInt(projectid);    	
    	String clonename = project.getName()+"(copy)";
        int i=0;
        while(projectService.findByName(clonename)!=null){
            i++;
            clonename = project.getName()+"(copy)("+i+")";
        }	
        try {
            ProjectDTO ProjectCloner = copyService.copyProject(nProjectId,clonename);
        } catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }		
        List<ProjectDTO> projects = projectService.findAll();
        model.put("projects", projects);
        return "openproject";
        // ;(projectid, clonename, true, false, true, false);
    }
    
    // Get Project from database based on model and projectID
    public ProjectDTO ParseProjectIDtoProjectDTO(Map<String, Object> model, String projectid){
    	
    	ProjectDTO project = (ProjectDTO) model.get("project");
    	int nProjectId = Integer.parseInt(projectid);
    	
    	try {
    		project = projectService.findByID(nProjectId);
    	} catch (EntityNotFoundException e1) {
    		e1.printStackTrace();
    	}    	
    	model.put("project", project);
    	return project;
    }
   
    @Secured({"ROLE_Administrator"})
    @RequestMapping(value="units", method=RequestMethod.GET)
    public String getUnits(Model model){
        List<UnitDTO> units = unitService.findAll();
        model.addAttribute("units", units);
        return "units";
    }

    //@Secured({"ROLE_Administrator","ROLE_Expert"})
    @PreAuthorize("hasRole('ROLE_Administrator') or"
    	+" isAuthenticated() and ("
    	+" hasPermission(#prjid,'ROLE_Administrator') or"
    	+" hasPermission(#prjid,'ROLE_Expert'))")    
    @RequestMapping(value="deleteunit",method=RequestMethod.GET)
    public String getDeleteUnit(Map<String, Object> model, @RequestParam(value="unitid", required=true) String unitid) {
        if (unitid != null)
        {
            UnitDTO unit = null;
            int nUnitId = Integer.parseInt(unitid);
            
            try {
                unit = unitService.findByID(Integer.parseInt(unitid));
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            try {
                unitService.delete(nUnitId);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            } catch(ObjectOptimisticLockingFailureException e) {
                model.put("errorMessage", "This project has been updated in the meantime, please reload.");
            }
        }

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);

        return "units";
    }

    @Secured({"ROLE_Administrator"})
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

    @Secured({"ROLE_Administrator"})
    @RequestMapping(value="createunit", method=RequestMethod.POST)
    public String getCreateUnitPost(UnitForm unitForm, Model model) {

        if (unitForm != null)
        {
            if (unitForm.getName() != null && unitForm.getType() != null && !unitForm.getName().isEmpty())
            {
                UnitDTO newUnit = new UnitDTO();
                newUnit.setName(unitForm.getName().trim());
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

    @Secured({"ROLE_Administrator","ROLE_Expert"})
    @RequestMapping(value="paramreliability", method=RequestMethod.GET)
    public String getParamReliability(Model model){

        return "paramreliability";
    }

    @PreAuthorize("hasAnyRole('ROLE_Administrator','ROLE_Expert','ROLE_Standard')")
    @RequestMapping(value="importdata", method=RequestMethod.GET)
    public String getImportData(Model model){
        return "importdata";
    }

    @PreAuthorize("hasAnyRole('ROLE_Administrator','ROLE_Expert','ROLE_Standard')")
    @RequestMapping(value="exportdata", method=RequestMethod.GET)
    public String getExportData(Model model){

        return "exportdata";
    }

    @PreAuthorize("hasAnyRole('ROLE_Administrator','ROLE_Expert','ROLE_Standard')")
    @RequestMapping(value="exportextparamsets", method=RequestMethod.GET)
    public void getExportExtParamSets(Map<String, Object> model, HttpServletRequest request, 
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
	        scenarioPath = tempDir.getPath().resolve("ext_param_sets.csv");
	        
	        List<ExtParamValSetDTO> extValSets = extParamValSetService.findAll();
	        Set<Integer> extParamValSetIds = new HashSet<Integer>();
	        
	        for (ExtParamValSetDTO extValSet : extValSets)
	        {
	        	extParamValSetIds.add(extValSet.getExtparamvalsetid());
	        }

	        Integer defaultExtSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
	        
	        if (defaultExtSetId == null)
	        {
	        	return;
	        }
	        
	    	importExportService.exportExtParamValSets(scenarioPath, timeSeriesPath, project.getPrjid(), defaultExtSetId);

	        fileTimeSeries = timeSeriesPath.toFile();
	        fileScenario = scenarioPath.toFile();
	        files.add(fileScenario);
	        files.add(fileTimeSeries);

			// Set the content type based to zip
			response.setContentType("Content-type: text/zip");
			response.setHeader("Content-Disposition", "attachment; filename=external_parameter_sets.zip");
	
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
					// file contents
					
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
			out.close();
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
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
            List<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
            model.put("inputParameters", inputParams);
        }

        model.put("project", project);

        List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
        model.put("extParamValSets", extParamValSets);

        List<ExtParamValDTO> extParamVals = null;
        Integer defaultExtSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
        
        if (selectedExtParamValSetId != null && defaultExtSetId != null)
        {
            try {
                extParamVals = extParamValSetService.getExtParamVals(defaultExtSetId);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }

            model.put("extParamVals", extParamVals);
        }

        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        return "projectdata";
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
        securityAuthorization.atLeastExpert_expert(project);

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
                cloneMetric.setUnit(metric.getUnit());
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
    public String createMetric(Map<String, Object> model,
        @RequestParam(value="reset", required=false) String reset,
        @RequestParam(value="selectedcompid", required=false) String selectedCompId,
    	@RequestParam(value="inputparamid", required=false) String inputParamId,
        @RequestParam(value="outputparamid", required=false) String outputParamId,
    	@RequestParam(value="metricid", required=false) String metricId,
        @RequestParam(value="extparamid", required=false) String extParamId,
    	@RequestParam(value="text", required=false) String text) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

        UserSession userSession = (UserSession) model.get("usersession");

        if (userSession == null)
        {
            userSession = new UserSession();
        }

        if (reset != null && reset.equalsIgnoreCase("true"))
        {
        	userSession.setExpression("");
        }
        
        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        if (selectedCompId != null && !selectedCompId.isEmpty())
        {
            int nSelectedCompId = Integer.parseInt(selectedCompId);

            if (nSelectedCompId > 0)
            {
                userSession.setComponentId(nSelectedCompId);

                List<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
                model.put("inputParameters", inputParams);

                List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
                model.put("outputVars", outputVars);
            }
            model.put("selectedcompid", nSelectedCompId);
        }

        if (inputParamId != null && !inputParamId.isEmpty())
        {
            int nInputParamId = Integer.parseInt(inputParamId);
            InputParameterDTO inputParam = null;
            
            if (nInputParamId > 0)
            {
            	try {
    				inputParam = inputParamService.findByID(nInputParamId);
    			} catch (EntityNotFoundException e1) {
    				e1.printStackTrace();
    			}
                String expression = userSession.getExpression();
                expression += inputParam.getQualifiedName();
                userSession.setExpression(expression);
            }
        }

        if (outputParamId != null && !outputParamId.isEmpty())
        {
            int nOutputParamId = Integer.parseInt(outputParamId);
            OutputVariableDTO outputVar = null;
            
            if (nOutputParamId > 0)
            {
            	try {
            		outputVar = outputVarService.findByID(nOutputParamId);
    			} catch (EntityNotFoundException e1) {
    				e1.printStackTrace();
    			}
                String expression = userSession.getExpression();
                expression += outputVar.getQualifiedName();
                userSession.setExpression(expression);
            }
        }

        if (metricId != null && !metricId.isEmpty())
        {
            int nMetricId = Integer.parseInt(metricId);
            MetricDTO metric = null;
            
            if (nMetricId > 0)
            {
            	try {
            		metric = metricService.findByID(nMetricId);
    			} catch (EntityNotFoundException e1) {
    				e1.printStackTrace();
    			}
                String expression = userSession.getExpression();
                expression += metric.getName();
                userSession.setExpression(expression);
            }
        }

        if (extParamId != null && !extParamId.isEmpty())
        {
            int nExtParamId = Integer.parseInt(extParamId);
            ExtParamDTO extParam = null;
            
            if (nExtParamId > 0)
            {
            	try {
            		extParam = extParamService.findByID(nExtParamId);
    			} catch (EntityNotFoundException e1) {
    				e1.printStackTrace();
    			}
                String expression = userSession.getExpression();
                expression += extParam.getName();
                userSession.setExpression(expression);
            }
        }

        if (text != null) {
        	if (text.equalsIgnoreCase("plus")) {
            	userSession.setExpression(userSession.getExpression() + "+");
        	} else {
        		userSession.setExpression(userSession.getExpression() + text);
        	}
        }
        
        model.put("usersession", userSession);
        
        Set<ExtParamDTO> extParams = projectService.getExtParams(project.getPrjid());
        model.put("extParams", extParams);
        
        Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
        model.put("metrics", metrics);

        return "createmetric";
    }

    @RequestMapping(value="updatemetric", method=RequestMethod.GET)
    public String updateMetric(Map<String, Object> model) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

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
        
        return "updatemetric";
    }

    @RequestMapping(value="updatemetric", method=RequestMethod.POST)
    public String updateMetricPost(ParamForm paramForm, Map<String, Object> model,
		@RequestParam(value="action", required=true) String action,
		@RequestParam(value="metricid", required=false) String metricId) {
        
    	ProjectDTO project = (ProjectDTO) model.get("project");
        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        String name = paramForm.getName();
        String expression = paramForm.getValue();
        String unit = paramForm.getUnit();
        
        if (name != null && expression != null)
        {
        	MetricDTO newMetric = new MetricDTO();
	        newMetric.setName(name.trim());
	        newMetric.setExpression(expression.trim());
	        newMetric.setProject (project);
	        
	        try {
				newMetric.setUnit(unitService.findByName(unit));
			} catch (EntityNotFoundException e2) {
				e2.printStackTrace();
			}
	        
	        if (action.equals("create")) {
	        	newMetric = metricService.save(newMetric);
	        } else if (action.equals("edit")) {
	        	int nMetricId = Integer.parseInt(metricId);
	        	MetricDTO metric = null;
				try {
					metric = metricService.findByID(nMetricId);
				} catch (EntityNotFoundException e1) {
					e1.printStackTrace();
				} 
	        	
	        	metric.setName(newMetric.getName());
	        	metric.setExpression(newMetric.getExpression());
	        	metric.setUnit(newMetric.getUnit());
	        	
	        	try {
					metric = metricService.update(metric);
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
	        }
        }
        
        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        model.put("project", project);
        Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
        model.put("metrics", metrics);

        return "metricdefinition";
    }

    @RequestMapping(value="editmetric", method=RequestMethod.GET)
    public String getEditMetric(Map<String, Object> model, 
        @RequestParam(value="metricid", required=true) String metricid) {

        int nMetricId = Integer.parseInt(metricid);
        MetricDTO metric = null;
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null) 
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        try { 
            metric = metricService.findByID(nMetricId);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        ParamForm paramForm = new ParamForm();
        paramForm.setName(metric.getName());
        paramForm.setValue(metric.getExpression());
        
        if (metric.getUnit() != null) {
        	paramForm.setUnit(metric.getUnit().getName());
        }
        
        model.put("paramForm", paramForm);
        model.put("action", "edit");
        model.put("metricid", nMetricId);

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);

        return "updatemetric";
    }
   
    @PreAuthorize("hasAnyRole('ROLE_Administrator','ROLE_Expert','ROLE_Standard')")
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

    @RequestMapping(value="infopage", method=RequestMethod.GET)
    public String getInfoPage(Map<String, Object> model)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");
        Integer nSimulationModelId = projectService.getSimulationmodelId(project.getPrjid());
        
        if (nSimulationModelId == null)
        {
        	return "error";
        }
        
        model.put("title", "Energy model description");

        try {
			model.put("infotext", simModelService.findByID(nSimulationModelId).getDescription());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

        return "infopage";
    }	

    @RequestMapping(value="error", method=RequestMethod.GET)
    public String getError(Map<String, Object> model)
    {
        return "error";
    }	
    
}
