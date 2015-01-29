package eu.cityopt.sim.eval.apros;

import java.io.FilterInputStream;
import java.io.InputStream;

/** Helper class to wrap ZipInputStream so that libraries won't close it. */
public class UncloseableInputStream extends FilterInputStream {
    UncloseableInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() {
    }
}
