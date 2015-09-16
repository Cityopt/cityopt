package eu.cityopt.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamCSVModel;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.SimResCSVModel;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesCSVModel;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.model.Type;
import eu.cityopt.model.Unit;
import eu.cityopt.opt.io.CsvTimeSeriesData;
import eu.cityopt.opt.io.JacksonBinder;
import eu.cityopt.opt.io.JacksonBinderScenario;
import eu.cityopt.opt.io.JacksonBinderScenario.ScenarioItem;
import eu.cityopt.opt.io.JacksonCsvModule;
import eu.cityopt.opt.io.TimeSeriesData;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ExtParamRepository;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValSetCompRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.InputParamValRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.OutputVariableRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioMetricsRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ImportService;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.util.TimeUtils;
import eu.cityopt.sim.service.ImportExportService;

@Service
public class ImportServiceImpl implements ImportService {
	
	private static Logger log = Logger.getLogger(ImportServiceImpl.class); 
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ExtParamValSetRepository extParamValSetRepository;
	
	@Autowired
	private UnitRepository unitRepository;
	
	@Autowired
	private ExtParamRepository extParamRepository;
	
	@Autowired
	private ExtParamValRepository extParamValRepository;
	
	@Autowired 
	private ExtParamValSetCompRepository extParamValSetCompRepository;
	
	@Autowired
	private TypeRepository typeRepository;
	
	@Autowired
	private TimeSeriesRepository timeSeriesRepository;
	
	@Autowired
	private TimeSeriesValRepository timeSeriesValRepository;
	
	@Autowired
	private ScenarioRepository scenarioRepository;
	
	@Autowired
	private ScenarioMetricsRepository scenarioMetricsRepository;
	
	@Autowired
	private MetricRepository metricRepository;
	
	@Autowired
	private MetricValRepository metricValRepository;
	
	@Autowired
	private SimulationResultRepository simulationResultRepository;
	
	@Autowired
	private ComponentRepository componentRepository;
	
	@Autowired
	private OutputVariableRepository outputVariableRepository;
	
	@Autowired
	private InputParameterRepository inputParameterRepository;
	
	@Autowired
	private InputParamValRepository inputParamValRepository;
	
	@Autowired
	private ImportExportService importExportService;
	
	@Override
	@Transactional
	public Map<Integer,TimeSeries> importTimeSeries(File timeSeriesInput) throws EntityNotFoundException, ParseException{
		
		CsvMapper mapper = new CsvMapper();
//		mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		CsvSchema schema = CsvSchema.builder()
				.addColumn("TimeseriesId", CsvSchema.ColumnType.NUMBER)
				.addColumn("TypeName")
				.addColumn("TimeSeriesValue")
				.addColumn("TimeStamp").build();
		schema = schema.withColumnSeparator(';').withHeader();
		MappingIterator<TimeSeriesCSVModel> it = null;
		try {
			it = mapper.reader(TimeSeriesCSVModel.class).with(schema).readValues(timeSeriesInput);
		} catch (IOException e) {
			log.error("could not read input file", e);
			// TODO throw custom exception
		}
		
		//skip headers
//		if(it.hasNext())
//			it.next();
		
		Map<Integer,TimeSeries> timeSeriesMap = new HashMap<Integer, TimeSeries>();
		DateFormat format = new SimpleDateFormat("dd.MM.yyyyHH:mm:ss");
		
		//read csv line by line
		while (it.hasNext()){
			TimeSeriesCSVModel row = it.next();
			
			TimeSeries ts = new TimeSeries();
			
			if(timeSeriesMap.containsKey(row.TimeseriesId)){
				ts = timeSeriesMap.get(row.TimeseriesId);
			}else{
				//construct new
				Type type = typeRepository.findByNameLike(row.TypeName);
				if(type == null){
					type = new Type();
					type.setName(row.TypeName);
					type = typeRepository.save(type);
					//throw new EntityNotFoundException("Type with name \""+ row.TypeName + "\" was not found.");
				}
				ts.setType(type);
				ts = timeSeriesRepository.save(ts);
				
				timeSeriesMap.put(row.TimeseriesId, ts);
			}			
			
			Date d;
			try {
				d = format.parse(row.TimeStamp);
			} catch (ParseException e) {
				log.error("could not read input file", e);
				e.printStackTrace();
				// TODO throw custom exception
				throw e;			
			}
			TimeSeriesVal val = new TimeSeriesVal();
			val.setTimeseries(ts);
			val.setTime(d);
			val.setValue(row.TimeSeriesValue);
			ts.getTimeseriesvals().add(val);
			val = timeSeriesValRepository.save(val);
			
			timeSeriesMap.put(row.TimeseriesId, ts);		
		}
		
		return timeSeriesMap;
	}
	
