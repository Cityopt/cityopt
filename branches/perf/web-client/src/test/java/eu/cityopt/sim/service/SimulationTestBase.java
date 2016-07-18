package eu.cityopt.sim.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;

import eu.cityopt.model.SimulationModel;
import eu.cityopt.repository.ScenarioRepository;
import eu.cityopt.repository.SimulationModelRepository;
import eu.cityopt.sim.eval.util.TempDir;

public class SimulationTestBase {
    @Autowired
    SimulationModelRepository simulationModelRepository;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    DataSource dataSource;

    public void loadModel(String modelName, String modelResource) throws IOException {
        // The simulation test model is not included in the XML test data.
        // Load it from a separate zip file.
        SimulationModel model = 
                simulationModelRepository.findByDescription(modelName).get(0);
        try (InputStream is = this.getClass().getResource(modelResource).openStream()) {
            model.setModelblob(IOUtils.toByteArray(is));
        }
        simulationModelRepository.saveAndFlush(model);
    }

    public void dumpTables(String caseName) throws Exception {
        dumpTables(caseName, false);
    }

    public Path makeTempPath(String filename) {
    	return Paths.get(System.getProperty("java.io.tmpdir")).resolve(filename);
    }

    public void dumpTables(String caseName, boolean includeModel) throws Exception {
        Path outputPath = makeTempPath(caseName + "_result.xml");
        scenarioRepository.flush();
        IDatabaseConnection dbConnection = new DatabaseConnection(
                DataSourceUtils.getConnection(dataSource));
        String[] tableNames = 
                TablesDependencyHelper.getAllDependentTables(dbConnection, "scenario");
        if ( ! includeModel) {
            tableNames = copyIfNotEqual(tableNames, "simulationmodel");
        }
        IDataSet depDataset = dbConnection.createDataSet(tableNames);
        FlatXmlDataSet.write(depDataset, new FileOutputStream(outputPath.toFile()));
    }

    private String[] copyIfNotEqual(String[] in, String toRemove) {
        List<String> out = new ArrayList<String>();
        for (String s : in) {
            if (!s.equalsIgnoreCase(toRemove)) {
                out.add(s);
            }
        }
        return out.toArray(new String[out.size()]);
    }
    
    public InputStream openResource(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    byte[] getResourceBytes(String resourceName) throws IOException {
        try (InputStream in = openResource(resourceName)) {
            return IOUtils.toByteArray(in);
        }
    }

    Path copyResource(String resourceName, TempDir tempDir) throws IOException {
        return copyResource(resourceName, tempDir.getPath());
    }

    Path copyResource(String resourceName, Path workDir) throws IOException {
        int i = resourceName.lastIndexOf('/');
        String baseName = (i >= 0) ? resourceName.substring(i+1) : resourceName;
        Path target = workDir.resolve(baseName);
        try (InputStream in = getClass().getResource(resourceName).openStream()) {
            Files.copy(in, target);
        }
        return target;
    }
}
