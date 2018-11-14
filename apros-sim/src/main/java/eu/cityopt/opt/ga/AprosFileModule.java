package eu.cityopt.opt.ga;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.start.Constant;

import com.google.inject.name.Names;

/**
 * Configure {@link CityoptModule} for file-based input.
 * Installs CityoptModule.  Supports the Opt4J GUI.
 * @author ttekth
 *
 */
@Info("Read the Cityopt problem from files")
public class AprosFileModule extends CityoptFileModule {
    @Info("Directory containing Apros profiles")
    @File
    @Constant(value="aprosDir", namespace=AprosFactory.class)
    private String aprosDir = "c:/apros/profiles";
    
    @Info("The zip file containing the model")
    @File(".zip")
    private String modelFile = "";
    
    public AprosFileModule() {
        simulator = "Apros-Combustion-5.13.06-64bit";
    }

    @Override
    public void config() {
        super.config();
        bind(ModelFactory.class).to(AprosFactory.class).in(SINGLETON);
        bindModelFile(Paths.get(modelFile));
    }

    public String getAprosDir() {
        return aprosDir;
    }

    public void setAprosDir(String aprosDir) {
        this.aprosDir = aprosDir;
    }

    public String getModelFile() {
        return modelFile;
    }

    public void setModelFile(String modelFile) {
        this.modelFile = modelFile;
    }
}
