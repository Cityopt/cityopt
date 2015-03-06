package eu.cityopt.sim.opt;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import eu.cityopt.sim.eval.CombinedObjectiveStatus;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationStorage;
import eu.cityopt.sim.eval.ConfigurationException;

public class GridSearchJob extends AbstractOptimisationJob {
    Instant deadline;
    int rangeSplit;
    int maxDomainSize;
    //TODO: implement input-only runs (needs support in SimulationStorage)
    boolean inputOnly;
 
    CompletableFuture<List<Solution>> paretoFrontJob =
            CompletableFuture.completedFuture(new ArrayList<>());

    public GridSearchJob(
            OptimisationProblem problem, AlgorithmParameters parameters,
            SimulationStorage storage, OutputStream messageSink,
            Executor executor)
                    throws ConfigurationException, IOException, 
                    ConfigurationException {
        super(problem, storage, messageSink, executor);

        deadline = Instant.now().plus(parameters.getMaxRunTime());
        maxDomainSize = parameters.getInt("max variable domain size", 1000000);
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
    public AlgorithmStatus doRun() throws Exception {
        List<DecisionIterator> decisionIterators = new ArrayList<>();
        DecisionValues decisions = new DecisionValues(
                problem.getExternalParameters());
        for (DecisionVariable variable : problem.decisionVars) {
            DecisionIterator it = makeDecisionIterator(variable.domain);
            decisionIterators.add(it);
            decisions.put(variable, it.reset());
        }

        boolean done = false;
        int n = decisionIterators.size();
        while (!done && Instant.now().isBefore(deadline)) {
            boolean carry = true;
            int i = n - 1;
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
            CompletableFuture<Solution> futureSolution = evaluateAsync(decisions);
            paretoFrontJob = paretoFrontJob.thenCombineAsync(
                    futureSolution, super::updateParetoFront, executor);
            done = carry;
        }
        return done ? AlgorithmStatus.COMPLETED_RESULTS : AlgorithmStatus.COMPLETED_TIME;
    }

    @Override
    protected Collection<Solution> getParetoFront()
            throws InterruptedException, ExecutionException {
        return paretoFrontJob.get();
    }
}
