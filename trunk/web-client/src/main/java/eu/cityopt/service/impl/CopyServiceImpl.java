package eu.cityopt.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
//import org.reflections.Reflections;
//import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.model.AlgoParamVal;
import eu.cityopt.model.Component;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.model.DecisionVariableResult;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.ModelParameter;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.ObjectiveFunctionResult;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptConstraintResult;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.OptSetScenarios;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenGenObjectiveFunction;
import eu.cityopt.model.ScenGenOptConstraint;
import eu.cityopt.model.ScenGenResult;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.AlgoParamValRepository;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.DecisionVariableRepository;
import eu.cityopt.repository.DecisionVariableResultRepository;
import eu.cityopt.repository.ExtParamRepository;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValSetCompRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ObjectiveFunctionRepository;
import eu.cityopt.repository.ObjectiveFunctionResultRepository;
import eu.cityopt.repository.OptConstraintRepository;
import eu.cityopt.repository.OptConstraintResultRepository;
import eu.cityopt.repository.OptSearchConstRepository;
import eu.cityopt.repository.OptSetScenariosRepository;
import eu.cityopt.repository.OptimizationSetRepository;
import eu.cityopt.repository.OutputVariableRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenGenObjectiveFunctionRepository;
import eu.cityopt.repository.ScenGenOptConstraintRepository;
import eu.cityopt.repository.ScenGenResultRepository;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.repository.ScenarioMetricsRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.service.CopyService;
import eu.cityopt.service.EntityNotFoundException;

@Service
public class CopyServiceImpl implements CopyService {
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AlgoParamValRepository algoParamValRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ScenarioRepository scenarioRepository;
	
	@Autowired
	private ComponentRepository componentRepository;
	
	@Autowired
	private DecisionVariableResultRepository decisionVariableResultRepository;
	
	@Autowired
	private ExtParamValRepository extParamValRepository;	
	
	@Autowired
	private ExtParamValSetRepository extParamValSetRepository;	
	
	@Autowired
	private ExtParamValSetCompRepository extParamValSetCompRepository;	
	
	@Autowired
	private ExtParamRepository extParamRepository;
	
	@Autowired
	private InputParameterRepository inputParameterRepository;
	
	@Autowired
	private InputParamValRepository inputparamvalRepository;
	
	@Autowired
	private TimeSeriesValRepository timeSeriesValRepository;
	
	@Autowired
	private SimulationResultRepository simulationResultRepository;
	
	@Autowired
	private ScenGenResultRepository scenGenResultRepository;
	
	@Autowired
	private TimeSeriesRepository timeSeriesRepository;
	
	@Autowired
	private ScenarioMetricsRepository scenarioMetricsRepository;
	
	@Autowired
	private OptSetScenariosRepository optSetScenarioRepository;
	
	@Autowired
	private OptimizationSetRepository optimizationSetRepository;
	
	@Autowired
	private OutputVariableRepository outputVariableRepository;
	
	@Autowired
	private OptSearchConstRepository optSearchConstRepository;
	
	@Autowired
	private OptConstraintResultRepository optConstraintResultRepository;
	
	@Autowired
	private ObjectiveFunctionRepository objectiveFunctionRepository;
	
	@Autowired
	private ObjectiveFunctionResultRepository objectiveFunctionResultRepository;
	
	@Autowired
	private DecisionVariableRepository decisionVariableRepository;
	
	@Autowired
	private ScenGenObjectiveFunctionRepository scenGenObjectiveFunctionRepository;
	
	@Autowired
	private ScenGenOptConstraintRepository scenGenOptConstraintRepository;
	
	@Autowired
	private OptConstraintRepository optConstraintRepository;
	
	@Autowired
	private ScenarioGeneratorRepository scenarioGeneratorRepository;
	
	@Autowired
	private MetricValRepository metricValRepository;
	
	@Autowired
	private MetricRepository metricRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	static Logger log = Logger.getLogger(ProjectServiceImpl.class);
	
	@Transactional
	@Override
	public ScenarioDTO copyScenario (int id, String name, boolean copyInputParamVals, 
			boolean copyMetricValues, boolean addToOptimizationSet, boolean copySimulationResults) throws 
	EntityNotFoundException {
		
		Scenario scenario = scenarioRepository.findOne(id);

		if(scenario == null) {
			throw new EntityNotFoundException();
		}
		
		Scenario copyScenario = copyScenario(id, name, copyInputParamVals, copyMetricValues, 
				addToOptimizationSet, copySimulationResults, new HashSet<MetricVal>());
		
		return modelMapper.map(copyScenario, ScenarioDTO.class);
	}
	
