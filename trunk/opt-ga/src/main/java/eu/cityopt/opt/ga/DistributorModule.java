package eu.cityopt.opt.ga;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.start.Opt4JModule;

import com.google.inject.name.Names;

@Icon(Icons.PUZZLE_BLUE)
@Info("Configure job distribution")
public class DistributorModule extends Opt4JModule {
    @Info("Distributed node configuration")
    @File(".json")
    private String nodeFile = "";

    @Override
    protected void config() {
        if (!nodeFile.isEmpty()) {
            bind(Path.class).annotatedWith(Names.named("nodeConfig"))
                    .toInstance(Paths.get(nodeFile));
        }
    }

    public String getNodeFile() {
        return nodeFile;
    }

    public void setNodeFile(String nodeFile) {
        this.nodeFile = nodeFile;
    }
}
