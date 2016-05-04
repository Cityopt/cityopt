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

	
}
