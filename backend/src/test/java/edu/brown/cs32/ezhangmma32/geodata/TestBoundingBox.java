package edu.brown.cs32.ezhangmma32.geodata;

import edu.brown.cs32.ezhang29mkearne1.geoData.BoundingBox;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.server.MultiLayerState;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.CachedSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.ExpensiveSearcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBoundingBox {

    private static GeoJSON.FeatureCollection fc;
    @BeforeAll
    public static void setupBeforeAll() {
        MultiLayerState state = new MultiLayerState();
        state.loadLayer("maplayers/redlining/test.json");
        fc = state.getLoadedLayer("maplayers/redlining/test.json").getFeatureCollection();
    }
    @Test
    public void boundingBoxRandomTesting() {
        ExpensiveSearcher<GeoJSON.FeatureCollection, GeoJSON.Feature> cs = new ExpensiveSearcher<>(fc);
        Random r = new Random();
        long sizeCount = 0;
        int i = 0;
        while (i < 100000) {
            BoundingBox box = new BoundingBox(r.nextDouble()*360.0-180.0, r.nextDouble()*180.0-90.0, r.nextDouble()*360.0-180.0, r.nextDouble()*180.0-90.0);
            sizeCount += cs.search((f) -> {
                return box.contains(f);
            }).size();
            i++;
        }
        System.out.println("Number of BoundingBoxes tested for validity: " + sizeCount);
        assertNotEquals(0, sizeCount);
    }

    @Test
    public void boundingBoxWithinBoundsRandomTesting() {
        Random r = new Random();
        int i = 0;
        while (i < 100000) {
            double minLon = r.nextDouble()*400.0-200.0;
            double minLat = r.nextDouble()*200.0-100.0;
            double maxLon = r.nextDouble()*400.0-200.0;
            double maxLat = r.nextDouble()*200.0-100.0;
            if (isValidLon(minLon) && isValidLat(minLat) && isValidLon(maxLon) && isValidLat(maxLat)) {
                try {
                    assertDoesNotThrow(() -> new BoundingBox(minLon, minLat, maxLon, maxLat));
                } catch(AssertionError e) {
                    System.out.println("Threw error: " + Arrays.toString(new double[]{minLon, minLat, maxLon, maxLat}));
                    e.printStackTrace();
                }
            } else {
                assertThrows(AssertionError.class, () -> new BoundingBox(minLon, minLat, maxLon, maxLat));
            }
            i++;
        }
    }

    @Test
    public void boundingBoxContainsRandomTesting() {
        ExpensiveSearcher<GeoJSON.FeatureCollection, GeoJSON.Feature> cs = new ExpensiveSearcher<>(fc);
        Random r = new Random();
        long sizeCount = 0;
        int i = 0;
        while (i < 1000) {
            BoundingBox box = new BoundingBox(r.nextDouble()*360.0-180.0, r.nextDouble()*180.0-90.0, r.nextDouble()*360.0-180.0, r.nextDouble()*180.0-90.0);
            sizeCount += cs.search((f) -> {
                boolean contains = box.contains(f);
                Arrays.stream(f.geometry().coordinates().toFlatList()).forEach((p) -> {
                    assert(p.isContained(box) == contains);
                });
                return contains;
            }).size();
            i++;
        }
        System.out.println("Number of FeatureCollection tested for containment: " + sizeCount);
        assertNotEquals(0, sizeCount);
    }
}
