package com.sllibrary.util.timers;

import com.sllibrary.util.scheduler.Scheduler;
import com.sllibrary.util.time.TimePoint;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Countdown {

    private final Set<TimePoint> queue = new TreeSet<>();
    private String defFormat = "%d:%d:%s";
    private String announcement = "There are %s remaining!";
    private long start = -1;
    private volatile ScheduledFuture<?> task;

    public void start(long duration, Runnable completion) {
        this.start = System.nanoTime() + TimeUnit.SECONDS.toNanos(duration);
        if (this.task != null) {
            this.task.cancel(true);
        }
        final TreeSet<TimePoint> test = new TreeSet<>(this.queue);
        this.task = Scheduler.getService().scheduleWithFixedDelay(() -> {
            if (this.start - System.nanoTime() < 0) {
                if (completion != null) {
                    completion.run();
                }
                if (this.task != null) {
                    this.task.cancel(false);
                }
            }
            TimePoint now = TimePoint.getTimePoint(this.start - System.nanoTime());
            TimePoint ref = TimePoint.findClosestAndWipe(test, now);
            if (ref != null) {
                Bukkit.broadcastMessage(String.format(this.announcement, ref.format(TimeUnit.SECONDS)));
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private String formatReadable(TimePoint timePoint, String format) {
        if (format.matches("(?!.*%[^sd])(?!.*%d.*%d)(?!.*%s.*%s)(?!.*%s.*%d)(?=.*%s)(?=.*%d).*")) {
            return String.format(format, timePoint.getAmount(TimeUnit.MINUTES), timePoint.getAmount(TimeUnit.SECONDS));
        } else {
            return String.format(format, timePoint.getAmount(TimeUnit.HOURS), timePoint.getAmount(TimeUnit.MINUTES), timePoint.getAmount(TimeUnit.SECONDS));
        }
    }

    public Countdown setTimeFormat(String format) {
        if (!format.matches("(?!.*%[^sd])(?!.*%d.*%d)(?!.*%s.*%s.*%s)(?!.*%s.*%d)(?=.*%s)(?=.*%d).*")) {
            throw new IllegalArgumentException("Countdown format must follow contract!");
        }
        this.defFormat = format;
        return this;
    }

    public Countdown setAnnouncementFormat(String format) {
        if (!format.matches("(?!.*%[^sd])")) {
            throw new IllegalArgumentException("Countdown format must follow contract!");
        }
        this.announcement = format;
        return this;
    }

    public Countdown announceAt(long time, TimeUnit timeUnit) {
        return this.announceAt(TimePoint.getTimePoint(timeUnit.toNanos(time)));
    }


    public Countdown announceAt(TimePoint timePoint) {
        this.queue.add(timePoint);
        return this;
    }

    public Countdown announceAtRange(long min, long max, TimeUnit timeUnit) {
        for (; min <= max; min++) {
            this.announceAt(min, timeUnit);
        }
        return this;
    }

    public String getDefaultFormat() {
        return this.defFormat;
    }
}
