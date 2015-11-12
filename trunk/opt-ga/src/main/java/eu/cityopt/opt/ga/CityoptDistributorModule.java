package eu.cityopt.opt.ga;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.opt4j.core.common.completer.IndividualCompleterModule;
import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.File;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Info;
import org.opt4j.core.start.Opt4JModule;

import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

@Icon(Icons.PUZZLE_BLUE)
@Info("Configure job distribution")
public class CityoptDistributorModule extends Opt4JModule {
    @Info("Distributed node configuration")
    @File(".json")
    private String nodeFile = "";
    
    @Override
    protected void config() {
        if (!nodeFile.isEmpty()) {
            try {
                List<Map<String, String>>
                    nodes = AprosFactory.readNodeConfig(Paths.get(nodeFile));
                bind(new TypeLiteral<List<Map<String, String>>>() {})
                    .annotatedWith(Names.named("nodeConfig"))
                    .toInstance(nodes);
                int n = AprosFactory.numCpus(nodes);
                if (n > 1) {
                    IndividualCompleterModule
                        icm = new IndividualCompleterModule();
                    icm.setType(IndividualCompleterModule.Type.PARALLEL);
                    icm.setThreads(n);
                    install(icm);
                }
            } catch (IOException e) {
                addError(e);
            }
        }
    }

    public String getNodeFile() {
        return nodeFile;
    }

    public void setNodeFile(String nodeFile) {
        this.nodeFile = nodeFile;
    }
}
