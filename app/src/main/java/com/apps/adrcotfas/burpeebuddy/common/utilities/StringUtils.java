package com.apps.adrcotfas.burpeebuddy.common.utilities;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

public class StringUtils {

    /**
     * Utility method to format seconds to a more friendly layout
     * @param elapsed seconds
     * @return a String in the format of HH:mm:ss
     */
    public static String secondsToTimerFormat(int elapsed) {
        final long hours = TimeUnit.SECONDS.toHours(elapsed);
        final long minutes = TimeUnit.SECONDS.toMinutes(elapsed) - hours * 60;
        final long seconds = elapsed - (minutes * 60);

        return insertPrefixZero(hours)
                + ":" + insertPrefixZero(minutes)
                + ":" + insertPrefixZero(seconds);
    }

    public static String secondsToTimerFormatAlt(int elapsed) {
        final long minutes = TimeUnit.SECONDS.toMinutes(elapsed);
        final long seconds = elapsed - (minutes * 60);

        return insertPrefixZero(minutes)
                + ":" + insertPrefixZero(seconds);
    }

    private static String insertPrefixZero(long value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("EEE', 'MMM d', ' yyyy, HH:mm");

    public static String formatDateAndTime(long millis) {
        return dateTimeFormatter.print(millis);
    }
}
