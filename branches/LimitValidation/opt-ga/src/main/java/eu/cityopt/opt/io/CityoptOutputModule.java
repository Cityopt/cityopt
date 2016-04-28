package eu.cityopt.opt.io;

import org.opt4j.core.IndividualSet;
import org.opt4j.core.common.logger.OutputModule;
import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Population;
import org.opt4j.core.start.Constant;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import eu.cityopt.sim.opt.OptimisationLog;

@Info("Cityopt solution and diagnostic output")
@Icon(Icons.TEXT)
public class CityoptOutputModule extends OutputModule {
    @Info("Error log file (empty for terse stderr output)")
    @Constant(value="filename", namespace=FileOptLog.class)
    @File(".log")
    private String errorLogFile = "";
    
    @Info("Verbose error logging")
    @Constant(value="verbose", namespace=FileOptLog.class)
    private boolean verbose = true;
    
    @Info("Archive output file (empty for no output)")
    @Constant(value="filename", namespace=PopulationDumper.class)
    @File(".csv")
    private String archiveFile = "";
    
    @Info("Dump the whole population (instead of just the archive)")
    private boolean dumpAll = false;

    @Info("Evalauation log file (empty for no output)")
    @Constant(value="filename", namespace=EvaluationLogger.class)
    @File(".csv")
    private String evalLogFile = "";
    
    @Override
    protected void config() {
        install(new FactoryModuleBuilder()
                .implement(SolutionWriter.class, CSVSolutionWriter.class)
                .build(SolutionWriterFactory.class));
        if (!errorLogFile.isEmpty()) {
            bind(FileOptLog.class).in(SINGLETON);
            bind(OptimisationLog.class).to(FileOptLog.class);
            addOptimizerStateListener(FileOptLog.class);
        }
        if (!archiveFile.isEmpty()) {
            bind(IndividualSet.class).annotatedWith(Names.named("outputSet"))
                    .to(dumpAll ? Population.class : Archive.class);
            bind(PopulationDumper.class).in(SINGLETON);
            addOptimizerStateListener(PopulationDumper.class);
        }
        if (!evalLogFile.isEmpty()) {
            bind(EvaluationLogger.class).in(SINGLETON);
            addOptimizerStateListener(EvaluationLogger.class);
            addIndividualStateListener(EvaluationLogger.class);
        }
    }

    public String getErrorLogFile() {
        return errorLogFile;
    }

    public void setErrorLogFile(String logFile) {
        errorLogFile = logFile;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getArchiveFile() {
        return archiveFile;
    }

    public void setArchiveFile(String archiveFile) {
        this.archiveFile = archiveFile;
    }

    public boolean getDumpAll() {
        return dumpAll;
    }

    public void setDumpAll(boolean dumpAll) {
        this.dumpAll = dumpAll;
    }

    public String getEvalLogFile() {
        return evalLogFile;
    }

    public void setEvalLogFile(String indLogFile) {
        this.evalLogFile = indLogFile;
    }
}
