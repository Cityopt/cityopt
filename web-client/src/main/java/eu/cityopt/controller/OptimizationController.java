package eu.cityopt.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.script.ScriptException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.omg.IOP.Encoding;
import org.python.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import eu.cityopt.DTO.AlgoParamValDTO;
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
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.OptConstraintDTO;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.OutputVariableDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.DTO.TypeDTO;
import eu.cityopt.DTO.UnitDTO;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.service.AlgoParamValService;
import eu.cityopt.service.AlgorithmService;
import eu.cityopt.service.AppUserService;
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
import eu.cityopt.service.ModelParameterGrouping;
import eu.cityopt.service.ObjectiveFunctionService;
import eu.cityopt.service.OptConstraintService;
import eu.cityopt.service.OptSearchConstService;
import eu.cityopt.service.OptimizationSetService;
import eu.cityopt.service.OutputVariableService;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.ScenarioGeneratorService;
import eu.cityopt.service.ScenarioService;
import eu.cityopt.service.SearchOptimizationResults;
import eu.cityopt.service.SimulationResultService;
import eu.cityopt.service.TimeSeriesService;
import eu.cityopt.service.TimeSeriesValService;
import eu.cityopt.service.TypeService;
import eu.cityopt.service.UnitService;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SyntaxChecker;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.eval.util.TempDir;
import eu.cityopt.sim.service.ImportExportService;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;
import eu.cityopt.sim.service.ScenarioGenerationService;
import eu.cityopt.sim.service.ScenarioGenerationService.RunInfo;
import eu.cityopt.sim.service.SimulationService;
import eu.cityopt.sim.service.SyntaxCheckerService;
import eu.cityopt.validators.InputParameterValidator;
import eu.cityopt.web.AlgoParamValForm;
import eu.cityopt.web.ExtParamValSetForm;
import eu.cityopt.web.ModelParamForm;
import eu.cityopt.web.ObjFuncForm;
import eu.cityopt.web.OptimizationRun;
import eu.cityopt.web.ParamForm;
import eu.cityopt.web.UserSession;

/**
 * @author Olli Stenlund, Hannu Rummukainen
 *
 */
@Controller
@SessionAttributes({"project", "scenario", "optimizationset", "scengenerator", "optresults", "usersession", "user"})
public class OptimizationController {
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
    DecisionVariableService decisionVarService;

    @Autowired
    DatabaseSearchOptimizationService dbOptService;

    @Autowired
    ImportExportService importExportService;

    @Autowired
    AlgorithmService algorithmService;

    @Autowired
    AlgoParamValService algoParamValService;

    @Autowired
    SimulationService simulationService;

    @Autowired
    ScenarioGenerationService scenarioGenerationService;

    @Autowired
    SecurityAuthorization securityAuthorization;

    @Autowired
    ControllerService controllerService;

    @Autowired
	SyntaxCheckerService syntaxCheckerService;

	@Autowired
	@Qualifier("inputParameterValidator")
    InputParameterValidator validator;

