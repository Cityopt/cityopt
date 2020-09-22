package eu.cityopt.opt.ga;

import javax.script.ScriptException;

import com.google.inject.Inject;

import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.opt.OptimisationLog;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.ScenarioNameFormat;

public abstract class CityoptDecoder {
    protected final OptimisationProblem problem;
    protected ScenarioNameFormat formatter = new ScenarioNameFormat() {
        @Override
        public String[] format(DecisionValues decisions,
                               SimulationInput input) {
            return format(decisions);
        }

        @Override
        public String[] format(DecisionValues decisions) {
            return new String[] { "-", decisions.toString() };
        }

        @Override
        public String extendDescription(
                String initialDescription,
                ConstraintStatus constraints, ObjectiveStatus objectives) {
            return initialDescription;
        }
    };
    protected OptimisationLog userLog = m -> System.err.println(m);

    public CityoptDecoder(OptimisationProblem problem) {
        this.problem = problem;
    }

    @Inject(optional = true)
    public void setFormatter(ScenarioNameFormat formatter) {
        this.formatter = formatter;
    }

    @Inject(optional = true)
    public void setUserLog(OptimisationLog log) {
        this.userLog = log;
    }

    /**
     * Make a phenotype from DecisionValues.
     */
    protected CityoptPhenotype makePt(DecisionValues dv) {
        SimulationInput inp = new SimulationInput(problem.inputConst);
        try {
            inp.putExpressionValues(dv, problem.inputExprs);
        } catch (ScriptException e) {
            userLog.logEvaluationFailure(formatter.format(dv), e);
            throw new RuntimeException(
                    "Input expression evaluation failed", e);
        }
        String[] desc = formatter.format(dv, inp);
        return new CityoptPhenotype(dv, inp, desc);
    }
}
