package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.inject.Singleton;

import eu.cityopt.opt.io.JacksonBinder.Input;
import eu.cityopt.opt.io.JacksonBinder.Kind;
import eu.cityopt.opt.io.JacksonBinder.Metric;
import eu.cityopt.opt.io.JacksonBinder.Output;

@Singleton
public class JacksonBinderScenario {
	
    public static class ScenarioItem {

    	public String scenarioname;
    	public String extparamvalsetname;
    	
    	@JsonIgnore
    	public JacksonBinder.Item item;
    	
//    	@JsonUnwrapped
//    	public JacksonBinder.Item item;    	 	
//        @JsonCreator
//        public ScenarioItem(
//        		@JsonProperty("scenarioname") String scenarioname, 
//        		@JsonProperty("extparamvalsetname") String extparamvalsetname,
//        		@JsonUnwrapped JacksonBinder.Item item) {
//        	this.scenarioname = scenarioname;
//            this.extparamvalsetname = extparamvalsetname;
//            this.item = item;
//        }
       

    	@JsonCreator
    	public ScenarioItem(
        		@JsonProperty("scenarioname") String scenarioname, 
        		@JsonProperty("extparamvalsetname") String extparamvalsetname,
        		@JsonProperty("kind") String kind, 
        		@JsonProperty("component") String component, 
        		@JsonProperty("name") String name,
        		@JsonProperty("type") String type, 
        		@JsonProperty("value") String value, 
        		@JsonProperty("lower") String lower, 
        		@JsonProperty("upper") String upper,
        		@JsonProperty("expression") String expression) {
        	this.scenarioname = scenarioname;
            this.extparamvalsetname = extparamvalsetname;

            Kind k = Kind.fromString(kind);
            
            switch(k){
            	case IN: 
            		Input in = new JacksonBinder.Input();
            		in.value = value;
            		in.expr = expression;
            		in.comp = component;
            		in.setType(type);
            		in.kind = Kind.IN;
            		in.name = name;
            		this.item = in;            		
            		break;
            	case MET: 
            		Metric met = new JacksonBinder.Metric();
            		met.expression = expression;
            		met.setType(type);
            		met.name = name;
            		met.kind = Kind.MET;
            		met.value = value;
            		this.item = met;            		
            		break;
            	case OUT: 
            		Output out = new JacksonBinder.Output();
            		out.setType(type);
            		out.comp = component;
            		out.name = name;
            		out.kind = Kind.OUT;
            		out.value = value;
            		this.item = out;	
            		break;
            	default:
            		this.item = null;
//            		throw new IllegalArgumentException();
            }
    	}

        public Kind getKind() {
            return item.kind;
        }

        public JacksonBinder.Item getItem() {
            return item;
        }            
    } 

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
        Collections.sort(items, (x1, x2) -> x1.item.kind.compareTo(x2.item.kind));
    }

}

