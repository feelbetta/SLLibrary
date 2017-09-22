package com.sllibrary.util.timers;

import org.apache.commons.lang.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class Counter {

    private final StopWatch stopwatch;

    public Counter() {
        this.stopwatch = new StopWatch();
    }

    public long getTimeElapsed(TimeUnit timeUnit) {
        return this.stopwatch.getTime();
    }

    public String formatReadable() {
        return this.stopwatch.toString();
    }
}
