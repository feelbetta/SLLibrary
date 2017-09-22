package com.sllibrary.util.objects;

import java.util.function.Predicate;

public class Predicates {

    private Predicates() {
    }

    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
