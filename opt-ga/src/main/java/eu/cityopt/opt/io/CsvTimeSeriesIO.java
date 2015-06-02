package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

/**
 * CSV I/O of {@link TimeSeriesData}. 
 * Static utility methods.
 * @author ttekth
 *
 */
public abstract class CsvTimeSeriesIO {
    //TODO Convert CsvTimeSeriesData to static methods and move here.
    
    private static <T> void writeRow(
            SequenceWriter seq, T time, Collection<T> values)
                    throws IOException {
        seq.write(Stream.concat(Stream.of(time), values.stream())
                  .collect(Collectors.toList()));
        
    }

    private static <T> void writeRow(
            SequenceWriter seq, T time, T[] values)
                    throws IOException {
        seq.write(Stream.concat(Stream.of(time), Arrays.stream(values))
                  .collect(Collectors.toList()));
    }

    public static void write(
            CsvMapper mapper, TimeSeriesData tsd, OutputStream ostr)
            throws IOException {
        MergedTimeSeries merge = new MergedTimeSeries(tsd);
        try (SequenceWriter seq = mapper.writer()
                    .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                    .writeValues(ostr)) {
            writeRow(seq, TimeSeriesData.TIMESTAMP_KEY, merge.getNames());
            Double[] values = new Double[merge.getNames().size()];
            Double time = null;
            for (MergedTimeSeries.Entry ent : merge) {
                if (time == null) {
                    time = ent.getTime();
                } else if (time != ent.getTime()) {
                    writeRow(seq, time, values);
                    Arrays.fill(values, null);
                    time = ent.getTime();
                }
                values[ent.getSeries()] = ent.getValue();
            }
            if (time != null) {
                writeRow(seq, time, values);
            }
        }
    }
}
