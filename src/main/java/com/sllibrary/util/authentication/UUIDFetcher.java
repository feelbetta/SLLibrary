package com.sllibrary.util.authentication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import com.sllibrary.SLLibrary;
import com.sllibrary.util.authentication.trackers.NameTracker;
import com.sllibrary.util.authentication.trackers.UUIDTracker;
import com.sllibrary.util.trackers.Purgeable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UUIDFetcher implements Purgeable {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";

    private final UUIDTracker uuidTracker;
    private final NameTracker nameTracker;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private String name;
    private UUID uuid;

    public UUIDFetcher(SLLibrary slLibrary) {
        this.nameTracker = new NameTracker(slLibrary);
        this.uuidTracker = new UUIDTracker(slLibrary);
    }

    private void getUUID(String name, Consumer<Optional<UUID>> action) {
        this.threadPool.execute(() -> action.accept(getUUID(name)));
    }

    public Optional<UUID> getUUID(String name) {
        return getUUIDAt(name, System.currentTimeMillis());
    }

    private void getUUIDAt(String name, long timestamp, Consumer<Optional<UUID>> action) {
        this.threadPool.execute(() -> action.accept(this.getUUIDAt(name, timestamp)));
    }

    private Optional<UUID> getUUIDAt(String name, long timestamp) {
        name = name.toLowerCase();
        if (!this.uuidTracker.isTracked(name)) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUIDFetcher.UUID_URL, name, timestamp / 1000)).openConnection();
                connection.setReadTimeout(5000);
                UUIDFetcher data = UUIDFetcher.gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

                this.uuidTracker.track(name, data.uuid);
                this.nameTracker.track(data.uuid, data.name);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return this.uuidTracker.get(name);
    }

    private void getName(UUID uuid, Consumer<Optional<String>> action) {
        this.threadPool.execute(() -> action.accept(getName(uuid)));
    }

    public Optional<String> getName(UUID uuid) {
        if (!this.nameTracker.isTracked(uuid)) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUIDFetcher.NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
                connection.setReadTimeout(5000);

                UUIDFetcher[] nameHistory = UUIDFetcher.gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher[].class);
                UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];

                this.uuidTracker.track(currentNameData.name.toLowerCase(), uuid);
                this.nameTracker.track(uuid, currentNameData.name);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return this.nameTracker.get(uuid);
    }

    @Override
    public void purge() {
        this.uuidTracker.purge();
        this.nameTracker.purge();
    }
}