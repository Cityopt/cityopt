package eu.cityopt.sim.eval;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;

import eu.cityopt.sim.eval.util.TimeUtils;

public class TestTimeUtils {
    @Test
    public void testTimeConversion() {
        Instant timeOrigin = Instant.ofEpochMilli(123456);
        assertEquals(new Date(124456), TimeUtils.toDate(1, timeOrigin));
        assertEquals(2.0, TimeUtils.toSimTime(new Date(125456), timeOrigin), 0.0);
        assertEquals(999, TimeUtils.toSimTime(TimeUtils.toDate(999, timeOrigin), timeOrigin), 0.0);
    }
}
