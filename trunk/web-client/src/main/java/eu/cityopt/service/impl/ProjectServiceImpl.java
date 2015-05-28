package eu.cityopt.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ComponentDTO;
import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.MetricDTO;
import eu.cityopt.DTO.ObjectiveFunctionDTO;
import eu.cityopt.DTO.OpenOptimizationSetDTO;
import eu.cityopt.DTO.OptSetToOpenOptimizationSetDTOMap;
import eu.cityopt.DTO.OptimizationSetDTO;
import eu.cityopt.DTO.ProjectDTO;
import eu.cityopt.DTO.ProjectScenariosDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.DTO.ScenarioGeneratorToOpenOptimizationSetDTOMap;
import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.ObjectiveFunction;
import eu.cityopt.model.OptConstraint;
import eu.cityopt.model.OptSearchConst;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.repository.CustomQueryRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{
	
	private ModelMapper modelMapper;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private CustomQueryRepository cqRepository;
	
	static Logger log = Logger.getLogger(ProjectServiceImpl.class);

	@Autowired
	public ProjectServiceImpl(ModelMapper modelMapper) {
//		modelMapper = new ModelMapper();
//		modelMapper.addMappings(new ScenarioMap());
		this.modelMapper = modelMapper;
		modelMapper.addMappings(new OptSetToOpenOptimizationSetDTOMap());
		modelMapper.addMappings(new ScenarioGeneratorToOpenOptimizationSetDTOMap());
	}

	@Transactional(readOnly = true)
	public List<ProjectDTO> findAll() {
		List<Project> projects = projectRepository.findAll();
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectScenariosDTO> findAllWithScenarios() {
		List<Project> projects = projectRepository.findAllWithScenarios();
		List<ProjectScenariosDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectScenariosDTO>>() {}.getType());
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<ProjectDTO> findByName(String name) {
		List<Project> projects = projectRepository.findByName(name);
		List<ProjectDTO> result 
			= modelMapper.map(projects, new TypeToken<List<ProjectDTO>>() {}.getType());
		return result;
	}

	@Transactional
	public ProjectDTO save(ProjectDTO projectDTO) {
		Project result = modelMapper.map(projectDTO, Project.class);
		result = projectRepository.save(result);
		projectDTO = modelMapper.map(result, ProjectDTO.class);
		return projectDTO;
	}

	@Transactional
	public void deleteAll() {
		projectRepository.deleteAll();
	}
	
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		Project project = projectRepository.findOne(id);
		
		if(project == null) {
			throw new EntityNotFoundException();
		}

		projectRepository.delete(project.getPrjid());
	}
	
	@PersistenceContext
	EntityManager em;
	
//	@Override
//	@Transactional
//	public void deepCopy(int id, String name) throws EntityNotFoundException {
//		Project project = projectRepository.findOne(id);
//		
//		if(project == null) {
//			throw new EntityNotFoundException();
//		}
//		
//		
//		//initialize all collections which are accessed when copying
//		try {
//			Hibernate.initialize(project);
//			for(Component c : project.getComponents()) {
//				for(InputParameter ip : c.getInputparameters()) {
//					Hibernate.initialize(ip.getInputparamvals());
//					Hibernate.initialize(ip.getModelparameters());
//				}
//				for(OutputVariable outVar : c.getOutputvariables()) {
//					for(SimulationResult sr : outVar.getSimulationresults()) {
//						Hibernate.initialize(sr.getTimeseries());
//						TimeSeries ts = sr.getTimeseries();
//						Hibernate.initialize(ts.getTimeseriesvals());
//					}
//				}		
//			}
//			for(ExtParam ep : project.getExtparams()) {
//				for(ExtParamVal epv : ep.getExtparamvals()){
//					Hibernate.initialize(epv.getExtparamvalsetcomps());
//					for(ExtParamValSetComp epvsc : epv.getExtparamvalsetcomps())
//						Hibernate.initialize(epvsc.getExtparamvalset());
//					
//					TimeSeries ts = epv.getTimeseries();
//					Hibernate.initialize(ts.getTimeseriesvals());
//				}
//				TimeSeries ts = ep.getTimeseries();
//				Hibernate.initialize(ts.getTimeseriesvals());
//			}
//			///check single fields
//			for(Metric m : project.getMetrics()){
//				for(MetricVal mv : m.getMetricvals()){
//					Hibernate.initialize(mv.getScenariometrics());		
//					Hibernate.initialize(mv.getScenariometrics().getExtparamvalset());					
//					TimeSeries ts = mv.getTimeseries();
//					Hibernate.initialize(ts.getTimeseriesvals());
//				}
//			}
//			for(ObjectiveFunction of : project.getObjectivefunctions()){
//				Hibernate.initialize(of.getOptimizationsets());
//				Hibernate.initialize(of.getScengenobjectivefunctions());
//			}
//			for(OptConstraint oc : project.getOptconstraints()){
//				for(OptSearchConst osc : oc.getOptsearchconsts()){
//					Hibernate.initialize(osc.getOptimizationset());
//				}
//				Hibernate.initialize(oc.getScengenoptconstraints());
//			}
//			for(ScenarioGenerator sg : project.getScenariogenerators()){
//				Hibernate.initialize(sg.getAlgoparamvals());
//				Hibernate.initialize(sg.getDecisionvariables());
//				Hibernate.initialize(sg.getExtparamvalset());
//				Hibernate.initialize(sg.getModelparameters());
//				Hibernate.initialize(sg.getScengenobjectivefunctions());
//			}
//			for(Scenario s : project.getScenarios()){
//				setIdZero(s);
//				Hibernate.initialize(s.getSimulationresults());
//				Hibernate.initialize(s.getOptimizationsets());
//				Hibernate.initialize(s.getOptsetscenarioses());
//				Hibernate.initialize(s.getInputparamvals());
//				Hibernate.initialize(s.getScenariometricses());
//			}
//			
////			Hibernate.initialize(project.getSimulationmodel());
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		Hibernate.initialize(project.getComponents());
////		Hibernate.initialize(project.getScenarios());
//		Project clonedObject = new Project();
////		Project clonedObject = (Project) SerializationHelper.clone((Serializable) project); 
////		
//		clonedObject = modelMapper.map(project, Project.class);
//		
//		em.detach(project);
////		net.sf.beanlib.provider.replicator.BeanReplicator br = new BeanReplicator();
////		clonedObject = br.replicateBean(project);
//		
//		try {
//			//remove all IDs of clone
//			//TODO what about usergroupproject?
//			setIdZero(clonedObject);
//			for(Component c : clonedObject.getComponents()) {
//				setIdZero(c);
//				for(InputParameter ip : c.getInputparameters()) {
//					setIdZero(ip);
//					for(InputParamVal ipv : ip.getInputparamvals())
//						setIdZero(ipv);
//					for(ModelParameter mp : ip.getModelparameters()){
//						setIdZero(mp);
////						setIdZero(mp.getScenariogenerator());
//					}
//				}
//				for(OutputVariable outVar : c.getOutputvariables()) {
//					setIdZero(outVar);
//					//not copy SimulationResults
//					outVar.setSimulationresults(new HashSet<SimulationResult>());
//					//copy results?
////					for(SimulationResult sr : outVar.getSimulationresults()) {
////						setIdZero(sr);
////						setIdZero(sr.getScenario());
////						TimeSeries ts = sr.getTimeseries();
////						setIdZero(ts);
////						for(TimeSeriesVal tsv : ts.getTimeseriesvals())
////							setIdZero(tsv);
////					}
//				}		
//			}
//			for(ExtParam ep : clonedObject.getExtparams()) {
//				setIdZero(ep);
//				for(ExtParamVal epv : ep.getExtparamvals()){
//					setIdZero(epv);
//					for(ExtParamValSetComp epvsc : epv.getExtparamvalsetcomps()){
//						setIdZero(epvsc);
//						setIdZero(epvsc.getExtparamvalset());
//					}
//					TimeSeries ts = epv.getTimeseries();
//					setIdZero(ts);
//					for(TimeSeriesVal tsv : ts.getTimeseriesvals())
//						setIdZero(tsv);
//				}
//				//set default timeseries 0
//				TimeSeries ts = ep.getTimeseries();
//				setIdZero(ts);
//				for(TimeSeriesVal tsv : ts.getTimeseriesvals())
//					setIdZero(tsv);
//			}
//			///check single fields
//			for(Metric m : clonedObject.getMetrics()){
//				setIdZero(m);
//				for(MetricVal mv : m.getMetricvals()){
//					setIdZero(mv.getScenariometrics());		
//					resetExtParamValSet(mv.getScenariometrics().getExtparamvalset());					
//					TimeSeries ts = mv.getTimeseries();
//					setIdZero(ts);
//					for(TimeSeriesVal tsv : ts.getTimeseriesvals())
//						setIdZero(tsv);
//				}
//			}
//			for(ObjectiveFunction of : clonedObject.getObjectivefunctions()){
//				setIdZero(of);
//				//delete optimizationset
//				of.setOptimizationsets(new HashSet<OptimizationSet>());
//				for(ScenGenObjectiveFunction sgof : of.getScengenobjectivefunctions())
//					setIdZero(sgof);
//			}
//			for(OptConstraint oc : clonedObject.getOptconstraints()){
//				setIdZero(oc);
//				for(OptSearchConst osc : oc.getOptsearchconsts()){
//					setIdZero(osc);
//					setIdZero(osc.getOptimizationset());
//				}
//				for(ScenGenOptConstraint sgoc : oc.getScengenoptconstraints())
//					setIdZero(sgoc);
//			}
//			for(ScenarioGenerator sg : clonedObject.getScenariogenerators()){
//				setIdZero(sg);
//				for(AlgoParamVal apv : sg.getAlgoparamvals())
//					setIdZero(apv);
//				for(DecisionVariable dv : sg.getDecisionvariables())
//					setIdZero(dv);
//				resetExtParamValSet(sg.getExtparamvalset());
//				for(ModelParameter mp : sg.getModelparameters())
//					setIdZero(mp);
//				//scenarios have their own connection to project
//				//sg.getScenarios();
//				for(ScenGenObjectiveFunction sgof : sg.getScengenobjectivefunctions())
//					setIdZero(sgof);
//				//should be removed with optconstraint
////				sg.getScengenoptconstraints();
//			}
//			for(Scenario s : clonedObject.getScenarios()){
//				setIdZero(s);
//				s.setSimulationresults(new HashSet<SimulationResult>());
//				s.setOptimizationsets(new HashSet<OptimizationSet>());
//				s.setOptsetscenarioses(new HashSet<OptSetScenarios>());
//				
//				//should already be removed
//				//s.getInputparamvals();
//				
//				for(ScenarioMetrics sm : s.getScenariometricses())
//					setIdZero(sm);
//			}
//			
////			SimulationModel sm = clonedObject.getSimulationmodel();		
////			setIdZero(sm);
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		clonedObject.setName(name);
//		projectRepository.save(clonedObject);
//	}
	
//	private void resetExtParamValSet(ExtParamValSet epvs) throws IllegalAccessException, 
//	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
//		setIdZero(epvs);
//		for(ExtParamValSetComp epvsc : epvs.getExtparamvalsetcomps()){
//			setIdZero(epvsc);
//			setIdZero(epvsc.getExtparamval().getExtparam());
//			setIdZero(epvsc.getExtparamval());
//		}
//	}
	
//	private void setIdZero(Object entity) throws IllegalAccessException, IllegalArgumentException,
//	InvocationTargetException, NoSuchMethodException, SecurityException{
//		
//		//Object id =em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
//		String asdf =entity.getClass().getPackage() + entity.getClass().getName();
//		Reflections r = new Reflections(entity.getClass().getName(), new MethodAnnotationsScanner());
//		Set<Method> deprecateds = r.getMethodsAnnotatedWith(javax.persistence.Id.class);
//		Set<Field> fields = r.getFieldsAnnotatedWith(javax.persistence.Id.class);
//
//		
////		if(deprecateds.size() < 1)
////			throw new IndexOutOfBoundsException("no identity setter found on object " + asdf);
//		String en = entity.getClass().getName();
//		String metname2 = null;
//		String metName = null;
//		for(Method m : deprecateds){
//			if(m.getDeclaringClass().getName().equals(en))
//				metName = m.getName();
//			metname2 = m.getDeclaringClass().getName();
//		}
//		
//		if(metName == null || metname2== null)
//			throw new IndexOutOfBoundsException("no identity setter found on object " + asdf);
//		
//		metName = metName.replace("get", "set");
//		Method method = entity.getClass().getMethod(metName, int.class);
//		method.invoke(entity, 0);
//		
//	}
	
	public void initializeCollections(Project project){
		Hibernate.initialize(project.getScenarios());//test
		for(Component c : project.getComponents()) {
			log.info("initializing components"); 
			for(InputParameter ip : c.getInputparameters()) {
				log.info("initializing inputparams");
				Hibernate.initialize(ip.getInputparamvals());
				Hibernate.initialize(ip.getModelparameters());
				log.info("initializing inputparams done");
			}
			for(OutputVariable outVar : c.getOutputvariables()) {
				log.info("initializing outputvariables");
				for(SimulationResult sr : outVar.getSimulationresults()) {
					Hibernate.initialize(sr.getTimeseries());
					TimeSeries ts = sr.getTimeseries();
					if(ts != null)
						Hibernate.initialize(ts.getTimeseriesvals());
				}
				log.info("initializing outputvariables done");
			}		
		}
		for(ExtParam ep : project.getExtparams()) {
			log.info("initializing extparams");
			for(ExtParamVal epv : ep.getExtparamvals()){
				Hibernate.initialize(epv.getExtparamvalsetcomps());
				for(ExtParamValSetComp epvsc : epv.getExtparamvalsetcomps())
					Hibernate.initialize(epvsc.getExtparamvalset());
				
				TimeSeries ts = epv.getTimeseries();
				if(ts != null)
					Hibernate.initialize(ts.getTimeseriesvals());
			}
//			TimeSeries ts = ep.getTimeseries();
//			if(ts != null)
//				Hibernate.initialize(ts.getTimeseriesvals());
//			log.info("initializing extparams done");
		}
		///check single fields
		for(Metric m : project.getMetrics()){
			log.info("initializing metrics");
			for(MetricVal mv : m.getMetricvals()){
				Hibernate.initialize(mv.getScenariometrics());		
				Hibernate.initialize(mv.getScenariometrics().getExtparamvalset());					
				TimeSeries ts = mv.getTimeseries();
				if(ts != null)
					Hibernate.initialize(ts.getTimeseriesvals());
			}
			log.info("initializing metrics done");
		}
		for(ObjectiveFunction of : project.getObjectivefunctions()){
			log.info("initializing objective functions");
			Hibernate.initialize(of.getOptimizationsets());
			Hibernate.initialize(of.getScengenobjectivefunctions());
			log.info("initializing objective functions done");
		}
		for(OptConstraint oc : project.getOptconstraints()){
			log.info("initializing optconstraints");
			for(OptSearchConst osc : oc.getOptsearchconsts()){
				Hibernate.initialize(osc.getOptimizationset());
			}
			Hibernate.initialize(oc.getScengenoptconstraints());
			log.info("initializing optconstraints done");
		}
		for(ScenarioGenerator sg : project.getScenariogenerators()){
			log.info("initializing scenariogenerators");
			Hibernate.initialize(sg.getAlgoparamvals());
			Hibernate.initialize(sg.getDecisionvariables());
			Hibernate.initialize(sg.getExtparamvalset());
			Hibernate.initialize(sg.getModelparameters());
			Hibernate.initialize(sg.getScengenobjectivefunctions());
			log.info("initializing scenariogenerators");
		}
		for(Scenario s : project.getScenarios()){
			log.info("initializing scenarios");
			Hibernate.initialize(s.getSimulationresults());
			Hibernate.initialize(s.getOptimizationsets());
			Hibernate.initialize(s.getOptsetscenarioses());
			Hibernate.initialize(s.getInputparamvals());
			Hibernate.initialize(s.getScenariometricses());
			log.info("initializing scenarios done");
		}
	}
	
//	@Override
//	@Transactional
//	public boolean copyByReflection (int id, String name) throws EntityNotFoundException{
//		
//		Project project = projectRepository.findOne(id);
////		initializeCollections(project);
//		
//		if(project == null) {
//			throw new EntityNotFoundException();
//		}
//		
//		Hibernate.initialize(project.getScenarios());
//		
//		Project target = new Project();
////		project.setSimulationmodel(null);
//		try {
//			parents = new HashMap<String, Object>();
//			target = (Project) doCopy(project, project);
//		} catch (InstantiationException | IllegalAccessException 
//				| IllegalArgumentException | InvocationTargetException 
//				| NoSuchMethodException | SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		
//		target.setName(name);
//		
//		try {
////			em.persist(target);
//			projectRepository.save(target);
//		} catch (Exception e) {
//			log.error("Copy Project succeded, but insert into database failed: ", e);
//			e.printStackTrace();
//			return false;
//		}
//		
//		return true;
//	}

//	private Map<String,Object> parents;
//	
//	@Transactional
//	private Object doCopy(Object src, Object caller) throws IllegalAccessException, IllegalArgumentException,
//	InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException{
//		
//		if (src instanceof HibernateProxy) {  
//			try {
//				src = ((HibernateProxy)src).getHibernateLazyInitializer().getImplementation();
//				caller = src.getClass().getName();
//			}catch(NullPointerException e){
//				log.error("could not copy object: " + src.getClass(), e);
//				e.printStackTrace();
//				return null;				
//			}
//		}
//				
//		Object target = src.getClass().newInstance();
////		String gak = target.getClass().getName();
////		String [] nameS = gak.split("\\.");
////		String name = nameS[nameS.length-1];
////		parents.put(name.toLowerCase(), target);
//
//		Reflections r = new Reflections(src.getClass().getName(), new MethodAnnotationsScanner());
//		//get Identifier setter:
//		Set<Method> identifier = r.getMethodsAnnotatedWith(javax.persistence.Id.class);
//		//get column setters and n:1 relations
//		Set<Method> columns = r.getMethodsAnnotatedWith(javax.persistence.Column.class);
////		columns.addAll(r.getMethodsAnnotatedWith(javax.persistence.ManyToOne.class));
//		Set<Method> manytoones = r.getMethodsAnnotatedWith(javax.persistence.ManyToOne.class);
//		//get 1:n relation (collection) setters
//		Set<Method> collections = r.getMethodsAnnotatedWith(javax.persistence.OneToMany.class);
//
//		String getName = null;
//		String setName = null;
//		for(Method m : columns){
//			//method is in the right class
//			if(m.getDeclaringClass().getName().equals(src.getClass().getName())){
//				
//				getName = m.getName();
//				setName = getName.replaceFirst("get", "set");
//				String parentName = getName.substring(3).toLowerCase();
//				
//				System.out.println("invoking: " + getName + " on " + m.getDeclaringClass());
//				Object getterResult = m.invoke(src);
//				System.out.println("invoke finished. return type: " + m.getReturnType());
//				Class<?> types = m.getReturnType();
//				if(identifier.contains(m)) //set id null
//					getterResult = 0;
//
//				//types.getClass().getPackage().equals(src.getClass().getPackage()) || 
//				if(getterResult instanceof HibernateProxy){
//					getterResult = doCopy(getterResult, src);
//				}
////				if(parents.containsKey(parentName))
////					getterResult = parents.get(parentName);
//				Method method = target.getClass().getMethod(setName, types);
//
//				if(caller.getClass().getName().equals(m.getReturnType().getName()))
//					getterResult = caller;
//				method.invoke(target, getterResult);
//			}
//		}
//		
//		for(Method m : manytoones){
//			//method is in the right class
//			if(m.getDeclaringClass().getName().equals(src.getClass().getName())){
//				
//				getName = m.getName();
//				setName = getName.replaceFirst("get", "set");
//				String parentName = getName.substring(3).toLowerCase();
//				Object getterResult = null;
//
//				Class<?> types = m.getReturnType();
//
////				if(types.getClass().getPackage().equals(src.getClass().getPackage()) || 
////						getterResult instanceof HibernateProxy){
////					getterResult = doCopy(getterResult);
////				}
////				if(parents.containsKey(parentName))
////					getterResult = parents.get(parentName);
//				if(caller.getClass().getName().equals(m.getReturnType().getName()))
//					getterResult = caller;
//				else if(m.getReturnType().getName().toLowerCase().equals("eu.cityopt.model.unit")
//						|| m.getReturnType().getName().toLowerCase().equals("eu.cityopt.model.type")
//						|| m.getReturnType().getName().toLowerCase().equals("eu.cityopt.model.datareliability")){
//					getterResult = m.invoke(src);
//				}
////				else { //if(!caller.equals(m.getReturnType().getName()))
////					System.out.println(m.getReturnType().getName());
////					getterResult = m.invoke(src);
////					if(types.getClass().getPackage().equals(src.getClass().getPackage()) 
////							|| getterResult instanceof HibernateProxy){
////						getterResult = doCopy(getterResult,src);
////					}
////				}
//				Method method = target.getClass().getMethod(setName, types);
//				
//				System.out.println("src name: " + src.getClass().getName());
//				System.out.println("return name: " + m.getReturnType().getName());
//				
//				method.invoke(target, getterResult);
//			}
//		}
//		
//		for(Method m : collections){
//			//method is in the right class
//			if(m.getDeclaringClass().getName().equals(src.getClass().getName())){
//				getName = m.getName();
//				setName = getName.replaceFirst("get", "set");
//				System.out.println("invoking: " + getName + " on " + m.getDeclaringClass());
//				
//				Object getterResult = m.invoke(src);
//				System.out.println("getter result is " + getterResult);
//				System.out.println("invoke finished. return type: " + m.getReturnType());
//				Collection<?> coll = (Collection<?>) getterResult;
//				Collection<Object> coll2 = new HashSet<Object>(); 
//				//does not work because its a hibernate specific collection implementation
//				//(Collection<Object>) m.getReturnType().newInstance();
//				
//				//if type is list: instantiate list
//				if(m.getReturnType() == java.util.List.class)
//					coll2 = new ArrayList<Object>();
//				
//				System.out.println("type is: " + m.getReturnType());
//				
//				for(Object o : coll){
//					Object result = doCopy(o, src);
//					coll2.add(result);
//				}
//				
//				Class<?> types = m.getReturnType();
//				Method method = target.getClass().getMethod(setName, types);
//				if(caller.equals(m.getReturnType().getName()))
//					continue;
//				method.invoke(target, coll2);
//			}
//		}
//		
//		return target;
//}
	
	@Transactional
	public void deleteWR(int id) throws EntityNotFoundException {
		
		if(projectRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		projectRepository.delete(id);
	}
	
	@Transactional
	public ProjectDTO update(ProjectDTO toUpdate) throws EntityNotFoundException {
		
		if(projectRepository.findOne(toUpdate.getPrjid()) == null) {
			throw new EntityNotFoundException();
		}
		return save(toUpdate);
	}
	
	@Transactional(readOnly = true)
	public ProjectDTO findByID(int id) {
		Project item = projectRepository.findOne(id);
		ProjectDTO itemDTO = modelMapper.map(item, ProjectDTO.class);
		return itemDTO;
	}
	
	@Transactional(readOnly = true)
	public Set<ScenarioDTO> getScenarios(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scenarios = item.getScenarios(); 
		return modelMapper.map(scenarios, new TypeToken<Set<ScenarioDTO>>() {}.getType());
	}
	
	@Transactional
	public void setScenarios(int prjid, Set<ScenarioDTO> scenarios) {
		Project item = projectRepository.findOne(prjid);
		Set<Scenario> scen = modelMapper.map(scenarios, new TypeToken<Set<Scenario>>() {}.getType());
		
		item.setScenarios(scen);
		projectRepository.saveAndFlush(item);
	}
	
	@Transactional(readOnly = true)
	public List<ComponentDTO> getComponents(int prjid) {
		Project item = projectRepository.findOne(prjid);
		List<Component> components = item.getComponents(); 
		return modelMapper.map(components, new TypeToken<List<ComponentDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<ExtParamDTO> getExtParams(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<ExtParam> extParams = item.getExtparams(); 
		return modelMapper.map(extParams, new TypeToken<Set<ExtParamDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<ExtParamValDTO> getExtParamVals(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<ExtParam> extParams = item.getExtparams(); 
		Set<ExtParamVal> extParamVals = new HashSet<ExtParamVal>();
		for(Iterator<ExtParam> i = extParams.iterator(); i.hasNext();){
			ExtParam ep = i.next();
			extParamVals.addAll(ep.getExtparamvals());
		}
		return modelMapper.map(extParamVals, new TypeToken<Set<ExtParamValDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	@Override
	public Set<OptimizationSetDTO> getSearchOptimizationSets(int prjid) throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(p.getOptimizationsets(), new TypeToken<Set<OptimizationSetDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	@Override
	public Set<OpenOptimizationSetDTO> getSearchAndGAOptimizationSets(int prjid) 
			throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null) {
			throw new EntityNotFoundException();
		}
		
		Set<OptimizationSet> osSet = p.getOptimizationsets();
		Set<ScenarioGenerator> sgSet = p.getScenariogenerators();
		Set<OpenOptimizationSetDTO> osSetDTO = modelMapper.map(osSet, 
				new TypeToken<Set<OpenOptimizationSetDTO>>() {}.getType());
		osSetDTO.addAll(modelMapper.map(sgSet, 
				new TypeToken<Set<OpenOptimizationSetDTO>>() {}.getType()));

		return osSetDTO;
	}
	
	@Transactional(readOnly = true)
	@Override
	public Set<ObjectiveFunctionDTO> getObjectiveFunctions(int prjid) throws EntityNotFoundException {
		Project p = projectRepository.findOne(prjid);
		
		if(p == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(p.getObjectivefunctions(), new TypeToken<Set<ObjectiveFunctionDTO>>() {}.getType());
	}
	
	@Transactional(readOnly = true)
	public Set<MetricDTO> getMetrics(int prjid) {
		Project item = projectRepository.findOne(prjid);
		Set<Metric> metrics = item.getMetrics(); 
		return modelMapper.map(metrics, new TypeToken<Set<MetricDTO>>() {}.getType());
	}
}


