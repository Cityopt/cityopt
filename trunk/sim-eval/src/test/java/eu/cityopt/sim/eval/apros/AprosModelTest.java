package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.Executors;

import org.junit.Test;

import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Namespace.Component;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.Type;

public class AprosModelTest extends AprosTestBase {
    private static final double delta = 1.0e-12;

    @Test
    public void testExtractInputsAndOutputs() throws Exception {
        SimulationInput input = findInputsAndOutputs(props.getProperty("testzip"));
        Namespace ns = input.getNamespace();

        assertEquals(8, ns.components.size());
        assertEquals(9, ns.components.get("SAMPLE_DISTRICT").inputs.size());
        assertEquals(0, ns.components.get("SAMPLE_DISTRICT").outputs.size());
        assertEquals(Type.DOUBLE, ns.components.get("SAMPLE_DISTRICT").inputs.get(
                "Insulation_layer_factor"));
        assertEquals(0, ns.components.get("HEATING_LOAD").inputs.size());
        assertEquals(1, ns.components.get("HEATING_LOAD").outputs.size());
        assertEquals(Type.TIMESERIES_LINEAR,
                ns.components.get("HEATING_LOAD").outputs.get("MULTIPLYER_OUTPUT"));

        assertEquals(17000.0, (Double)input.get("SAMPLE_DISTRICT", "Burner_nominal_power"), delta);
        assertEquals(0.7, (Double)input.get("SAMPLE_DISTRICT", "Burner_efficiency"), delta);
        assertEquals(1.0, (Double)input.get("SAMPLE_DISTRICT", "Insulation_layer_factor"), delta);
    }

    @Test
    public void testExtractInputsOnly() throws Exception {
        SimulationInput input = findInputsAndOutputs("/plumbing.zip");
        Namespace ns = input.getNamespace();
        assertEquals(2, ns.components.size());
        assertNotNull(ns.components.get(Namespace.CONFIG_COMPONENT));
        assertEquals(1, ns.components.get("C01").inputs.size());
        assertEquals(0, ns.components.get("C01").outputs.size());
        
        assertEquals(2.0, (Double)input.get("C01", "typ"), delta);
    }

    private SimulationInput findInputsAndOutputs(String modelResource) throws Exception {
        try (SimulatorManager manager = new AprosManager(
                profileDir, Executors.newSingleThreadExecutor());
             InputStream in = getClass().getResourceAsStream(modelResource)) {
            SimulationModel model = manager.parseModel(profileName, in);

            Namespace ns = new Namespace(new Evaluator(), model.getTimeOrigin());
            ns.initConfigComponent();
            StringWriter warnings = new StringWriter();
            SimulationInput defaultInput = model.findInputsAndOutputs(ns, 0, warnings);

            System.out.print(warnings.toString());
            System.out.println("Structure of " + modelResource + ":");
            for (Map.Entry<String, Component> ce : ns.components.entrySet()) {
                String cname = ce.getKey();
                System.out.println(cname + ": {");
                for (Map.Entry<String, Type> ie : ce.getValue().inputs.entrySet()) {
                    String iname = ie.getKey();
                    String valueText = (cname.equals(Namespace.CONFIG_COMPONENT))
                            ? "-" : defaultInput.getString(cname, iname);
                    System.out.println("  in " + iname + ": " + ie.getValue()
                            + ", default " + valueText);
                }
                for (Map.Entry<String, Type> oe : ce.getValue().outputs.entrySet()) {
                    System.out.println("  out " + oe.getKey() + ": " + oe.getValue());
                }
                System.out.println("}");
            }
            return defaultInput;
        }
    }
}