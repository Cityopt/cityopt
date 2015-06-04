package eu.cityopt.opt.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import eu.cityopt.opt.io.JacksonBinder.Kind;
import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionValues;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace.Component;
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
 * be included.
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
     * Add a set of external parameter values. 
     * @param ext values to add
     * @param extSet name of the set
     */
    public void add(ExternalParameters ext, String extSet) {
        EvaluationSetup evs = tsd.getEvaluationSetup();
        for (Map.Entry<String, Type>
                nt : ext.getNamespace().externals.entrySet()) {
            Object val = ext.get(nt.getKey());
            if (val == null)
                continue;
            ExtParam p = new ExtParam();
            p.extparamvalsetname = extSet;
            p.item = new JacksonBinder.ExtParam();
            p.item.kind = Kind.EXT;
            p.item.name = nt.getKey();
            p.item.type = nt.getValue();
            if (p.item.type.isTimeSeriesType()) {
                String tslabel = makeTSLabel(p.item.name, null, extSet);
                tsd.put(tslabel, ext.getTS(p.item.name));
                p.item.value = tslabel;
            } else {
                p.item.value = p.item.type.format(val, evs);
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
        m.item.kind = Kind.MET;
        m.item.name = met.getMetricName();
        m.item.type = type;
        m.item.expression = met.getSource();
        binder.getItems().add(m);
    }

    /**
     * Add input values for a scenario.
     * @param input values to add
     * @param scenario scenario name
     */
    public void add(SimulationInput input, String scenario) {
        EvaluationSetup evs = tsd.getEvaluationSetup();
        for (Map.Entry<String, Component> 
                comp : input.getNamespace().components.entrySet()) {
            for (Map.Entry<String, Type>
                    nt : comp.getValue().inputs.entrySet()) {
                Object val = input.get(comp.getKey(), nt.getKey());
                if (val == null)
                    continue;
                Input in = new Input();
                in.scenarioname = scenario;
                in.item = new JacksonBinder.Input();
                in.item.kind = Kind.IN;
                in.item.comp = comp.getKey();
                in.item.name = nt.getKey();
                in.item.type = nt.getValue();
                in.item.value = in.item.type.format(val, evs);
                binder.getItems().add(in);
            }
        }
    }
    
    /**
     * Add output values for a scenario.
     * This does not add the inputs.
     * @param res results to add
     * @param scenario name of the scenario
     */
    public void add(SimulationResults res, String scenario) {
        for (Map.Entry<String, Component> 
                comp : res.getNamespace().components.entrySet()) {
            for (Map.Entry<String, Type>
                    nt : comp.getValue().outputs.entrySet()) {
                TimeSeriesI val = res.getTS(comp.getKey(), nt.getKey());
                if (val == null)
                    continue;
                Output out = new Output();
                out.scenarioname = scenario;
                out.item = new JacksonBinder.Output();
                out.item.kind = Kind.OUT;
                out.item.comp = comp.getKey();
                out.item.name = nt.getKey();
                out.item.type = nt.getValue();
                String lbl = makeTSLabel(out.item.name, scenario, null);
                tsd.put(lbl, val);
                out.item.value = lbl;
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
            m.item.kind = Kind.MET;
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
    
    public void add(DecisionVariable dv) {
        //TODO
    }
    
    public void add(InputExpression in) {
        //TODO
    }
    
    public void add(Constraint c) {
        //TODO
    }
    
    public void add(ObjectiveExpression obj) {
        //TODO
    }

    /**
     * Add decision variable values.
     */
    public void add(DecisionValues dv, String scenario) {
        //TODO
    }
    
    public JacksonBinderScenario getBinder() {
        return binder;
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
