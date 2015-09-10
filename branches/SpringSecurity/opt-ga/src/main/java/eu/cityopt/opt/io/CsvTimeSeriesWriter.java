package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;

import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Type;

/**
 * CSV output of {@link TimeSeriesData}. 
 * @author ttekth
 */
public class CsvTimeSeriesWriter {
    private final ObjectWriter writer;
    private boolean numeric = false;

    @Inject
    public CsvTimeSeriesWriter(@Named("timeSeries") ObjectWriter writer) {
        this.writer = writer.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }

    private static <T> void writeRow(
            SequenceWriter seq, T time, Stream<T> values)
                    throws IOException {
        seq.write(Stream.concat(Stream.of(time), values)
                  .collect(Collectors.toList()));
    }
    
    private void writeRow(
            SequenceWriter seq, double time, Double[] values,
            EvaluationSetup es) throws IOException {
        if (isNumeric()) {
            writeRow(seq, time, Arrays.stream(values));
        } else {
            writeRow(seq, Type.TIMESTAMP.format(time, es),
                     Arrays.stream(values).map(
                             x -> x == null ? null
                                            : Type.DOUBLE.format(x, es)));
        }
    }

    /**
     * Write time series data to a output stream.
     * This does not close the stream.
     */
    public void write(OutputStream ostr, TimeSeriesData tsd)
            throws IOException {
        MergedTimeSeries merge = new MergedTimeSeries(tsd);
        try (SequenceWriter seq = writer.writeValuesAsArray(ostr)) {
            writeRow(seq, TimeSeriesData.TIMESTAMP_KEY,
                     merge.getNames().stream());
            Double[] values = new Double[merge.getNames().size()];
            Double time = null;
            EvaluationSetup es = tsd.getEvaluationSetup();
            for (MergedTimeSeries.Entry ent : merge) {
                if (time == null) {
                    time = ent.getTime();
                } else if (time != ent.getTime()) {
                    writeRow(seq, time, values, es);
                    Arrays.fill(values, null);
                    time = ent.getTime();
                }
                values[ent.getSeries()] = ent.getValue();
            }
            if (time != null) {
                writeRow(seq, time, values, es);
            }
        }
    }

    /**
     * Return whether numeric mode is on.
     * @see #setNumeric
     */
    public boolean isNumeric() {
        return numeric;
    }

    /**
     * Enable or disable numeric mode.
     * In default mode (numeric = false) times and values are formatted
     * with {@link Type#format} and passed as strings to Jackson.  Times
     * appear as ISO 8601 timestamps.  In numeric mode (numeric = true)
     * times and values are passed as numbers to Jackson, which formats them.
     * Times appear as seconds from the origin defined by TimeSeriesData.
     * <p>
     * This method is not thread-safe: if you are going to share the writer,
     * set the desired mode before sharing.
     */
    public void setNumeric(boolean numeric) {
        this.numeric = numeric;
    }
}
