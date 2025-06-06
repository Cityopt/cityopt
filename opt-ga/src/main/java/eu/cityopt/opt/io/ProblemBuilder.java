package eu.cityopt.opt.io;

import java.text.ParseException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.script.ScriptException;

import eu.cityopt.opt.io.JacksonBinder.Constr;
import eu.cityopt.opt.io.JacksonBinder.DecisionVar;
import eu.cityopt.opt.io.JacksonBinder.ExtParam;
import eu.cityopt.opt.io.JacksonBinder.Input;
import eu.cityopt.opt.io.JacksonBinder.Obj;
import eu.cityopt.opt.io.JacksonBinder.TSRef;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.TimeSeriesData;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * An {@link ImportBuilder} for an {@link OptimisationProblem}.
 * @author ttekth
 */
public class ProblemBuilder extends SimulationStructureBuilder {
    private final TimeSeriesData tsData;

    /** Modify a given problem.
     * @param initial OptimisationProblem to modify (in place).
     * @param tsdata Time series data
     */
    public ProblemBuilder(OptimisationProblem initial, TimeSeriesData tsdata) {
        super(initial);
        tsData = tsdata;
    }
    
    /**
     * Construct a new OptimisationProblem.
     * @param model a simulation model reference
     * @param ns a Namespace for the problem (must be completely populated)
     * @param tsdata Time series data (must be completely populated).
     * @see NamespaceBuilder
     */
    @Inject
    public ProblemBuilder(@Nullable SimulationModel model, Namespace ns,
                          TimeSeriesData tsdata) {
        this(new OptimisationProblem(model, new ExternalParameters(ns)),
             tsdata);
    }
    
    @Override
    public OptimisationProblem getResult() {
        return (OptimisationProblem)super.getResult();
    }
    
    public TimeSeriesI getTS(TSRef item, Type type) {
        TimeSeriesData.Series sd = tsData.getSeries(item.tsKey());
        if (sd == null) {
            throw new IllegalArgumentException(String.format(
                    "No time series data for %s (kind %s, type %s)",
                    item.getQName(), item.getKind(), type));
        }
        return ns.evaluator.makeTS(type, sd.times, sd.values);
    }

    @Override
    protected void add(ExtParam item) throws ParseException {
        ExternalParameters ext = getResult().inputConst.getExternalParameters();
        if (item.type.isTimeSeriesType()) {
            ext.put(item.name, getTS(item, item.type));
        } else {
            ext.putString(item.name, item.value);
        }
    }

    @Override
    protected void add(DecisionVar dv) throws ParseException {
        switch (dv.type) {
        case DOUBLE:
        case INTEGER:
            Object
                lb = (dv.lower == null ? null : dv.type.parse(dv.lower, ns)),
                ub = (dv.upper == null ? null : dv.type.parse(dv.upper, ns));
            DecisionDomain
            dom = (dv.type == Type.DOUBLE
                   ? NumericInterval.makeRealInterval(
                           (Double)lb, (Double)ub)
                   : NumericInterval.makeIntInterval(
                           (Integer)lb, (Integer)ub));
            getResult().decisionVars.add(
                    new DecisionVariable(dv.comp, dv.name, dom));
            break;
        default:
            throw new IllegalArgumentException(
                    "Unsupported decision variable type " + dv.type);
        }
    }

    @Override
    protected void add(Input in) throws ParseException, ScriptException {
        if (in.value == null && in.expr == null)
            throw new IllegalArgumentException(String.format(
                    "Either value or expr must be present"
                            + " on input %s,%s", in.comp, in.name));
        if (in.expr == null) {
            SimulationInput ic = getResult().inputConst;
            if (in.type.isTimeSeriesType()) {
                ic.put(in.comp, in.name, getTS(in, in.type));
            } else {
                ic.putString(in.comp, in.name, in.value);
            }
        } else {
            getResult().inputExprs.add(new InputExpression(
                    in.comp, in.name, in.expr, ns.evaluator));
        }
    }

    @Override
    protected void add(Constr c) throws ScriptException {
        if (c.expression == null)
            throw new IllegalArgumentException("Missing expression");
        else {
            double
                lb = (c.lower != null ? Double.valueOf(c.lower)
                                      : Double.NEGATIVE_INFINITY),
                ub = (c.upper != null ? Double.valueOf(c.upper)
                                      : Double.POSITIVE_INFINITY);
            getResult().constraints.add(new Constraint(
                    null, c.name, c.expression, lb, ub, ns.evaluator));
        }
    }

    @Override
    protected void add(Obj o) throws ScriptException {
        Boolean is_max = o.isMaximize();
        if (o.expression == null)
            throw new IllegalArgumentException(
                    "Missing objective expression");
        if (is_max == null)
            throw new IllegalArgumentException(
                    "Invalid objective type " + o.type);
        getResult().objectives.add(new ObjectiveExpression(
                null, o.name, o.expression, is_max,
                ns.evaluator));

    }
}
