package eu.cityopt.sim.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.python.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.Metric;
import eu.cityopt.model.MetricVal;
import eu.cityopt.model.Scenario;
import eu.cityopt.model.ScenarioMetrics;
import eu.cityopt.model.TimeSeries;
import eu.cityopt.model.TimeSeriesVal;
import eu.cityopt.repository.CustomQueryRepository;
import eu.cityopt.repository.MetricRepository;
import eu.cityopt.repository.MetricValRepository;
import eu.cityopt.repository.ScenarioMetricsRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.repository.TimeSeriesValRepository;
import eu.cityopt.repository.TypeRepository;
import eu.cityopt.sim.eval.ExternalParameters;
import eu.cityopt.sim.eval.MetricExpression;
import eu.cityopt.sim.eval.MetricValues;
import eu.cityopt.sim.eval.Namespace;
import eu.cityopt.sim.eval.TimeSeriesData;
import eu.cityopt.sim.eval.TimeSeriesI;
import eu.cityopt.sim.eval.Type;
import eu.cityopt.sim.eval.util.TimeUtils;

/**
 * Basic database access functionality to aid other classes of this package,
 * which in turn contain more specific conversions to/from sim-eval objects.
 *
 * @author Hannu Rummukainen
 */
@Service("simulationstoreservice")
class SimulationStoreService {

    @Autowired private ScenarioMetricsRepository scenarioMetricsRepository;
    @Autowired private MetricRepository metricRepository;
    @Autowired private MetricValRepository metricValRepository;
    @Autowired private TypeRepository typeRepository;
    @Autowired private TimeSeriesRepository timeSeriesRepository;
    @Autowired private TimeSeriesValRepository timeSeriesValRepository;

    @Autowired CustomQueryRepository customQueryRepository;
    @PersistenceContext private EntityManager em;

    @Autowired private ApplicationContext applicationContext;

    /**
     * Load the data of a time series.
     * @param tsid time series id
     * @param timeOrigin for translating timestamps to seconds
     */
    @Transactional(readOnly=true)
    public TimeSeriesData.Series loadTimeSeriesData(
            int tsid, Instant timeOrigin) {

        List<TimeSeriesVal> timeSeriesVals =
                timeSeriesValRepository.findTimeSeriesValOrderedByTime(tsid);
        int n = timeSeriesVals.size();
        double[] times = new double[n];
        double[] values = new double[n];
        for (int i = 0; i < n; ++i) {
            TimeSeriesVal tsVal = timeSeriesVals.get(i);
            times[i] = TimeUtils.toSimTime(tsVal.getTime(), timeOrigin);
            values[i] = Double.valueOf(tsVal.getValue());
        }
        return new TimeSeriesData.Series(times, values);
    }

    DbSimulationStorageI makeDbSimulationStorage(int prjid, ExternalParameters externals) {
        return makeDbSimulationStorage(prjid, externals, null, null);
    }

    DbSimulationStorageI makeDbSimulationStorage(
            int prjid, ExternalParameters externals, Integer userId, Integer scenGenId) {
        DbSimulationStorageI storage =
                (DbSimulationStorageI) applicationContext.getBean("dbSimulationStorage");
        storage.initialize(storage, prjid, externals, userId, scenGenId);
        return storage;
    }