    @RequestMapping(value="createobjfunction",method=RequestMethod.GET)
    public String createObjFunction(Map<String, Object> model,
    	@RequestParam(value="reset", required=false) String reset,
    	@RequestParam(value="type", required=true) String type,
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

        model.put("type", type);

        if (type.equals("db")) {
        	model.put("cancelPage", "editoptimizationset.html");
        } else if (type.equals("ga")) {
        	model.put("cancelPage", "geneticalgorithm.html");
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

        return "createobjfunction";
    }

    @RequestMapping(value="updateobjfunction", method=RequestMethod.GET)
    public String updateObjFunc(Map<String, Object> model,
    	@RequestParam(value="type", required=true) String type)
    {
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

        ObjFuncForm objFuncForm = new ObjFuncForm();
        objFuncForm.setExpression(userSession.getExpression());
        model.put("objFuncForm", objFuncForm);

        model.put("type", type);
        controllerService.getFunctions(model);

        return "updateobjfunction";
    }

    @RequestMapping(value="updateobjfunction", method=RequestMethod.POST)
    public String updateObjFuncPost(ObjFuncForm objFuncForm,
		Map<String, Object> model,
       	@RequestParam(value="cancel", required=false) String cancel,
    	@RequestParam(value="type", required=true) String type,
        HttpServletRequest request)
    {
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
        	if (type.equals("db")) {
        		return "redirect:/editoptimizationset.html";
        	} else if (type.equals("ga")) {
        		return "redirect:/geneticalgorithm.html";
        	}
        }

        OptimizationSetDTO optSet = null;
    	ScenarioGeneratorDTO scenGen = null;

        if (type.equals("db")) {
	        if (model.containsKey("optimizationset"))
		    {
		        optSet = (OptimizationSetDTO) model.get("optimizationset");

		        try {
		            optSet = optSetService.findByID(optSet.getOptid());
		        } catch (EntityNotFoundException e) {
		            e.printStackTrace();
		        }
		    }
		    else
		    {
		        return "error";
		    }
        } else if (type.equals("ga")) {
		    if (model.containsKey("scengenerator"))
		    {
		        scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

		        try {
		            scenGen = scenGenService.findByID(scenGen.getScengenid());
		        } catch (EntityNotFoundException e) {
		            e.printStackTrace();
		        }
		    }
		    else
		    {
		        return "error";
		    }
        }

        String name = objFuncForm.getName();
        String expression = objFuncForm.getExpression();

        if (name != null && expression != null)
        {
        	ObjectiveFunctionDTO oldFunc = new ObjectiveFunctionDTO();

        	if (type.equals("db") && optSet.getObjectivefunction() != null) {
        		oldFunc = optSet.getObjectivefunction();
        	}

	        oldFunc.setExpression(expression);
	        oldFunc.setName(name);
	        oldFunc.setProject(project);

	        String optSense = request.getParameter("optsense");
	        boolean isMaximize = false;

	        if (optSense.equals("1"))
	        {
	            isMaximize = false;
	        }
	        else if (optSense.equals("2"))
	        {
	            isMaximize = true;
	        }

	        oldFunc.setIsmaximise(isMaximize);

	        if (name == null || name.isEmpty() || expression == null || expression.isEmpty())
	        {
	        	model.put("objFuncForm", objFuncForm);
	        	model.put("error", controllerService.getMessage("write_name_and_expression", request));
	        	model.put("type", type);
	        	controllerService.getFunctions(model);
	            return "updateobjfunction";
	        }

	        if (objFuncService.existsByName(project.getPrjid(), name))
	        {
	        	model.put("objFuncForm", objFuncForm);
	        	model.put("error", controllerService.getMessage("objective_function_exists", request));
	        	model.put("type", type);
	        	controllerService.getFunctions(model);
	            return "updateobjfunction";
	        }

	        SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
        	eu.cityopt.sim.eval.SyntaxChecker.Error error = checker.checkObjectiveExpression(expression);

        	if (error != null) {
        	    model.put("error", error.message);
        	    model.put("objFuncForm", objFuncForm);
	        	model.put("type", type);
	        	controllerService.getFunctions(model);
	            return "updateobjfunction";
        	}

	        oldFunc = objFuncService.save(oldFunc);

        	if (type.equals("db")) {
		        try {
		            optSet = optSetService.findByID(optSet.getOptid());
		        } catch (EntityNotFoundException e) {
		            e.printStackTrace();
		        }

		        optSet.setObjectivefunction(oldFunc);

		        try {
		            optSet = optSetService.save(optSet);
		        } catch(ObjectOptimisticLockingFailureException e) {
		            model.put("error", controllerService.getMessage("optimization_set_updated", request));
		        }
        	} else if (type.equals("ga")) {
        		try {
					scenGenService.addObjectiveFunction(scenGen.getScengenid(), oldFunc);
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}

        		scenGen = scenGenService.save(scenGen);
        		return "redirect:/geneticalgorithm.html";
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
        model.put("optimizationset", optSet);

        controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());

	    return "editoptimizationset";
	}

    @RequestMapping(value="editobjfunction",method=RequestMethod.GET)
    public String editObjFunction(Map<String, Object> model,
        @RequestParam(value="selectedcompid", required=false) String selectedCompId)
    {
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
        securityAuthorization.atLeastExpert_expert(project);

        OptimizationSetDTO optSet = (OptimizationSetDTO) model.get("optimizationset");

        if (optSet == null)
        {
            optSet = new OptimizationSetDTO();
        }

        UserSession userSession = (UserSession) model.get("usersession");

        if (userSession == null)
        {
            userSession = new UserSession();
        }
        model.put("usersession", userSession);

        controllerService.initEditObjFunc(model, project.getPrjid(), selectedCompId, userSession);

        ObjectiveFunctionDTO function = optSet.getObjectivefunction();
        model.put("function", function);

        return "editobjfunction";
    }

    @RequestMapping(value="editobjfunction", method=RequestMethod.POST)
    public String editObjFunctionPost(ObjectiveFunctionDTO function, HttpServletRequest request, Map<String, Object> model) {
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
        OptimizationSetDTO optSet = null;

        if (model.containsKey("optimizationset"))
        {
            optSet = (OptimizationSetDTO) model.get("optimizationset");

            try {
                optSet = optSetService.findByID(optSet.getOptid());
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            return "error";
        }

        if (function != null && function.getExpression() != null && function.getName() != null)
        {
        	String name = function.getName();
        	String expression = function.getExpression();

        	if (name.isEmpty() || expression.isEmpty())
        	{
        		model.put("error", controllerService.getMessage("write_name_and_expression", request));
                controllerService.initEditObjFunc(model, project.getPrjid(), null, null);
                function = optSet.getObjectivefunction();
                model.put("function", function);

                return "editobjfunction";
        	}

        	SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
         	eu.cityopt.sim.eval.SyntaxChecker.Error error = checker.checkObjectiveExpression(expression);

         	if (error != null) {
         	    model.put("error", error.message);
         	    controllerService.initEditObjFunc(model, project.getPrjid(), null, null);
 	        	function = optSet.getObjectivefunction();
 	        	model.put("function", function);

 	        	return "editobjfunction";
         	}

            ObjectiveFunctionDTO oldFunc = optSet.getObjectivefunction();

            oldFunc.setExpression(function.getExpression());
            oldFunc.setName(function.getName());
            oldFunc.setProject(project);

            String optSense = request.getParameter("optsense");
            boolean isMaximize = false;

            if (optSense.equals("1"))
            {
                isMaximize = false;
            }
            else if (optSense.equals("2"))
            {
                isMaximize = true;
            }

            oldFunc.setIsmaximise(isMaximize);

            try {
                oldFunc = objFuncService.update(oldFunc);
            } catch (EntityNotFoundException e1) {
                e1.printStackTrace();
            }

            try {
                optSet = optSetService.findByID(optSet.getOptid());
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }

            optSet.setObjectivefunction(oldFunc);

            try {
                optSet = optSetService.save(optSet);
            } catch(ObjectOptimisticLockingFailureException e) {
                model.put("error", controllerService.getMessage("optimization_set_updated", request));
            }
        }

        model.put("optimizationset", optSet);

        controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());

        return "editoptimizationset";
    }

    @RequestMapping(value="cloneoptimizer", method=RequestMethod.GET)
    public String cloneOptimizer(Map<String, Object> model,
		@RequestParam(value="optimizerid") String optimizerid,
    	@RequestParam(value="optsettype", required=false) String optsettype) {

	    ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		if (optsettype != null)
		{
	        securityAuthorization.atLeastExpert_expert(project);
	        OptimizationSetDTO optSet = null;
	        ScenarioGeneratorDTO gaSet = null;
	        String name = "";
	        int noptimizerid = Integer.parseInt(optimizerid);
	        Set<OpenOptimizationSetDTO> optSets = null;

	        if (optsettype.equals("db"))
			{
		        try {
		            optSet = (OptimizationSetDTO) optSetService.findByID(noptimizerid);
		        } catch (EntityNotFoundException e2) {
		            e2.printStackTrace();
		        }
		        name = optSet.getName();
			}
	        else if (optsettype.equals("ga"))
	        {
		        try {
		            gaSet = (ScenarioGeneratorDTO) scenGenService.findByID(noptimizerid);
		        } catch (EntityNotFoundException e2) {
		            e2.printStackTrace();
		        }

	        	name = gaSet.getName();
	        }

	        String clonename = name+"(copy)";
	        int i=0;

	        if (optsettype.equals("db"))
	        {
		        while(optSetService.findByName(clonename, project.getPrjid()) != null) {
		            i++;
		            clonename=name+"(copy)("+i+")";
		        }

	        	try {
	        		OptimizationSetDTO cloneoptimisation = copyService.copyOptimizationSet(noptimizerid, clonename, true);
	            	cloneoptimisation=optSetService.save(cloneoptimisation);

		        } catch (EntityNotFoundException e1) {
		            e1.printStackTrace();
		        }
	        } else if (optsettype.equals("ga")) {
	            while(scenGenService.findByName(clonename, project.getPrjid()) != null) {
		            i++;
		            clonename=name+"(copy)("+i+")";
		        }
	            try {
	        		ScenarioGeneratorDTO newScenGen = copyService.copyScenarioGenerator(noptimizerid, clonename);
	        		newScenGen.setStatus("");
	        		newScenGen = scenGenService.save(newScenGen);
		        } catch (EntityNotFoundException e1) {
		            e1.printStackTrace();
		        }
	        }

	        try {
				optSets = projectService.getSearchAndGAOptimizationSets(project.getPrjid());
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
	        model.put("openoptimizationsets", optSets);
		}

        return "openoptimizationset";
    }

    @RequestMapping(value="deleteconstraint", method=RequestMethod.GET)
    public String deleteConstraint(Map<String, Object> model, @RequestParam(value="constraintid", required=true) String constraintId) {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        int nConstraintId = Integer.parseInt(constraintId);

        OptimizationSetDTO optSet = null;

        if (model.containsKey("optimizationset"))
        {
            optSet = (OptimizationSetDTO) model.get("optimizationset");
            model.put("optimizationset", optSet);
        }
        else
        {
            return "error";
        }

        try {
            optSetService.removeOptConstraint(optSet.getOptid(), nConstraintId);
            //optConstraintService.delete(constraint.getOptconstid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        securityAuthorization.atLeastExpert_expert(project);

        model.put("project", project);
        controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());

        return "editoptimizationset";
    }

    @RequestMapping(value="createoptimizationset",method=RequestMethod.GET)
    public String createOptimizationSet(Map<String, Object> model,
		HttpServletRequest request)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        if (projectService.getSimulationmodelId(project.getPrjid()) == null)
        {
        	model.put("error", controllerService.getMessage("import_energy_model_first", request));
        	return "error";
        }

        OpenOptimizationSetDTO openOptSet = new OpenOptimizationSetDTO();
        model.put("openoptimizationset", openOptSet);

        Set<String> optSetTypes = new HashSet<String>();
        optSetTypes.add("Database search");
        optSetTypes.add("Genetic algorithm");
        model.put("optimizationsettypes", optSetTypes);

        return "createoptimizationset";
    }

    @RequestMapping(value="createoptimizationset", method=RequestMethod.POST)
    public String createOptimizationSetPost(Map<String, Object> model, HttpServletRequest request, OpenOptimizationSetDTO openOptSet) {

        String type = request.getParameter("type");
        int nType = Integer.parseInt(type);
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null || openOptSet == null)
        {
            return "error";
        }
        securityAuthorization.atLeastExpert_expert(project);

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        if (openOptSet != null)
        {
        	if (openOptSet.getName() == null || openOptSet.getName().isEmpty())
        	{
        		OpenOptimizationSetDTO newOpenOptSet = new OpenOptimizationSetDTO();
        		model.put("openoptimizationset", newOpenOptSet);
        		model.put("error", controllerService.getMessage("write_optimization_set_name", request));
        		return "createoptimizationset";
        	}

        	if (nType == 1)
            {
            	if (optSetService.findByName(openOptSet.getName(), project.getPrjid()) != null)
            	{
            		OpenOptimizationSetDTO newOpenOptSet = new OpenOptimizationSetDTO();
            	    newOpenOptSet.setName(openOptSet.getName());
            	    newOpenOptSet.setDescription(openOptSet.getDescription());
            	    newOpenOptSet.setOptSetType(openOptSet.getOptSetType());
            		model.put("openoptimizationset", newOpenOptSet);
            	    model.put("success",false);
            		return "createoptimizationset";
            	}

                OptimizationSetDTO optSet = new OptimizationSetDTO();
                optSet.setName(openOptSet.getName());
                optSet.setDescription(openOptSet.getDescription());
                optSet.setProject(project);

                Integer nDefaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());

                if (nDefaultExtParamValSetId != null)
                {
	                ExtParamValSetDTO extParamValSet = null;

					try {
						extParamValSet = extParamValSetService.findByID(nDefaultExtParamValSetId);
					} catch (EntityNotFoundException e) {
						e.printStackTrace();
					}
	                optSet.setExtparamvalset(extParamValSet);
                }

                optSet = optSetService.save(optSet);

                UserSession session = (UserSession) model.get("usersession");
                session.setActiveOptSet(optSet.getName());
                model.put("usersession", session);
                model.put("optimizationset", optSet);

                controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());
                model.put("info", controllerService.getMessage("optimization_set_created", request));

                controllerService.clearOptResults(model);

                return "editoptimizationset";
            }
            else if (nType == 2)
            {
            	if (scenGenService.findByName(openOptSet.getName(), project.getPrjid()) != null)
            	{
            		OpenOptimizationSetDTO newOpenOptSet = new OpenOptimizationSetDTO();
            	    newOpenOptSet.setName(openOptSet.getName());
            	    newOpenOptSet.setDescription(openOptSet.getDescription());
            	    newOpenOptSet.setOptSetType(openOptSet.getOptSetType());
            		model.put("openoptimizationset", newOpenOptSet);
            	    model.put("success",false);
            		return "createoptimizationset";
            	}

            	ScenarioGeneratorDTO scenGen = scenGenService.create(project.getPrjid(), openOptSet.getName());
            	scenGen.setDescription(openOptSet.getDescription());

                Integer nDefaultExtParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());

                if (nDefaultExtParamValSetId != null)
                {
	                ExtParamValSetDTO extParamValSet = null;

					try {
						extParamValSet = extParamValSetService.findByID(nDefaultExtParamValSetId);
					} catch (EntityNotFoundException e) {
						e.printStackTrace();
					}
	                scenGen.setExtparamvalset(extParamValSet);
                }

                scenGen = scenGenService.save(scenGen);

                UserSession session = (UserSession) model.get("usersession");
                session.setActiveScenGen(scenGen.getName());
                model.put("usersession", session);
                model.put("scengenerator", scenGen);
                model.put("info", controllerService.getMessage("optimization_set_created", request));

