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

@Info("Cityopt solution output")
@Icon(Icons.TEXT)
public class CityoptOutputModule extends OutputModule {
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
