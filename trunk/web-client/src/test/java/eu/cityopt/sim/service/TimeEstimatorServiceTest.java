package eu.cityopt.sim.service;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import eu.cityopt.sim.service.TimeEstimatorService.TimePair;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/jpaContext.xml", "classpath:/test-context.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class TimeEstimatorServiceTest {
    @Autowired TimeEstimatorService timeEstimatorService;

    @Test
    public void test() throws Exception {
        Map<Integer, TimePair> runtimes = new HashMap<>();
        Duration nominalRuntime = Duration.ofSeconds(123);
        int s = 0;

        // no data points
        assertEquals(Duration.ofSeconds(0), predict(runtimes, null, null));
        assertEquals(nominalRuntime, predict(runtimes, null, nominalRuntime));

        // one data point
        runtimes.put(++s, makeSimRealPair(1.0, 10.0));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, null, nominalRuntime));
        assertEquals(Duration.ofSeconds(5), predict(runtimes, 0.5, nominalRuntime));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, 1.0, nominalRuntime));
        assertEquals(Duration.ofSeconds(20), predict(runtimes, 2.0, nominalRuntime));

        // two data points, same simulation time
        runtimes.put(++s, makeSimRealPair(1.0, 14.0));
        assertEquals(Duration.ofSeconds(12), predict(runtimes, null, nominalRuntime));
        assertEquals(Duration.ofSeconds(6), predict(runtimes, 0.5, nominalRuntime));
        assertEquals(Duration.ofSeconds(12), predict(runtimes, 1.0, nominalRuntime));
        assertEquals(Duration.ofSeconds(24), predict(runtimes, 2.0, nominalRuntime));

        // two data points, different simulation time
        runtimes.put(s, makeSimRealPair(2.0, 12.0));
        assertEquals(Duration.ofSeconds(11), predict(runtimes, null, nominalRuntime));
        assertEquals(Duration.ofSeconds(9), predict(runtimes, 0.5, nominalRuntime));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, 1.0, nominalRuntime));
        assertEquals(Duration.ofSeconds(12), predict(runtimes, 2.0, nominalRuntime));
        assertEquals(Duration.ofSeconds(14), predict(runtimes, 3.0, nominalRuntime));

        // three data points, two different simulation times
        runtimes.put(++s, makeSimRealPair(2.0, 8.0));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, null, nominalRuntime));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, 0.5, nominalRuntime));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, 1.0, nominalRuntime));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, 2.0, nominalRuntime));
        assertEquals(Duration.ofSeconds(10), predict(runtimes, 3.0, nominalRuntime));
    }

    private Duration predict(Map<Integer, TimePair> runtimes,
            Double simulationTime, Duration nominalRuntime) {
        Duration s = (simulationTime != null)
                ? Duration.ofNanos((long) (1e9 * simulationTime))
                : null;
        return timeEstimatorService.predictSimulationRuntime(
                runtimes, s, nominalRuntime);
    }


    private TimePair makeSimRealPair(Double s, Double r) {
        TimePair p = new TimePair();
        p.realTime = (r != null) ? Duration.ofNanos((long) (r * 1e9)) : null;
        p.simulationTime = (s != null) ? Duration.ofNanos((long) (s * 1e9)) : null;
        return p;
    }
}
