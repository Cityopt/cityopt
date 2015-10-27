package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
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

    @JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="kind")
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

        public abstract Kind getKind();
    }
    
    /** For Items that may reference {@link TimeSeriesData} */
    public interface TSRef {
        /** Key to use in {@link TimeSeriesData#getSeriesData} */
        @JsonIgnore
        public String tsKey();
    }
    
    public abstract static class Var extends Item {
        public Type type;
        public String unit;
        
        public String getType() {
            return (type == Type.DYNAMIC) ? "" : type.name;
        }
        public void setType(String name) {type = Type.getByName(name);}
        
        protected void addToNSMap(Map<String, Type> map) {
            if (map.putIfAbsent(name, type) != null) {
                throw new IllegalArgumentException(
                        "duplicate " + getKind() + " name " + getQName());
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
    
    public static class ExtParam extends Var implements TSRef {
        public String value;

        @Override
        public String tsKey() {
            return value != null ? value : name;
        }

        @Override
        public Kind getKind() {
            return Kind.EXT;
        }
    }
    
    public static class Input extends CompVar {
        public String value;
        @JsonProperty("expression")
        public String expr;
 
        @Override
        public Kind getKind() {
            return Kind.IN;
        }
    }
    
    public static class Output extends CompVar implements TSRef {
    	public String value;

    	@Override
        public String tsKey() {
            return value != null ? value : getQName();
        }

        @Override
        public Kind getKind() {
            return Kind.OUT;
        }
    }
    
    public static class DecisionVar extends CompVar {
        public String lower, upper;

        @Override
        public Kind getKind() {
            return Kind.DV;
        }
    }
    
    public static class Metric extends Var implements TSRef {
        public String expression, value;

        @Override
        public String tsKey() {
            return value != null ? value : name;
        }

        @Override
        public Kind getKind() {
            return Kind.MET;
        }
    }
    
    public static class Constr extends Item {
        public String expression, lower, upper;

        @Override
        public Kind getKind() {
            return Kind.CON;
        }
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
        
        @JsonIgnore
        public void setMaximize(boolean isMax) {
            type = isMax ? "max" : "min";
        }

        @Override
        public Kind getKind() {
            return Kind.OBJ;
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

    /**
     * Read from an input stream.
     */
    public JacksonBinder(
            ObjectReader reader, InputStream stream)
            throws JsonProcessingException, IOException {
        JacksonBinder bd = reader.readValue(stream);
        this.items = bd.getItems();
    }

    @JsonCreator
    public JacksonBinder(List<Item> items) {
        this.items = items;
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
     * Apply a builder.
     * @return the builder
     */
    public <Builder extends ImportBuilder>
    Builder buildWith(Builder builder)
            throws ParseException, ScriptException {
        for (Item it : items) {
            builder.add(it);
        }
        return builder;
    }

    /**
     * Apply a builder.
     * @return the builder
     */
    public <Builder extends RobustImportBuilder>
    Builder buildWith(Builder builder) {
        items.forEach(builder::add);
        return builder;
    }
    
    /**
     * Apply a UnitBuilder.  Another special case bacause it doesn't throw.
     * @return builder;
     */

    /**
     * Create a {@link Namespace} and populate it with our items.
     * @param evaluator
     * @param timeOrigin time stamp corresponding to t = 0
     * @return a new Namespace.
     */
    public Namespace makeNamespace(Evaluator evaluator, Instant timeOrigin) {
        checkNames();
        return buildWith(new NamespaceBuilder(evaluator, timeOrigin))
                .getResult();
    }
}
