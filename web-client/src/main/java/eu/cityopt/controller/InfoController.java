package eu.cityopt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectService;
import eu.cityopt.service.SimulationModelService;


@Controller
public class InfoController {
	
	@Autowired
    ControllerService controllerService;
    
    @Autowired
    SecurityAuthorization securityAuthorization;

    @Autowired
    ProjectService projectService;

    @Autowired
    SimulationModelService simModelService;
    
	@RequestMapping(value ="createproject_info", method=RequestMethod.GET)
	public String InfoCreateProject() {		
		return "createproject_info";				
	}
		
	// Pagination test
	@RequestMapping(value ="pagination_prototype", method=RequestMethod.GET)
	public String PaginationPrototype() {
		return "pagination_prototype";
	}
	
	@RequestMapping( value="importdata_info", method=RequestMethod.GET)
	public String ImportDataInfo() {		
		return "importdata_info";
	}

	@RequestMapping(value="createmetric_info", method=RequestMethod.GET)
	public String CreateMetricInfo() {		
		return "createmetric_info";
	}

	@RequestMapping( value="editproject_info", method=RequestMethod.GET)
	public String EditProjectInfo() {		
		return "editproject_info";
	}

	@RequestMapping( value="editscenario_info", method=RequestMethod.GET)
	public String EditScenarioInfo() {		
		return "editscenario_info";
	}

	@RequestMapping( value="projectparameters_info", method=RequestMethod.GET)
	public String ProjectParametersInfo() {		
		return "projectparameters_info";
	}

	@RequestMapping( value="dboptimization_info", method=RequestMethod.GET)
	public String DBOptimizationInfo() {		
		return "dboptimization_info";
	}

	@RequestMapping( value="ga_info", method=RequestMethod.GET)
	public String GAInfo() {		
		return "ga_info";
	}

	@RequestMapping(value="extparams_info", method=RequestMethod.GET)
	public String ExtParamsInfo() {		
		return "extparams_info";
	}

	@RequestMapping(value="scenarioparameters_info", method=RequestMethod.GET)
	public String ScenarioParamsInfo() {		
		return "scenarioparameters_info";
	}

	@RequestMapping(value="createoptimizationset_info", method=RequestMethod.GET)
	public String CreateOptSetInfo() {		
		return "createoptimizationset_info";
	}

	@RequestMapping(value="createobjfunction_info", method=RequestMethod.GET)
	public String CreateObjFuncInfo() {		
		return "createobjfunction_info";
	}

	@RequestMapping(value="editsgmodelparams_info", method=RequestMethod.GET)
	public String EditModelParamsInfo() {		
		return "editsgmodelparams_info";
	}

	@RequestMapping(value="editsgalgoparamval_info", method=RequestMethod.GET)
	public String EditAlgoParamValInfo() {		
		return "editsgalgoparamval_info";
	}

	@RequestMapping(value="gridsearch_info", method=RequestMethod.GET)
	public String GridSearchInfo() {		
		return "gridsearch_info";
	}

	@RequestMapping(value="timeserieschart_info", method=RequestMethod.GET)
	public String TimeSeriesChartInfo() {		
		return "timeserieschart_info";
	}

	@RequestMapping(value="summarychart_info", method=RequestMethod.GET)
	public String SummaryChartInfo() {		
		return "summarychart_info";
	}

	@RequestMapping(value="gachart_info", method=RequestMethod.GET)
	public String GAChartInfo() {		
		return "gachart_info";
	}

	@RequestMapping(value="table_info", method=RequestMethod.GET)
	public String TableInfo() {		
		return "table_info";
	}

	@RequestMapping(value="modelinfo", method=RequestMethod.GET)
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

        return "modelinfo";
    }	

	@RequestMapping(value="simulationinfo", method=RequestMethod.GET)
    public String simInfoPage(Map<String, Object> model,
		HttpServletRequest request)
    {
    	ProjectDTO project = (ProjectDTO) model.get("project");
		ScenarioDTO scenario = (ScenarioDTO) model.get("scenario");
		
        if (project == null || scenario == null)
        {
        	return "error";
        }

		securityAuthorization.atLeastGuest_guest(project);

        model.put("title", controllerService.getMessage("simulation_info_for_scenario", request) + " " + scenario.getName());
		
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
