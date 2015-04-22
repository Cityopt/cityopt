package eu.cityopt.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamCSVModel;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimResCSVModel;
import eu.cityopt.model.SimulationModel;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesCSVModel;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.model.Type;
import eu.cityopt.model.Unit;
import eu.cityopt.opt.io.CsvTimeSeriesData;
import eu.cityopt.opt.io.TimeSeriesData;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ExtParamRepository;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValSetCompRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.OutputVariableRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Evaluator;
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
	private SimulationResultRepository simulationResultRepository;
	
	@Autowired
	private ComponentRepository componentRepository;
	
	@Autowired
	private OutputVariableRepository outputVariableRepository;
	
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
		Project project = projectRepository.findOne(1);
		if(project == null)
			throw new EntityNotFoundException();
		
		List<ExtParamCSVModel> epvCsv = new ArrayList<ExtParamCSVModel>();
		CsvMapper mapper = new CsvMapper();
//		mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
		CsvSchema schema = CsvSchema.builder()
				.addColumn("ExtParamValSetName")
				.addColumn("ExtParamUnitName")
				.addColumn("ExtParamdefaultValue")
				.addColumn("ExtParamdefaultTimeseriesId", CsvSchema.ColumnType.NUMBER)
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
				Unit unit = unitRepository.findByNameLike(row.ExtParamUnitName);
				ep.setUnit(unit);
				ep.setDefaultvalue(row.ExtParamdefaultValue);
				if(row.ExtParamdefaultTimeseriesId != null && row.ExtParamdefaultTimeseriesId != 0){
					if(!tsMap.containsKey(row.ExtParamdefaultTimeseriesId)){
						//time series used as default was not imported
					}else{
						TimeSeries ts = tsMap.get(row.ExtParamdefaultTimeseriesId);
						ep.setTimeseries(ts);
						ts.getExtparams().add(ep);
					}
				}
				
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
	public void importSimulationResults(int scenid, File simResInput, 
			File timeSeriesInput, int typeid)
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
				ts = saveTimeSeriesData(tsd.getSeriesData(row.TimeseriesName), type, timeOrigin.toInstant());
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
	
}
