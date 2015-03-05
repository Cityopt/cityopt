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

/**
 * Bindings for reading CSV with Jackson.  These configure Jackson-csv
 * appropriately for our purposes.
 * @author ttekth
 *
 */
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
    
    /**
     * Create a mapper.
     * Different kinds of objects require different fields, hence we
     * accept and ignore unknown columns.  Empty cells are mapped to
     * null also when the target is String.
     * @return
     */
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
    
    /**
     * Create a reader.
     * A header row is required in the CSV.  Columns are identified by their
     * names in the header.  Column order is thus irrelevant.
     * @param mapper
     * @return
     */
    @Provides
    @Named("problem")
    public static ObjectReader getReader(CsvMapper mapper) {
        return mapper.reader(JacksonBinder.class)
                .with(CsvSchema.emptySchema().withHeader());
    }
    
    /**
     * Create a writer.
     * This uses a fixed schema: always the same columns in the same order.
     * This method needs to be modified if new columns are required.
     * @param mapper
     * @return
     */
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
