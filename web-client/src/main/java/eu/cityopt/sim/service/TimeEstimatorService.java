package eu.cityopt.sim.service;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.Component;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.InputParameter;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ComponentRepository;
import eu.cityopt.repository.InputParameterRepository;
import eu.cityopt.repository.ProjectRepository;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationInput;

@Service("timeestimatorservice")
public class TimeEstimatorService {
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ComponentRepository componentRepository;
    @Autowired private InputParameterRepository inputParameterRepository;

    static class TimePair {
        Duration simulationTime;
        Duration realTime;
    }

    /**
     * Predicts expected simulation runtime for a scenario, based on existing
     * scenarios of the project.
     */
    @Transactional(readOnly=true)
    public Duration predictSimulationRuntime(int projectId, int scenarioId) {
        Duration nominalRuntime = Duration.ofMinutes(1);
        Map<Integer, TimePair> runtimes = findScenarioRuntimes(projectId);
        TimePair p = runtimes.get(scenarioId);
        Duration simTime = (p != null) ? p.simulationTime : null;
        return predictSimulationRuntime(runtimes, simTime, nominalRuntime);
    }

    Duration predictSimulationRuntime(
            int projectId, SimulationInput input, Duration nominalRuntime) {
        Map<Integer, TimePair> runtimes = findScenarioRuntimes(projectId);
        String cc = Namespace.CONFIG_COMPONENT;
        Double t0 = (Double) input.get(cc, Namespace.CONFIG_SIMULATION_START);
        Double t1 = (Double) input.get(cc, Namespace.CONFIG_SIMULATION_END);
        Duration simTime = (t0 != null && t1 != null)
                ? Duration.ofNanos((long) (1e9 * (t1 - t0))) : null;
        return predictSimulationRuntime(runtimes, simTime, nominalRuntime);
    }

    Map<Integer, TimePair> findScenarioRuntimes(int projectId) {
        Map<Integer, TimePair> timePairs = new HashMap<>();

        Project project = projectRepository.findOne(projectId);
        for (Scenario s : project.getScenarios()) {
            if (s.getStatus() != null
                    && s.getStatus().equals(SimulationService.STATUS_SUCCESS)
                    && s.getRunstart() != null && s.getRunend() != null) {
                try {
                    Instant realStart = s.getRunstart().toInstant();
                    Instant realEnd = s.getRunend().toInstant();
                    TimePair p = new TimePair();
                    p.realTime = Duration.between(realStart, realEnd);
                    timePairs.put(s.getScenid(), p);
                } catch (DateTimeParseException x) {
                    /* ignore */
                }
            }
        }

        Component comp = componentRepository.findByNameAndProject(
                projectId, Namespace.CONFIG_COMPONENT);
        InputParameter startParam = inputParameterRepository.findByNameAndCompId(
                Namespace.CONFIG_SIMULATION_START, comp.getComponentid());
        InputParameter endParam = inputParameterRepository.findByNameAndCompId(
                Namespace.CONFIG_SIMULATION_END, comp.getComponentid());
        Map<Integer, String> startValues = new HashMap<>();
        for (InputParamVal s : startParam.getInputparamvals()) {
            startValues.put(s.getScenario().getScenid(), s.getValue());
        }
        for (InputParamVal e : endParam.getInputparamvals()) {
            int scenId = e.getScenario().getScenid();
            String startValue = startValues.get(scenId);
            String endValue = e.getValue();
            if (startValue != null && endValue != null) {
                try {
                    Instant simStart = Instant.parse(startValue);
                    Instant simEnd = Instant.parse(endValue);
                    TimePair p = timePairs.computeIfAbsent(scenId,
                            key -> new TimePair());
                    p.simulationTime = Duration.between(simStart, simEnd);
                } catch (DateTimeParseException x) {
                    /* ignore */
                }
            }
        }
        return timePairs;
    }

    Duration predictSimulationRuntime(
            Map<Integer, TimePair> timePairs, Duration simulationTime,
            Duration nominalRuntime) {
        SimpleRegression regression = new SimpleRegression();
        double sumOfRealTimes = 0;
        double minSimTime = Double.MAX_VALUE, maxSimTime = Double.MIN_VALUE;
        int n = 0;
        for (TimePair s : timePairs.values()) {
            if (s.realTime != null && s.simulationTime != null) {
                double realNanos = s.realTime.toNanos();
                double simNanos = s.simulationTime.toNanos();
                regression.addData(simNanos, realNanos);
                sumOfRealTimes += realNanos;
                minSimTime = Math.min(minSimTime, simNanos);
                maxSimTime = Math.max(maxSimTime, simNanos);
                ++n;
            }
        }
        if (n == 0) {
            return (nominalRuntime != null)
                    ? nominalRuntime : Duration.ofNanos(0);
        }
        double predicted = 0;
        if (simulationTime != null && maxSimTime > minSimTime) {
            predicted = regression.predict(simulationTime.toNanos());
        } else {
            double averageRealTime = sumOfRealTimes / n;
            if (simulationTime != null) {
                predicted = simulationTime.toNanos() / minSimTime
                        * averageRealTime;
            } else {
                predicted = averageRealTime;
            }
        }
        return Duration.ofNanos((long)predicted);
    }

    public Instant estimateScenGenCompletionTime(
            Instant started, int scenarios,
            Instant deadline, int maxScenarios,
            Duration priorTimePerScenario) {
        Duration elapsed = Duration.between(started, Instant.now());
        Duration timePerScenario;
        if (priorTimePerScenario != null) {
            timePerScenario = elapsed.plus(priorTimePerScenario)
                    .dividedBy(1 + scenarios);
        } else if (scenarios > 0) {
            timePerScenario = elapsed.dividedBy(scenarios);
        } else {
            return deadline;
        }
        Instant estimated =
                started.plus(timePerScenario.multipliedBy(maxScenarios));
        if (estimated.isBefore(deadline)) {
            return estimated;
        } else {
            return deadline;
        }
    }

}