	@Transactional
	@Override
	public ProjectDTO copyProject (int id, String name) throws EntityNotFoundException{
		
		Project project = projectRepository.findOne(id);
	
		if(project == null) {
			throw new EntityNotFoundException();
		}
		
		Project copyProject = copyProject(project, name);
		
		return modelMapper.map(copyProject, ProjectDTO.class);
	}
	
	@Transactional
	private Scenario copyScenario(int id, String name, boolean copyInputParamVals, 
			boolean copyMetricValues, boolean addToOptimizationSet, boolean copySimulationResults,
			Set<MetricVal> mvM) 
					throws 	EntityNotFoundException {
		
		Scenario scenario = scenarioRepository.findOne(id);
	
		if(scenario == null) {
			throw new EntityNotFoundException();
		}
		
		Scenario copyScenario = scenario.clone();
		copyScenario.setName(name);
		copyScenario.setScenid(0);
		//may only be referenced by one scenario
		copyScenario.setInputparamvals(null);
		copyScenario.setScenariometricses(null);
		copyScenario.setOptimizationsets(null);
		copyScenario.setOptsetscenarioses(null);	
		copyScenario.setSimulationresults(null);
		copyScenario.setScengenresults(null);
		copyScenario = scenarioRepository.save(copyScenario);
		
		if(copyInputParamVals){
//			Set<InputParamVal> valuesC = new HashSet<InputParamVal>();
			for(InputParamVal val : scenario.getInputparamvals()){
				InputParamVal valC = val.clone();
				valC.setInputparamvalid(0);
				valC.setScenario(copyScenario);
//				valuesC.add(valC);
				valC = inputparamvalRepository.save(valC);
			}			
		}		
		
		//copy scenario metrics
		if(copyMetricValues){
			Set<ScenarioMetrics> smSet = new HashSet<ScenarioMetrics>();
			for(ScenarioMetrics sm : scenario.getScenariometricses()){
				ScenarioMetrics smC = sm.clone();
				smC.setScenmetricid(0);
				smC.setScenario(copyScenario);
				smC.setMetricvals(null);
				smC = scenarioMetricsRepository.save(smC);
				Set<MetricVal> mvalC = new HashSet<MetricVal>();
				
				for(MetricVal mv : sm.getMetricvals()){
					MetricVal mvC = mv.clone();
					mvC.setMetricvalid(0);
					TimeSeries ts = mv.getTimeseries();
					if(ts != null){
						TimeSeries tsc = new TimeSeries();
						tsc.setType(ts.getType());
						tsc = timeSeriesRepository.save(tsc);
						Set<TimeSeriesVal> tsVals = copyTimeSeriesVals(tsc, ts.getTimeseriesvals());
						timeSeriesValRepository.save(tsVals);					
						mvC.setTimeseries(tsc);
					}
					mvC.setScenariometrics(smC);
					mvC = metricValRepository.save(mvC);
					mvalC.add(mvC);
					mvM.add(mvC);
				}
				smC.setMetricvals(mvalC);
				smSet.add(smC);
			}
			copyScenario.setScenariometricses(smSet);
		}
		
		if(addToOptimizationSet){
			for(OptSetScenarios oss : scenario.getOptsetscenarioses()){
				OptSetScenarios ossC = oss.clone();
				ossC.setOptscenid(0);
				ossC.setScenario(copyScenario);
				ossC = optSetScenarioRepository.save(ossC);
			}
		}
		
		if(copySimulationResults){
			for(SimulationResult sr : scenario.getSimulationresults()){
				SimulationResult srC = copySimulationResult(sr);		
				srC.setScenario(copyScenario);
				srC = simulationResultRepository.save(srC);
			}
		}
		
		copyScenario = scenarioRepository.saveAndFlush(copyScenario);

		return copyScenario;
	}
	
