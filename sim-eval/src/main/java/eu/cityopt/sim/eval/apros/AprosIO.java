package eu.cityopt.sim.eval.apros;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Doubles;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.Type;

/**
 * Support for reading and writing Apros IO_SETs.
 */
public class AprosIO {
    public static List<String[]> parseResultHeader(BufferedReader in)
            throws IOException {
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
    
        List<String[]> variables = new ArrayList<>();
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
            variables.add(line);
        }
        return variables;
    }

    public static void readResultFile(BufferedReader in, SimulationResults res) 
            throws IOException {
        Namespace ns = res.getNamespace();
        List<String []> variables = parseResultHeader(in);
        int n_cols = variables.size() + 1;
        List<String []> names = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        List<Type> types = new ArrayList<>();
        List<List<Double>> vals = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        String[] line;
        for (int i = 1; i != n_cols; ++i) {
            line = variables.get(i - 1);
            System.out.printf("Output: %s.%s%n", line[0], line[1]);
            Namespace.Component comp = ns.components.get(line[0]);
            Type type = comp != null ? comp.outputs.get(line[1]) : null;
            if (type != null) {
                names.add(line);
                cols.add(i);
                types.add(type);
                vals.add(new ArrayList<>());
            }
        } 
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
            String[] n = names.get(i);
            double[] t = Doubles.toArray(times); 
            res.put(n[0], n[1],
                    ns.evaluator.makeTS(types.get(i), t, 
                            Doubles.toArray(vals.get(i))));
        }
    }

    private static String[] readAndSplit(BufferedReader rd)
            throws IOException {
        String line = rd.readLine(); 
        return line == null ? null : line.trim().split("[ \t]+");
    }
    
    public static BufferedReader makeReader(InputStream str) {
        return new BufferedReader(new InputStreamReader(str));
    }
}
