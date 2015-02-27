package eu.cityopt.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.cityopt.model.Metric;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
//import eu.cityopt.sim.eval.ConstraintExpression;
import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.HashSimulationStorage;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulationRunnerWithStorage;
import eu.cityopt.sim.eval.TimeSeries;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.service.SimulationService;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup({"classpath:/testData/globalTestData.xml", "classpath:/testData/project1TestData.xml",
	"classpath:/testData/Sample Test case - SC1.xml"})
public class SimEvalTest {
	
	@Autowired
	ComponentRepository componentRepository;
	
	@Autowired
	ProjectRepository projectRepository;
	
	@Autowired
	SimulationResultRepository simResRepository;
	
	@Autowired
	SimulationService simulationService;
	
	@Autowired
	TimeSeriesValRepository timeSeriesValRepository;
	
	@Autowired
	TimeSeriesRepository timeSeriesRepository;
	
//	@BeforeClass
//	public static void setUp() throws Exception {
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//	}

	@Test
//	@Rollback(false)
	public void simEval2() throws Exception{
		Project project = projectRepository.findOne(1);
		Scenario scen = project.getScenarios().iterator().next();
		
		Namespace ns = simulationService.makeProjectNamespace(project);
                ExternalParameters externalParameters = simulationService.loadExternalParametersFromDefaults(project, ns);
		SimulationInput input = simulationService.loadSimulationInput(scen, externalParameters);
        
		
        List<MetricExpression> metrics = simulationService.loadMetricExpressions(project, ns);

        SimulationResults output = new SimulationResults(input, null);
        assertTrue(output instanceof SimulationResults);
        

        //for each component -> get outputvars and their simulationresults:
        for(Iterator<eu.cityopt.model.Component> i = project.getComponents().iterator(); i.hasNext();){
        	eu.cityopt.model.Component c = i.next();
        	String componentName = c.getName();
        	String outputName = null;
//        	List<Double> vals = new ArrayList<Double>();
//          List<Double> times = new ArrayList<Double>();
        	
            double [] valsd = null;
            double [] timesd = null;
            
        	for(Iterator<OutputVariable> io = c.getOutputvariables().iterator(); io.hasNext();){
        		OutputVariable outvar = io.next();
        		outputName = outvar.getName();
//        		//this is not ordered...
//        		Set<SimulationResult> simresi = outvar.getSimulationresults();
        		
        		SimulationResult simresi = simResRepository.findByScenAndOutvar(1, outvar.getOutvarid());
        		List<TimeSeriesVal> tsValues = timeSeriesValRepository.findTimeSeriesValOrderedByTime(simresi
        						.getTimeseries().getTseriesid());
        		
        		valsd = new double[tsValues.size()];
        		timesd = new double[tsValues.size()];
        		Calendar cal = Calendar.getInstance();
        		
        		//construct times/values as double arrays
        		TimeSeriesVal[] tsValuesa= tsValues.toArray(new TimeSeriesVal[0]);
        		for(int is = 0; is < tsValues.size(); is++){
            		TimeSeriesVal singleRes = tsValuesa[is];  
            		timesd[is] = singleRes.getTime().getTime();
            		valsd[is] = Double.parseDouble(singleRes.getValue().replace(',', '.'));
            	}

        		TimeSeries ts = new TimeSeries(0, timesd, valsd);
            	output.put(componentName, outputName, ts);
            	System.out.println("added: "+ outputName + " to component " + componentName);
        	}        	
        }
        
        if(output.isComplete())
        	System.out.println("output complete");
        else
        	System.out.println("output incomplete");
        
        SimulationResults results = (SimulationResults)output;
        System.out.println(output.getMessages());
        MetricValues mv = new MetricValues(results, metrics);

        
//        Iterator<double[]> it = Arrays.asList(mv.metricValues).iterator();
//        for(Iterator<MetricExpression> i = metrics.iterator(); i.hasNext(); ) {
//            MetricExpression met = i.next();
//        	System.out.println("Metric " + met.getMetricName()
//                    + ": " + it.next());
//        }
        
        Iterator<MetricExpression> iter = metrics.iterator();
        String[] metricValues = new String[metrics.size()];
        for (int i = 0; i < metrics.size(); ++i) {
            String n = iter.next().getMetricName();
            metricValues[i] = mv.get(n).toString();
            System.out.println("Metric " + n + ": " + metricValues[i]);
        }
        
//        for (Map.Entry<String, Namespace.Component> entry : ns.components.entrySet()) {
//            String componentName = entry.getKey().toString();
//            Namespace.Component component = entry.getValue();
//            for (String outputName : component.outputs.keySet()) {
//                System.out.println(
//                        componentName + "." + outputName + " = "
//                        + results.getTS(entry.getKey(), outputName).getValues()[0]);
//            }
//        }
        
	}
	
}
