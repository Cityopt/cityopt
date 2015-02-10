package eu.cityopt.sim.eval;

import java.io.Closeable;
import java.time.Instant;

import org.w3c.dom.Document;

/**
 * Generic simulator-agnostic handle to a simulation model.
 *
 * TODO: generic access to model structure for import
 * @author Hannu Rummukainen
 */
public interface SimulationModel extends Closeable {
    /** Returns origin of simulation time, or null if not known. */
    Instant getTimeOrigin();

    /** Access to Apros user component structure, or null if not available. */
    Document getAprosUserComponentStructure();
}
