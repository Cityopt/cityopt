package eu.cityopt.sim.eval;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.cityopt.sim.eval.SimulationInput;
import eu.cityopt.sim.eval.SimulationOutput;
import eu.cityopt.sim.eval.SimulationResults;
import eu.cityopt.sim.eval.TimeSeries;

public class SimJob implements Future<SimulationOutput> {
    private SimulationInput input;
    private SimulationOutput output;

    SimJob(SimulationInput input) {
        this.input = input;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public SimulationOutput get() {
        if (output == null) {
            output = computeJob();
        }
        return output;
    }

    private SimulationOutput computeJob() {
        SimulationResults out = new SimulationResults(input, "");
        double x5 = (Double) input.get("C1", "x5");
        double x6 = (Double) input.get("C1", "x6");
        double x7 = (Double) input.get("C1", "x7");
        double x8 = (Double) input.get("C1", "x8");
        double x9 = (Double) input.get("C2", "x9");
        double x3 = x5 - x6;
        double x4 = x8 - x7;
        out.put("C1", "x1", ts((50 * x9 - 0.5) * (x3 + x4)));
        out.put("C1", "x2", ts((-50 * x9 + 1.5) * (x3 + x4)));
        out.put("C2", "x3", ts(x3));
        out.put("C2", "x4", ts(x4));
        return out;
    }

    private TimeSeries ts(double value) {
        return new TimeSeries(new long[] { 0 }, new double[] { value });
    }

    @Override
    public SimulationOutput get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }

}
