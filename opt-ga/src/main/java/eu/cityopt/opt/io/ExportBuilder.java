package eu.cityopt.opt.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Symbol;
import eu.cityopt.sim.eval.Namespace.Component;
import eu.cityopt.sim.eval.NumberInterval;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import static eu.cityopt.opt.io.JacksonBinderScenario.*;

/**
 * A builder for exporting sim-eval objects.
 * All data should be for a single project and, if applicable, a single
 * scenario generator.  Multiple scenarios and external parameter sets can
 * be included.  Items appear in the binder in the order they are added,
 * so either add them in the correct order or sort the binder later.
 * If you add an item multiple times, it will appear multiple times, so
 * don't do that.
 * @author ttekth
 */
public class ExportBuilder {
    private JacksonBinderScenario binder;
    private TimeSeriesData tsd;
    
    /**
     * Start with empty output objects.
     * @param evsup
     */
    public ExportBuilder(EvaluationSetup evsup) {
        binder = new JacksonBinderScenario(new ArrayList<>());
        tsd = new TimeSeriesData(evsup);
    }
    
    /**
     * Build on existing output objects.
     */
    public ExportBuilder(JacksonBinderScenario binder, TimeSeriesData tsd) {
        this.binder = binder;
        this.tsd = tsd;
    }

    /**
     * Add all external parameters without values.
     * @param ns
     */
    public void addExtParams(Namespace ns) {
        addExtParams(ns, null, null);
    }

    /**
     * Add a set of external parameter values. 
     * @param ext values to add
     * @param extSet name of the set
     */
    public void add(ExternalParameters ext, String extSet) {
        addExtParams(ext.getNamespace(), ext, extSet);
    }
    
    private void addExtParams(
            Namespace ns, ExternalParameters ext, String extSet) {
        EvaluationSetup evs = ext == null ? null : tsd.getEvaluationSetup();
        for (Map.Entry<String, Type> nt : ns.externals.entrySet()) {
            Object val = null;
            if (ext != null) {
                val = ext.get(nt.getKey());
                if (val == null)
                    continue;
            }
            ExtParam p = new ExtParam();
            p.extparamvalsetname = extSet;
            p.item = new JacksonBinder.ExtParam();
            p.item.name = nt.getKey();
            p.item.type = nt.getValue();
            if (val != null) {
                if (p.item.type.isTimeSeriesType()) {
                    String tslabel = makeTSLabel(p.item.name, null, extSet);
                    tsd.put(tslabel, ext.getTS(p.item.name));
                    p.item.value = tslabel;
                } else {
                    p.item.value = p.item.type.format(val, evs);
                }
            }
            binder.getItems().add(p);
        }
    }

    /**
     * Add the definition of a metric.
     */
    public void add(Type type, MetricExpression met) {
        Metric m = new Metric();
        m.item = new JacksonBinder.Metric();
        m.item.name = met.getMetricName();
        m.item.type = type;
        m.item.expression = met.getSource();
        binder.getItems().add(m);
    }
    
    /**
     * Add the definition of a metric.
     * Look up the type in a Namespace.
     */
    public void add(MetricExpression met, Namespace ns) {
        add(ns.metrics.get(met.getMetricName()), met);
    }
    
    /**
     * Add all input parameters without values.
     * @param ns
     */
    public void addInputs(Namespace ns) {
        addInputs(ns, null, null);
    }

    /**
     * Add input values for a scenario.
     * @param input values to add
     * @param scenario scenario name
     */
    public void add(SimulationInput input, String scenario) {
        addInputs(input.getNamespace(), input, scenario);
    }
    
    private void addInputs(
            Namespace ns, SimulationInput input, String scenario) {
        EvaluationSetup evs = input == null ? null : tsd.getEvaluationSetup();
        for (Map.Entry<String, Component> 
                comp : ns.components.entrySet()) {
            for (Map.Entry<String, Type>
                    nt : comp.getValue().inputs.entrySet()) {
                Object val = null;
                if (input != null) {
                    val = input.get(comp.getKey(), nt.getKey());
                    if (val == null)
                        continue;
                }
                Input in = new Input();
                in.scenarioname = scenario;
                in.item = new JacksonBinder.Input();
                in.item.comp = comp.getKey();
                in.item.name = nt.getKey();
                in.item.type = nt.getValue();
                if (val != null) {
                    in.item.value = in.item.type.format(val, evs);
                }
                binder.getItems().add(in);
            }
        }

    }
    
    /**
     * Add output variables without values.
     * @param ns
     */
    public void addOutputs(Namespace ns) {
        addOutputs(ns, null, null);
    }
    
    /**
     * Add output values for a scenario.
     * This does not add the inputs.  All outputs in the namespace
     * are added, whether they have values or not.
     * @param res results to add
     * @param scenario name of the scenario
     */
    public void add(SimulationResults res, String scenario) {
        addOutputs(res.getNamespace(), res, scenario);
    }
    
