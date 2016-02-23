package eu.cityopt.opt.ga;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import javax.annotation.Nullable;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectReader;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import eu.cityopt.opt.io.CsvTimeSeriesData;
import eu.cityopt.sim.eval.EvaluationSetup;
import eu.cityopt.sim.eval.Evaluator;
import eu.cityopt.sim.eval.SimulationModel;
import eu.cityopt.sim.eval.TimeSeriesData;

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
            @Named("timeSeries") ObjectReader reader,
            @Named("timeseries") Path... paths)
                    throws IOException, ParseException {
        Instant timeOrigin = t0 != null ? t0 : model.getDefaults().timeOrigin;
        EvaluationSetup setup = new EvaluationSetup(evaluator, timeOrigin);
        CsvTimeSeriesData tsd = new CsvTimeSeriesData(reader, setup);
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
