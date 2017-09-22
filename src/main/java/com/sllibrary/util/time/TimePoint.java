package com.sllibrary.util.time;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class TimePoint implements Comparable<TimePoint> {

    private final long time;
    private final TimeUnit unit;
    private TimePoint next;

    public TimePoint(long time, TimeUnit unit, TimePoint next) {
        this.time = time;
        this.unit = unit;
        this.next = next;
    }

    public long getAmount(TimeUnit unit) {
        TimePoint point = this.getPoint(unit);
        return point == null ? 0 : point.getTime();
    }

    public TimePoint getPoint(TimeUnit unit) {
        int diff = this.unit.compareTo(unit);
        if (diff < 0) {
            return null;
        }
        TimePoint back = this.next;
        for (; diff > 0; diff--) {
            back = back.next;
        }
        return back;
    }

    public long getTime() {
        return this.time;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    public TimePoint getNext() {
        return this.next;
    }

    public TimePoint getNextNonZero() {
        if (this.next == null || this.next.getTime() <= 0) {
            return null;
        }
        TimePoint back = this;
        while (back.next != null && back.next.getTime() <= 0) {
            back = back.next;
        }
        return back.next;
    }

    private TimePoint setNext(TimePoint next) {
        this.next = next;
        return this;
    }

    public String properName() {
        return Time.getProperUnitName(this.unit, this.time);
    }

    public String format(TimeUnit min) {
        return Time.formatTime(this, min);
    }

    public String format() {
        return this.format(TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(TimePoint o) {
        //Do not check for null, Comparable contract calls for NPE
        int curr = this.unit.compareTo(o.unit);
        if (curr != 0) {
            return curr > 0 ? 1 : -1;
        }
        curr = Long.compare(this.time, o.time);
        if (curr == 0 && this.next != null && o.next != null) {
            curr = this.next.compareTo(o.next);
        }
        return curr > 0 ? 1 : -1;
    }

    public static TimePoint findClosestAndWipe(TreeSet<? extends TimePoint> points, TimePoint now) {
        Iterator<? extends TimePoint> itr = points.iterator();
        while (itr.hasNext()) {
            TimePoint next = itr.next();
            if (next.compareTo(now) > 0) {
                itr.remove();
                return next;
            }
        }
        return null;
    }

    public static TimePoint getTimePoint(long diff) {
        if (diff < 0) {
            return null;
        }
        long temp;
        TimeUnit u = TimeUnit.NANOSECONDS;
        TimePoint root = null;
        if ((temp = u.toDays(diff)) > 0) {
            root = new TimePoint(temp, TimeUnit.DAYS, null);
            diff -= root.getUnit().toNanos(root.getTime());
        }
        if ((temp = u.toHours(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.HOURS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMinutes(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MINUTES, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toSeconds(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.SECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMillis(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MILLISECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if ((temp = u.toMicros(diff)) > 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.MICROSECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        if (diff >= 0 || (root != null && temp >= 0)) {
            TimePoint p = new TimePoint(temp, TimeUnit.NANOSECONDS, null);
            root = TimePoint.allocateNodes(root, p);
            diff -= p.getUnit().toNanos(p.getTime());
        }
        return root;
    }

    private static TimePoint allocateNodes(TimePoint root, TimePoint allocate) {
        if (root == null) {
            return allocate;
        } else if (root.getNext() != null) {
            return root.setNext(TimePoint.allocateNodes(root.getNext(), allocate));
        } else {
            return root.setNext(allocate);
        }
    }
}
