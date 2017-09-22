package com.sllibrary.util.exceptions;

import com.sllibrary.SLLibrary;
import com.sllibrary.util.logging.Debugger;
import com.sllibrary.util.logging.Logging;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public final class Exceptions {

    private Exceptions() {
    }

    public static void allNotNull(String message, Object... check) {
        for (Object o : check) {
            if (o == null) {
                throw Exceptions.newException(IllegalArgumentException.class, message);
            }
        }
    }

    public static void allNotNull(Object... check) {
        Exceptions.allNotNull(null, check);
    }

    public static void illegalState(boolean state, String message) {
        if (!state) {
            throw Exceptions.newException(IllegalStateException.class, message);
        }
    }

    public static void illegalState(boolean state) {
        Exceptions.illegalState(state, null);
    }

    public static void illegalInvocation(boolean state, String message) {
        if (!state) {
            throw Exceptions.newException(IllegalInvocationException.class, message);
        }
    }

    public static void illegalInvocation(boolean state) {
        Exceptions.illegalInvocation(state, null);
    }

    public static void unsupportedOperation(boolean state, String message) {
        if (!state) {
            throw Exceptions.newException(UnsupportedOperationException.class, message);
        }
    }

    public static void unsupportedOperation(boolean state) {
        Exceptions.unsupportedOperation(state, null);
    }

    public static <T extends RuntimeException> void notNull(Object obj, String message, Class<T> ex) {
        Exceptions.isTrue(obj != null, message, ex);
    }

    public static <T extends RuntimeException> void notNull(Object obj, Class<T> ex) {
        Exceptions.notNull(obj, null, ex);
    }

    public static <T extends RuntimeException> void isTrue(boolean validate, String message, Class<T> ex) {
        if (!validate) {
            throw Exceptions.newException(ex, message);
        }
    }

    public static <T extends RuntimeException> void isTrue(boolean validate, Class<T> ex) {
        Exceptions.isTrue(validate, null, ex);
    }

    private static <T extends RuntimeException> T newException(Class<T> ex, String message) {
        if (message != null) {
            try {
                Constructor<T> c = ex.getConstructor(String.class);
                c.setAccessible(true);
                return c.newInstance(message);
            } catch (NoSuchMethodException e) {
                Debugger.print(Level.WARNING, String.format("Class '%s' does not have a String message constructor! Using default constructor...", ex.getName()));
            } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logging.warn(JavaPlugin.getPlugin(SLLibrary.class), "Error creating new exception instance");
            }
        }
        try {
            return ex.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Logging.warn(JavaPlugin.getPlugin(SLLibrary.class), "Error creating new exception instance");
        }
        throw new IllegalArgumentException(String.format("Class '%s' does not have the appropriate constructors to be instantiated", ex.getName()));
    }

    public static String readableStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement elem : trace) {
            sb.append("\tat ").append(elem).append('\n');
        }
        if (t.getCause() != null) {
            Exceptions.readableStackTraceAsCause(sb, t.getCause(), trace);
        }
        return sb.toString();
    }

    private static void readableStackTraceAsCause(StringBuilder sb, Throwable t, StackTraceElement[] causedTrace) {
        StackTraceElement[] trace = t.getStackTrace();
        int m = trace.length - 1;
        int n = causedTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
            m--; n--;
        }
        int common = trace.length - 1 - m;

        sb.append("Caused by: ").append(t).append('\n');
        for (int i = 0; i <= m; i++) {
            sb.append("\tat ").append(trace[i]).append('\n');
        }
        if (common != 0) {
            sb.append("\t... ").append(common).append(" more\n");
        }
        if (t.getCause() != null) {
            Exceptions.readableStackTraceAsCause(sb, t.getCause(), trace);
        }
    }
}
