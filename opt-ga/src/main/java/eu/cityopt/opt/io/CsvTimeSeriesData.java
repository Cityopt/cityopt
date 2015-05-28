package eu.cityopt.opt.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Type;

/**
 * Parser for time series data from CSV files. The CSV files should have a
 * header row with time series names as column names.  There must also be
 * a column named 'timestamp'.  The data rows contain time series values
 * in the named series columns, and either ISO-8601 timestamps or simulation
 * time values in the timestamp column.
 *
 * @author Hannu Rummukainen
 */
public class CsvTimeSeriesData implements TimeSeriesData {
    static final String TIMESTAMP_KEY = "timestamp";

    private Map<String, Series> seriesDatas = new HashMap<>();
    private final EvaluationSetup evaluationSetup;

    public CsvTimeSeriesData(EvaluationSetup evaluationSetup) {
        this.evaluationSetup = evaluationSetup;
    }

    /**
     * Reads time and value data for one or more time series from a CSV file.
     * Empty input files are ignored.
     */
    public void read(Path path) throws IOException, ParseException {
        try (InputStream stream = new FileInputStream(path.toFile())) {
            read(stream, path.toString());
        }
    }

    /**
     * Reads time and value data for one or more time series from a CSV
     * formatted input stream.  Empty input files are ignored.
     */
    public void read(InputStream inputStream, String streamName)
            throws IOException, ParseException {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        mapper.enable(CsvParser.Feature.TRIM_SPACES);
        MappingIterator<String[]> it =
                mapper.reader(String[].class).readValues(inputStream);
        if (!it.hasNext()) {
            return;
        }
        Map<String, Integer> dataIndices = new HashMap<>();
        int timeIndex = parseHeader(it.next(), streamName, 1, dataIndices);
        int nColumns = dataIndices.size() + 1;
        List<Double[]> data = new ArrayList<>();

        for (int rowNumber = 2; it.hasNext(); ++rowNumber) {
            String[] row = it.next();
            if (row.length > 0) {
                Double[] rowData = parseRow(row, streamName, rowNumber,
                        nColumns, timeIndex); 
                data.add(rowData);
            }
        }

        Double[] times = getColumn(data, timeIndex);
        for (Map.Entry<String, Integer> entry : dataIndices.entrySet()) {
            Double[] col = getColumn(data, entry.getValue()); 
            int[] rows = IntStream.range(0, col.length)
                    .filter(i -> col[i] != null).toArray();
            Series sd = new Series();
            sd.times = Arrays.stream(rows)
                    .mapToDouble(i -> times[i]).toArray();
            sd.values = Arrays.stream(rows)
                    .mapToDouble(i -> col[i]).toArray();
            seriesDatas.put(entry.getKey(), sd);
        }
    }

    @Override
    public Series getSeriesData(String seriesName) {
        return seriesDatas.get(seriesName);
    }

    private Double[] getColumn(List<Double[]> rows, int column) {
        int n = rows.size();
        Double[] data = new Double[n];
        for (int i = 0; i < n; ++i) {
            data[i] = rows.get(i)[column];
        }
        return data;
    }

    private int parseHeader(String[] row, String streamName, int rowNumber,
            Map<String, Integer> newDataIndices)
            throws ParseException {
        int timeIndex = -1;
        for (int i = 0; i < row.length; ++i) {
            if (TIMESTAMP_KEY.equals(row[i])) {
                if (timeIndex < 0) {
                    timeIndex = i;
                } else {
                    throw new ParseException(streamName + ":" + rowNumber
                            +": Duplicate column '" + TIMESTAMP_KEY + "'", 0);
                }
            } else {
                Integer old = newDataIndices.put(row[i], i);
                if (old != null) {
                    throw new ParseException(streamName + ":" + rowNumber
                            + ": Duplicate column '" + row[i] + "'", 0);
                }
            }
        }
        return timeIndex;
    }

    private Double[] parseRow(String[] row, String streamName, int rowNumber,
            int nColumns, int timeIndex) throws ParseException {
        if (row.length <= timeIndex) {
            throw new ParseException(streamName + ":" + rowNumber 
                    + ": missing time value", 0);
        }
        Double[] rowData = new Double[nColumns];
        int n = Math.min(row.length, nColumns);
        for (int i = 0; i < n; ++i) {
            if (i == timeIndex) {
                try {
                    rowData[i] = (Double) Type.TIMESTAMP.parse(
                            row[i], evaluationSetup);
                } catch (ParseException e) {
                    throw new ParseException(streamName + ":" + rowNumber
                            + ": Invalid timestamp '" + row[i]
                            + "'", 0);
                }
            } else {
                if (row[i] != null && !row[i].isEmpty()) {
                    try {
                        rowData[i] = Double.valueOf(row[i]);
                    } catch (NumberFormatException e) {
                        throw new ParseException(streamName + ":" + rowNumber
                                + ": Invalid value '" + row[i]
                                        + "' in column " + (i+1), 0);
                    }
                }
            }
        }
        return rowData;
    }

    @Override
    public EvaluationSetup getEvaluationSetup() {
        return evaluationSetup;
    }
}
