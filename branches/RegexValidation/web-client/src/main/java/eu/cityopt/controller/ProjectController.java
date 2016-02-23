package eu.cityopt.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import eu.cityopt.DTO.AppUserDTO;
import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.InputParameterDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.ObjectiveFunctionResultDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.SimulationModelDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
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
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.web.ParamForm;
import eu.cityopt.web.UnitForm;
import eu.cityopt.web.UserSession;

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
    
    @RequestMapping(value="createproject", method=RequestMethod.GET)
    public String createProject(Map<String, Object> model) {
        ProjectDTO newProject = new ProjectDTO();
        model.put("newProject", newProject);

		securityAuthorization.atLeastExpert();

        AppUserDTO user = (AppUserDTO) model.get("user");
        
        if (user != null)
        {
        	model.put("user", user);
        }

        return "createproject";
    }

    @RequestMapping(value = "createproject", method = RequestMethod.POST)
    public String createProjectPost(Map<String, Object> model,
    		@Validated @ModelAttribute ("newProject") ProjectDTO projectForm, 
    		BindingResult bindingResult,
    		@RequestParam(value="nextpage", required=false) String nextpage,
            HttpServletRequest request) {

		securityAuthorization.atLeastExpert();

        if (bindingResult.hasErrors()) {
        	ProjectDTO project = new ProjectDTO();
        	project.setName(projectForm.getName());
        	project.setDescription(projectForm.getDescription());
        	project.setLocation(projectForm.getLocation());
        	project.setDesigntarget(projectForm.getDesigntarget());
            model.put("newProject", project);
            model.put("error", "Please write project name!");
        	return "createproject";
        } else if (nextpage != null) {
        	return "editproject";
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

            if (projectService.findByName(project.getName()) == null) {
                
                // Set up the project Rights.
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String user= ((UserDetails)principal).getUsername();
                project = projectManagementService.createProjectWithAdminUser(project, user);
                
                controllerService.clearSession(model, request);
                model.put("newProject", project);
                model.put("project", project);
                model.put("success",true);              

                return "createproject";
            } else {
                model.put("newProject", new ProjectDTO());                        
                model.put("success",false);                           
                return "createproject";
            }
        }
    }

    @RequestMapping(value="openproject", method=RequestMethod.GET)
    public String openProject(Map<String, Object> model)
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
   
    @RequestMapping(value="editproject", method=RequestMethod.GET)
    public String editProject(Map<String, Object> model, HttpServletRequest request, 
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
    public String importEnergyModel(Map<String, Object> model,
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
                securityAuthorization.atLeastExpert_standard(project);

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

                List<Locale.LanguageRange> languageList = 
            		Locale.LanguageRange.parse(RequestContextUtils.getLocale(request).getLanguage() + ",en");
                importExportService.importSimulationModel(project.getPrjid(), 0, languageList, bytes, simulatorName, timeOrigin);
                importExportService.importModelInputsAndOutputs(project.getPrjid(), 0);
                
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
            	model.put("error", e.getStackTrace().toString());
                return "error";
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
                securityAuthorization.atLeastExpert_standard(project);
                
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
            	e.printStackTrace();
            	model.put("error", e.getStackTrace().toString());
                return "error";
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
                securityAuthorization.atLeastExpert_standard(project);

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

	                Integer defaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());
	                
	                System.out.println("Default ext param set id " + defaultExtParamValSetId);
                    
	                if (defaultExtParamValSetId != null)
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
		                extParamValSetService.updateExtParamValInSet(extParamValSet.getExtparamvalsetid(), extParamVal, ts);
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
        return "extparams";
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
            securityAuthorization.atLeastStandard_guest(project);

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

    @RequestMapping(value="editproject", method=RequestMethod.POST)
    public String editProjectPost(ProjectDTO projectForm, Map<String, Object> model, 
        @RequestParam(value="action", required=false) String action) {

    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_standard(project);

        if (projectForm != null && action != null)
        {
            if (action.equals("create"))
            {
                project.setName(projectForm.getName());
                project = projectService.save(project,0,0);
                model.put("project", project);
            }
            else if (action.equals("update"))
            {
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
                    model.put("error", "This project has been updated in the meantime, please reload.");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                model.put("project", project);
            }
        }
        return "editproject";
    }

    @RequestMapping(value="closeproject", method=RequestMethod.GET)
    public String closeProject(Map<String, Object> model, HttpServletRequest request)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastGuest_guest(project);

        controllerService.clearSession(model, request);
        
        return "start";
    }	

    @RequestMapping(value="deleteproject",method=RequestMethod.GET)
    public String deleteProject(Map<String, Object> model, @RequestParam(value="prjid", required=false) String prjid){
    	securityAuthorization.atLeastExpert();
        
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
                model.put("error", "This project has been updated in the meantime, please reload.");
            }
        }

        List<ProjectDTO> projects = projectService.findAll();
        model.put("projects",projects);

        return "deleteproject";
    }

    @RequestMapping(value="cloneproject", method=RequestMethod.GET)
    public String cloneProject(Map<String, Object> model, @RequestParam(value = "projectid") String projectid) {
      
    	ProjectDTO project= this.ParseProjectIDtoProjectDTO(model, projectid);	
        securityAuthorization.atLeastAdmin();
        int nProjectId = Integer.parseInt(projectid);

        String clonename = project.getName()+"(copy)";
        int i=0;
        while(projectService.findByName(clonename)!=null) {
            i++;
            clonename = project.getName()+"(copy)("+i+")";
        }	
        
        try {
            ProjectDTO ProjectCloner = copyService.copyProject(nProjectId,clonename);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        
        List<ProjectDTO> projects = projectService.findAll();
        model.put("projects", projects);
        return "openproject";
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
   
    @RequestMapping(value="units", method=RequestMethod.GET)
    public String units(Model model) {
    	securityAuthorization.atLeastExpert();
        List<UnitDTO> units = unitService.findAll();
        model.addAttribute("units", units);
        return "units";
    }

    @RequestMapping(value="deleteunit",method=RequestMethod.GET)
    public String deleteUnit(Map<String, Object> model, @RequestParam(value="unitid", required=true) String unitid) {
    	
    	securityAuthorization.atLeastExpert();
        
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
                model.put("error", "This project has been updated in the meantime, please reload.");
            }
        }

        List<UnitDTO> units = unitService.findAll();
        model.put("units", units);

        return "units";
    }

    @RequestMapping(value="createunit", method=RequestMethod.GET)
    public String createUnit(Map<String, Object> model) {

    	securityAuthorization.atLeastExpert();
        
        UnitForm unitForm = new UnitForm();
        model.put("unitForm", unitForm);

        List<TypeDTO> types = typeService.findAll();
        List<String> typeStrings = new ArrayList<String>();

        for (int i = 0; i < types.size(); i++)
        {
            typeStrings.add(types.get(i).getName());
        }

        model.put("types", typeStrings);

        return "createunit";
    }

    @RequestMapping(value="createunit", method=RequestMethod.POST)
    public String createUnitPost(UnitForm unitForm, Model model) {
    	
    	securityAuthorization.atLeastExpert();
        
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

    @RequestMapping(value="paramreliability", method=RequestMethod.GET)
    public String paramReliability(Model model){

        securityAuthorization.atLeastExpert();
        
        return "paramreliability";
    }

    @RequestMapping(value="importdata", method=RequestMethod.GET)
    public String importData(Map<String, Object> model){
    
    	ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_standard(project);

        return "importdata";
    }

    @RequestMapping(value="exportdata", method=RequestMethod.GET)
    public String exportData(Map<String, Object> model){

    	ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_guest(project);
        
        return "exportdata";
    }

    @RequestMapping(value="exportextparamsets", method=RequestMethod.GET)
    public void exportExtParamSets(Map<String, Object> model, HttpServletRequest request, 
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
    public String projectData(Map<String, Object> model, 
            @RequestParam(value="selectedcompid", required=false) String selectedCompId,
            @RequestParam(value="selectedextparamvalsetid", required=false) String selectedExtParamValSetId) {
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
            List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
            model.put("outputVars", outputVars);
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
    public String metricDefinition(Map<String, Object> model,
            @RequestParam(value="metricid", required=false) String metricid,
            @RequestParam(value="action", required=false) String action) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastGuest_guest(project);

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        if (action != null && metricid != null)
        {
            securityAuthorization.atLeastExpert_expert(project);
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
        securityAuthorization.atLeastExpert_expert(project);
        
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
        securityAuthorization.atLeastExpert_expert(project);
        
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
		@RequestParam(value="metricid", required=false) String metricId,
    	@RequestParam(value="cancel", required=false) String cancel) {
            
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

        if (cancel != null) {
        	Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
            model.put("metrics", metrics);
            return "metricdefinition";
        }
        
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
    public String editMetric(Map<String, Object> model, 
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
   
    @RequestMapping(value="infopage", method=RequestMethod.GET)
    public String infoPage(Map<String, Object> model)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");
    	if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastGuest_guest(project);
        
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

	@RequestMapping("overview.png")
	public void renderOverviewImage(Map<String, Object> model, OutputStream stream) throws Exception {
		ProjectDTO project = (ProjectDTO) model.get("project");
		
		if (project == null)
		{
			return;
		}
		securityAuthorization.atLeastGuest_guest(project);
		
		Integer nSimulationModelId = projectService.getSimulationmodelId(project.getPrjid());
        
        if (nSimulationModelId == null)
        {
        	return;
        }
        
        SimulationModelDTO simModel = simModelService.findByID(nSimulationModelId);
        
        if (simModel != null)
        {
        	byte[] imageBlob = simModel.getImageblob();
         
        	if (imageBlob != null)
        	{
        		stream.write(imageBlob);
        	}
        }
	}

    @RequestMapping(value="error", method=RequestMethod.GET)
    public String error(Map<String, Object> model)
    {
        return "error";
    }	
    
    @RequestMapping(value="settings", method=RequestMethod.GET)
    public String settings(Map<String, Object> model)
    {
        return "settings";
    }	
}
