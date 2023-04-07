package edu.brown.cs32.ezhang29mkearne1.server;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class State {
    private static final String redliningLayer = "maplayers/redlining/usa.json";
    private static final String maplayersDir = "maplayers";
    private MapLayer redlining = null;
    private final Map<String, File> availableLayers;
    private final Map<String, MapLayer> loadedLayers;

    public State() {
        availableLayers = constructFileMap("", new File(State.class.getClassLoader().getResource(maplayersDir).getPath()));
        loadedLayers = new HashMap<>();
    }

    public void init() throws IOException {
        if (loadLayer(redliningLayer)) {
            redlining = getLoadedLayer(redliningLayer);
            return;
        }
        throw new IOException();
    }

    public MapLayer getRedliningLayer() {
        return redlining;
    }

    private Map<String, File> constructFileMap(String parentName, File file) {
        Map<String, File> files = new HashMap<>();
        if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                files.putAll(constructFileMap(parentName + file.getName() + "/", subfile));
            }
        } else {
            files.put(parentName + file.getName(), file);
        }
        return files;
    }

    public boolean isLayerAvailable(String filepath) {
        return availableLayers.containsKey(filepath) && availableLayers.get(filepath).isFile();
    }

    public boolean isLayerLoaded(String filepath) {
        return loadedLayers.containsKey(filepath);
    }

    public boolean loadLayer(String filepath) {
        if (!isLayerAvailable(filepath)) {
            return false;
        }
        try {
            MapLayer layer = new MapLayer(filepath);
            loadedLayers.put(filepath, layer);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public MapLayer getLoadedLayer(String filepath) {
        if (!isLayerLoaded(filepath)) {
            return null;
        }
        return loadedLayers.get(filepath);
    }

    public Set<String> getAvailableLayers() {
        return Set.copyOf(availableLayers.keySet());
    }

    public int getAvailableLayerCount() {
        return availableLayers.size();
    }

    public int getLoadedLayersCount() {
        return loadedLayers.size();
    }
}
