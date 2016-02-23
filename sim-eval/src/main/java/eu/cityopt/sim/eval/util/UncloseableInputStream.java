package eu.cityopt.sim.eval.util;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * Helper class to wrap an InputStream so that it cannot be closed via the
 * wrapper.
 */
public class UncloseableInputStream extends FilterInputStream {
    public UncloseableInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() {
    }
}
