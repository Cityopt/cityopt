package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.script.ScriptException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.DecisionVariable;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * A Jackson-mapped class for {@link OptimisationProblem} definition.
 * If you have a {@link SimulationModel}, the time origin (possibly from
 * the model object), external parameter time series in
 * {@link TimeSeriesData} and a JacksonBinder, you can construct an
 * OptimisationProblem.  JacksonBinder is (de)serialisable with Jackson.
 * 
 * @author ttekth
 */
@Singleton
public class JacksonBinder {
    /** Values in the kind column. */
    public enum Kind {
        EXT, DV, IN, OUT, MET, CON, OBJ;
        
        @JsonCreator
        public static Kind fromString(String str) {
            return valueOf(str.toUpperCase());
        }
        
        @JsonValue
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="kind",
                  visible=true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value=ExtParam.class, name="ext"),
        @JsonSubTypes.Type(value=Input.class, name="in"),
        @JsonSubTypes.Type(value=Output.class, name="out"),
        @JsonSubTypes.Type(value=DecisionVar.class, name="dv"),
        @JsonSubTypes.Type(value=Metric.class, name="met"),
        @JsonSubTypes.Type(value=Constr.class, name="con"),
        @JsonSubTypes.Type(value=Obj.class, name="obj")
    })
    public abstract static class Item {
        public Kind kind;
        public String name;

        /**
         * Add this item to a {@link Namespace}.
         */
        public abstract void addToNamespace(Namespace ns);
        
        /**
         * Add this item to an {@link OptimisationProblem}.
         */
        public abstract void addToProblem(
                OptimisationProblem prob, TimeSeriesData tsData)
                throws ParseException, ScriptException;
        
        /**
         * Return the qualified name of this item.
         * Objects of the same kind must have unique qualified names.
         * @return either name or component.name if applicable.
         */
        @JsonIgnore
        public String getQName() {
            return name;
        }
    }
    
    public abstract static class Var extends Item {
        public Type type;
        
        public String getType() {
            return (type == Type.DYNAMIC) ? "" : type.name;
        }
        public void setType(String name) {type = Type.getByName(name);}
        
        protected void addToNSMap(Map<String, Type> map) {
            if (map.putIfAbsent(name, type) != null) {
                throw new IllegalArgumentException(
                        "duplicate " + kind + " name " + getQName());
            }
        }
    }
   
    public abstract static class CompVar extends Var {
        @JsonProperty("component")
        public String comp;
        
        protected void addToNSComp(
                Namespace ns,
                Function<Namespace.Component, Map<String, Type>> getMap) {
            addToNSMap(getMap.apply(ns.getOrNew(comp)));
        }
        
        @Override
        public String getQName() {
            return comp != null ? comp + "." + name : name;
        }
    }
    
    public static class ExtParam extends Var {
        public String value;

        @Override
        public void addToNamespace(Namespace ns) {
            addToNSMap(ns.externals);
        }

        @Override
        public void addToProblem(OptimisationProblem prob,
                TimeSeriesData tsData) throws ParseException {
            ExternalParameters ext = prob.inputConst.getExternalParameters();
            if (type.isTimeSeriesType()) {
                Evaluator ev = prob.getNamespace().evaluator;
                TimeSeriesData.Series sd = tsData.getSeriesData(name);
                if (sd == null) {
                    throw new IllegalArgumentException(
                            "No time series data for external parameter "
                            + name);
                }
                TimeSeriesI ts = ev.makeTS(type, sd.times, sd.values);
                ext.put(name, ts);
            } else {
                ext.putString(name, value);
            }
        }
    }
    
    public static class Input extends CompVar {
        public String value;
        @JsonProperty("expression")
        public String expr;

        @Override
        public void addToNamespace(Namespace ns) {
            addToNSComp(ns, c -> c.inputs);
        }

        @Override
        public void addToProblem(
                OptimisationProblem prob, TimeSeriesData tsData)
                        throws ParseException, ScriptException {
            Namespace ns = prob.getNamespace();
            if (!(value == null ^ expr == null))
                throw new IllegalArgumentException(String.format(
                        "Either value or expr (not both) must be present"
                                + " on input %s,%s", comp, name));
            if (value != null) {
                prob.inputConst.putString(comp, name, value);
            } else {
                prob.inputExprs.add(new InputExpression(
                        comp, name, expr, ns.evaluator));
            }
        }
    }
    
    public static class Output extends CompVar {
        @Override
        public void addToNamespace(Namespace ns) {
            addToNSComp(ns, c -> c.outputs);
        }

        @Override
        public void addToProblem(
                OptimisationProblem prob, TimeSeriesData tsData) {}
    }
    
    public static class DecisionVar extends CompVar {
        public String lower, upper;

        @Override
        public void addToNamespace(Namespace ns) {
            if (comp != null) {
                addToNSComp(ns, c -> c.decisions);
            } else {
                addToNSMap(ns.decisions);
            }
        }

        @Override
        public void addToProblem(OptimisationProblem prob,
                TimeSeriesData tsData) throws ParseException {
            Namespace ns = prob.getNamespace();
            switch (type) {
            case DOUBLE:
            case INTEGER:
                Object
                    lb = (lower == null ? null : type.parse(lower, ns)),
                    ub = (upper == null ? null : type.parse(upper, ns));
                DecisionDomain
                dom = (type == Type.DOUBLE
                       ? NumericInterval.makeRealInterval(
                               (Double)lb, (Double)ub)
                       : NumericInterval.makeIntInterval(
                               (Integer)lb, (Integer)ub));
                prob.decisionVars.add(new DecisionVariable(comp, name, dom));
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported decision variable type " + type);
            }
        }
    }
    
    public static class Metric extends Var {
        public String expression;

        @Override
        public void addToNamespace(Namespace ns) {
            addToNSMap(ns.metrics);
        }

        public void addToCollection(Collection<MetricExpression> metrics,
                Namespace ns) throws ScriptException {
            if (expression == null)
                throw new IllegalArgumentException("Missing expression");
            metrics.add(new MetricExpression(
                    null, name, expression, ns.evaluator));
        } 

        @Override
        public void addToProblem(OptimisationProblem prob,
                TimeSeriesData tsData) throws ScriptException {
            addToCollection(prob.metrics, prob.getNamespace());
        } 
    }
    
    public static class Constr extends Item {
        public String expression, lower, upper;

        @Override
        public void addToNamespace(Namespace ns) {}

        @Override
        public void addToProblem(OptimisationProblem prob,
                TimeSeriesData tsData) throws ScriptException {
            if (expression == null)
                throw new IllegalArgumentException("Missing expression");
            else {
                double
                    lb = (lower != null ? Double.valueOf(lower)
                                        : Double.NEGATIVE_INFINITY),
                    ub = (upper != null ? Double.valueOf(upper)
                                        : Double.POSITIVE_INFINITY);
                prob.constraints.add(new Constraint(
                        null, name, expression, lb, ub,
                        prob.getNamespace().evaluator));
            }
        }
    }
    
    public static class Obj extends Item {
        public String expression, type;

        @Override
        public void addToNamespace(Namespace ns) {}

        @Override
        public void addToProblem(OptimisationProblem prob,
                TimeSeriesData tsData) throws ScriptException {
            Boolean is_max = isMaximize(type);
            if (expression == null)
                throw new IllegalArgumentException(
                        "Missing objective expression");
            if (is_max == null)
                throw new IllegalArgumentException(
                        "Invalid objective type " + type);
            prob.objectives.add(new ObjectiveExpression(
                    null, name, expression, is_max,
                    prob.getNamespace().evaluator));

        }

        public static Boolean isMaximize(String type) {
            if ("max".equalsIgnoreCase(type))
                return true;
            else if ("min".equalsIgnoreCase(type))
                return false;
            else return null;
        }
    }
 
    @JsonIgnore
    final private List<Item> items;

    @JsonIgnore
    final private TimeSeriesData tsData;
    
    /**
     * Read from a file.
     */
    @Inject
    public JacksonBinder(
            @Named("problem") ObjectReader reader,
            @Named("problem") Path file,
            TimeSeriesData tsData)
            throws JsonProcessingException, IOException {
        JacksonBinder bd = reader.readValue(file.toFile());
        this.items = bd.getItems();
        this.tsData = tsData;
    }

    /**
     * Read from an input stream.
     */
    public JacksonBinder(
            ObjectReader reader, InputStream stream, TimeSeriesData tsData)
            throws JsonProcessingException, IOException {
        JacksonBinder bd = reader.readValue(stream);
        this.items = bd.getItems();
        this.tsData = tsData;
    }

    @JsonCreator
    public JacksonBinder(List<Item> items) {
        this.items = items;
        this.tsData = null;
    }

    @JsonValue
    public List<Item> getItems() {return items;}

    /**
     * Check for name uniqueness.
     * @throws IllegalArgumentException on duplicate names
     */
    public void checkNames() {
        Map<Kind, Set<String>> names = new HashMap<>();
        for (Item it : items) {
            Set<String> ns = names.computeIfAbsent(
                    it.kind, k -> new HashSet<>());
            String qn = it.getQName();
            if (!ns.add(qn)) {
                throw new IllegalArgumentException(
                        "duplicate " + it.kind + " name " + qn);
            }
        }
    }        

    /**
     * Sort items by kind (in place).
     */
    public void sort() {
        Collections.sort(items, (x1, x2) -> x1.kind.compareTo(x2.kind));
    }
    
    /**
     * Create a {@link Namespace} and populate it with our items.
     * @param evaluator
     * @param timeOrigin time stamp corresponding to t = 0
     * @return a new Namespace.
     */
    public Namespace makeNamespace(Evaluator evaluator, Instant timeOrigin) {
        checkNames();
        Namespace ns = new Namespace(evaluator, timeOrigin, true);
        items.forEach(item -> item.addToNamespace(ns));
        return ns;
    }
    
    /**
     * Add our items to an {@link OptimisationProblem}.
     * The problem must have been constructed with a {@link Namespace}
     * containing our items.
     * 
     * @param prob the problem to modify
     * @see #makeNamespace
     */
    public void addToProblem(
            OptimisationProblem prob)
                    throws ParseException, ScriptException {
        for (Item item : items) {
            item.addToProblem(prob, tsData);
        }
    }

    /**
     * Add metrics to a collection.
     * 
     * @param metrics the collection to modify
     * @param namespace a namespace containing our items
     * @see #makeNamespace(Evaluator, Instant)
     */
    public void addMetrics(Collection<MetricExpression> metrics,
            Namespace namespace) throws ScriptException {
        for (Item item : items) {
            if (item instanceof Metric) {
                ((Metric) item).addToCollection(metrics, namespace);
            }
        }
    }

    //TODO OptimisationProblem -> JacksonBinder (serialisation)
}
