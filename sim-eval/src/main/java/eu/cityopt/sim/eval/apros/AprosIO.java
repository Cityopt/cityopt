package eu.cityopt.sim.eval.apros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.primitives.Doubles;

import eu.cityopt.sim.eval.MergedTimeSeries;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.PiecewiseFunction;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.TimeSeriesData;

/**
 * Support for reading and writing Apros IO_SETs.
 */
public class AprosIO {
    /**
     * Return the variables contained in an Apros data file.
     * The first column is always time and is not included in the return value.
     * The file position will be left at the beginning of data.
     * @param in Input reader
     * @return a list of (module, attribute) pairs.
     */
    public static List<Pair<String, String>>
    parseResultHeader(BufferedReader in) throws IOException {
        String [] line = readAndSplit(in);
        int n_cols = 0;
        if (line != null && line.length == 1) {
            try {
                n_cols = Integer.parseInt(line[0]);
            } catch (NumberFormatException e) {}
        }
        if (n_cols < 1) {
            throw new IOException("Bad first line");
        }

        // First column is always time.
        line = readAndSplit(in);
        if (line == null) {
            throw new IOException("Premature EOF");
        }
        if (line.length != 2
                || ! line[0].equals("SIMULATION")
                || ! line[1].equals("TIME")) {
            throw new IOException("Expected SIMULATION TIME, got "
                + String.join(" ",  line));
        }

        List<Pair<String, String>> variables = new ArrayList<>();
        for (int i = 1; i != n_cols; ++i) {
            line = readAndSplit(in);
            if (line == null) {
                throw new IOException("Premature EOF");
            }
            if (line.length != 2) {
                throw new IOException(
                        "Bad header line " + (i + 1) + ": "
                        + line.length + " columns");
            }
            variables.add(Pair.of(line[0], line[1]));
        }
        return variables;
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    /**
     * Read and process an Apros data file.
     * The file will be read to the end even if all variables are filtered
     * out.  The purpose of the filter is to avoid parsing uninteresting
     * columns.  Closing the reader is up to the caller.
     * @param in Input reader
     * @param filter Return whether a variable is interesting.  If null
     *   all variables are interesting
     * @param consumer Called for interesting variables with name, times
     *   and values
     */
    public static void readFile(
            BufferedReader in, Predicate<Pair<String, String>> filter,
            TriConsumer<Pair<String, String>, double[], double[]> consumer)
                    throws IOException {
        List<Pair<String,String>> contents = parseResultHeader(in);
        int n_cols = contents.size() + 1;
        List<Pair<String, String>> names = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        List<List<Double>> vals = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        for (int i = 1; i != n_cols; ++i) {
            Pair<String, String> var = contents.get(i - 1);
            System.out.printf("File variable: %s.%s%n",
                              var.getLeft(), var.getRight());
            if (filter == null || filter.test(var)) {
                names.add(var);
                cols.add(i);
                vals.add(new ArrayList<>());
            }
        }
        String[] line;
        for (int ln = n_cols + 2;
                (line = readAndSplit(in)) != null;
                ++ln) {
            if (line.length < n_cols) {
                throw new IOException(
                        "Line " + ln + " too short: " + line.length
                        + " < " + n_cols + " columns");
            }
            times.add(Double.parseDouble(line[0]));
            for (int i = 0; i != cols.size(); ++i) {
                String v = line[cols.get(i)];
                try {
                    vals.get(i).add(Double.parseDouble(v));
                } catch (NumberFormatException e) {
                    throw new IOException("Line " + ln
                            + ": invalid value '" + v + "'");
                }
            }
        }
        for (int i = 0; i != names.size(); ++i) {
            Pair<String, String> n = names.get(i);
            double[] t = Doubles.toArray(times);
            consumer.accept(n, t, Doubles.toArray(vals.get(i)));
        }
    }

    public static void readResultFile(BufferedReader in, SimulationResults res)
            throws IOException {
        Namespace ns = res.getNamespace();
        readFile(in,
                 op -> {
                     Namespace.Component c = ns.components.get(op.getLeft());
                     return c != null && c.outputs.containsKey(op.getRight());
                 },
                 (op, t, v) -> res.put(
                         op.getLeft(), op.getRight(),
                         ns.evaluator.makeTS(
                                 ns.components.get(op.getLeft())
                                   .outputs.get(op.getRight()),
                                 t, v)));
    }

    private static String[] readAndSplit(BufferedReader rd)
            throws IOException {
        String line = rd.readLine();
        return line == null ? null : line.trim().split("[ \t]+");
    }

    /**
     * Produce a BufferedReader from an InputStream.
     * Closing the reader appears to close the stream, although this is not
     * well documented.
     */
    public static BufferedReader makeReader(InputStream str) {
        return new BufferedReader(new InputStreamReader(str));
    }

    public static BufferedReader makeReader(Path path) throws IOException {
        /* Not sure what the right charset is but I'd bet against UTF-8. */
        return Files.newBufferedReader(path, StandardCharsets.US_ASCII);
    }

    /**
     * Write a time series input file.
     * The file can be read into Apros with IO_SET.  The inputs listed in
     * vars are written in that order.  It should correspond to DB_NAMES
     * configuration in the Apros model.  It is assumed that EXT_NAMES is not
     * used: inputs appear as "COMPONENT NAME" in the file.  Values are
     * retrieved from input; all the listed variables must be time series.
     * <p>
     * The time series are clipped to the simulation period according
     * to their type.  Beyond that the types have no effect; only the points
     * are written out and Apros interprets them in its own fashion.
     *
     * @param str Stream to write into.  May or may not be closed by us.
     * @param vars Names of inputs to write
     * @param input Input data
     */
    public static void writeTsInput(
            OutputStream str, List<Pair<String, String>> vars,
            SimulationInput input) {
        double
            start = (Double)input.get(Namespace.CONFIG_COMPONENT,
                                      Namespace.CONFIG_SIMULATION_START),
            end = (Double)input.get(Namespace.CONFIG_COMPONENT,
                                    Namespace.CONFIG_SIMULATION_END);
        TimeSeriesData tsd = new TimeSeriesData(input.getEvaluationSetup());
        List<String> anames = new ArrayList<>(vars.size());
        for (Pair<String, String> inp : vars) {
            String
                comp = inp.getLeft(), name = inp.getRight(),
                aname = comp + " " + name;
            if (tsd.getSeries(aname) != null) {
                throw new IllegalArgumentException(
                        "AprosIO.writeTsInput: duplicate column " + aname);
            }
            anames.add(aname);
            /* Clip series to simulation period.
               This keeps the file smaller.  Also, I hear Apros is buggy
               and acts funny if an input file begins before the simulation
               period. */
            PiecewiseFunction
                pws = input.getTS(comp, name).internalFunction()
                        .slice(start, end);
            tsd.put(aname, pws.getTimes(), pws.getValues());
        }
        MergedTimeSeries merge = new MergedTimeSeries(anames, tsd);
        try (PrintStream out = new PrintStream(str)) {
            out.println(merge.getNames().size() + 1);
            out.println("SIMULATION TIME");
            merge.getNames().forEach(out::println);
            double time = start;
            double[] values = new double[merge.getNames().size()];
            for (MergedTimeSeries.Entry ent : merge) {
                if (time != ent.getTime()) {
                    writeRow(out, time, values);
                    time = ent.getTime();
                }
                values[ent.getSeries()] = ent.getValue();
            }
            writeRow(out, time, values);
        }
    }

    private static void
    writeRow(PrintStream out, double time, double[] values) {
        out.printf("%G", time);
        for (double v : values) {
            out.printf("\t%G", v);
        }
        out.println();
    }
}
