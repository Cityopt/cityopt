package eu.cityopt.sim.eval.apros;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectWriter.GeneratorSettings;

import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.Namespace.Component;
import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.SimulatorManager;
import eu.cityopt.sim.eval.TimeSeriesI;
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
        try (SimulatorManager manager = newSimulatorManager();
             SimulationModel
                 model = readModelResource(manager, modelResource)) {

            Map<String, Map<String, String>> units = new HashMap<>();
            SimulationInput defaultInput = getModelVars(model, units);
            System.out.println("Structure of " + modelResource + ":");
            for (Map.Entry<String, Component>
                    ce : defaultInput.getNamespace().components.entrySet()) {
                String cname = ce.getKey();
                System.out.println(cname + ": {");
                Map<String, String> cunits = units.get(cname);
                for (Map.Entry<String, Type> ie : ce.getValue().inputs.entrySet()) {
                    String iname = ie.getKey();
                    String valueText
                        = (cname.equals(Namespace.CONFIG_COMPONENT)
                           || ie.getValue().isTimeSeriesType())
                          ? "-" : defaultInput.getString(cname, iname);
                    String unit = (cunits != null) ? cunits.get(iname) : null;
                    System.out.println("  in " + iname + ": " + ie.getValue()
                            + ", default " + valueText + " [" + unit + "]");
                }
                for (Map.Entry<String, Type> oe : ce.getValue().outputs.entrySet()) {
                    String unit = (cunits != null) ? cunits.get(oe.getKey()) : null;
                    System.out.println("  out " + oe.getKey() + ": " + oe.getValue()
                    			+ " [" + unit + "]");
                }
                System.out.println("}");
            }
            return defaultInput;
        }
    }

    @Test
    public void testDescriptions() throws Exception {
        try (SimulatorManager manager = newSimulatorManager();
             SimulationModel
                 model = readModelResourceProp(manager, "testzip")) {
            assertEquals("en förenklad modell för programtestning",
                         model.getDescription("sv"));
            assertNotNull(model.getDescription("en"));
            assertNotNull(model.getDescription("fi"));
            assertNotNull(model.getDescription("de"));
            assertNull(model.getDescription("et"));
            assertNotNull(model.getDescription("et,en"));
            assertNotNull(model.getDescription("*-*"));
            assertEquals("test project", model.getDescription("*-*;q=0.1,en"));
        }
    }

    @Test
    public void testTsInputs() throws Exception {
        try (SimulatorManager mgr = newSimulatorManager();
             SimulationModel
                 model = readModelResourceProp(mgr, "tsinput_test")) {
            SimulationInput defaults = getModelVars(model, null);
            TimeSeriesI ts = defaults.getTS("SP01", "SP_VALUE");
            assertEquals(3, ts.getTimes().length);
        }
    }
}