    /**
     * Save values of the listed metrics.
     * @param to_save List of metrics to save
     * @param mvs Values are retrieved from here.
     * @param scen Associated scenario
     * @param xpvs Associated xpvset
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void saveMetricValues(
            List<MetricExpression> to_save, MetricValues mvs,
            Scenario scen, ExtParamValSet xpvs) {
        ScenarioMetrics sm = scenarioMetricsRepository
                .findByScenarioAndExtparamvalset(scen, xpvs);
        if (sm == null) {
            sm = makeScenarioMetrics(scen, xpvs);
        }
        Map<Integer, MetricVal> mvmap = new HashMap<>();
        for (MetricVal mv : sm.getMetricvals()) {
            mvmap.put(mv.getMetric().getMetid(), mv);
        }
        for (MetricExpression expr : to_save) {
            int metid = expr.getMetricId();
            MetricVal mv = mvmap.get(metid);
            if (mv == null) {
                mv = new MetricVal();
                mv.setScenariometrics(sm);
                mv.setMetric(metricRepository.findOne(metid));
                mv = metricValRepository.save(mv);
            }
            storeMetricVal(expr, mvs, mv);
        }
//        em.flush();
    }

    private MetricVal storeMetricVal(
            MetricExpression expr, MetricValues mvs, MetricVal mv) {
        Namespace ns = mvs.getNamespace();
        String name = expr.getMetricName();
        Type type = ns.metrics.get(name);
        if (type.isTimeSeriesType()) {
            mv.setValue(null);
            mv.setTimeseries(saveTimeSeries(
                    mvs.getTS(name), type, ns.timeOrigin));
            // Flush time series writes to save memory.
            em.flush();
        } else {
            mv.setValue(mvs.getString(name));
            mv.setTimeseries(null);
        }
        return metricValRepository.save(mv);
    }

    private ScenarioMetrics makeScenarioMetrics(
            Scenario scen, ExtParamValSet xpvs) {
        ScenarioMetrics sm = new ScenarioMetrics();
        sm.setScenario(scen);
        sm.setExtparamvalset(xpvs);
        return scenarioMetricsRepository.save(sm);
    }

    TimeSeries saveTimeSeries(TimeSeriesI simTS, eu.cityopt.model.Type type,
            Instant timeOrigin) {
        if (simTS.getTimeSeriesId() != null) {
            TimeSeries timeSeries = timeSeriesRepository.findOne(simTS.getTimeSeriesId());
            if (timeSeries != null) {
                //TODO: should we check if the time series has changed?
                return timeSeries;
            }
            simTS.setTimeSeriesId(null);
        }
        TimeSeries timeSeries = saveTimeSeries(
                simTS.getTimes(), simTS.getValues(), type, timeOrigin);
        // Copy the database row id.
        simTS.setTimeSeriesId(timeSeries.getTseriesid());
        return timeSeries;
    }

    TimeSeries saveTimeSeries(TimeSeriesI simTS, Type type,
                              Instant timeOrigin) {
        return saveTimeSeries(simTS, typeRepository.findByNameLike(type.name),
                              timeOrigin);
    }

    /**
     * Save a time series into the database.
     * Because this returns a TimeSeries rather than a TimeSeriesDTO,
     * it is unlikely to be useful outside the .sim.service package.
     * Hence the visibility.
     * @param times Time points as seconds from timeOrigin
     * @param values Series values at the time points.
     * @param type Type attribute of the time series.  We save it but
     *   I don't think it is used for anything currently.
     * @param timeOrigin Time origin for converting seconds to timestamps.
     * @return the saved TimeSeries.
     */
    TimeSeries saveTimeSeries(
            double[] times, double[] values,
            eu.cityopt.model.Type type, Instant timeOrigin) {
        TimeSeries timeSeries = new TimeSeries();
        timeSeries.setType(type);
        List<TimeSeriesVal> tsvals = timeSeries.getTimeseriesvals();
        int n = times.length;
        for (int i = 0; i < n; ++i) {
            TimeSeriesVal timeSeriesVal = new TimeSeriesVal();

            timeSeriesVal.setTime(TimeUtils.toDate(times[i], timeOrigin));
            timeSeriesVal.setValue(values[i]);

            timeSeriesVal.setTimeseries(timeSeries);
            tsvals.add(timeSeriesVal);
        }


        return customQueryRepository.insertTimeSeries(timeSeries);

        /*
        timeSeriesValRepository.save(tsvals);
        return timeSeriesRepository.save(timeSeries);
        */
    }

}