                return "redirect:/geneticalgorithm.html";
            }
            else
            {
                return "createoptimizationset";
            }
        }
        else
        {
            return "createoptimizationset";
        }
    }

    @RequestMapping(value="editoptimizationset",method=RequestMethod.GET)
    public String editOptimizationSet(Map<String, Object> model,
        @RequestParam(value="optsetid", required=false) String optsetid,
        @RequestParam(value="optsettype", required=false) String optsettype)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_standard(project);

        OptimizationSetDTO optSet = null;

        if (model.containsKey("optimizationset"))
        {
            optSet = (OptimizationSetDTO) model.get("optimizationset");
            model.put("optimizationset", optSet);
        }
        else
        {
            return "error";
        }

        controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());

        return "editoptimizationset";
    }

    @RequestMapping(value="editoptimizationset",method=RequestMethod.POST)
    public String editOptimizationSetPost(OptimizationSetDTO optSet, Map<String, Object> model,
		HttpServletRequest request)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }
        securityAuthorization.atLeastStandard_standard(project);

        if (optSet.getName() == null || optSet.getName().isEmpty())
        {
        	model.put("error", controllerService.getMessage("write_optimization_set_name", request));
            controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());
            return "editoptimizationset";
        }

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        OptimizationSetDTO oldOptSet = null;

        if (model.containsKey("optimizationset"))
        {
            oldOptSet = (OptimizationSetDTO) model.get("optimizationset");
        }
        else
        {
            return "error";
        }

        oldOptSet.setName(optSet.getName());
        oldOptSet.setDescription(optSet.getDescription());

        oldOptSet = optSetService.save(oldOptSet);
        model.put("optimizationset", oldOptSet);

        controllerService.initEditOptSet(model, project.getPrjid(), oldOptSet.getOptid());

        return "editoptimizationset";
    }

    @RequestMapping(value = "exportoptimizationset", method = RequestMethod.GET)
	public void exportOptimizationSet(Map<String, Object> model, HttpServletRequest request,
		HttpServletResponse response) {

        ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return;
		}

		securityAuthorization.atLeastStandard_standard(project);

		OptimizationSetDTO optSet = (OptimizationSetDTO)model.get("optimizationset");

		if (optSet == null)
		{
			return;
		}

		Path timeSeriesPath = null;
		Path problemPath = null;
		File fileProblem = null;
		File fileTimeSeries = null;
		List<File> files = new ArrayList<File>();

		try (TempDir tempDir = new TempDir("export")) {
	        timeSeriesPath = tempDir.getPath().resolve("timeseries.csv");
	        problemPath = tempDir.getPath().resolve("optimization_problem.csv");

	        importExportService.exportOptimisationSet(optSet.getOptid(), problemPath, timeSeriesPath);

	        fileTimeSeries = timeSeriesPath.toFile();
	        fileProblem = problemPath.toFile();
	        files.add(fileProblem);
	        files.add(fileTimeSeries);

			// Set the content type based to zip
			response.setContentType("Content-type: text/zip");
			response.setHeader("Content-Disposition", "attachment; filename=optimization_set.zip");

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

    @RequestMapping(value = "exportoptimizationproblem", method = RequestMethod.GET)
	public void exportOptimizationProblem(Map<String, Object> model, HttpServletRequest request,
		HttpServletResponse response) {

        ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return;
		}
		securityAuthorization.atLeastStandard_standard(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

		if (scenGen == null)
		{
			return;
		}

		Path timeSeriesPath = null;
		Path problemPath = null;
		File fileProblem = null;
		File fileTimeSeries = null;
		List<File> files = new ArrayList<File>();

		try (TempDir tempDir = new TempDir("export")) {
	        timeSeriesPath = tempDir.getPath().resolve("timeseries.csv");
	        problemPath = tempDir.getPath().resolve("optimization_problem.csv");

	        importExportService.exportOptimisationProblem(scenGen.getScengenid(), problemPath, timeSeriesPath);

	        fileTimeSeries = timeSeriesPath.toFile();
	        fileProblem = problemPath.toFile();
	        files.add(fileProblem);
	        files.add(fileTimeSeries);

			// Set the content type based to zip
			response.setContentType("Content-type: text/zip");
			response.setHeader("Content-Disposition", "attachment; filename=optimization_problem.zip");

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
					System.out.println("Could not find file " + file.getAbsolutePath());
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

    @RequestMapping(value="extparamsets",method=RequestMethod.GET)
    public String extParamSets(
        Map<String, Object> model,
        @RequestParam(value="extparamvalsetid", required=false) String id) {

    	ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		controllerService.initExtParamSets(model, id, project.getPrjid());

		return "extparamsets";
    }

	@RequestMapping(value="extparamsets",method=RequestMethod.POST)
    public String extParamSetsPost(Map<String, Object> model,
		@RequestParam(value="extparamvalsetid", required=false) String strId,
		HttpServletRequest request) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

        OptimizationSetDTO optSet = (OptimizationSetDTO)model.get("optimizationset");

        try {
            optSet = optSetService.findByID(optSet.getOptid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        model.put("optimizationset", optSet);

        if (optSet == null) {
            // Invalid state
            return "error";
        }

        if (strId == null)
        {
    		controllerService.initExtParamSets(model, null, project.getPrjid());
    		return "extparamsets";
        }

        try {
            ExtParamValSetDTO extParamValSet = extParamValSetService.findByID(Integer.parseInt(strId));
            optSet.setExtparamvalset(extParamValSet);
            optSet = optSetService.update(optSet);
            model.put("optimizationset", optSet);
        } catch (EntityNotFoundException e) {
            model.put("error", "Entity not found.");
            e.printStackTrace();
        } catch(ObjectOptimisticLockingFailureException e) {
            model.put("error", controllerService.getMessage("concurrent_modification_detected", request));
            e.printStackTrace();
        }

        return "editoptimizationset";
    }

	@RequestMapping(value="gaextparamsets", method=RequestMethod.GET)
    private String GAExtParamValSet(Map<String, Object> model,
        @RequestParam(value="extparamvalsetid", required=false) String id) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		model.put("extparamvalsetid", id);
		List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
		model.put("extParamValSets", extParamValSets);

		Integer extParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());

		if (id != null && !id.isEmpty()) {
			extParamValSetId = new Integer(id);
		}

		if (extParamValSetId != null) {
			List<ExtParamValDTO> extParamVals = null;
			try {
				extParamVals = extParamValSetService.getExtParamVals(extParamValSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "error";
			}
			model.put("extParamVals", extParamVals);
		}

		model.put("postpage", "gaextparamsets.html");
		model.put("backpage", "geneticalgorithm.html");

		return "extparamsets";
	}

	@RequestMapping(value="gaextparamsets",method=RequestMethod.POST)
    public String GAExtParamSetsPost(Map<String, Object> model,
		@RequestParam(value="extparamvalsetid", required=true) int id,
		HttpServletRequest request) {

		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

        try {
            scenGen = scenGenService.findByID(scenGen.getScengenid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        model.put("optimizationset", scenGen);

        if (scenGen == null) {
            // Invalid state
            return "error";
        }

        try {
            ExtParamValSetDTO extParamValSet = extParamValSetService.findByID(id);
            scenGen.setExtparamvalset(extParamValSet);
            scenGen = scenGenService.update(scenGen);
            model.put("scengenerator", scenGen);
        } catch (EntityNotFoundException e) {
            model.put("error", "Entity not found.");
            e.printStackTrace();
        } catch(ObjectOptimisticLockingFailureException e) {
            model.put("error", controllerService.getMessage("concurrent_modification_detected", request));
            e.printStackTrace();
        }

        model.put("extparamvalsetid", id);

        return "redirect:/geneticalgorithm.html";
    }

	public ProjectDTO initiateProject(Map<String, Object> model){
		ProjectDTO project = (ProjectDTO) model.get("project");
        return project;
	}

    @RequestMapping(value = "importoptimizationset", method = RequestMethod.POST)
    public String importOptimizationSet(Map<String, Object> model,
		@RequestParam("file") MultipartFile file,
		@RequestParam("fileTimeSeries") MultipartFile fileTimeSeries,
		HttpServletRequest request) {

        if (!file.isEmpty()) {
            try {
                ProjectDTO project = (ProjectDTO) model.get("project");

        		if (project == null)
        		{
        			return "error";
        		}

        		securityAuthorization.atLeastExpert_expert(project);

                AppUserDTO user = (AppUserDTO) model.get("user");

                if (user == null)
                {
                	return "error";
                }

                try {
                    project = projectService.findByID(project.getPrjid());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                model.put("project", project);

                try (InputStream structureStream = file.getInputStream()) {
                    importExportService.importSimulationStructure(
                            project.getPrjid(), structureStream);
                }

                System.out.println("Starting importing optimization set");
                try (InputStream optset = file.getInputStream();
                     InputStream ts = fileTimeSeries.getInputStream()) {
                    importExportService.importOptimisationSet(
                            project.getPrjid(), user.getUserid(),
                            "optimization set", optset, ts);
                }

                model.put("info", controllerService.getMessage("file_imported", request));
            } catch (Exception e) {
                e.printStackTrace();
            	model.put("error", e.getMessage());
                return "importdata";
            }
        } else {
        	model.put("error", controllerService.getMessage("file_missing", request));
        }
        return "importdata";
    }

    @RequestMapping(value = "importoptimizationproblem", method = RequestMethod.POST)
    public String importOptimizationProblem(
            Map<String, Object> model,
            @RequestParam("fileProblem") MultipartFile fileProblem,
            @RequestParam("fileTimeSeries") MultipartFile fileTimeSeries,
            HttpServletRequest request) {

        if (!fileProblem.isEmpty()) {
            try {
                ProjectDTO project = (ProjectDTO) model.get("project");

                if (project == null)
                {
                    return "error";
                }

        		securityAuthorization.atLeastExpert_expert(project);

                try {
                    project = projectService.findByID(project.getPrjid());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                model.put("project", project);

                /*StringWriter writer = new StringWriter();
				IOUtils.copy(structureStream, writer);
				String theString = writer.toString();

				System.out.println("Starting importing of this file: " + theString);*/

                try (InputStream in = fileProblem.getInputStream()) {
                    importExportService.importSimulationStructure(
                            project.getPrjid(), in);
                }
                try (InputStream problem = fileProblem.getInputStream();
                     InputStream ts = fileTimeSeries.getInputStream()) {
                    importExportService.importOptimisationProblem(
                            project.getPrjid(), "test_name", problem,
                            null, null, ts);
                }
            } catch (Exception e) {
                e.printStackTrace();
            	model.put("error", e.toString());
                return "error";
            }
        } else {
        	model.put("error", controllerService.getMessage("file_missing", request));
        }
        return "importdata";
    }

    @RequestMapping(value="databaseoptimization", method=RequestMethod.GET)
    public String databaseOptimization(Map<String, Object> model,
		HttpServletRequest request) {

        OptimizationSetDTO optSet = null;

        if (model.containsKey("optimizationset"))
        {
            optSet = (OptimizationSetDTO) model.get("optimizationset");

            try {
                optSet = optSetService.findByID(optSet.getOptid());
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
            model.put("optimizationset", optSet);
        }
        else
        {
            return "error";
        }

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

		securityAuthorization.atLeastExpert_expert(project);

        if (optSet.getObjectivefunction() == null)
        {
        	controllerService.getProjectMetricVals(model, project.getPrjid());
            model.put("error", controllerService.getMessage("obj_func_missing", request));
            return "editoptimizationset";
        }

        try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }
        model.put("project", project);

        SearchOptimizationResults optResults = null;
        System.out.println("Starting database optimization");

        try {
            optResults = dbOptService.searchConstEval(project.getPrjid(), optSet.getOptid(), 5);
        } catch (ParseException e) {
            e.printStackTrace();
            model.put("error", e.getMessage());
        } catch (ScriptException e) {
            e.printStackTrace();
            model.put("error", e.getMessage());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            model.put("error", e.getMessage());
        }
        model.put("optresults", optResults);

        controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());

        return "editoptimizationset";
    }

    @RequestMapping(value="openoptimizationset",method=RequestMethod.GET)
    public String openOptimizationSet(Map<String, Object> model,
            @RequestParam(value="optsetid", required=false) String optsetid,
            @RequestParam(value="optsettype", required=false) String optsettype) {

        ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastStandard_standard(project);

        if (optsettype != null)
        {
            if (optsettype.equals("db"))
            {
                OptimizationSetDTO optSet = null;

                if (optsetid != null)
                {
                    int nOptSetId = Integer.parseInt(optsetid);

                    try {
                        optSet = optSetService.findByID(nOptSetId);
                    } catch (NumberFormatException | EntityNotFoundException e) {
                        e.printStackTrace();
                    }
                    model.put("optimizationset", optSet);

                    UserSession session = (UserSession) model.get("usersession");
                    session.setActiveOptSet(optSet.getName());
                    model.put("usersession", session);
                }
                else if (model.containsKey("optimizationset"))
                {
                    optSet = (OptimizationSetDTO) model.get("optimizationset");

                    try {
                        optSet = optSetService.findByID(optSet.getOptid());
                    } catch (EntityNotFoundException e) {
                        e.printStackTrace();
                    }

                    UserSession session = (UserSession) model.get("usersession");
                    session.setActiveOptSet(optSet.getName());
                    model.put("usersession", session);
                    model.put("optimizationset", optSet);
                }
                else
                {
                    return "error";
                }

                List<OptConstraintDTO> optSearchConstraints = null;

                try {
                    optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
                } catch (EntityNotFoundException e) {
                    e.printStackTrace();
                }

                model.put("constraints", optSearchConstraints);

                project = (ProjectDTO) model.get("project");

                if (project == null)
                {
                    return "error";
                }

                try {
                    project = projectService.findByID(project.getPrjid());
                } catch (EntityNotFoundException e) {
                    e.printStackTrace();
                }
                List<MetricValDTO> listMetricVals = metricValService.findAll();
                List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();

                for (int i = 0; i < listMetricVals.size(); i++)
                {
                    MetricValDTO metricVal = listMetricVals.get(i);

                    if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid())
                    {
                        listProjectMetricVals.add(metricVal);
                    }
                }
                model.put("metricVals", listProjectMetricVals);

                controllerService.clearOptResults(model);
                return "editoptimizationset";
            }
            else
            {
                ScenarioGeneratorDTO scenGen = null;

                if (optsetid != null)
                {
                    int nOptSetId = Integer.parseInt(optsetid);

                    try {
                        scenGen = (ScenarioGeneratorDTO) scenGenService.findByID(nOptSetId);
                    } catch (EntityNotFoundException e) {
                        e.printStackTrace();
                    }

                    UserSession session = (UserSession) model.get("usersession");
                    session.setActiveScenGen(scenGen.getName());
                    model.put("usersession", session);
                    model.put("scengenerator", scenGen);
                }
                else
                {
                    return "error";
                }
                return "redirect:/geneticalgorithm.html";
            }
        }
        else
        {
            project = (ProjectDTO) model.get("project");

            if (project == null)
            {
                return "error";
            }

            try {
                project = projectService.findByID(project.getPrjid());
            } catch (EntityNotFoundException e1) {
                e1.printStackTrace();
            }
            Set<OpenOptimizationSetDTO> optSets = null;

            try {
                optSets = projectService.getSearchAndGAOptimizationSets(project.getPrjid());
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
            model.put("openoptimizationsets", optSets);

            return "openoptimizationset";
        }
    }

    @RequestMapping(value="deleteoptimizationset",method=RequestMethod.GET)
    public String deleteOptimizationSet(Map<String, Object> model,
        @RequestParam(value="optsetid", required=false) String optsetid,
        @RequestParam(value="optsettype", required=false) String optsettype,
        HttpServletRequest request) {

        ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		if (optsettype != null)
        {
            if (optsettype.equals("db"))
            {
                if (optsetid != null)
                {
                	OptimizationSetDTO optSet = null;

                    if (model.containsKey("optimizationset"))
                    {
                        optSet = (OptimizationSetDTO) model.get("optimizationset");
                        model.put("optimizationset", optSet);
                    }
                    int nOptSetId = Integer.parseInt(optsetid);

                    if (optSet != null && optSet.getOptid() == nOptSetId)
                    {
                    	model.put("error", controllerService.getMessage("cant_delete_active_opt_set", request));
                    }
                    else
                    {
	                    try {
	                        optSetService.delete(nOptSetId);
	                    } catch (NumberFormatException | EntityNotFoundException e) {
	                        e.printStackTrace();
	                    } catch(ObjectOptimisticLockingFailureException e){
	                        model.put("error", controllerService.getMessage("optimization_set_updated", request));
	                    }
                    }
                }
                else
                {
                    return "error";
                }
            }
            else
            {
                if (optsetid != null)
                {
                    ScenarioGeneratorDTO scenGen = null;

                    if (model.containsKey("scengenerator"))
                    {
                        scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
                        model.put("scengenerator", scenGen);
                    }
                    int nOptSetId = Integer.parseInt(optsetid);

                    if (scenGen != null && scenGen.getScengenid() == nOptSetId)
                    {
                    	model.put("error", controllerService.getMessage("cant_delete_active_scen_gen", request));
                    }
                    else
                    {
	                    try {
	                        scenGenService.delete(nOptSetId);
	                    } catch (EntityNotFoundException e) {
	                        e.printStackTrace();
	                    } catch(ObjectOptimisticLockingFailureException e){
	                        model.put("error", controllerService.getMessage("optimization_set_updated", request));
	                    }
                    }
                }
                else
                {
                    return "error";
                }
            }
        }

       try {
            project = projectService.findByID(project.getPrjid());
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }
        Set<OpenOptimizationSetDTO> optSets = null;

        try {
            optSets = projectService.getSearchAndGAOptimizationSets(project.getPrjid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        model.put("openoptimizationsets", optSets);

        return "deleteoptimizationset";
    }

    @RequestMapping(value="createconstraint",method=RequestMethod.GET)
    public String createConstraint(Map<String, Object> model,
            @RequestParam(value="selectedcompid", required=false) String selectedCompId) {

        OptConstraintDTO constraint = new OptConstraintDTO();
        model.put("constraint", constraint);

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

        UserSession userSession = (UserSession) model.get("usersession");

        if (userSession == null)
        {
            userSession = new UserSession();
        }

        model.put("usersession", userSession);

        return "createconstraint";
    }

    @RequestMapping(value="createconstraint", method=RequestMethod.POST)
    public String createConstraintPost(OptConstraintDTO constraint, Map<String, Object> model,
		HttpServletRequest request) throws EntityNotFoundException {

        ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		OptimizationSetDTO optSet = null;

        if (model.containsKey("optimizationset"))
        {
            optSet = (OptimizationSetDTO) model.get("optimizationset");

            try {
                optSet = optSetService.findByID(optSet.getOptid());
            } catch (NumberFormatException | EntityNotFoundException e) {
                e.printStackTrace();
            }

            model.put("optimizationset", optSet);
        }
        else
        {
            return "error";
        }

        OptConstraintDTO testConstraint = optConstraintService.findByNameAndOptSet(constraint.getName(), optSet.getOptid());

        if (testConstraint != null)
        {
        	model.put("error", controllerService.getMessage("optimization_set_updated", request));
        	model.put("constraint", constraint);
        	return "createconstraint";
        }

        if (constraint != null && constraint.getExpression() != null && constraint.getName() != null)
        {
        	if (constraint.getName().isEmpty() || constraint.getExpression().isEmpty())
        	{
        		model.put("error", controllerService.getMessage("write_name_and_expression", request));
        		model.put("constraint", constraint);
            	return "createconstraint";
        	}

        	String expression = constraint.getExpression();
        	SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
         	eu.cityopt.sim.eval.SyntaxChecker.Error error = checker.checkConstraintExpression(expression);

         	if (error != null) {
         	    model.put("error", error.message);
         	    model.put("constraint", constraint);
         	    return "createconstraint";
         	}

            OptConstraintDTO newOptConstraint = new OptConstraintDTO();
            newOptConstraint.setName(constraint.getName());
            newOptConstraint.setExpression(constraint.getExpression());
            newOptConstraint.setLowerbound(constraint.getLowerbound());
            newOptConstraint.setUpperbound(constraint.getUpperbound());
            optConstraintService.save(newOptConstraint);

            try {
                optSetService.addOptConstraint(optSet.getOptid(), newOptConstraint);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }
        }

        List<OptConstraintDTO> optSearchConstraints = null;

        try {
            optSearchConstraints = optSetService.getOptConstraints(optSet.getOptid());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        model.put("constraints", optSearchConstraints);

        return "editoptimizationset";
    }

    @RequestMapping(value="editconstraint",method=RequestMethod.GET)
    public String editConstraint(Map<String, Object> model,
            @RequestParam(value="selectedcompid", required=false) String selectedCompId,
            @RequestParam(value="constraintid", required=false) String constraintId) {

        int nConstraintId = Integer.parseInt(constraintId);

        OptConstraintDTO constraint = null;

        try {
            constraint = optConstraintService.findByID(nConstraintId);
        } catch (EntityNotFoundException e1) {
            e1.printStackTrace();
        }

        model.put("constraint", constraint);

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

        UserSession userSession = (UserSession) model.get("usersession");

        if (userSession == null)
        {
            userSession = new UserSession();
        }

        model.put("usersession", userSession);

        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        if (selectedCompId != null && !selectedCompId.isEmpty())
        {
            int nSelectedCompId = Integer.parseInt(selectedCompId);

            if (nSelectedCompId > 0)
            {
                userSession.setComponentId(nSelectedCompId);
                List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
                model.put("outputVars", outputVars);

                List<InputParameterDTO> inputParams = componentService.getInputParameters(nSelectedCompId);
                model.put("inputParams", inputParams);
            }
            model.put("selectedcompid", nSelectedCompId);
        }

        Set<MetricDTO> metrics = projectService.getMetrics(project.getPrjid());
        model.put("metrics", metrics);

        return "editconstraint";
    }

    @RequestMapping(value="editconstraint", method=RequestMethod.POST)
    public String editConstraintPost(OptConstraintDTO constraint, Map<String, Object> model,
		HttpServletRequest request) throws EntityNotFoundException {
        OptimizationSetDTO optSet = null;

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

		securityAuthorization.atLeastExpert_expert(project);

        if (model.containsKey("optimizationset"))
        {
            optSet = (OptimizationSetDTO) model.get("optimizationset");

            try {
                optSet = optSetService.findByID(optSet.getOptid());
            } catch (NumberFormatException | EntityNotFoundException e) {
                e.printStackTrace();
            }

            model.put("optimizationset", optSet);
        }
        else
        {
            return "error";
        }

        if (constraint != null && constraint.getExpression() != null && constraint.getName() != null)
        {
        	if (constraint.getName().isEmpty() || constraint.getExpression().isEmpty())
        	{
        		model.put("error", controllerService.getMessage("write_name_and_expression", request));
        		model.put("constraint", constraint);
            	return "editconstraint";
        	}

        	String expression = constraint.getExpression();
        	SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid());
         	eu.cityopt.sim.eval.SyntaxChecker.Error error = checker.checkConstraintExpression(expression);

         	if (error != null) {
         	    model.put("error", error.message);
         	    model.put("constraint", constraint);
         	    return "editconstraint";
         	}

        	String lowerbound = constraint.getLowerbound();
            String upperbound = constraint.getUpperbound();
            String name = constraint.getName();

            constraint = optConstraintService.findByID(constraint.getOptconstid());
            constraint.setLowerbound(lowerbound);
            constraint.setUpperbound(upperbound);
            constraint.setName(name);
            constraint.setExpression(expression);

            optConstraintService.update(constraint);
        }

        controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());

        return "editoptimizationset";
    }

    @RequestMapping(value="importobjfunction",method=RequestMethod.GET)
    public String importObjFunction(Map<String, Object> model,
            @RequestParam(value="objectivefunctionid", required=false) String selectedObjFuncId) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

		securityAuthorization.atLeastStandard_standard(project);

        if (selectedObjFuncId != null && !selectedObjFuncId.isEmpty())
        {
            int nSelectedObjFuncId = Integer.parseInt(selectedObjFuncId);
            ObjectiveFunctionDTO objFunc = null;

            try {
                objFunc = objFuncService.findByID(nSelectedObjFuncId);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }

            OptimizationSetDTO optSet = null;

            if (model.containsKey("optimizationset"))
            {
                optSet = (OptimizationSetDTO) model.get("optimizationset");

                try {
                    optSet = optSetService.findByID(optSet.getOptid());
                } catch (NumberFormatException | EntityNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                return "error";
            }

            if (optSet != null && objFunc != null)
            {
                optSet.setObjectivefunction(objFunc);

                try {
                    optSet = optSetService.update(optSet);
                } catch (EntityNotFoundException e) {
                    e.printStackTrace();
                }
                model.put("optimizationset", optSet);
            }

            controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());
            return "editoptimizationset";
        }
        Set<ObjectiveFunctionDTO> objFuncs = null;

        try {
			objFuncs = projectService.getObjectiveFunctions(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
        model.put("objFuncs", objFuncs);

        return "importobjfunction";
    }

    @RequestMapping(value="importsearchconstraint",method=RequestMethod.GET)
    public String importSearchConstraint(Map<String, Object> model,
        @RequestParam(value="constraintid", required=false) String selectedConstraintId,
        HttpServletRequest request) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

		securityAuthorization.atLeastStandard_standard(project);

        if (selectedConstraintId != null && !selectedConstraintId.isEmpty())
        {
            int nSelectedConstraintId = Integer.parseInt(selectedConstraintId);
            OptConstraintDTO constraint = null;

            try {
                constraint = optConstraintService.findByID(nSelectedConstraintId);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }

            OptimizationSetDTO optSet = null;

            if (model.containsKey("optimizationset"))
            {
                optSet = (OptimizationSetDTO) model.get("optimizationset");

                try {
                    optSet = optSetService.findByID(optSet.getOptid());
                } catch (NumberFormatException | EntityNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                return "error";
            }

            if (optSet != null && constraint != null)
            {
            	OptConstraintDTO testConstraint = optConstraintService.findByNameAndOptSet(constraint.getName(), optSet.getOptid());

            	if (testConstraint == null) {
	                try {
	                    optSetService.addOptConstraint(optSet.getOptid(), constraint);
	                } catch (EntityNotFoundException e1) {
	                    e1.printStackTrace();
	                }

	                try {
	                    optSet = optSetService.update(optSet);
	                } catch (EntityNotFoundException e) {
	                    e.printStackTrace();
	                }
            	} else {
            		model.put("error", controllerService.getMessage("constraint_exists", request));
            	}
                model.put("optimizationset", optSet);
            }

            controllerService.initEditOptSet(model, project.getPrjid(), optSet.getOptid());
            return "editoptimizationset";
        }

        Set<OptConstraintDTO> optSearchConstraints = null;

        try {
			optSearchConstraints = projectService.getOptConstraints(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

        model.put("constraints", optSearchConstraints);

        return "importsearchconstraint";
    }

    @RequestMapping(value="importgaconstraint",method=RequestMethod.GET)
    public String importGAConstraint(Map<String, Object> model,
        @RequestParam(value="constraintid", required=false) String selectedConstraintId,
        HttpServletRequest request) {

        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        {
            return "error";
        }

		securityAuthorization.atLeastStandard_standard(project);

        if (selectedConstraintId != null && !selectedConstraintId.isEmpty())
        {
            int nSelectedConstraintId = Integer.parseInt(selectedConstraintId);
            OptConstraintDTO constraint = null;

            try {
                constraint = optConstraintService.findByID(nSelectedConstraintId);
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
            }

            ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

            try {
                scenGen = scenGenService.findByID(scenGen.getScengenid());
            } catch (NumberFormatException | EntityNotFoundException e) {
                e.printStackTrace();
            }

            OptConstraintDTO testConstraint = optConstraintService.findByNameAndScenGen(constraint.getName(), scenGen.getScengenid());

            if (testConstraint != null) {
            	// Constraint already exists
            	model.put("error", controllerService.getMessage("constraint_exists", request));
            }
            else if (scenGen != null && constraint != null)
            {
                try {
                    scenGenService.addOptConstraint(scenGen.getScengenid(), constraint);
                } catch (EntityNotFoundException e1) {
                    e1.printStackTrace();
                }

                try {
                	scenGen = scenGenService.update(scenGen);
                } catch (EntityNotFoundException e) {
                    e.printStackTrace();
                }
                model.put("scengenerator", scenGen);
                return "redirect:/geneticalgorithm.html";
            }
        }

        Set<OptConstraintDTO> optSearchConstraints = null;

        try {
			optSearchConstraints = projectService.getOptConstraints(project.getPrjid());
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
        model.put("constraints", optSearchConstraints);

        return "importgaconstraint";
    }

    @RequestMapping(value="showresults",method=RequestMethod.GET)
    public String showResults(Map<String, Object> model,
        @RequestParam(value="selectedcompid", required=false) String selectedCompId,
        @RequestParam(value="scenarioid", required=false) String scenarioId) {

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

        UserSession userSession = (UserSession) model.get("usersession");

        if (userSession == null)
        {
            return "error";
        }

        SearchOptimizationResults optResults = (SearchOptimizationResults) model.get("optresults");
        model.put("optresults", optResults);

        int nResultScenarioId = Integer.parseInt(scenarioId);
        ScenarioDTO resultScenario = null;

        try {
            resultScenario = scenarioService.findByID(nResultScenarioId);
        } catch (EntityNotFoundException e) {
            System.out.println("Entity " + nResultScenarioId + " not found");
            e.printStackTrace();
        }

        model.put("scenario", resultScenario);

        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        if (selectedCompId != null && !selectedCompId.isEmpty())
        {
            int nSelectedCompId = Integer.parseInt(selectedCompId);

            if (nSelectedCompId > 0)
            {
                userSession.setComponentId(nSelectedCompId);
                List<OutputVariableDTO> outputVars = componentService.getOutputVariables(nSelectedCompId);
                model.put("outputVars", outputVars);

                List<InputParamValDTO> listInputParamVals = inputParamValService.findByComponentAndScenario(nSelectedCompId, nResultScenarioId);
                model.put("inputParamVals", listInputParamVals);
            }
            model.put("selectedcompid", nSelectedCompId);
        }

        //metricService.getMetricVals(metricId, scenId)

        /*MetricDTO metric1 = metricService.findByID(metric1Id);
		MetricDTO metric2 = metricService.findByID(metric2Id);
		//Set<MetricValDTO> metricVals1 = metricService.getMetricVals(metric1Id);
		//Set<MetricValDTO> metricVals2 = metricService.getMetricVals(metric2Id);
		TimeSeries timeSeries = new TimeSeries("Scenario metric values");

		Set<Integer> scenarioIds = userSession.getScenarioIds();
		Iterator<Integer> scenIter = scenarioIds.iterator();
		DefaultXYDataset dataset = new DefaultXYDataset();

		while (scenIter.hasNext())
		{
			Integer integerScenarioId = (Integer) scenIter.next();
			int nScenarioId = (int)integerScenarioId;
			ScenarioDTO scenarioTemp = scenarioService.findByID(nScenarioId);
			MetricValDTO metricVal1 = metricService.getMetricVals(metric1Id, nScenarioId).get(0);
		}*/

        List<MetricValDTO> listMetricVals = metricValService.findAll();
        List<MetricValDTO> listProjectMetricVals = new ArrayList<MetricValDTO>();

        Iterator<MetricValDTO> metricValIter = listMetricVals.iterator();

        while (metricValIter.hasNext())
        {
            MetricValDTO metricVal = metricValIter.next();

            if (metricVal.getMetric().getProject().getPrjid() == project.getPrjid()
                    && metricVal.getScenariometrics().getScenario().getScenid() == resultScenario.getScenid())
            {
                listProjectMetricVals.add(metricVal);
            }
        }

        model.put("metricVals", listProjectMetricVals);

        return "showresults";
    }

    @RequestMapping(value="garuns", method=RequestMethod.GET)
    public String runningGeneticOptimizations(Map<String, Object> model) {

        ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		controllerService.updateGARuns(model);

		return "garuns";
    }

    @RequestMapping(value="abortgarun", method=RequestMethod.GET)
    public String abortGARun(Map<String, Object> model,
    	@RequestParam(value="id", required=true) int id) {

        ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);

		scenarioGenerationService.cancelOptimisation(id);

		controllerService.updateGARuns(model);

		return "garuns";
    }

    @RequestMapping(value="editsgobjfunction", method=RequestMethod.GET)
    public String editSGObjFunction(ModelMap model,
            @RequestParam(value="obtfunctionid", required=false) Integer obtfunctionid,
            @RequestParam(value="selectedcompid", required=false) Integer selectedCompId) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        ObjectiveFunctionDTO function = null;
        if (obtfunctionid != null) {
            try {
                function = (ObjectiveFunctionDTO) objFuncService.findByID(obtfunctionid);
                if (function.getProject().getPrjid() != project.getPrjid()) {
                    return "error";
                }
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
                return "error";
            }
        } else {
            function = new ObjectiveFunctionDTO();
        }

        if (obtfunctionid != null) {
    		model.put("obtfunctionid", obtfunctionid);
        }

        return getEditSGObjFunction(project, function, model, selectedCompId);
    }

    private String getEditSGObjFunction(
        ProjectDTO project, ObjectiveFunctionDTO function,
        ModelMap model, Integer selectedCompId) {

		securityAuthorization.atLeastExpert_expert(project);
        model.put("function", function);

        return "editsgobjfunction";
    }

    @RequestMapping(value="editsgobjfunction", method=RequestMethod.POST)
    public String editSGObjFunctionPost (ObjectiveFunctionDTO function, ModelMap model,
        @RequestParam(value="obtfunctionid", required=true) String obtfunctionid,
        @RequestParam("optsense") String optSense) {

        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

        int nObjFuncId = Integer.parseInt(obtfunctionid);

		securityAuthorization.atLeastExpert_expert(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid(), scenGen.getScengenid());
      	eu.cityopt.sim.eval.SyntaxChecker.Error error = checker.checkObjectiveExpression(function.getExpression());

      	if (error != null) {
      	    model.put("error", error.message);
            model.put("function", function);
            return "editsgobjfunction";
      	}

        if (StringUtils.isBlank(function.getExpression())) {
            return getEditSGObjFunction(project, function, model, null);
        }
        function.setIsmaximise("max".equals(optSense));
        function.setProject(project);
        function.setObtfunctionid(nObjFuncId);

        try {
            objFuncService.update(function);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
        return "redirect:/geneticalgorithm.html";
   	}

    @RequestMapping(value="deletesgobjfunction", method=RequestMethod.POST)
    public String deleteSGObjFunctionPost (
            ModelMap model, @RequestParam("objid") int objid) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            // TODO: This only unlinks.  Should probably delete the actual ObjectiveFunction too, if it's not used elsewhere?
            scenGenService.removeObjectiveFunction(scenGen.getScengenid(), objid);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="addsgobjfunction", method=RequestMethod.GET)
    public String addSGObjFunctionPost(ModelMap model,
    	@RequestParam(value="obtfunctionid", required=false) String obtfunctionid,
    	HttpServletRequest request) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        if (obtfunctionid != null) {
        	 try {
                 ObjectiveFunctionDTO objFunc = objFuncService.findByID(Integer.parseInt(obtfunctionid));
                 if (objFunc.getProject().getPrjid() != project.getPrjid()) {
                     return "error";
                 }

                 ObjectiveFunctionDTO testObjFunc = objFuncService.findByNameAndScenGen(scenGen.getScengenid(), objFunc.getName());

                 if (testObjFunc == null) {
                	 scenGenService.addObjectiveFunction(scenGen.getScengenid(), objFunc);
                 } else {
                	 model.put("error", controllerService.getMessage("objective_function_exists", request));

                	 try {
        	            model.put("objFuncs", sortBy(ObjectiveFunctionDTO::getName,
                    		projectService.getObjectiveFunctions(project.getPrjid())));
        	            // TODO filter out functions that are already in scenGen
        	        } catch (EntityNotFoundException e) {
        	            e.printStackTrace();
        	            return "error";
        	        }
         	        return "addsgobjfunction";
                 }
             } catch (EntityNotFoundException e) {
                 e.printStackTrace();
                 return "error";
             }
        	return "redirect:/geneticalgorithm.html";
        } else {
	        try {
	            model.put("objFuncs", sortBy(ObjectiveFunctionDTO::getName,
            		projectService.getObjectiveFunctions(project.getPrjid())));
	            // TODO filter out functions that are already in scenGen
	        } catch (EntityNotFoundException e) {
	            e.printStackTrace();
	            return "error";
	        }
	        return "addsgobjfunction";
        }
    }

    @RequestMapping(value="editsgconstraint", method=RequestMethod.GET)
    public String editSGConstraint(ModelMap model,
        @RequestParam(value="optconstid", required=false) Integer constrid,
        @RequestParam(value="selectedcompid", required=false) Integer selectedCompId) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        OptConstraintDTO constraint = null;
        if (constrid != null) {
        	model.put("optconstid", constrid.toString());

            try {
                constraint = (OptConstraintDTO) optConstraintService.findByID(constrid);
                if (constraint.getProject().getPrjid() != project.getPrjid()) {
                    return "error";
                }
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
                return "error";
            }
        } else {
            constraint = new OptConstraintDTO();
        }
        return getEditSGConstraint(project, constraint, model, selectedCompId);
    }

    private String getEditSGConstraint(
        ProjectDTO project, OptConstraintDTO constraint,
        ModelMap model, Integer selectedCompId) {

		securityAuthorization.atLeastExpert_expert(project);

		UserSession userSession = getUserSession(model);

        model.put("constraint", constraint);

        List<ComponentDTO> components = projectService.getComponents(project.getPrjid());
        model.put("components", components);

        if (selectedCompId != null && selectedCompId > 0) {
            userSession.setComponentId(selectedCompId);
            model.put("selectedcompid", selectedCompId);
            model.put("outputVars", componentService.getOutputVariables(selectedCompId));
            model.put("inputParams", componentService.getInputParameters(selectedCompId));
        }
        model.put("metrics", projectService.getMetrics(project.getPrjid()));
        model.put("extParams", projectService.getExtParams(project.getPrjid()));

        return "editsgconstraint";
    }

    @RequestMapping(value="editsgconstraint", method=RequestMethod.POST)
    public String editSGConstraintPost(
        OptConstraintDTO constraint, ModelMap model,
        @RequestParam("optconstid") int constrid,
        HttpServletRequest request)
    {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        if (StringUtils.isBlank(constraint.getLowerbound())) {
            constraint.setLowerbound(null);
        }
        if (StringUtils.isBlank(constraint.getUpperbound())) {
            constraint.setUpperbound(null);
        }
        // TODO validate expression & bounds syntax, check for name clashes
        if ((constraint.getLowerbound() == null && constraint.getUpperbound() == null)
                || StringUtils.isBlank(constraint.getName())
                || StringUtils.isBlank(constraint.getExpression())) {
        	model.put("error", controllerService.getMessage("fill_all_fields", request));
            return getEditSGConstraint(project, constraint, model, null);
        }

        OptConstraintDTO testConstraint = optConstraintService.findByNameAndProject(constraint.getName(), project.getPrjid());

        if (constrid <= 0 && testConstraint != null) {
        	// Constraint already exists
        	model.put("error", controllerService.getMessage("constraint_exists", request));
        	return getEditSGConstraint(project, constraint, model, null);
        }

        String expression = constraint.getExpression();
    	SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid(), scenGen.getScengenid());
     	eu.cityopt.sim.eval.SyntaxChecker.Error error = checker.checkConstraintExpression(expression);

     	if (error != null) {
     	    model.put("error", error.message);
     		return getEditSGConstraint(project, constraint, model, null);
        }

        constraint.setProject(project);
        try {
            if (constrid > 0) {
                // TODO: clone if referenced from elsewhere
                optConstraintService.update(constraint);
            } else {
                scenGenService.addOptConstraint(scenGen.getScengenid(), constraint);
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="deletesgconstraint", method=RequestMethod.POST)
    public String deleteSGConstraintPost(
            ModelMap model, @RequestParam("constrid") Integer constrid) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            // TODO: This only unlinks.  Should probably delete the actual ObjectiveFunction too, if it's not used elsewhere?
            scenGenService.removeOptConstraint(scenGen.getScengenid(), constrid);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="addsgconstraint", method=RequestMethod.GET)
    public String addSGConstraint(ModelMap model) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            model.put("constraints", sortBy(OptConstraintDTO::getName,
                    projectService.getOptConstraints(project.getPrjid())));
            // TODO filter out constraints that are already in scenGen
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
        return "addsgconstraint";
    }

    @RequestMapping(value="addsgconstraint", method=RequestMethod.POST)
    public String addSGConstraintPost(ModelMap model,
            @RequestParam("constrid") int constrid) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            OptConstraintDTO constraint = optConstraintService.findByID(constrid);
            if (constraint.getProject().getPrjid() != project.getPrjid()) {
                return "error";
            }
            scenGenService.addOptConstraint(scenGen.getScengenid(), constraint);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="editsgdecisionvariable", method=RequestMethod.GET)
    public String editSGDecisionVariable(ModelMap model,
            @RequestParam(value="decisionvarid", required=false) Integer decisionvarid,
            @RequestParam(value="selectedcompid", required=false) Integer selectedCompId) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        DecisionVariableDTO decVar = null;
        if (decisionvarid != null) {
            try {
                decVar = decisionVarService.findByID(decisionvarid);
                if (decVar.getScenariogenerator().getScengenid() != scenGen.getScengenid()) {
                    return "error";
                }
            } catch (EntityNotFoundException e) {
                e.printStackTrace();
                return "error";
            }
        } else {
            decVar = new DecisionVariableDTO();
        }

        if (decisionvarid != null) {
        	model.put("decisionvarid", decisionvarid.intValue());
        }
        return getEditSGDecisionVariable(project, decVar, model, selectedCompId);
    }

    private String getEditSGDecisionVariable(ProjectDTO project,
        DecisionVariableDTO decVar, ModelMap model, Integer selectedCompId) {

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);
        model.put("decVar", decVar);
        List<TypeDTO> typechoices = typeService.findAll().stream().filter(
                t -> (t.getName().equalsIgnoreCase("Integer")
                        || t.getName().equalsIgnoreCase("Double")))
                        .collect(Collectors.toList());
        model.put("typechoices", typechoices);
        return "editsgdecisionvariable";
    }

    @RequestMapping(value="editsgdecisionvariable", method=RequestMethod.POST)
    public String editSGDecisionVariablePost(
        DecisionVariableDTO decVar, ModelMap model,
        @RequestParam("decisionvarid") int decisionvarid,
        @RequestParam("typeid") int typeid,
        HttpServletRequest request) {

    	ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        if (StringUtils.isBlank(decVar.getLowerbound())) {
            decVar.setLowerbound(null);
        }
        if (StringUtils.isBlank(decVar.getUpperbound())) {
            decVar.setUpperbound(null);
        }
        if (StringUtils.isBlank(decVar.getName())) {
        	model.put("error", controllerService.getMessage("write_decision_variable_name", request));
        	return getEditSGDecisionVariable(project, decVar, model, null);
        }

        if (decisionvarid <= 0 && decisionVarService.findByNameAndScenGen(decVar.getName(), scenGen.getScengenid()) != null) {
        	// Decision variable already exists
        	model.put("error", controllerService.getMessage("decision_variable_exists", request));
        	return getEditSGDecisionVariable(project, decVar, model, null);
        }

        SyntaxChecker checker = syntaxCheckerService.getSyntaxChecker(project.getPrjid(), scenGen.getScengenid());
     	boolean isValid = checker.isValidTopLevelName(decVar.getName());

     	if (!isValid)
     	{
            model.put("error", controllerService.getMessage("write_another_decision_variable_name", request));
        	return getEditSGDecisionVariable(project, decVar, model, null);
        }

        // TODO validate that the bounds are ordered, allowing for expression bounds
        try {
            decVar.setType(typeService.findByID(typeid));
            decVar.setScenariogenerator(ScenarioGeneratorService.convertDTO(scenGen));
            //TODO: implement tying to input parameter as an alternative to specifying a name
            decVar.setInputparameter(null);
            if (decisionvarid > 0) {
                decisionVarService.update(decVar);
            } else {
                scenGenService.addDecisionVariable(scenGen.getScengenid(), decVar);
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="deletesgdecisionvariable", method=RequestMethod.POST)
    public String deleteSGDecisionVariablePost(
            ModelMap model, @RequestParam("decisionvarid") Integer decisionvarid) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            scenGenService.removeDecisionVariable(scenGen.getScengenid(), decisionvarid);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="geneticalgorithm", method=RequestMethod.GET)
    public String geneticAlgorithm(ModelMap model) {
        ProjectDTO project = (ProjectDTO) model.get("project");

        if (project == null)
        	return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);

        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");

        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid())
        	return "error";

        return getGeneticAlgorithm(project, scenGen, model);
    }

    private String getGeneticAlgorithm(ProjectDTO project, ScenarioGeneratorDTO scenGen, Map<String, Object> model) {

    	try {
			scenGen = scenGenService.findByID(scenGen.getScengenid());
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}

    	model.put("scengenerator", scenGen);

        RunInfo runInfo = scenarioGenerationService.getRunningOptimisations().get(scenGen.getScengenid());
        String strInfo = "";

        if (runInfo != null) {
        	strInfo += runInfo.toString();
        }

        String status = scenGen.getStatus();
        boolean locked = false;

        if ((runInfo != null && !runInfo.toString().isEmpty())
    		|| (status != null && !status.isEmpty()))
        {
        	strInfo += " status: " + status;
        	locked = true;
        }

    	model.put("runinfo", strInfo);
        model.put("locked", locked);
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

    private List<ComponentDTO> pickInputComponents(
            List<ModelParameterDTO> modelParams, List<ComponentDTO> components) {
        Set<Integer> componentIds = new HashSet<>();
        for (ModelParameterDTO mp : modelParams) {
            componentIds.add(mp.getInputparameter().getComponentComponentid());
        }
        return components.stream().filter(
                c -> componentIds.contains(c.getComponentid()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value="geneticalgorithm", method=RequestMethod.POST)
    public String geneticAlgorithmPost(ModelMap model,
            @ModelAttribute("scengenerator") ScenarioGeneratorDTO scenGenForm,
            @RequestParam("algorithmid") int algorithmId,
            @RequestParam(value="run", required=false) String run) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        if (StringUtils.isBlank(scenGenForm.getName())) {
            return getGeneticAlgorithm(project, scenGen, model);
        }
        try {
            scenGen = scenGenService.update(scenGen.getScengenid(), scenGenForm.getName(), scenGenForm.getDescription(), algorithmId);
            model.put("scengenerator", scenGen);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return getGeneticAlgorithm(project, scenGen, model);
        }

        if (!StringUtils.isBlank(run)) {
            try {
                //TODO userid
                scenarioGenerationService.startOptimisation(scenGen.getScengenid(), null);
            } catch (ConfigurationException | ParseException | ScriptException
                | IOException e) {
                e.printStackTrace();
                model.put("error", e.getMessage());
                return getGeneticAlgorithm(project, scenGen, model);
            }
        }

        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="editextparamvalset", method=RequestMethod.GET)
    private String editExtParamValSet(Map<String, Object> model,
        @RequestParam(value="id", required=false) String id) {

		model.put("id", id);
		ProjectDTO project = (ProjectDTO) model.get("project");

		if (project == null)
		{
			return "error";
		}

		securityAuthorization.atLeastExpert_expert(project);
		List<ExtParamValSetDTO> extParamValSets = projectService.getExtParamValSets(project.getPrjid());
		model.put("extParamValSets", extParamValSets);
		Integer extParamValSetId = projectService.getDefaultExtParamSetId(project.getPrjid());

		if (id != null) {
			extParamValSetId = Integer.parseInt(id);
		}

		if (extParamValSetId != null) {
			List<ExtParamValDTO> extParamVals = null;
			try {
				extParamVals = extParamValSetService.getExtParamVals(extParamValSetId);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "error";
			}
			model.put("extParamVals", extParamVals);
		}
		return "extparamsets";
    }

    @RequestMapping(value="editextparamvalset", method=RequestMethod.POST)
    public String editExtParamValSetPost(ModelMap model,
            ExtParamValSetForm form,
            @RequestParam("context") String context) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);

		try {
            int extParamValSetId = form.getExtParamValSet().getExtparamvalsetid();
            List<ExtParamValDTO> extParamVals = extParamValSetService.getExtParamVals(extParamValSetId);
            Map<Integer, TimeSeriesDTOX> timeSeriesByParamId = new HashMap<>();
            for (ExtParamValDTO epv : extParamVals) {
                int extParamId = epv.getExtparam().getExtparamid();
                // TODO validate values
                // TODO import time series as an alternative to scalar value
                epv.setValue(form.getValueByParamId().get(extParamId));
                epv.setComment(form.getCommentByParamId().get(extParamId));
            }
            extParamValSetService.update(form.getExtParamValSet(), extParamVals, timeSeriesByParamId, false);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        //TODO could support other sources...
        return "redirect:/" + context;
    }

    @RequestMapping(value="editsgalgoparamval", method=RequestMethod.GET)
    public String editSGAlgoParamVal(ModelMap model) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            List<AlgoParamValDTO> algoParamVals = scenGenService.getOrCreateAlgoParamVals(scenGen.getScengenid());
            model.put("algoparamvals", algoParamVals);

            AlgoParamValForm form = new AlgoParamValForm();
            for (AlgoParamValDTO apv : algoParamVals) {
                String editValue = (StringUtils.equals(apv.getValue(), apv.getAlgoparam().getDefaultvalue()))
                        ? "" : apv.getValue();
                form.getValueByParamId().put(apv.getAlgoparam().getAparamsid(), editValue);
            }
            model.put("algoparamvalform", form);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/geneticalgorithm.html";
        }
        return "editsgalgoparamval";
    }

    @RequestMapping(value="editsgalgoparamval", method=RequestMethod.POST)
    public String editSGAlgoParamValPost(ModelMap model,
            AlgoParamValForm form) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            List<AlgoParamValDTO> algoParamVals = scenGenService.getAlgoParamVals(scenGen.getScengenid());
            Map<Integer, AlgoParamValDTO> algoParamValMap = new HashMap<>();
            for (AlgoParamValDTO apv : algoParamVals) {
                algoParamValMap.put(apv.getAlgoparam().getAparamsid(), apv);
            }
            for (Map.Entry<Integer, String> entry : form.getValueByParamId().entrySet()) {
                if (StringUtils.isBlank(entry.getValue())) {
                    AlgoParamValDTO apv = algoParamValMap.get(entry.getKey());
                    if (apv != null) {
                        entry.setValue(apv.getAlgoparam().getDefaultvalue());
                    }
                }
                //TODO check if the values are numbers etc.
            }
            scenGenService.setAlgoParamVals(scenGen.getScengenid(), form.getValueByParamId());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return "redirect:/geneticalgorithm.html";
    }

    @RequestMapping(value="editsgmodelparams", method=RequestMethod.GET)
    public String editSGModelParams(ModelMap model) {
        ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);
        ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            ModelParameterGrouping grouping = scenGenService.getModelParameterGrouping(scenGen.getScengenid());
            ModelParamForm form = new ModelParamForm();

            for (int inputId : grouping.getInputParameters().keySet())
            {
                ModelParameterGrouping.MultiValue multivalue = grouping.getMultiValued().get(inputId);
                String value = "";
                String group = "";
                if (multivalue != null) {
                    value = multivalue.getValueString();
                    group = multivalue.getGroup().getName();
                } else {
                	value = grouping.getFreeText(inputId);
                }
                form.getValueByInputId().put(inputId, value);
                form.getGroupByInputId().put(inputId, group);
            }
            return getEditSGModelParams(project, scenGen, model, form, grouping);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return "redirect:/geneticalgorithm.html";
        }
    }

    String getEditSGModelParams(
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

    @RequestMapping(value="editsgmodelparams", method=RequestMethod.POST)
    public String editSGModelParamsPost(ModelMap model,
        ModelParamForm form,
        @RequestParam(value="newgroup", required=false) String newGroup,
        @RequestParam(value="cleangroups", required=false) String cleanGroups,
        BindingResult result) {

    	ProjectDTO project = (ProjectDTO) model.get("project");
        if (project == null) return "redirect:/openproject.html";

		securityAuthorization.atLeastExpert_expert(project);

		ScenarioGeneratorDTO scenGen = (ScenarioGeneratorDTO) model.get("scengenerator");
        if (scenGen == null || scenGen.getProject().getPrjid() != project.getPrjid()) return "redirect:/openproject.html";

        try {
            ModelParameterGrouping grouping = scenGenService.getModelParameterGrouping(scenGen.getScengenid());
            String errors = "";

            UserSession session = getUserSession(model);
            int nComponentId = session.getComponentId();
        	System.out.println("selected component " + nComponentId);

            for (Map.Entry<Integer, String> entry : form.getValueByInputId().entrySet())
            {
            	int inputId = entry.getKey();
                String value = entry.getValue();
                String group = form.getGroupByInputId().get(inputId);
                InputParameterDTO inputParam = null;

                try {
        			inputParam = inputParamService.findByID(inputId);
        		} catch (EntityNotFoundException e) {
        			e.printStackTrace();
        		}

                /*if (inputParam != null)
                	System.out.println("input param id " + inputParam.getInputid() + " " + inputParam.getComponentName() + " input: " + inputParam.getName() + " value " + value);
                */

                if (nComponentId == inputParam.getComponentComponentid())
                {
                	System.out.println("same component " + nComponentId + " input param id " + inputParam.getInputid() + " " + inputParam.getComponentName() + " input: " + inputParam.getName() + " value " + value);
	                ModelParameterDTO modelParamTemp = new ModelParameterDTO();
	                modelParamTemp.setValue(value);
	                modelParamTemp.setInputparameter(inputParam);
	       	 		validator.validate(modelParamTemp, result);
                }
                else
                {

                }

    		    if (result.hasErrors()) {
    		    	errors = errors + result.getGlobalError().getCode() + "<br>\n";
    		    }
    		    else
    		    {
	                try {
		                if (StringUtils.isBlank(group)) {
		                	grouping.setFreeText(inputId, value);
		                } else {
		                	grouping.setMultiValue(inputId, value, group);
		                }
	                } catch (ParseException e) {
	                	errors = errors + e.getMessage() + "<br>\n";
	                }
    		    }
            }

            for (ModelParameterGrouping.Group group : grouping.findMismatchingGroups()) {
            	errors = errors + "Error in group " + group.getName()
            			+ ": number of values varies between " + group.getMinNumberOfValues()
            			+ " and " + group.getMaxNumberOfValues() + "<br>\n";
            }
            if (cleanGroups != null || newGroup != null || !errors.isEmpty()) {
            	if (newGroup != null) {
            		grouping.addGroup();
            	}
                if (cleanGroups != null) {
                	grouping.deleteEmptyGroups();
                }
                // Decision variable changes are saved here because their content
                // is not preserved in the form command object.
                scenGenService.updateDecisionVariables(scenGen.getScengenid(), grouping);
                model.put("error", errors);
            	return getEditSGModelParams(project, scenGen, model, form, grouping);
            } else {
            	scenGenService.setModelParameterGrouping(scenGen.getScengenid(), grouping);
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    	return "redirect:/geneticalgorithm.html";
    }

    String validateTypedValue(TypeDTO type, String value) {
        Type simType = Type.getByName((type != null) ? type.getName() : null);
        try {
            simType.parse(value, simulationService.getDummyEvaluationSetup());
            return null;
        } catch (ParseException e) {
            // TODO: use SyntaxChecker to validate
            return e.getMessage();
        }
    }

    @RequestMapping(value="selectcomponent", method=RequestMethod.POST)
    public @ResponseBody String selectComponentPost(ModelMap model,
            @RequestParam("selectedcompid") int selectedCompId) {
	    UserSession userSession = getUserSession(model);
	    userSession.setComponentId(selectedCompId);
	    model.put("usersession", userSession);
    	System.out.println("component selected id " + selectedCompId);

        return "";
    }

    private UserSession getUserSession(Map<String, Object> model) {
        UserSession userSession = (UserSession) model.get("usersession");
        if (userSession == null) {
            userSession = new UserSession();
            model.put("usersession", userSession);
        }
        return userSession;
    }

    private static <T> List<T> sortBy(Function<T, String> key, Collection<T> collection) {
        List<T> list = new ArrayList<>(collection);
        list.sort(Comparator.comparing(key,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return list;
    }

    /// Sorts components by name, leaving the special CITYOPT component last.
    private List<ComponentDTO> sortComponentsByName(List<ComponentDTO> components) {
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
}