	@Transactional
	private Project copyProject(Project project, String name) throws EntityNotFoundException{
		
		Project copyProject = project.clone();
		copyProject.setName(name);
		copyProject.setPrjid(0);
		//may only be referenced by one project
		copyProject.setUsergroupprojects(null);
		copyProject.setExtparams(null);
		copyProject.setComponents(null);
		copyProject.setMetrics(null);
		copyProject.setObjectivefunctions(null);
		copyProject.setScenariogenerators(null);
		copyProject.setOptconstraints(null);
		copyProject.setOptimizationsets(null); //copy?
		copyProject.setScenarios(null);
		
		copyProject = projectRepository.save(copyProject);
		
		//save copied extparamvalsets to set references later...
		Map<Integer, ExtParamValSet> copiedEPVSets = new HashMap<Integer, ExtParamValSet>();
		for(ExtParam ep : project.getExtparams()){
			ExtParam epC = ep.clone();
			epC.setExtparamid(0);
			epC.setProject(copyProject);
			epC.setExtparamvals(null);
//			if(epC.getTimeseries() != null){
//				epC.setTimeseries(copyTimeSeries(epC.getTimeseries()));
//			}
			epC = extParamRepository.save(epC);
			for(ExtParamVal epv : ep.getExtparamvals()){
				ExtParamVal epvC = epv.clone();
				epvC.setExtparamvalid(0);
				epvC.setExtparam(epC);
				epvC.setExtparamvalsetcomps(null);
				
				if(epvC.getTimeseries() != null){
					epvC.setTimeseries(copyTimeSeries(epvC.getTimeseries()));
				}
				
				epvC = extParamValRepository.save(epvC);
				
				//n:n relation is a bit tricky: it has to be checked if the epvSet has already been copied
				for(ExtParamValSetComp epvsc : epv.getExtparamvalsetcomps()){
					ExtParamValSetComp epvscC = new ExtParamValSetComp();
					epvscC.setExtparamval(epvC);
					Integer epvsIdSrc = epvsc.getExtparamvalset().getExtparamvalsetid();
					if(copiedEPVSets.containsKey(epvsIdSrc)){ //epvs has been copied
						epvscC.setExtparamvalset(copiedEPVSets.get(epvsIdSrc));
					}else{
						//there is nothing to clone
						//epvsc.getExtparamvalset().clone
						ExtParamValSet epvsC = new ExtParamValSet();
						epvsC.setName(epvsc.getExtparamvalset().getName());
						epvsC = extParamValSetRepository.saveAndFlush(epvsC);
						copiedEPVSets.put(epvsc.getExtparamvalset().getExtparamvalsetid(), epvsC);
						epvscC.setExtparamvalset(epvsC);
					}
					extParamValSetCompRepository.save(epvscC);
				}				
			}
		}		
		em.flush();
		//copy components
		Set<InputParamVal> componentsInputParamValues = new HashSet<InputParamVal>();
		Map<Integer, InputParameter> componentsInputParameter = new HashMap<Integer, InputParameter>();
		Map<Integer, SimulationResult> copiedSimulationResults = new HashMap<Integer, SimulationResult>();
		//inputParamValues need to be aligned to the right scenario later...
		for(Component c : project.getComponents()){
			Component cC = c.clone();
			cC.setComponentid(0);
			cC.setProject(copyProject);
			cC.setInputparameters(null);
			cC.setOutputvariables(null);
			cC = componentRepository.save(cC);
			//outvars, inputparam
			for(InputParameter ip : c.getInputparameters()){
				InputParameter ipC = ip.clone();
				ipC.setInputid(0);
				ipC.setComponent(cC);
				ipC.setInputparamvals(null);
				ipC.setModelparameters(null);
				ipC = inputParameterRepository.save(ipC);
				for(InputParamVal ipv : ip.getInputparamvals()){
					InputParamVal ipvC = ipv.clone();
					ipvC.setInputparamvalid(0);
					ipvC.setInputparameter(ipC);
					ipvC = inputparamvalRepository.save(ipvC);
					componentsInputParamValues.add(ipvC);
				}
				componentsInputParameter.put(ip.getInputid(), ipC);
			}
			for(OutputVariable ov : c.getOutputvariables()){
				OutputVariable ovC = ov.clone();
				ovC.setOutvarid(0);
				ovC.setComponent(cC);
				ovC.setSimulationresults(null);
				ovC = outputVariableRepository.save(ovC);
				for(SimulationResult sr : ov.getSimulationresults()){
					SimulationResult srC = copySimulationResult(sr);
					srC.setOutputvariable(ovC);
					srC.setScenario(null);
//					srC = simulationResultRepository.save(srC);
					//cannot be saved here, because there is no copy of the scenario yet
					copiedSimulationResults.put(sr.getSimresid(), srC);
				}
			}
		}
		em.flush();
		HashMap<Integer, Metric> copiedMetrics = new HashMap<Integer, Metric>();
		for(Metric m : project.getMetrics()){
			Metric mC = m.clone();
			mC.setMetid(0);
			//TODO: support copying metric values?
			mC.setMetricvals(null);
			mC.setProject(copyProject);
			mC = metricRepository.save(mC);
			copiedMetrics.put(m.getMetid(), mC);
		}
		em.flush();
		//copied objective functions might be needed, if there's a reference to scenariogenerator
		Map<Integer, ObjectiveFunction> copiedOptFunctions = new HashMap<Integer, ObjectiveFunction>();
		Map<Integer, OptimizationSet> copiedOptimizationSets = new HashMap<Integer, OptimizationSet>();
		Map<Integer, ScenGenResult> copiedScenGenResults = new HashMap<Integer, ScenGenResult>();
		
		for(ObjectiveFunction of : project.getObjectivefunctions()){
			ObjectiveFunction ofC = of.clone();
			ofC.setObtfunctionid(0);
			ofC.setOptimizationsets(null);
			ofC.setScengenobjectivefunctions(null);	
			ofC.setObjectivefunctionresults(null);
			ofC.setProject(copyProject);
			ofC = objectiveFunctionRepository.save(ofC);
			
			for(OptimizationSet os : of.getOptimizationsets()){
				OptimizationSet osC = os.clone();
				osC.setOptid(0);
				osC.setExtparamvalset(copiedEPVSets.get(osC.getExtparamvalset().getExtparamvalsetid()));
				osC.setObjectivefunction(ofC);
				osC.setOptsearchconsts(null);
				osC.setOptsetscenarioses(null);
				osC = optimizationSetRepository.save(osC);
				copiedOptimizationSets.put(os.getOptid(), osC);
			}
			
			for(ObjectiveFunctionResult ofr : of.getObjectivefunctionresults()){
				//copy ScenGenResult:
				ScenGenResult sgrC = null;
				if(copiedScenGenResults.containsKey(ofr.getScengenresult().getScengenresultid())){
					sgrC = copiedScenGenResults.get(ofr.getScengenresult().getScengenresultid());
				}else{
					sgrC = copyScenGenResult(ofr.getScengenresult());
					copiedScenGenResults.put(ofr.getScengenresult().getScengenresultid(), sgrC);
				}
				
				ObjectiveFunctionResult ofrC = ofr.clone();
				ofrC.setObjectivefunctionresultid(0);
				ofrC.setScengenresult(sgrC);
				ofrC.setObjectivefunction(ofC);
				ofrC = objectiveFunctionResultRepository.save(ofrC);
			}	
			
			if(of.getScengenobjectivefunctions() != null && of.getScengenobjectivefunctions().size() > 0){
				copiedOptFunctions.put(of.getObtfunctionid(), ofC);
			}
		}
		em.flush();
		Map<Integer, ScenarioGenerator> copiedScenarioGenerators = new HashMap<Integer, ScenarioGenerator>();
		for(ScenarioGenerator sg : project.getScenariogenerators()){
			ScenarioGenerator sgC = sg.clone();
			sgC.setScengenid(0);
			sgC.setAlgoparamvals(null);
			sgC.setDecisionvariables(null);
			sgC.setExtparamvalset(null); 
			sgC.setModelparameters(null); 
			sgC.setScenarios(null);
			sgC.setScengenobjectivefunctions(null);
			sgC.setScengenoptconstraints(null);
			sgC.setScengenresults(null);
			sgC = scenarioGeneratorRepository.save(sgC);
			
			//copy all scengenresults
			for(ScenGenResult sgr : sg.getScengenresults()){
				if(!copiedScenGenResults.containsKey(sgr.getScengenresultid())){
					ScenGenResult sgrC = copyScenGenResult(sgr);
					sgrC.setScenariogenerator(sgC);
					copiedScenGenResults.put(sgr.getScengenresultid(), sgrC);
				}
			}
			
			//set reference to previously copied objective functions
			if(sg.getScengenobjectivefunctions() != null)
			for(ScenGenObjectiveFunction sgof : sg.getScengenobjectivefunctions()){
				ScenGenObjectiveFunction sgofC = new ScenGenObjectiveFunction();
				sgofC.setScenariogenerator(sgC);
				ObjectiveFunction of = copiedOptFunctions.get(sgof.getSgobfunctionid());
				sgofC.setObjectivefunction(of);
				scenGenObjectiveFunctionRepository.save(sgofC);
			}
			
			if(sg.getDecisionvariables() != null)
			for(DecisionVariable dv : sg.getDecisionvariables()){
				DecisionVariable dvC = new DecisionVariable();
				dvC.setDecisionvarid(0);
				dvC.setScenariogenerator(sgC);
				//copy decisionvariableresult
				for(DecisionVariableResult dvr : dv.getDecisionvariableresults()){
					DecisionVariableResult dvrC = dvr.clone();
					dvrC.setDecvarresultid(0);
					//get previously copied scengenresult
					dvrC.setScengenresult(copiedScenGenResults.get(dvr.getScengenresult().getScengenresultid()));
					decisionVariableResultRepository.save(dvrC);
				}
				
				//set reference to inputParameter
				InputParameter ip = componentsInputParameter.get(dv.getInputparameter().getInputid());
				dvC.setInputparameter(ip);
				decisionVariableRepository.save(dvC);
			}
			
			if(sg.getExtparamvalset() != null){
				sgC.setExtparamvalset(copiedEPVSets.get(sg.getExtparamvalset().getExtparamvalsetid()));
			}
			
			if(sg.getModelparameters() != null)
			for(ModelParameter mp : sg.getModelparameters()){
				ModelParameter mpC = mp.clone();
				mpC.setModelparamid(0);
				InputParameter ip = componentsInputParameter.get(mp.getInputparameter().getInputid());
				mpC.setInputparameter(ip);
			}
			
			if(sg.getAlgoparamvals() != null)
			for(AlgoParamVal apv : sg.getAlgoparamvals()){
				AlgoParamVal apvC = apv.clone();
				apvC.setAparamvalid(0);
				apvC.setScenariogenerator(sgC);
				algoParamValRepository.save(apvC);
			}
			
			
			copiedScenarioGenerators.put(sg.getScengenid(), sgC);
		}
		em.flush();
		for(OptConstraint oc : project.getOptconstraints()){
			OptConstraint ocC = oc.clone();
			ocC.setOptconstid(0);
			ocC.setProject(copyProject);
			ocC.setScengenoptconstraints(null);
			ocC.setOptsearchconsts(null);
			ocC.setOptconstraintresults(null);
			ocC = optConstraintRepository.save(ocC);
			for(ScenGenOptConstraint sgoc : oc.getScengenoptconstraints()){
				ScenGenOptConstraint sgocC = new ScenGenOptConstraint();
				ScenarioGenerator sgC = copiedScenarioGenerators.get(sgoc.getScenariogenerator().getScengenid());
				sgocC.setScenariogenerator(sgC);
				sgocC.setOptconstraint(ocC);
				scenGenOptConstraintRepository.save(sgocC);
			}
			for(OptSearchConst osc : oc.getOptsearchconsts()){
				OptSearchConst oscC = new OptSearchConst();
				oscC.setOptimizationset(copiedOptimizationSets.get(osc.getOptimizationset().getOptid()));
				oscC.setOptconstraint(ocC);
				optSearchConstRepository.save(oscC);
			}
			for(OptConstraintResult ocr : oc.getOptconstraintresults()){
				OptConstraintResult ocrC = ocr.clone();
				ocrC.setOptconstresultid(0);
				//get previously copied scengenresult
				ocrC.setScengenresult(copiedScenGenResults.get(ocrC.getScengenresult().getScengenresultid()));
				optConstraintResultRepository.save(ocrC);
			}
		}
		em.flush();
		for(Scenario s : project.getScenarios()){
			Set<MetricVal> mvM = new HashSet<MetricVal>();
			
			Scenario sC = copyScenario(s.getScenid(), "copy of " + s.getName(), 
					false, true, false, false, mvM);
			
			for(OptSetScenarios oss : s.getOptsetscenarioses()){
				OptSetScenarios ossC = oss.clone();
				OptimizationSet osC = copiedOptimizationSets.get(oss.getOptimizationset().getOptid());
				ossC.setOptimizationset(osC);
				ossC.setScenario(sC);
				optSetScenarioRepository.save(ossC);
			}
			
			for(SimulationResult sr : s.getSimulationresults()){
				SimulationResult srC = copiedSimulationResults.get(sr.getSimresid());
				srC.setScenario(sC);
				srC = simulationResultRepository.save(srC);
			}
			
			if(s.getScenariogenerator() != null){
				sC.setScenariogenerator(copiedScenarioGenerators.get(s.getScenariogenerator().getScengenid()));
			}
			
			for(ScenGenResult sgr : s.getScengenresults()){
				ScenGenResult sgrC = copiedScenGenResults.get(sgr.getScengenresultid());
				sgrC.setScenario(sC);
				scenGenResultRepository.save(sgrC);
			} 
			
			for(MetricVal mv : mvM){
				mv.setMetric(copiedMetrics.get(mv.getMetric().getMetid()));
				mv = metricValRepository.save(mv);
			}
			
			if(sC.getScenariometricses() != null)
				for(ScenarioMetrics sm : sC.getScenariometricses()){
					sm.setExtparamvalset(copiedEPVSets.get(sm.getExtparamvalset().getExtparamvalsetid()));
					scenarioMetricsRepository.save(sm);
				}
			
			for(InputParamVal ipv : componentsInputParamValues){
				ipv.setScenario(sC);
				ipv = inputparamvalRepository.save(ipv);
			}
			
			sC.setProject(copyProject);
			sC.setName(s.getName());
			
		}
		em.flush();
		return copyProject;
	}
	
