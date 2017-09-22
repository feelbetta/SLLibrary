package com.sllibrary.util.trackers;

import com.sllibrary.util.objects.Optionals;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DataTracker<K, V> implements Trackable<K, V>, Purgeable {

    private final Plugin plugin;

    private final Map<K, V> data = new ConcurrentHashMap<>();

    public DataTracker(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isTracked(K key) {
        return this.data.containsKey(key);
    }

    @Override
    public Optional<V> get(K key) {
        return Optionals.getOptionalOf(this.data.get(key));
    }

    @Override
    public void track(K key, V value) {
        this.data.put(key, value);
    }

    @Override
    public void untrack(K key) {
        this.data.remove(key);
    }

    @Override
    public void purge() {
        this.data.clear();
    }
}
