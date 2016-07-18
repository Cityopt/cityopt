package eu.cityopt.opt.ga;

import javax.script.ScriptException;

import org.opt4j.core.genotype.MapGenotype;
import org.opt4j.core.problem.Decoder;

import com.google.inject.Inject;

import eu.cityopt.sim.eval.ConstraintStatus;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationLog;
import eu.cityopt.sim.opt.OptimisationProblem;
import eu.cityopt.sim.opt.ScenarioNameFormat;

public class ComponentwiseDecoder
implements Decoder<ComponentwiseGenotype, CityoptPhenotype> {
    private final OptimisationProblem problem;

    private ScenarioNameFormat formatter = new ScenarioNameFormat() {
        @Override
        public String[] format(DecisionValues decisions, SimulationInput input) {
            return format(decisions);
        }

        @Override
        public String[] format(DecisionValues decisions) {
            return new String[] { "-", decisions.toString() };
        }

		@Override
		public String extendDescription(String initialDescription,
				ConstraintStatus constraints, ObjectiveStatus objectives) {
			return initialDescription;
		}
    };

    private OptimisationLog userLog =
            m -> System.err.println(m);

    @Inject(optional=true)
    public void setFormatter(ScenarioNameFormat formatter) {
        this.formatter = formatter;
    }

    @Inject(optional=true)
    public void setUserLog(OptimisationLog log) {
        this.userLog = log;
    }

    @Inject
    public ComponentwiseDecoder(OptimisationProblem problem) {
        this.problem = problem;
    }

    @Override
    public CityoptPhenotype decode(ComponentwiseGenotype genotype) {
        DecisionValues dv = new DecisionValues(
                problem.inputConst.getExternalParameters());
        for (String comp : genotype.keySet()) {
            MixedGenotype<String> comp_gt = genotype.get(comp);
            for (Type t : comp_gt.keySet()) {
                MapGenotype<String, ?> type_gt = comp_gt.get(t);
                for (String var : type_gt.getKeys()) {
                    dv.put(comp, var, type_gt.getValue(var));
                }
            }
        }
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
