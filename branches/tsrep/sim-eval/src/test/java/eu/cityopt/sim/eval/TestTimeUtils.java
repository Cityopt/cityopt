package eu.cityopt.sim.eval;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Test
    public void testFormat() {
        ZonedDateTime zdt = ZonedDateTime.of(2015,12,31, 23,59,58,0, ZoneId.of("UTC-1"));
        assertEquals("2016-01-01T00:59:58Z", TimeUtils.formatISO8601(zdt.toInstant()));
    }

    @Test
    public void testParse() {
        ZonedDateTime zdt = ZonedDateTime.of(2015,12,31, 23,59,58,0, ZoneId.of("UTC-1"));
        assertEquals(zdt.toInstant(), TimeUtils.parseISO8601("2016-01-01T00:59:58Z"));
        zdt = ZonedDateTime.of(2015,12,31, 23,59,58,0, ZoneId.of("UTC"));
        assertEquals(zdt.toInstant(), TimeUtils.parseISO8601("2016-01-01T00:59:58+01:00"));
        assertEquals(zdt.toInstant(), TimeUtils.parseISO8601("2015-12-31T23:59:58"));
        zdt = ZonedDateTime.of(2015,12,31, 0,0,0,0, ZoneId.of("UTC"));
        assertEquals(zdt.toInstant(), TimeUtils.parseISO8601("2015-12-31"));
    }
}
