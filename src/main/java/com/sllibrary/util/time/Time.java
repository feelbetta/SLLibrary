package com.sllibrary.util.time;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class Time {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E MMMM d y hh:mm a z");

    private Time() {
    }

    public static long toTicks(TimeUnit timeUnit, int duration) {
        return timeUnit.toMillis(duration) / 50;
    }

    public static String getTime(long start) {
        return Time.formatTime(System.nanoTime() - start);
    }

    public static String formatTime(long durationNS) {
        return Time.formatTime(durationNS, TimeUnit.NANOSECONDS);
    }

    public static String formatTime(long durationNS, TimeUnit min) {
        if (min == null) {
            throw new IllegalArgumentException("Minimum TimeUnit cannot be null");
        }
        TimePoint point = TimePoint.getTimePoint(durationNS);
        return Time.formatTime(point, min);
    }

    public static String formatTime(TimePoint point) {
        return Time.formatTime(point, TimeUnit.NANOSECONDS);
    }

    public static String formatTime(TimePoint point, TimeUnit min) {
        TimePoint next = point.getNextNonZero();
        if (next != null && next.getUnit().compareTo(min) > 0) {
            return String.format("%d %s, %d %s", point.getTime(), point.properName(),
                    next.getTime(), next.properName());
        } else {
            return String.format("%d %s", point.getTime(), point.properName());
        }
    }

    public static String getProperUnitName(TimeUnit unit, long amount) {
        String proper = unit.toString().substring(0, 1) + unit.toString().substring(1, unit.toString().length()).toLowerCase();
        return amount != 1 ? proper : proper.substring(0, proper.length() - 1);
    }
}
