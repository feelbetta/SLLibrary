package com.sllibrary.util.reflections;

import com.google.common.primitives.Primitives;
import com.sllibrary.util.exceptions.Exceptions;
import com.sllibrary.util.logging.Debugger;
import org.apache.commons.lang.Validate;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class Reflections {

    /**
     * Thanks to 1Rogue for contributions.
     */

    private Reflections() {
    }

    public static boolean hasAnnotation(AnnotatedElement target, Class<? extends Annotation> check) {
        return target.getAnnotation(check) != null;
    }

    public static boolean accessedFrom(String regex) {
        return Reflections.getCaller(1).getClassName().matches(regex);
    }

    public static boolean accessedFrom(Class<?> clazz) {
        return Reflections.getCaller(1).getClassName().equals(clazz.getName());
    }

    public static StackTraceElement getCaller(int offset) {
        Validate.isTrue(offset >= 0, "Offset must be a positive number");
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        if (elems.length < 4 + offset) {
            throw new IndexOutOfBoundsException("Offset too large for current stack");
        }
        return elems[3 + offset];
    }

    public static StackTraceElement getCaller() {
        return Reflections.getCaller(1);
    }

    public static File findPluginJarfile(String name) {
        File plugins = new File("plugins");
        Exceptions.illegalState(plugins.isDirectory(), "'plugins' isn't a directory!");
        for (File f : plugins.listFiles((File pathname) -> pathname.getPath().endsWith(".jar"))) {
            try (InputStream is = new FileInputStream(f); ZipInputStream zi = new ZipInputStream(is)) {
                ZipEntry ent;
                while ((ent = zi.getNextEntry()) != null) {
                    if (ent.getName().equalsIgnoreCase("plugin.yml")) {
                        break;
                    }
                }
                if (ent == null) {
                    continue;
                }
                ZipFile z = new ZipFile(f);
                try (InputStream fis = z.getInputStream(ent);
                     InputStreamReader fisr = new InputStreamReader(fis);
                     BufferedReader scan = new BufferedReader(fisr)) {
                    String in;
                    while ((in = scan.readLine()) != null) {
                        if (in.startsWith("name: ")) {
                            if (in.substring(6).equalsIgnoreCase(name)) {
                                return f;
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                Debugger.error(ex, "Error reading plugin jarfiles.");
            }
        }
        return null;
    }

    public static <K, V> Map<K, V> difference(Map<K, V> initial, Map<K, V> replacer) {
        Map<K, V> back = new HashMap<>();
        back.putAll(initial);
        back.putAll(replacer);
        back.entrySet().removeAll(initial.entrySet());
        return back;
    }

    public static <T> T defaultPrimitiveValue(Class<T> c) {
        if (c.isPrimitive() || Primitives.isWrapperType(c)) {
            c = Primitives.unwrap(c);
            T back = null;
            if (c == boolean.class) {
                back = c.cast(false);
            } else if (c == char.class) { //god help me
                back = c.cast((char) -1);
            } else if (c == float.class) {
                back = c.cast(-1F);
            } else if (c == long.class) {
                back = c.cast(-1L);
            } else if (c == double.class) {
                back = c.cast(-1D);
            } else if (c == int.class) {
                back = c.cast(-1); //ha
            } else if (c == short.class) {
                back = c.cast((short) -1);
            } else if (c == byte.class) {
                back = c.cast((byte) -1);
            }
            return back;
        }
        return null;
    }

    public static Set<String> matchClosestKeys(Map<String, ?> map, String search) {
        return map.keySet().stream().filter(k -> k.startsWith(search)).collect(Collectors.toSet());
    }

    public static <T> List<T> matchClosestValues(Map<String, T> map, String search) {
        return Reflections.matchClosestKeys(map, search).stream().map(map::get).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> List<T> nonFixedList(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    public static <I, R> R nullSafeMutation(I in, Function<I, R> act) {
        Validate.notNull(in);
        return act.apply(in);
    }

    public static void operateLock(Lock lock, Runnable operation) {
        lock.lock();
        try {
            operation.run();
        } finally {
            lock.unlock();
        }
    }

    public static <R> R operateLock(Lock lock, Supplier<R> operation) {
        lock.lock();
        try {
            return operation.get();
        } finally {
            lock.unlock();
        }
    }

    public static void trace() {
        trace(10);
    }

    public static void trace(int length) {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder("Callback history:");
        for (int i = 2; i < elems.length && i < length + 2; i++) {
            sb.append(String.format("\n\tCalled from:\t%s#%s:%d\t\tFile: %s", elems[i].getClassName(), elems[i].getMethodName(), elems[i].getLineNumber(), elems[i].getFileName()));
        }
        Debugger.print(Level.INFO, sb.toString());
    }

    public static UUID parseUUID(String uuid) {
        Validate.isTrue(uuid.length() == 32 || uuid.length() == 36, "Invalid UUID format supplied");
        if (uuid.length() == 36) {
            return UUID.fromString(uuid);
        } else {
            return UUID.fromString(uuid.substring(0, 8)
                    + "-" + uuid.substring(8, 12)
                    + "-" + uuid.substring(12, 16)
                    + "-" + uuid.substring(16, 20)
                    + "-" + uuid.substring(20, 32));
        }
    }

    public static String objectString(Object in) {
        if (in == null) {
            return "null";
        }
        return in.getClass().getName() + "@" + Integer.toHexString(in.hashCode());
    }

    public static Class<?> getArrayClass(Class<?> componentType) throws ClassNotFoundException {
        ClassLoader classLoader = componentType.getClassLoader();
        String name;
        if (componentType.isArray()) {
            name = "[" + componentType.getName();
        } else if (componentType == boolean.class) {
            name = "[Z";
        } else if (componentType == byte.class) {
            name = "[B";
        } else if (componentType == char.class) {
            name = "[C";
        } else if (componentType == double.class) {
            name = "[D";
        } else if (componentType == float.class) {
            name = "[F";
        } else if (componentType == int.class) {
            name = "[I";
        } else if (componentType == long.class) {
            name = "[J";
        } else if (componentType == short.class) {
            name = "[S";
        } else {
            name = "[L" + componentType.getName() + ";";
        }
        return classLoader != null ? classLoader.loadClass(name) : Class.forName(name);
    }

    public static <T extends Number> T convertNumber(Number in, Class<T> out) {
        if (Primitives.isWrapperType(out)) {
            out = Primitives.unwrap(out);
        }
        if (out == int.class) {
            return (T) (Number) in.intValue();
        } else if (out == byte.class) {
            return (T) (Number) in.byteValue();
        } else if (out == short.class) {
            return (T) (Number) in.shortValue();
        } else if (out == long.class) {
            return (T) (Number) in.longValue();
        } else if (out == float.class) {
            return (T) (Number) in.floatValue();
        } else if (out == double.class) {
            return (T) (Number) in.doubleValue();
        } else {
            return (T) in; //CCE
        }
    }

    public static Optional<Integer> parseInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(String s) {
        try {
            return Optional.of(Double.parseDouble(s));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Float> parseFloat(String s) {
        try {
            return Optional.of(Float.parseFloat(s));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Short> parseShort(String s) {
        try {
            return Optional.of(Short.parseShort(s));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(String s) {
        try {
            return Optional.of(Long.parseLong(s));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Byte> parseByte(String s) {
        try {
            return Optional.of(Byte.parseByte(s));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static String properEnumName(Enum<?> val) {
        String s = val.name().toLowerCase();
        char[] ch = s.toCharArray();
        boolean skip = false;
        for (int i = 0; i < ch.length; i++) {
            if (skip) {
                skip = false;
                continue;
            }
            if (i == 0) {
                ch[i] = Character.toUpperCase(ch[i]);
                continue;
            }
            if (ch[i] == '_') {
                ch[i] = ' ';
                if (i < ch.length - 1) {
                    ch[i + 1] = Character.toUpperCase(ch[i + 1]);
                }
                skip = true;
            }
        }
        return new String(ch).intern();
    }

    public static String stackTraceToString(Throwable t) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t.toString();
    }

}
