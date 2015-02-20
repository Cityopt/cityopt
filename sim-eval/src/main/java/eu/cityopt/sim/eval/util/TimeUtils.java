package eu.cityopt.sim.eval.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Utility functions for parsing and converting time stamps.
 * @author Hannu Rummukainen
 */
public class TimeUtils {
    /**
     * Parses an ISO-8601 timestamp.
     * If the time zone is omitted, UTC is assumed.
     * If the time is omitted, 00:00 is assumed.
     * @throws DateTimeParseException
     */
    public static Instant parseISO8601(String dateString) {
        try {
            return ZonedDateTime.parse(dateString).toInstant();
        } catch (DateTimeParseException e) {}
        try {
            return LocalDateTime.parse(dateString).atZone(ZoneId.of("UTC")).toInstant();
        } catch (DateTimeParseException e) {}
        return LocalDate.parse(dateString).atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Formats a timestamp in ISO-8601 format in the UTC time zone.
     */
    public static String formatISO8601(Instant t) {
        return t.toString();
    }

    /**
     * Converts a Date object to simulation time. 
     * @param t arbitrary date and time
     * @param timeOrigin simulation time origin
     * @return a simulation time value representing the same point in time
     */
    public static double toSimTime(Date t, Instant timeOrigin) {
        return toSimTime(t.toInstant(), timeOrigin);
    }

    /**
     * Converts an Instant object to simulation time. 
     * @param t arbitrary date and time
     * @param timeOrigin simulation time origin
     * @return a simulation time value representing the same point in time
     */
    public static double toSimTime(Instant t, Instant timeOrigin) {
        long millis = t.toEpochMilli() - timeOrigin.toEpochMilli();
        return millis / 1000.0;
    }

    /**
     * Converts a simulation time value to a Date object.
     * @param simtime simulation time in seconds from simulation time origin
     * @param timeOrigin simulation time origin
     * @return a Date object representing the same point in time
     */
    public static Date toDate(double simtime, Instant timeOrigin) {
        return new Date(timeOrigin.toEpochMilli()
                + (long) (simtime * 1000 + 0.5));
    }

    /**
     * Converts a simulation time value to an Instant object.
     * @param simtime simulation time in seconds from simulation time origin
     * @param timeOrigin simulation time origin
     * @return an Instant object representing the same point in time
     */
    public static Instant toInstant(double simtime, Instant timeOrigin) {
        return Instant.ofEpochMilli(
                timeOrigin.toEpochMilli() + (long) (simtime * 1000 + 0.5));
    }
}
