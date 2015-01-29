package eu.cityopt.sim.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.cityopt.model.Component;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Metric;
import eu.cityopt.model.OutputVariable;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.SimulationResult;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationResultRepository;
import eu.cityopt.repository.UnitRepository;
import eu.cityopt.sim.eval.EvaluationException;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.SimulationRunner;
import eu.cityopt.sim.eval.SimulatorConfigurationException;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.SimulatorManagers;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;

@Service
public class SimulationService {
    class SimulationJob implements Callable<SimulationOutput> {
        SimulationInput input;
        Scenario scenario;
        SimulationModel model;
        SimulationRunner runner;

        SimulationJob(SimulatorManager manager, byte[] modelZipBytes, SimulationInput input,
                Scenario scenario) throws IOException, SimulatorConfigurationException {
            this.input = input;
            this.scenario = scenario;
            this.model = manager.parseModel(modelZipBytes);
            try {
                this.runner = manager.makeRunner(model, input.getNamespace());
            } catch (Throwable t) {
                model.close();
            }
        }

        @Override
        public SimulationOutput call() throws Exception {
            try {
                Future<SimulationOutput> job = runner.start(input);
                SimulationOutput output = job.get();
                saveSimulationResults(scenario, output);
                return output;
            } finally {
                try {
                    runner.close();
                } finally {
                    model.close();
                }
            }
        }
    }

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    SimulationResultRepository simulationResultRepository;

    //@Autowired
    //private TaskExecutor taskExecutor;

    private Evaluator evaluator;

    private SimulationService() throws EvaluationException, ScriptException {
        evaluator = new Evaluator();
    }

    public Future<SimulationOutput> startSimulation(int scenid)
            throws ParseException, IOException, SimulatorConfigurationException,
            InterruptedException, ExecutionException {
        return startSimulation(scenarioRepository.findOne(scenid));
    }

    public Future<SimulationOutput> startSimulation(Scenario scenario)
            throws ParseException, IOException, SimulatorConfigurationException,
            InterruptedException, ExecutionException {
        Project project = scenario.getProject();
        Namespace namespace = makeProjectNamespace(project);

        SimulationInput input = loadSimulationInput(scenario, namespace);

        String simulatorName = project.getSimulationmodel().getSimulator();
        SimulatorManager manager = SimulatorManagers.get(simulatorName);
        if (manager == null) {
            throw new SimulatorConfigurationException(
                    "Unknown simulator " + simulatorName);
        }
        byte[] modelZipBytes = project.getSimulationmodel().getModelblob();
        FutureTask<SimulationOutput> futureTask = new FutureTask<SimulationOutput>(
                new SimulationJob(manager, modelZipBytes, input, scenario));
        //FIXME: won't work in test, appears to end up in different database transaction
        //taskExecutor.execute(futureTask);
        futureTask.run();
        return futureTask;
    }

    public SimulationInput loadSimulationInput(Scenario scenario, Namespace namespace) 
            throws ParseException {
        ExternalParameters simExternals = new ExternalParameters(namespace);
        for (ExtParam mExternal : scenario.getProject().getExtparams()) {
            for (ExtParamVal mValue : mExternal.getExtparamvals()) {
                //TODO implement time series
                simExternals.putString(mExternal.getName(), mValue.getValue());
            }
        }

        SimulationInput simInput = new SimulationInput(simExternals);
        for (InputParamVal mValue : scenario.getInputparamvals()) {
            InputParameter mInput = mValue.getInputparameter();
            String componentName = mInput.getComponent().getName();
            simInput.putString(componentName, mInput.getName(), mValue.getValue());
        }

        return simInput;
    }

    public void saveSimulationResults(Scenario scenario, SimulationOutput simOutput) {
        //TODO where to save output.getMessages()
        if (simOutput instanceof SimulationResults) {
            SimulationResults simResults = (SimulationResults) simOutput;
            Set<SimulationResult> mResults = new HashSet<SimulationResult>(); 
            for (Component mComponent : scenario.getProject().getComponents()) {
                String componentName = mComponent.getName();
                for (OutputVariable mOutput : mComponent.getOutputvariables()) {
                    TimeSeriesI simTS = simResults.getTS(componentName, mOutput.getName());
                    double[] times = simTS.getTimes();
                    double[] values = simTS.getValues();
                    for (int i = 0; i < times.length; ++i) {
                        SimulationResult mResult = new SimulationResult();
                        mResult.setOutputvariable(mOutput);
                        mResult.setScenario(scenario);
                        mResult.setTime(new Date((long) (times[i] / 1000.0 + 0.5)));
                        mResult.setValue(Double.toString(values[i]));
                        mResults.add(mResult);
                    }
                }
            }
            scenario.setSimulationresults(mResults);
            simulationResultRepository.save(mResults);
            scenarioRepository.save(scenario);
        }
    }

    public Namespace makeProjectNamespace(Project project) {
        Namespace namespace = new Namespace(evaluator);
        for (ExtParam mExternal : project.getExtparams()) {
            String typeName = mExternal.getTimeseries().getType().getName();
            Type extType = Type.getByName(typeName);
            namespace.externals.put(mExternal.getName(), extType);
        }
        for (Component mComponent : project.getComponents()) {
            Namespace.Component nsComponent = namespace.getOrNew(mComponent.getName());
            for (InputParameter mInput : mComponent.getInputparameters()) {
                String typeName = mInput.getUnit().getType().getName();
                Type inputType = Type.getByName(typeName);
                nsComponent.inputs.put(mInput.getName(), inputType);
            }
            for (OutputVariable mOutput : mComponent.getOutputvariables()) {
                String typeName = unitRepository.findOne(mOutput.getUnitid()).getType().getName();
                Type outputType = Type.getByName(typeName);
                nsComponent.outputs.put(mOutput.getName(), outputType);
            }
        }
        for (Metric mMetric : project.getMetrics()) {
            String typeName = mMetric.getUnit().getType().getName();
            Type metricType = Type.getByName(typeName);
            namespace.metrics.put(mMetric.getName(), metricType);
        }
        return namespace;
    }
}
