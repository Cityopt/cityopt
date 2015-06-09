package eu.cityopt.opt.ga;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import javax.annotation.Nullable;
import javax.inject.Singleton;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import eu.cityopt.opt.io.CsvTimeSeriesData;
import eu.cityopt.opt.io.TimeSeriesData;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.SimulationModel;

/**
 * Loads TimeSeriesData from files.
 */
@Singleton
public class TimeSeriesLoader implements Provider<TimeSeriesData> {
    private TimeSeriesData tsData;

    @Inject
    public TimeSeriesLoader(
            Evaluator evaluator,
            @Named("timeOrigin") @Nullable Instant t0,
            @Nullable SimulationModel model,
            @Named("timeseries") Path... paths)
                    throws IOException, ParseException {
        Instant timeOrigin = t0 != null ? t0 : model.getTimeOrigin();
        EvaluationSetup setup = new EvaluationSetup(evaluator, timeOrigin);
        CsvTimeSeriesData tsd = new CsvTimeSeriesData(setup);
        for (Path p : paths) {
            tsd.read(p);
        }
        this.tsData = tsd;
    }
    
    @Override
    public TimeSeriesData get() {
        return tsData;
    }
}
