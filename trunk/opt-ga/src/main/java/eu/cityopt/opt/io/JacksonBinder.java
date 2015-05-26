package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationModel;
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
         * Return the qualified name of this item.
         * Objects of the same kind must have unique qualified names.
         * @return either name or component.name if applicable.
         */
        @JsonIgnore
        public String getQName() {
            return name;
        }

        public Kind getKind() {
            return kind;
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
    }
    
    public static class Input extends CompVar {
        public String value;
        @JsonProperty("expression")
        public String expr;
    }
    
    public static class Output extends CompVar {
    	public String value;
    }
    
    public static class DecisionVar extends CompVar {
        public String lower, upper;
    }
    
    public static class Metric extends Var {
        public String expression, value;
        
        public void addToCollection(Collection<MetricExpression> metrics,
                Namespace ns) throws ScriptException {
            if (expression == null)
                throw new IllegalArgumentException("Missing expression");
            metrics.add(new MetricExpression(
                    null, name, expression, ns.evaluator));
        } 
    }
    
    public static class Constr extends Item {
        public String expression, lower, upper;
    }
    
    public static class Obj extends Item {
        public String expression, type;

        public static Boolean isMaximize(String type) {
            if ("max".equalsIgnoreCase(type))
                return true;
            else if ("min".equalsIgnoreCase(type))
                return false;
            else return null;
        }
        
        @JsonIgnore
        public Boolean isMaximize() {return isMaximize(type);}
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
        NameChecker chk = new NameChecker();
        items.forEach(chk::add);
    }        

    /**
     * Sort items by kind (in place).
     */
    public void sort() {
        Collections.sort(items,
                         (x1, x2) -> x1.getKind().compareTo(x2.getKind()));
    }
    
    /**
     * Create a {@link Namespace} and populate it with our items.
     * @param evaluator
     * @param timeOrigin time stamp corresponding to t = 0
     * @return a new Namespace.
     */
    public Namespace makeNamespace(Evaluator evaluator, Instant timeOrigin) {
        checkNames();
        NamespaceBuilder bld = new NamespaceBuilder(evaluator, timeOrigin);
        items.forEach(bld::add);
        return bld.getResult();
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
        ProblemBuilder bld = new ProblemBuilder(prob, tsData);
        for (Item item : items) {
            bld.add(item);
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
