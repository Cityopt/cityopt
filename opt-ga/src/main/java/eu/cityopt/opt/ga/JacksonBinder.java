package eu.cityopt.opt.ga;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

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
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.opt.OptimisationProblem;

/**
 * A Jackson-mapped class for {@link OptimisationProblem} definition.
 * If you have a {@link SimulationModel}, the time origin (possibly from
 * the model object) and a JacksonBinder, you can construct an
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

        /**
         * Add this item to a {@link Namespace}.
         */
        public abstract void addToNamespace(Namespace ns);
        
        /**
         * Add this item to an {@link OptimisationProblem}.
         */
        public abstract void addToProblem(OptimisationProblem prob)
                throws ParseException, ScriptException;
    }
    
    public abstract static class Var extends Item {
        @JsonProperty("variable")
        public String var;
        public Type type;
        
        public String getType() {return type.name;}
        public void setType(String name) {type = Type.getByName(name);}
    }
    
    public abstract static class CompVar extends Var {
        @JsonProperty("component")
        public String comp;
    }
    
    public static class ExtParam extends Var {
        public String value;

        @Override
        public void addToNamespace(Namespace ns) {
            ns.externals.put(var, type);
        }

        @Override
        public void addToProblem(OptimisationProblem prob)
                throws ParseException {
            ExternalParameters ext = prob.inputConst.getExternalParameters();
            if (type.isTimeSeriesType())
                return; // Not supported.
            ext.putString(var, value);
        }
    }
    
    public static class Input extends CompVar {
        public String value;
        @JsonProperty("expression")
        public String expr;

        @Override
        public void addToNamespace(Namespace ns) {
            ns.getOrNew(comp).inputs.put(var, type);
        }

        @Override
        public void addToProblem(OptimisationProblem prob)
                throws ParseException, ScriptException {
            Namespace ns = prob.getNamespace();
            if (!(value == null ^ expr == null))
                throw new IllegalArgumentException(String.format(
                        "Either value or expr (not both) must be present"
                                + " on input %s,%s", comp, var));
            if (value != null) {
                prob.inputConst.putString(comp, var, value);
            } else {
                prob.inputExprs.add(new InputExpression(
                        comp, var, expr, ns.evaluator));
            }
        }
    }
    
    public static class Output extends CompVar {
        @Override
        public void addToNamespace(Namespace ns) {
            ns.getOrNew(comp).outputs.put(var, type);
        }

        @Override
        public void addToProblem(OptimisationProblem prob) {}
    }
    
    public static class DecisionVar extends CompVar {
        public String lower, upper;

        @Override
        public void addToNamespace(Namespace ns) {
            (comp == null ? ns.decisions : ns.getOrNew(comp).decisions)
                    .put(var, type);
        }

        @Override
        public void addToProblem(OptimisationProblem prob)
                throws ParseException {
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
                prob.decisionVars.add(new DecisionVariable(comp, var, dom));
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
            ns.metrics.put(var, type);
        }

        @Override
        public void addToProblem(OptimisationProblem prob)
                throws ScriptException {
            Namespace ns = prob.getNamespace();
            if (expression == null)
                throw new IllegalArgumentException("Missing expression");
            prob.metrics.add(new MetricExpression(
                    prob.metrics.size(), var, expression, ns.evaluator));
        } 
    }
    
    public static class Constr extends Item {
        public String expression, lower, upper;

        @Override
        public void addToNamespace(Namespace ns) {}

        @Override
        public void addToProblem(OptimisationProblem prob)
                throws ScriptException {
            if (expression == null)
                throw new IllegalArgumentException("Missing expression");
            else {
                double
                    lb = (lower != null ? Double.valueOf(lower)
                                        : Double.NEGATIVE_INFINITY),
                    ub = (upper != null ? Double.valueOf(upper)
                                        : Double.POSITIVE_INFINITY);
                String id = "con" + prob.constraints.size();
                prob.constraints.add(new Constraint(
                        id, expression, lb, ub,
                        prob.getNamespace().evaluator));
            }
        }
    }
    
    public static class Obj extends Item {
        public String expression, type;

        @Override
        public void addToNamespace(Namespace ns) {}

        @Override
        public void addToProblem(OptimisationProblem prob)
                throws ScriptException {
            Boolean is_max = isMaximize(type);
            if (expression == null)
                throw new IllegalArgumentException(
                        "Missing objective expression");
            if (is_max == null)
                throw new IllegalArgumentException(
                        "Invalid objective type " + type);
            String id = "obj" + prob.objectives.size(); 
            prob.objectives.add(new ObjectiveExpression(
                    id, expression, is_max,
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
    
    /**
     * Read from a file.
     */
    @Inject
    public JacksonBinder(
            @Named("problem") ObjectReader reader,
            @Named("problem") Path file)
            throws JsonProcessingException, IOException {
        JacksonBinder bd = reader.readValue(file.toFile());
        this.items = bd.getItems();
    }
    
    @JsonCreator
    public JacksonBinder(List<Item> items) {
        this.items = items;
    }

    @JsonValue
    public List<Item> getItems() {return items;}

    /**
     * Sort items by kind (in place).
     */
    public void sort() {
        Collections.sort(items, (x1, x2) -> x1.kind.compareTo(x2.kind));
    }
    
    /**
     * Create a {@link Namespace} and populate it with our items.
     * @param timeOrigin time stamp corresponding to t = 0
     * @return a new Namespace.
     */
    public Namespace makeNamespace(
            Instant timeOrigin) {
        Namespace ns = new Namespace(new Evaluator(), timeOrigin, true);
        items.forEach(item -> item.addToNamespace(ns));
        return ns;
    }
    
    /**
     * Add our items to an {@link OptimisationProblem}.
     * The problem must have been constructed with a {@link Namespace}
     * containing our items.
     * 
     * @param prob the problem to modify
     * @see #makeNamespace(Instant)
     */
    public void addToProblem(
            OptimisationProblem prob)
                    throws ParseException, ScriptException {
        for (Item item : items) {
            item.addToProblem(prob);
        }
    }
    
    //TODO OptimisationProblem -> JacksonBinder (serialisation)
    //TODO time series (in external parameters)
}
