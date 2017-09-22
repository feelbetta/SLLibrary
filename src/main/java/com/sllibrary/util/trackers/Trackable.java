package com.sllibrary.util.trackers;

import java.util.Optional;

public interface Trackable<K, V> {

    boolean isTracked(K key);

    Optional<V> get(K key);

    void track(K key, V value);

    void untrack(K key);
}
