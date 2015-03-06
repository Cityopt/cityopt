package eu.cityopt.sim.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.AlgoParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.ModelParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.ScenarioGeneratorRepository;
import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.AlgorithmParameters;
import eu.cityopt.sim.opt.OptimisationAlgorithm;
import eu.cityopt.sim.opt.OptimisationAlgorithms;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.OptimisationResults;

@Service
public class ScenarioGenerationService {
    @Autowired private ApplicationContext applicationContext;
    @Autowired private ExecutorService executorService;
    @Autowired private SimulationService simulationService;
    @Autowired private ScenarioGeneratorRepository scenarioGeneratorRepository; 
    @Autowired private OptimisationSupport optimisationSupport; 

    // TODO register opt-ga in SearchAlgorithms

    @Transactional
    public Future<OptimisationResults> startOptimisation(int scenGenId)
            throws ConfigurationException, ConfigurationException, 
            ParseException, ScriptException, IOException {
        return startOptimisation(scenGenId, executorService);
    }

    public Future<OptimisationResults> startOptimisation(int scenGenId, Executor executor)
            throws ConfigurationException, ConfigurationException, 
            ParseException, ScriptException, IOException {
        ScenarioGenerator scenarioGenerator = scenarioGeneratorRepository.findOne(scenGenId);
        Project project = scenarioGenerator.getProject();

        String algorithmName = scenarioGenerator.getAlgorithm().getDescription();
        OptimisationAlgorithm searchAlgorithm = OptimisationAlgorithms.get(algorithmName);
        AlgorithmParameters algorithmParameters = loadAlgorithmParameters(scenarioGenerator);

        Namespace namespace = simulationService.makeProjectNamespace(project, scenarioGenerator);
        ExternalParameters externals = simulationService.loadExternalParametersFromSet(
                scenarioGenerator.getExtparamvalset(), namespace);

        OptimisationProblem problem = new OptimisationProblem();
        problem.decisionVars = loadDecisionDomains(scenarioGenerator, namespace);
        problem.inputConst = new SimulationInput(externals);
        problem.inputExprs = loadInputExpressions(scenarioGenerator, namespace, problem.inputConst);
        problem.metrics = simulationService.loadMetricExpressions(project, namespace);
        problem.constraints = optimisationSupport.loadConstraints(scenarioGenerator, namespace);
        problem.model = simulationService.loadSimulationModel(project);
        problem.objectives = optimisationSupport.loadObjectives(scenarioGenerator, namespace);

        DbSimulationStorageI storage =
                (DbSimulationStorageI) applicationContext.getBean("dbSimulationStorage");
        storage.initialize(project.getPrjid(), externals, null, null);

        ByteArrayOutputStream messageStream = new ByteArrayOutputStream(); 
        CompletableFuture<OptimisationResults> job = searchAlgorithm.start(
                problem, algorithmParameters, storage, messageStream, executor);
        job.whenCompleteAsync(
                (results, throwable) -> {
                    String messages = messageStream.toString();
                    if (throwable != null) {
                        messages += "\nOptimisation terminated: " + throwable.getMessage();
                    }
                    storage.saveScenarioGeneratorStatus(scenGenId, results, messages);
                }, executor);
        return job;
    }

    AlgorithmParameters loadAlgorithmParameters(ScenarioGenerator scenarioGenerator) {
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();
        for (AlgoParamVal algoParamVal : scenarioGenerator.getAlgoparamvals()) {
            String parameterName = algoParamVal.getAlgoparam().getName();
            algorithmParameters.put(parameterName, algoParamVal.getValue());
        }
        return algorithmParameters;
    }

    public List<DecisionVariable> loadDecisionDomains(
            ScenarioGenerator scenarioGenerator, Namespace namespace)
                    throws ScriptException, ParseException {
        List<DecisionVariable> simDecisions = new ArrayList<>();
        for (eu.cityopt.model.DecisionVariable decisionVariable 
                : scenarioGenerator.getDecisionvariables()) {
            String componentName = null;
            String variableName = null;
            InputParameter inputParameter = decisionVariable.getInputparameter();
            if (inputParameter != null) {
                componentName = inputParameter.getComponent().getName();
                variableName = inputParameter.getName();
            } else {
                variableName = decisionVariable.getName();
            }
            DecisionDomain domain = null;
            Type variableType = namespace.getDecisionType(componentName, variableName);
            String lbText = decisionVariable.getLowerbound();
            String ubText = decisionVariable.getUpperbound();
            Object lb = lbText != null ? variableType.parse(lbText, namespace) : null;
            Object ub = (ubText != null) ? variableType.parse(ubText, namespace) : null;
            domain = NumericInterval.makeInterval(variableType, lb, ub);

            simDecisions.add(new DecisionVariable(componentName, variableName, domain));
        }
        return simDecisions;
    }

    public List<InputExpression> loadInputExpressions(
            ScenarioGenerator scenarioGenerator, Namespace namespace,
            SimulationInput constantInputHolder) throws ScriptException, ParseException {
        List<InputExpression> simInputExpressions = new ArrayList<>();
        for (ModelParameter modelParameter : scenarioGenerator.getModelparameters()) {
            InputParameter inputParameter = modelParameter.getInputparameter();
            String componentName = inputParameter.getComponent().getName();
            String inputName = inputParameter.getName();
            Namespace.Component nsComponent = namespace.components.get(componentName); 
            Type inputType = nsComponent.inputs.get(inputParameter.getName());
            String valueText = modelParameter.getValue();
            if (valueText != null) {
                Object value = inputType.parse(valueText, namespace);
                constantInputHolder.put(componentName, inputName, value);
            } else {
                InputExpression simExpression =
                        new InputExpression(componentName, inputName,
                                modelParameter.getExpression(), namespace.evaluator);
                simInputExpressions.add(simExpression);
            }
        }
        return simInputExpressions;
    }
}