    private void addOutputs(
            Namespace ns, SimulationResults res, String scenario) {
        for (Map.Entry<String, Component> 
                comp : ns.components.entrySet()) {
            for (Map.Entry<String, Type>
                    nt : comp.getValue().outputs.entrySet()) {
                TimeSeriesI val = res != null
                                  ? res.getTS(comp.getKey(), nt.getKey())
                                  : null;
                Output out = new Output();
                out.scenarioname = scenario;
                out.item = new JacksonBinder.Output();
                out.item.comp = comp.getKey();
                out.item.name = nt.getKey();
                out.item.type = nt.getValue();
                if (val != null) {
                    String lbl = makeTSLabel(out.item.name, scenario, null);
                    tsd.put(lbl, val);
                    out.item.value = lbl;
                }
                binder.getItems().add(out);
            }
        }
    }
    
    /**
     * Add metric values.
     * The set of metrics is retrieved from the namespace of mv.
     * @param mv values to add
     * @param scenario scenario name
     * @param extSet external parameter set name
     */
    public void add(MetricValues mv, String scenario, String extSet) {
        EvaluationSetup evs = tsd.getEvaluationSetup();
        for (Map.Entry<String, Type>
                nt : mv.getNamespace().metrics.entrySet()) {
            Object val = mv.get(nt.getKey());
            if (val == null)
                continue;
            Metric m = new Metric();
            m.scenarioname = scenario;
            m.extparamvalsetname = extSet;
            m.item = new JacksonBinder.Metric();
            m.item.name = nt.getKey();
            m.item.type = nt.getValue();
            if (m.item.type.isTimeSeriesType()) {
                String tslabel = makeTSLabel(m.item.name, scenario, extSet);
                tsd.put(tslabel, mv.getTS(m.item.name));
                m.item.value = tslabel;
            } else {
                m.item.value = m.item.type.format(val, evs);
            }
            binder.getItems().add(m);
        }
    }

    /**
     * Add constant input values.
     * These are the inputs of a scenario generation run that do not
     * depend on decision variables, i.e., scenario.
     */
    public void add(SimulationInput input) {
        add(input, null);
    }
    
    /**
     * Add a decision variable.
     */
    public void add(DecisionVariable dv) {
        DecisionVar v = new DecisionVar();
        v.item = new JacksonBinder.DecisionVar();
        v.item.comp = dv.componentName;
        v.item.name = dv.name;
        v.item.type = dv.domain.getValueType();
        EvaluationSetup sup = tsd.getEvaluationSetup();
        switch(v.item.type) {
        case DOUBLE:
        case INTEGER:
            NumberInterval dom = (NumberInterval)dv.domain;
            v.item.lower = v.item.type.format(dom.getLowerBound(), sup);
            v.item.upper = v.item.type.format(dom.getUpperBound(), sup);
            break;
        default:
            throw new IllegalArgumentException(
                    "Unsupported decision variable type " + v.item.type);    
        }
        binder.getItems().add(v);
    }

    /**
     * Add an input expression.
     */
    public void add(Type type, InputExpression ine) {
        Input in = new Input();
        in.item = new JacksonBinder.Input();
        in.item.comp = ine.getInput().componentName;
        in.item.name = ine.getInput().name;
        in.item.type = type;
        in.item.expr = ine.getSource();
        binder.getItems().add(in);
    }

    /**
     * Add an input expression.  Look up the type in a Namespace.
     */
    public void add(InputExpression ine, Namespace ns) {
        Symbol in = ine.getInput();
        add(ns.components.get(in.componentName).inputs.get(in.name), ine);
    }
    
    public void add(Constraint con) {
        Constr c = new Constr();
        c.item = new JacksonBinder.Constr();
        c.item.name = con.getName();
        c.item.expression = con.getExpression().getSource();
        double
                lb = con.getLowerBound(),
                ub = con.getUpperBound();
        c.item.lower = lb == Double.NEGATIVE_INFINITY ? ""
                                                      : Double.toString(lb);
        c.item.upper = ub == Double.POSITIVE_INFINITY ? ""
                                                      : Double.toString(ub);
        binder.getItems().add(c);
    }
    
    public void add(ObjectiveExpression obj) {
        Obj o = new Obj();
        o.item = new JacksonBinder.Obj();
        o.item.name = obj.getName();
        o.item.setMaximize(obj.isMaximize());
        o.item.expression = obj.getSource();
        binder.getItems().add(o);
    }

    /**
     * Add decision variable values.
     */
    public void add(DecisionValues dv, String scenario) {
        //TODO
    }
    
    public JacksonBinderScenario getScenarioBinder() {
        return binder;
    }
    
    /**
     * Convert our data into a JacksonBinder.
     * Discard scenario and external parameter set labels.
     * This can be used when the data contain at most one external
     * parameter set and scenario.  Do not attempt to modify
     * the returned JacksonBinder.
     */
    public JacksonBinder getBinder() {
        return new JacksonBinder(
                binder.getItems().stream().map(x -> x.getItem())
                        .collect(Collectors.toList()));
    }

    public TimeSeriesData getTimeSeriesData() {
        return tsd;
    }
    
    private static String makeTSLabel(
            String name, String scenario, String extSet) {
        String sfx = maybeJoin(",", scenario, extSet);
        return sfx.isEmpty() ? name : name + "@" + sfx;
    }
    
    private static String maybeJoin(String sep, String... args) {
        return String.join(sep, (Iterable<String>)Arrays.stream(args)
                .filter(x -> x != null)::iterator);
    }
}
