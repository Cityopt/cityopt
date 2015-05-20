package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Executors;

import org.junit.Test;

import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Namespace.Component;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.Type;

public class AprosModelTest extends AprosTestBase {
    @Test
    public void testExtractInputsAndOutputs() throws Exception {
        Namespace ns = findInputsAndOutputs(props.getProperty("testzip"));

        assertEquals(8, ns.components.size());
        assertEquals(9, ns.components.get("SAMPLE_DISTRICT").inputs.size());
        assertEquals(0, ns.components.get("SAMPLE_DISTRICT").outputs.size());
        assertEquals(Type.DOUBLE, ns.components.get("SAMPLE_DISTRICT").inputs.get(
                "Insulation_layer_factor"));
        assertEquals(0, ns.components.get("HEATING_LOAD").inputs.size());
        assertEquals(1, ns.components.get("HEATING_LOAD").outputs.size());
        assertEquals(Type.TIMESERIES_LINEAR,
                ns.components.get("HEATING_LOAD").outputs.get("MULTIPLYER_OUTPUT"));
    }

    @Test
    public void testExtractInputsOnly() throws Exception {
        Namespace ns = findInputsAndOutputs("/plumbing.zip");
        assertEquals(2, ns.components.size());
        assertNotNull(ns.components.get(Namespace.CONFIG_COMPONENT));
        assertEquals(2, ns.components.get("C01").inputs.size());
        assertEquals(0, ns.components.get("C01").outputs.size());
    }

    private Namespace findInputsAndOutputs(String modelResource) throws Exception {
        try (SimulatorManager manager = new AprosManager(
                profileDir, Executors.newSingleThreadExecutor());
             InputStream in = getClass().getResourceAsStream(modelResource)) {
            SimulationModel model = manager.parseModel(profileName, in);

            Namespace ns = new Namespace(new Evaluator(), model.getTimeOrigin());
            ns.initConfigComponent();
            String warnings = model.findInputsAndOutputs(ns, 0);

            System.out.print(warnings);
            System.out.println("Structure of " + modelResource + ":");
            for (Map.Entry<String, Component> ce : ns.components.entrySet()) {
                System.out.println(ce.getKey() + ": {");
                for (Map.Entry<String, Type> ie : ce.getValue().inputs.entrySet()) {
                    System.out.println("  in " + ie.getKey() + ": " + ie.getValue());
                }
                for (Map.Entry<String, Type> oe : ce.getValue().outputs.entrySet()) {
                    System.out.println("  out " + oe.getKey() + ": " + oe.getValue());
                }
                System.out.println("}");
            }
            return ns;
        }
    }
}
