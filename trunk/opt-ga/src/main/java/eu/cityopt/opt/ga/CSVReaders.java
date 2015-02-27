package eu.cityopt.opt.ga;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.HashMap;

import javax.script.ScriptException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import eu.cityopt.sim.eval.Constraint;
import eu.cityopt.sim.eval.DecisionDomain;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.InputExpression;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.NumericInterval;
import eu.cityopt.sim.eval.ObjectiveExpression;
import eu.cityopt.sim.eval.Type;

/**
 * Static methods for reading CSV files.
 * @author ttekth
 *
 */
public class CSVReaders {
    /** Column names expected in various CSV files. */
    public class Cols {
        public static final String
            comp = "component",
            var = "variable",
            kind = "kind",
            type = "type",
            value = "value",
            expr = "expression",
            lb = "lower",
            ub = "upper";
    }
    
    /** Values in the kind column. */
    public enum Kind {
        EXT, IN, OUT, DV, MET, CON, OBJ;
        
        public boolean matches(String str) {
            return toString().equalsIgnoreCase(str);
        }
        
        public static Kind fromString(String str) {
            for (Kind k : values()) {
                if (k.matches(str))
                    return k;
            }
            throw new IllegalArgumentException("No such kind: " + str);
        }
    }
    
    public static Boolean isMaximize(String type) {
        if ("max".equalsIgnoreCase(type))
            return true;
        else if ("min".equalsIgnoreCase(type))
            return false;
        else return null;
    }
    
    /** CSV format used in all files. */
    public static final CSVFormat fmt = CSVFormat.DEFAULT
            .withHeader().withNullString("");
    
    public static Namespace readNamespace(Instant timeOrigin, Path file)
            throws IOException {
        Namespace ns = new Namespace(new Evaluator(), timeOrigin, true);
        try (Reader rd = Files.newBufferedReader(file)) {
            for (CSVRecord r : fmt.parse(rd)) {
                String
                    comp = r.get(Cols.comp),
                    var = r.get(Cols.var),
                    type = r.get(Cols.type);
                switch (Kind.fromString(r.get(Cols.kind))) {
                case EXT:
                    ns.externals.put(var, Type.getByName(type));
                    break;
                case IN:
                    ns.getOrNew(comp).inputs.put(var, Type.getByName(type));
                    break;
                case OUT:
                    ns.getOrNew(comp).outputs.put(var, Type.getByName(type));
                    break;
                case DV:
                    (comp == null ? ns.decisions
                                  : ns.getOrNew(comp).decisions)
                    .put(var, Type.getByName(type));
                    break;
                case MET:
                    ns.metrics.put(var, Type.getByName(type));
                    break;
                default:
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Error parsing CSV", e);
        }
        return ns;
    }
    
    public static void readProblemFile(OptimisationProblem p, Path file)
            throws IOException {
        Namespace ns = p.inputConst.getNamespace();
        try (Reader rd = Files.newBufferedReader(file)) {
            CSVParser parser = fmt.parse(rd);
            for (CSVRecord r : parser) {
                readRecord(Kind.fromString(r.get(Cols.kind)),
                           parser.getRecordNumber(), r, ns, p);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Error parsing CSV", e);
        }
    }
    
    private static void readRecord(
            Kind kind, long recordNumber, CSVRecord r, Namespace ns,
            OptimisationProblem p) throws IOException {
        ExternalParameters ext = p.inputConst.getExternalParameters();
        String comp, var, value, expr;
        Type t;
        try {
            switch (kind) {
            case EXT:
                var = r.get(Cols.var);
                t = ns.externals.get(var);
                if (t == null)
                    throw new IOException(
                            "Unknown external parameter " + var);
                if (t.isTimeSeriesType())
                    break; // Not supported
                ext.putString(var, r.get(Cols.value));
                break;
            case IN:
                comp = r.get(Cols.comp);
                var = r.get(Cols.var);
                value = r.get(Cols.value);
                expr = r.get(Cols.expr);
                if (ns.getInputType(comp, var) == null)
                    throw new IOException(String.format(
                            "Unknown input %s.%s", comp, var));
                if (!(value == null ^ expr == null))
                    throw new IOException(String.format(
                            "Either value or expr (not both) must be present"
                                    + " on input %s,%s", comp, var));
                if (value != null) {
                    p.inputConst.putString(comp, var, value);
                } else {
                    p.inputExprs.add(new InputExpression(
                            comp, var, expr, ns.evaluator));
                }
                break;
            case DV:
                comp = r.get(Cols.comp);
                var = r.get(Cols.var);
                t = ns.getDecisionType(comp, var);
                if (t == null)
                    throw new IOException(String.format(
                            "Unknown decision variable %s.%s", comp, var));
                switch (t) {
                case DOUBLE:
                case INTEGER:
                    try {
                        String
                            lbs = r.get(Cols.lb),
                            ubs = r.get(Cols.ub);
                        Object
                            lb = (lbs == null ? null : t.parse(lbs, ns)),
                            ub = (ubs == null ? null : t.parse(ubs, ns));
                        DecisionDomain
                            dom = (t == Type.DOUBLE
                                   ? NumericInterval.makeRealInterval(
                                           (Double)lb, (Double)ub)
                                   : NumericInterval.makeIntInterval(
                                           (Integer)lb, (Integer)ub));
                        p.decisionVars.computeIfAbsent(
                                comp, k -> new HashMap<>()).put(var, dom);
                    } catch (IllegalArgumentException e) {
                        throw new IOException(
                                String.format(
                                        "Error in decision variable %s.%s",
                                        comp, var),
                                e);
                    }
                    break;
                default:
                    throw new IOException(
                            "Unsupported decision variable type " + t);
                }
                break;
            case CON:
                expr = r.get(Cols.expr);
                if (expr == null)
                    throw new IOException("Missing expression");
                else {
                    String
                        lbs = r.get(Cols.lb),
                        ubs = r.get(Cols.ub);
                    double
                        lb = (lbs != null ? Double.valueOf(lbs)
                                          : Double.NEGATIVE_INFINITY),
                        ub = (ubs != null ? Double.valueOf(ubs)
                                          : Double.POSITIVE_INFINITY);
                    int id = (int)recordNumber;
                    try {
                        p.constraints.add(new Constraint(
                                (int)recordNumber, expr, lb, ub,
                                ns.evaluator));
                    } catch (IllegalArgumentException e) {
                        throw new IOException(
                                "Error in constraint " + id,
                                e);
                    }
                }
                break;
            case MET:
                var = r.get(Cols.var);
                expr = r.get(Cols.expr);
                if (expr == null)
                    throw new IllegalArgumentException("Missing expression");
                p.metrics.add(new MetricExpression(
                        (int)recordNumber, var, expr, ns.evaluator));
                break;
            case OBJ:
                String type = r.get(Cols.type);
                Boolean is_max = isMaximize(type);
                expr = r.get(Cols.expr);
                if (expr == null)
                    throw new IOException("Missing objective expression");
                if (is_max == null)
                    throw new IOException(
                            "Invalid objective type " + type);
                p.objs.add(new ObjectiveExpression(
                        (int)recordNumber, expr, is_max, ns.evaluator));
                break;
            default:
            }
        } catch (ParseException | ScriptException e) {
            throw new IOException(String.format(
                    "Error reading %s from record %s", kind, recordNumber),
                    e);
        }
    }
}
