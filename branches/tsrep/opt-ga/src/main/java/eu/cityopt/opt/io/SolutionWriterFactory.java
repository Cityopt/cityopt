package eu.cityopt.opt.io;

import java.io.OutputStream;

public interface SolutionWriterFactory {
    public SolutionWriter create(OutputStream str);
}
