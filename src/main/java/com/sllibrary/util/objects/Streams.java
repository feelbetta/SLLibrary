package com.sllibrary.util.objects;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {

    private Streams() {
    }

    @SafeVarargs
    public static <T> Stream<T> stream(T... obj) {
        return Arrays.stream(obj);
    }

    public static <T> Stream<T> stream(Iterable<T> itr) {
        if (itr instanceof Collection) {
            return ((Collection<T>) itr).stream();
        } else {
            return StreamSupport.stream(itr.spliterator(), false);
        }
    }

    @SafeVarargs
    public static <T> Stream<T> concat(Stream<T>... streams) {
        return Stream.of(streams).reduce(Stream.empty(), Stream::concat);
    }

    public static <T> IntStream concat(IntStream... intStreams) {
        return Stream.of(intStreams).reduce(IntStream.empty(), IntStream::concat);
    }
}