	@Override
	@Transactional
	public void importExtParamValSet(Integer prjid, File epValSetInput, File timeSeriesInput) throws EntityNotFoundException{
//		File input = new File("testfile.csv"); 
		Map<String,ExtParamValSet> epvsMap = new HashMap<String, ExtParamValSet>();
		Map<String,ExtParam> epMap = new HashMap<String, ExtParam>();
		Map<Integer,TimeSeries> tsMap = new HashMap<Integer,TimeSeries>();
		try {
			tsMap = importTimeSeries(timeSeriesInput);
		} catch (ParseException e1) {
			log.error("error importing timeSeries", e1);
			e1.printStackTrace();
			//TODO throw something
		}
		Project project = projectRepository.findOne(prjid);
		if(project == null)
			throw new EntityNotFoundException();
		
		List<ExtParamCSVModel> epvCsv = new ArrayList<ExtParamCSVModel>();
		CsvMapper mapper = new CsvMapper();
//		mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		CsvSchema schema = CsvSchema.builder()
				.addColumn("ExtParamValSetName")
				.addColumn("ExtParamUnitName")
				.addColumn("ExtParamName")
				.addColumn("ExtParamValValue")
				.addColumn("ExtParamValTimeSeriesId", CsvSchema.ColumnType.NUMBER)
				.addColumn("ExtParamValComment").build();
		schema = schema.withColumnSeparator(';').withHeader();
		MappingIterator<ExtParamCSVModel> it = null;
		try {
			it = mapper.reader(ExtParamCSVModel.class).with(schema).readValues(epValSetInput);
		} catch (IOException e) {
			log.error("could not read input file", e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//skip headers
//		if(it.hasNext())
//			it.next();
		
		//read csv line by line
		while (it.hasNext()){
			ExtParamCSVModel row = it.next();
			epvCsv.add(row);
		
			ExtParamValSet set = new ExtParamValSet();
			ExtParam ep = new ExtParam();
			ExtParamVal epv = new ExtParamVal();
			ExtParamValSetComp epvsc = new ExtParamValSetComp();
			
			if(row.ExtParamValSetName == null || row.ExtParamValSetName.equals(""))
				set = null;
			else if(epvsMap.containsKey(row.ExtParamValSetName))
				set = epvsMap.get(row.ExtParamValSetName);
			else{
				set.setName(row.ExtParamValSetName);
				set = extParamValSetRepository.save(set);
				epvsMap.put(row.ExtParamValSetName, set);
			}
			
			if(epMap.containsKey(row.ExtParamName))
				ep = epMap.get(row.ExtParamName);
			else{
				ep.setName(row.ExtParamName);
				ep.setProject(project);
				Unit unit = unitRepository.findByName(row.ExtParamUnitName);
				ep.setUnit(unit);
//				ep.setDefaultvalue(row.ExtParamdefaultValue);
//				if(row.ExtParamdefaultTimeseriesId != null && row.ExtParamdefaultTimeseriesId != 0){
//					if(!tsMap.containsKey(row.ExtParamdefaultTimeseriesId)){
//						//time series used as default was not imported
//					}else{
//						TimeSeries ts = tsMap.get(row.ExtParamdefaultTimeseriesId);
//						ep.setTimeseries(ts);
//						ts.getExtparams().add(ep);
//					}
//				}
				
				ep = extParamRepository.save(ep);
				epMap.put(row.ExtParamName, ep);
			}
			
			epv.setExtparam(ep);
			epv.setComment(row.ExtParamValComment);
			epv.setValue(row.ExtParamValValue);
			if(row.ExtParamValTimeSeriesId != null && row.ExtParamValTimeSeriesId != 0){
				if(!tsMap.containsKey(row.ExtParamValTimeSeriesId)){
					//time series used as default was not imported
					log.error("not found: " + row.ExtParamValTimeSeriesId);
					throw new EntityNotFoundException("time series with id "+ row.ExtParamValTimeSeriesId + " was not found.");
				}else{
					TimeSeries ts = tsMap.get(row.ExtParamValTimeSeriesId);
					epv.setTimeseries(ts);
					ts.getExtparamvals().add(epv);
				}
			}
			epv = extParamValRepository.save(epv);
			
			if(set != null){
				epvsc.setExtparamval(epv);
				epvsc.setExtparamvalset(set);
				extParamValSetCompRepository.save(epvsc);
			}
		}
	}

	@Override
	@Transactional
	public TimeSeries saveTimeSeriesData(TimeSeriesData.Series data, Type type, Instant timeOrigin){
		TimeSeries timeSeries = new TimeSeries();
        timeSeries.setType(type);
        double[] times = data.getTimes();
        double[] values = data.getValues();
        
        int n = times.length;
        for (int i = 0; i < n; i++) {
            TimeSeriesVal timeSeriesVal = new TimeSeriesVal();
            
            timeSeriesVal.setTime(TimeUtils.toDate(times[i], timeOrigin));
            timeSeriesVal.setValue(Double.toString(values[i]));
            timeSeriesVal.setTimeseries(timeSeries);
            
            timeSeries.getTimeseriesvals().add(timeSeriesVal);
        }
        
        timeSeriesValRepository.save(timeSeries.getTimeseriesvals());
        return timeSeriesRepository.save(timeSeries);
	}
	
	@Override
	@Transactional
	public void importSimulationResults(int scenid, File simResInput, File timeSeriesInput, int typeid)
			throws EntityNotFoundException, ParseException{
		
        //get scenario 
		Scenario scen = scenarioRepository.findOne(1);
		if(scen == null)
			throw new EntityNotFoundException("Scenario not found");
		//get type
		Type type = typeRepository.findOne(typeid);
		if(type == null)
			throw new EntityNotFoundException("Type not found");
		
		//get timeseries data
        CsvTimeSeriesData tsd = importExportService.makeTimeSeriesReader(scen.getProject());
        
        String tsName = timeSeriesInput.getName();
 
        try {
            tsd.read(new FileInputStream(timeSeriesInput), tsName);
        } catch(Exception ex) {
        	log.error("Could not import TimeSeries", ex);
        	//TODO: throw exception
        }
		
        //read simulationResult csv
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.builder()
				.addColumn("ComponentOutVarName")
				.addColumn("TimeseriesName").build();
		schema = schema.withColumnSeparator(',').withHeader();
		MappingIterator<SimResCSVModel> it = null;
		try {
			it = mapper.reader(SimResCSVModel.class).with(schema).readValues(simResInput);
		} catch (IOException e) {
			log.error("could not read input file", e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//read csv line by line
		while (it.hasNext()){
			SimResCSVModel row = it.next();
		
			SimulationResult simRes = new SimulationResult();
			
			//separate comp and outvar name
			String [] splitRes = row.ComponentOutVarName.split("\\.");
			if(splitRes.length != 2){
				log.error("wrong format for CSV SimulationResult: " + row.ComponentOutVarName);
				//TODO: throw?
				continue;
			}
			
			String componentName = splitRes[0];
			String outVarName = splitRes[1];
			Component comp = componentRepository.findByNameAndProject(scen.getProject().getPrjid(),componentName);
			if(comp == null){
				throw new EntityNotFoundException("component with name '"+ componentName
						+ "' not found. ");
			}
			
			OutputVariable outVar = outputVariableRepository.findByComponentAndName(comp.getComponentid(), outVarName);
			if(outVar == null){
				throw new EntityNotFoundException("outputVariable with name '"+ outVarName
						+ "' not found. ");
			}
			
			TimeSeries ts = new TimeSeries();
			Date timeOrigin = scen.getProject().getSimulationmodel().getTimeorigin();
			
			try{
				ts = saveTimeSeriesData(tsd.getSeries(row.TimeseriesName), type, timeOrigin.toInstant());
			}catch(Exception ex){
				log.error("error saving timeSeries: " + row.TimeseriesName, ex);
				//TODO: throw?
				continue;
			}
			
			simRes.setTimeseries(ts);
			simRes.setScenario(scen);
			simRes.setOutputvariable(outVar);
			
			simRes = simulationResultRepository.save(simRes);
			outVar.getSimulationresults().add(simRes);
			ts.getSimulationresults().add(simRes);
			scen.getSimulationresults().add(simRes);
		}
	}
	
	public JacksonBinderScenario getBinder(File file) throws IOException {
		ObjectReader reader = JacksonCsvModule.getScenarioProblemReader(JacksonCsvModule.getCsvMapper());
		FileInputStream fis = new FileInputStream(file);
		JacksonBinderScenario binder = new JacksonBinderScenario(reader, fis);
		binder.getItems().forEach( i -> System.out.println(i.scenarioname));
		return binder;
	}
	
	/**
	 * reads scenario parameters (currently inputparamval, simulationresults and metricval) from the given files and saves them in the database. 
	 * the project structure needs to exist - if a component, inputparameter, outputvariable, metric 
	 * or externalparamvalset is not found, an exception will be thrown
	 * 
	 * @param prjid defines the project for the data import
	 * @param scenarioInput defines (scenario-specific) import file
	 * @param timeSeriesInput multiple time series files can be used, they are linked according their name
	 * @throws EntityNotFoundException
	 */
	@Transactional
	public void importScenarioData(int prjid, File scenarioInput, 
			List<File> timeSeriesInput) throws EntityNotFoundException{
		
		Project project = projectRepository.findOne(prjid);
		if(project == null)
			throw new EntityNotFoundException();
		
		//create project structure from import file
//		try {
//			importExportService.importSimulationStructure(prjid, scenarioInput.toPath());
//		} catch (IOException | ParseException | ScriptException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		Date timeOrigin = null;
		
		//import TS data
		CsvTimeSeriesData tsd = importExportService.makeTimeSeriesReader(project);
		if(timeSeriesInput != null){
	        for(File tsinFile : timeSeriesInput){
	        	String tsName = tsinFile.getName();
	        	 
	            try {
	                tsd.read(new FileInputStream(tsinFile), tsName);
	            } catch(Exception ex) {
	            	log.error("Could not import TimeSeries", ex);
	            	//TODO: throw exception
	            }
	        }
	        //get origin if TS are used
	        timeOrigin = project.getSimulationmodel().getTimeorigin();
		}        
		
		JacksonBinderScenario binder = null;
		try {
			binder = getBinder(scenarioInput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(ScenarioItem it : binder.getItems()){
			
			if(it.scenarioname == null || it.scenarioname.equals("")) //cannot relate to scenario
				continue;
			
		    Scenario scenario = scenarioRepository.findByNamePrjid(it.scenarioname, prjid);
		    if(scenario == null){
		    	//create by name and default values
		    	scenario = new Scenario();
		    	scenario.setName(it.scenarioname);
		    	scenario.setCreatedon(Calendar.getInstance().getTime());
		    	scenario.setUpdatedon(Calendar.getInstance().getTime());
		    	scenario.setProject(project);
		    	scenario = scenarioRepository.save(scenario);
		    }
		    
		    //determine type
		    switch(it.getKind()){
		    	case IN:
		    		JacksonBinder.Input input = (JacksonBinder.Input) it.getItem();
		    		
		    		Component component = componentRepository.findByNameAndProject(prjid, input.comp);
		    		if(Namespace.CONFIG_COMPONENT.equals(input.comp) //configuration component
		    				|| input.value == null || input.value.equals("") // no value
		    				|| input.name == null || input.name.equals("")) //no name
		    			break;
		    		if(component == null){
		    			throw new EntityNotFoundException();
		    		}
		    		
		    		InputParameter iParameter = inputParameterRepository
		    				.findByNameAndCompId(input.name, component.getComponentid());
		    		if(iParameter == null){
		    			throw new EntityNotFoundException();
		    		}
		    		
		    		InputParamVal ipVal = new InputParamVal();
		    		ipVal.setInputparameter(iParameter);
		    		ipVal.setScenario(scenario);
		    		ipVal.setValue(input.value);
		    		ipVal.setUpdatedon(Calendar.getInstance().getTime());
		    		ipVal.setCreatedon(Calendar.getInstance().getTime());
		    		inputParamValRepository.save(ipVal);
		    		
		    		break;
				case MET:
					
					JacksonBinder.Metric metricJack = (JacksonBinder.Metric) it.getItem();
		    		
					Metric metricDB = metricRepository.findByNameAndProject(prjid, metricJack.name);
					
		    		if(metricDB == null) //metric not existing
		    			throw new EntityNotFoundException("Metric named " + metricJack.name + " does not exist on prjid: " + prjid);
		    			
		    		//get timeseries if type specifies a TS
		    		TimeSeries ts = null;
		    		if(metricJack.type.isTimeSeriesType()){
		    			
		    			Type typModel = typeRepository.findByNameLike(metricJack.type.name);
		    			if(typModel == null)
		    				throw new EntityNotFoundException("Type with name: \"" + metricJack.type.name + "\" not found.");
		    			
		    			try{
		    				ts = saveTimeSeriesData(tsd.getSeries(metricJack.tsKey()), typModel, timeOrigin.toInstant());
		    			}catch(Exception ex){
		    				log.error("error saving timeSeries: " + metricJack.tsKey(), ex);
		    				//TODO: throw?
		    				continue;
		    			}
		    		} else {
//		    			try{
		    			if(metricJack.value == null || metricJack.value.equals("")) //no ts and no value..just metric definition?
		    				break;
		    			Double.parseDouble(metricJack.value);
//		    			}catch(NumberFormatException ex){
//		    				
//		    			}
		    		}
		    		
		    		//find extparamvalset for scenariometrics
		    		ExtParamValSet epvs = extParamValSetRepository.findByNameAndProject(project.getPrjid(), it.extparamvalsetname);
		    		if(epvs == null)
		    			throw new EntityNotFoundException("ExtParamValSet with name \"" + it.extparamvalsetname + "\" not found.");
		    		
		    		//check if scenariometrics exist for this set
		    		epvs.getScenariometricses();
		    		ScenarioMetrics sm = scenarioMetricsRepository.findByScenidAndExtParamValSetid(scenario.getScenid(), epvs.getExtparamvalsetid());
		    		
		    		if(sm == null){
		    			//create scenariometrics
		    			sm = new ScenarioMetrics();
		    			sm.setExtparamvalset(epvs);
		    			sm.setScenario(scenario);
		    			sm = scenarioMetricsRepository.saveAndFlush(sm);
		    		}
		    		
		    		//create metric value
		    		MetricVal mVal = new MetricVal();
		    		mVal.setMetric(metricDB);
		    		if(ts != null)
		    			mVal.setTimeseries(ts);
		    		else
		    			mVal.setValue(metricJack.value);
		    		
		    		mVal.setScenariometrics(sm);
		    		mVal = metricValRepository.save(mVal);	
					
					break;
					
				case OUT:
					JacksonBinder.Output outJack = (JacksonBinder.Output) it.getItem();
					SimulationResult simRes = new SimulationResult();
					
					Component comp = componentRepository.findByNameAndProject(project.getPrjid(),
							outJack.comp);
					if(comp == null){
						throw new EntityNotFoundException("component with name '"+ outJack.comp
								+ "' not found. ");
					}
					
					OutputVariable outVar = outputVariableRepository.findByComponentAndName(comp.getComponentid(), 
							outJack.name);
					if(outVar == null){
						throw new EntityNotFoundException("outputVariable with name '"+ outJack.name
								+ "' not found. ");
					}
					
					TimeSeries simResTs = new TimeSeries();
					
					if(outJack.type.isTimeSeriesType()){
		    			
		    			Type typModel = typeRepository.findByNameLike(outJack.type.name);
		    			if(typModel == null)
		    				throw new EntityNotFoundException("Type with name: \"" + outJack.type.name + "\" not found.");
		    			
		    			try{
		    				simResTs = saveTimeSeriesData(tsd.getSeries(outJack.tsKey()), typModel, timeOrigin.toInstant());
		    			}catch(Exception ex){
		    				log.error("error saving timeSeries: " + outJack.tsKey(), ex);
		    				//TODO: throw?
		    				continue;
		    			}
		    		} else {
		    			//outvar must be a timeseries type
		    			//TODO throw
		    			break;
		    		}
					
					simRes.setTimeseries(simResTs);
					simRes.setScenario(scenario);
					simRes.setOutputvariable(outVar);
					
					simRes = simulationResultRepository.save(simRes);
					outVar.getSimulationresults().add(simRes);
					simResTs.getSimulationresults().add(simRes);
					scenario.getSimulationresults().add(simRes);
					
					break;
				default:
					break;
		    }
		}
	}
}