	private ScenGenResult copyScenGenResult(ScenGenResult sgr){
		ScenGenResult sgrC = sgr.copy();
		sgrC.setObjectivefunctionresults(null);
		sgrC.setDecisionvariableresults(null);
		sgrC.setOptconstraintresults(null);
		sgrC.setScenariogenerator(null);
		sgrC.setScenario(null);
		return scenGenResultRepository.save(sgrC);
	}
	
	@Transactional
	public MetricDTO copyMetric (int id, String name) throws EntityNotFoundException{
		
		Metric metric = metricRepository.findOne(id);
		
		if(metric == null) {
			throw new EntityNotFoundException();
		}
		
		Metric metricC = metric.clone();
		metricC.setMetid(0);
		metricC.setName(name);
		//TODO: support metric value copying?
		metricC.setMetricvals(null);
		
		metricC = metricRepository.save(metricC);
		
		return modelMapper.map(metricC, MetricDTO.class);
	}
	
	@Transactional
	private SimulationResult copySimulationResult(SimulationResult src){
		//the copy result can't be saved, because scenario/outvar combination needs to be unique
		//timeSeries is needed before
		TimeSeries tscopy = copyTimeSeries(src.getTimeseries());
		SimulationResult srC = src.clone();
		srC.setSimresid(0);
		srC.setTimeseries(tscopy);
		
//		srC.setScenario(copyScenario);
//		srC = simulationResultRepository.save(srC);
		return srC;
	}
	
	@Transactional
	private TimeSeries copyTimeSeries(TimeSeries src){
		TimeSeries tscopy = new TimeSeries();
		tscopy.setType(src.getType());
		tscopy = timeSeriesRepository.save(tscopy);
		//copy values
		Set<TimeSeriesVal> tsvCopies = copyTimeSeriesVals(tscopy, src.getTimeseriesvals());
		tscopy.setTimeseriesvals(tsvCopies);
		return tscopy;
	}
	
	@Transactional
	private Set<TimeSeriesVal> copyTimeSeriesVals(TimeSeries timeseries, Set<TimeSeriesVal> timeseriesvals){			
		
		Set<TimeSeriesVal> tsvCopies = new HashSet<TimeSeriesVal>();
		for(TimeSeriesVal tsv : timeseriesvals){
			TimeSeriesVal  tsvC = tsv.clone();
			tsvC.setTseriesvalid(0);
			tsvC.setTimeseries(timeseries);
			tsvC = timeSeriesValRepository.save(tsvC);
			tsvCopies.add(tsvC);
		}
		em.flush();
		return tsvCopies;		
	}
	
}
