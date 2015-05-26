package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;

import eu.cityopt.opt.io.JacksonBinder.Kind;

@Singleton
public class JacksonBinderScenario {
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
    public abstract static class ScenarioItem {
    	public String scenarioname;
    	public String extparamvalsetname;
    	
    	public abstract JacksonBinder.Item getItem();
        public Kind getKind() {return getItem().getKind();}
    }

    public abstract static class Item<Type extends JacksonBinder.Item>
    extends ScenarioItem {
        @JsonUnwrapped
        public Type item;
        
        @Override
        public Type getItem() {return item;}
    }
    
    public static class ExtParam extends Item<JacksonBinder.ExtParam> {}
    public static class Input extends Item<JacksonBinder.Input> {}
    public static class Output extends Item<JacksonBinder.Output> {}
    public static class DecisionVar extends Item<JacksonBinder.DecisionVar> {}
    public static class Metric extends Item<JacksonBinder.Metric> {}
    public static class Constr extends Item<JacksonBinder.Constr> {}
    public static class Obj extends Item<JacksonBinder.Obj> {}

    @JsonIgnore
    final private List<ScenarioItem> items;
    
    /**
     * Read from a file.
     */
    @Inject
    public JacksonBinderScenario(
            @Named("problemScen") ObjectReader reader,
            @Named("scenario") Path file)
            throws JsonProcessingException, IOException {
        JacksonBinderScenario bd = reader.readValue(file.toFile());
        this.items = bd.getItems();
    }
    
    /**
     * Read from an input stream.
     */
    public JacksonBinderScenario(
            ObjectReader reader, InputStream stream)
            throws JsonProcessingException, IOException {
    	JacksonBinderScenario bd = reader.readValue(stream);
        this.items = bd.getItems();
    }
    
    @JsonValue
    public List<ScenarioItem> getItems() {return items;}
    
//    @JsonIgnore
//    final private TimeSeriesData tsData;
    
    @JsonCreator
    public JacksonBinderScenario(List<ScenarioItem> items) {
        this.items = items;
    }
    
    /**
     * Sort items by kind (in place).
     */
    public void sort() {
        Collections.sort(
                items, (x1, x2) -> x1.getKind().compareTo(x2.getKind()));
    }

}

