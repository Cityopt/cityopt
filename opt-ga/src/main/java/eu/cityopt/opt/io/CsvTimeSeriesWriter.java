package eu.cityopt.opt.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Type;

/**
 * CSV output of {@link TimeSeriesData}. 
 * @author ttekth
 */
public class CsvTimeSeriesWriter {
    private final CsvMapper mapper;
    private boolean numeric = false;

    @Inject
    public CsvTimeSeriesWriter(CsvMapper mapper) {
        this.mapper = mapper;
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
                             x -> x != null ? Type.DOUBLE.format(x, es) : ""));
        }
    }

    /**
     * Write time series data to a output stream.
     * This does not close the stream.
     */
    public void write(OutputStream ostr, TimeSeriesData tsd)
            throws IOException {
        MergedTimeSeries merge = new MergedTimeSeries(tsd);
        try (SequenceWriter seq = mapper.writer()
                    .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                    .writeValues(ostr)) {
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

    public boolean isNumeric() {
        return numeric;
    }

    public void setNumeric(boolean numeric) {
        this.numeric = numeric;
    }
}
