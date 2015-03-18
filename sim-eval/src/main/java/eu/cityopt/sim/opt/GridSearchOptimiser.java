package eu.cityopt.sim.opt;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

import javax.script.ScriptException;

import eu.cityopt.sim.eval.ConfigurationException;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.SimulationStorage;

public class GridSearchOptimiser extends AbstractOptimiser {
    Instant deadline;
    int rangeSplit;
    int maxEvaluationsTotal;
    //TODO: implement input-only runs (needs support in SimulationStorage)
    boolean inputOnly;

    public GridSearchOptimiser(
            OptimisationProblem problem, AlgorithmParameters parameters,
            SimulationStorage storage, OutputStream messageSink,
            Executor executor)
                    throws ConfigurationException, IOException, 
                    ConfigurationException {
        super(problem, storage, messageSink, executor,
                parameters.getInt("max parallel evaluations", 100));

        deadline = Instant.now().plus(parameters.getMaxRunTime());
        maxEvaluationsTotal = parameters.getInt("max scenarios", 10000);
        rangeSplit = parameters.getInt("continuous range split factor", 2);
        inputOnly = parameters.getBoolean("disable simulation", false);
    }

    static interface DecisionIterator {
        Object reset();
        Object next();
    }

    static class IntegerIterator implements DecisionIterator {
        NumericInterval<Integer> interval;
        int value;

        IntegerIterator(NumericInterval<Integer> interval) {
            this.interval = interval;
            reset();
        }

        @Override
        public
        Object reset() {
            value = interval.getLowerBound();
            return value;
        }

        @Override
        public
        Object next() {
            if (value == interval.getUpperBound()) {
                return null;
            } else {
                ++value;
                return value;
            }
        }
    }

    static class DoubleIterator implements DecisionIterator {
        final NumericInterval<Double> interval;
        final int choices;
        int index;

        DoubleIterator(NumericInterval<Double> interval, int choices) {
            this.interval = interval;
            if (interval.getLowerBound() >= interval.getUpperBound()) {
                this.choices = 1;
            } else if (interval.getLowerBound() > Double.NEGATIVE_INFINITY
                        && interval.getUpperBound() < Double.POSITIVE_INFINITY) {
                this.choices = Math.max(choices, 2);
            } else {
                this.choices = 2;
            }
            reset();
        }

        double getValue() {
            if (index == 0) {
                return interval.getLowerBound();
            } else if (index == choices - 1) {
                return interval.getUpperBound();
            } else {
                double f = (double) index / (double) (choices - 1);
                return f * interval.getLowerBound() + (1.0 - f) * interval.getUpperBound();
            }
        }

        @Override
        public Object reset() {
            index = 0;
            return getValue();
        }

        @Override
        public Object next() {
            if (index == choices) {
                return null;
            } else {
                ++index;
                return getValue();
            }
        }
        
    }

    @SuppressWarnings("unchecked")
    DecisionIterator makeDecisionIterator(DecisionDomain domain)
            throws ConfigurationException {
        switch (domain.getValueType()) {
        case INTEGER:
            return new IntegerIterator((NumericInterval<Integer>) domain);
        case DOUBLE:
        case TIMESTAMP:
            return new DoubleIterator((NumericInterval<Double>) domain, rangeSplit);
        default:
            throw new ConfigurationException(
                    "Unsupported domain type " + domain.getValueType());
        }
    }

    @Override
    public AlgorithmStatus doRun()
            throws InterruptedException, TimeoutException, ConfigurationException,
            ScriptException, IOException {
        List<DecisionIterator> decisionIterators = new ArrayList<>();
        DecisionValues decisions = new DecisionValues(
                problem.getExternalParameters());
        for (DecisionVariable variable : problem.decisionVars) {
            DecisionIterator it = makeDecisionIterator(variable.domain);
            decisionIterators.add(it);
            decisions.put(variable, it.reset());
        }

        int numberOfEvaluations = 0;
        while (true) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            int jobId = getJobId(deadline);
            queueJob(decisions, jobId);
            ++numberOfEvaluations;

            boolean carry = true;
            int i = decisionIterators.size() - 1;
            while (carry && i >= 0) {
                DecisionVariable variable = problem.decisionVars.get(i);
                Object value = decisionIterators.get(i).next();
                if (value != null) {
                    carry = false;
                } else {
                    value = decisionIterators.get(i).reset();
                }
                decisions.put(variable, value);
                --i;
            }
            // At this point carry is set if we have rolled over to the initial solution.
            if (carry || numberOfEvaluations >= maxEvaluationsTotal) {
                waitForCompletion(deadline);
                return AlgorithmStatus.COMPLETED_RESULTS;
            }
        }
    }
}
