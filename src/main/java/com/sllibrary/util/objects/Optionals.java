package com.sllibrary.util.objects;

import java.util.Optional;

public class Optionals {

    private Optionals() {
    }

    public static <T> Optional<T> getOptionalOf(T type) {
        return type == null ? Optional.empty() : Optional.of(type);
    }
}
