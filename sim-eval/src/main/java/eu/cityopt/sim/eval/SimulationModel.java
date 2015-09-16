package eu.cityopt.sim.eval;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.util.Map;

import org.w3c.dom.Document;

/**
 * Simulator-agnostic handle to a simulation model.
 *
 * @author Hannu Rummukainen
 */
public interface SimulationModel extends Closeable {
    /** Returns the associated SimulatorManager instance. */
    SimulatorManager getSimulatorManager();

    /** Returns the name of the specific simulator variant to be used. */
    String getSimulatorName();

    /** Returns origin of simulation time, or null if not known. */
    Instant getTimeOrigin();

    /**
     * Determines the available model input parameters and output variables
     * as far as possible.  The results may have to be completed manually.
     *
     * @param newNamespace an empty Namespace to which new components,
     *   inputs and outputs will be created.  You will probably also need
     *   to call {@link Namespace#initConfigComponent()} before entering
     *   here.
     * @param units an empty Map to which units of inputs and outputs
     *   will be stored insofar they are known.
     * @param detailLevel indicates how much of the available input
     *   parameters and output variables are to be included.  0 is minimal,
     *   larger numbers may provide more results.
     * @param warningWriter where to put human-readable warning messages
     *   about any problems found, e.g. if there are invalid component or
     *   variable names.
     * @return default values for the input parameters.  Some or all values
     *   may be left unset (null).
     */
    SimulationInput findInputsAndOutputs(Namespace newNamespace,
    		Map<String, Map<String, String>> units,
            int detailLevel, Writer warningWriter) throws IOException;

    /** Access to Apros user component structure, or null if not available. */
    Document getAprosUserComponentStructure();
}
