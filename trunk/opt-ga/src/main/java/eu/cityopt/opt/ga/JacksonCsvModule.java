package eu.cityopt.opt.ga;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class JacksonCsvModule extends AbstractModule {
    /**
     * Deserialise empty strings as nulls.
     * ACCEPT_EMPTY_STRING_AS_NULL_OBJECT does not do the trick for String
     * fields, only other classes.  This does.  Note that it becomes impossible
     * to represent the empty string in input.
     * @author ttekth
     *
     */
    @SuppressWarnings("serial")
    public static class NullStringDeserializer
    extends StdDeserializer<String> {
        public NullStringDeserializer() {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            String txt = _parseString(p, ctxt);
            return txt == null || txt.isEmpty() ? null : txt;
        }
    }
    
    @Provides
    @Singleton
    public static CsvMapper getCsvMapper() {
        CsvMapper m = new CsvMapper();
        m.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        m.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        m.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        m.registerModule(new SimpleModule().addDeserializer(
                String.class, new NullStringDeserializer()));
        return m;
    }
    
    @Provides
    @Named("problem")
    public static ObjectReader getReader(CsvMapper mapper) {
        return mapper.reader(JacksonBinder.class)
                .with(CsvSchema.emptySchema().withHeader());
    }
    
    @Provides
    @Named("problem")
    public static ObjectWriter getWriter(CsvMapper mapper) {
        /* Automatic schema creation does not appear to work for polymorphic
         * data.
         */
        CsvSchema sch = CsvSchema.builder()
                .addColumn("kind").addColumn("component")
                .addColumn("variable").addColumn("type").addColumn("value")
                .addColumn("lower").addColumn("upper").addColumn("expression")
                .build().withHeader();
        return mapper.writer(sch);
    }
    
    @Override
    protected void configure() { }
}
