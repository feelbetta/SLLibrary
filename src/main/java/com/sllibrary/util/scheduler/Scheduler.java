package com.sllibrary.util.scheduler;

import com.sllibrary.SLLibrary;
import com.sllibrary.util.logging.Debugger;
import com.sllibrary.util.reflections.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public final class Scheduler {

    private static final List<Future<?>> executives = new ArrayList<>(); //TODO implement a cache pattern
    private static final ReadWriteLock execLock = new ReentrantReadWriteLock();
    private static Supplier<? extends ScheduledExecutorService> supplier = () -> Executors.newScheduledThreadPool(10); //Going to find an expanding solution to this soon
    private static ScheduledExecutorService es;

    private Scheduler() {
    }

    public static ScheduledFuture<?> runAsyncTaskRepeat(Runnable r, long startAfter, long delay) {
        ScheduledFuture<?> sch = Scheduler.getService().scheduleWithFixedDelay(r, startAfter, delay, TimeUnit.MILLISECONDS);
        Scheduler.addTask(sch);
        return sch;
    }

    private static void addTask(Future<?> task) {
        Reflections.operateLock(Scheduler.execLock.writeLock(), () -> {
            Scheduler.executives.removeIf(f -> f.isDone() || f.isCancelled());
            Scheduler.executives.add(task);
        });
    }

    public static ScheduledFuture<?> runAsyncTask(Runnable runnable, long delay) {
        ScheduledFuture<?> sch = Scheduler.getService().schedule(runnable, delay, TimeUnit.MILLISECONDS);
        Scheduler.addTask(sch);
        return sch;
    }

    public static ScheduledFuture<?> runAsyncTask(Runnable runnable) {
        return Scheduler.runAsyncTask(runnable, 0);
    }

    public static <T> ScheduledFuture<T> runCallable(Callable<T> c, long delay) {
        ScheduledFuture<T> sch = Scheduler.getService().schedule(c, delay, TimeUnit.MILLISECONDS);
        Scheduler.addTask(sch);
        return sch;
    }

    public static <R> CompletableFuture<R> complete(Supplier<R> supplier) {
        CompletableFuture<R> back = CompletableFuture.supplyAsync(supplier, Scheduler.getService());
        Scheduler.addTask(back);
        return back;
    }

    public static ScheduledFuture<?> runSyncTask(Runnable runnable, long delay) {
        return Scheduler.runAsyncTask(() -> Bukkit.getServer().getScheduler().callSyncMethod(JavaPlugin.getPlugin(SLLibrary.class), () -> {
            runnable.run();
            return null;
        }), delay);
    }

    public static ScheduledFuture<?> runSyncTask(Runnable runnable) {
        return Scheduler.runSyncTask(runnable, 0);
    }

    public static ScheduledFuture<?> runSyncTaskRepeat(Runnable runnable, long startAfter, long delay) {
        return Scheduler.runAsyncTaskRepeat(() -> Bukkit.getServer().getScheduler().callSyncMethod(JavaPlugin.getPlugin(SLLibrary.class), () -> {
            runnable.run();
            return null;
        }), startAfter, delay);
    }

    public static void cancelAllTasks() {
        Reflections.operateLock(Scheduler.execLock.writeLock(), () -> {
            List<? extends Future<?>> back = new ArrayList<>(Scheduler.executives);
            Scheduler.executives.clear();
            return back;
        }).forEach(s -> s.cancel(false));
    }

    public static void cancelAndShutdown() {
        Scheduler.cancelAllTasks();
        try {
            Scheduler.getService().awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Scheduler.getService().shutdownNow();
            Debugger.error(ex, "Error halting scheduler service");
        }
        Scheduler.getService().shutdown();
    }

    public static ScheduledExecutorService getService() {
        if (Scheduler.es == null || Scheduler.es.isShutdown()) {
            Scheduler.es = Scheduler.supplier.get();
        }
        return Scheduler.es;
    }

    public static void setProvider(Supplier<? extends ScheduledExecutorService> serviceProvider) {
        if (serviceProvider == null) {
            throw new IllegalArgumentException("Cannot register a null service provider");
        }
        Scheduler.supplier = serviceProvider;
    }

    public static int getTaskCount() {
        return Reflections.operateLock(Scheduler.execLock.readLock(), Scheduler.executives::size);
    }
}
