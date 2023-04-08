package edu.brown.cs32.ezhangmma32.geodata;

import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.server.MultiLayerState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TestGeoJSON {
    private static GeoJSON.FeatureCollection fc;
    @BeforeAll
    public static void setupBeforeAll() {
        MultiLayerState state = new MultiLayerState();
        state.loadLayer("maplayers/redlining/test.json");
        fc = state.getLoadedLayer("maplayers/redlining/test.json").getFeatureCollection();
    }
    @Test
    public void testParsing() {
        Set<String> set = new HashSet<>();
        for (GeoJSON.FeatureLike f: fc.features()) {
            set.add((String) f.props().get("holc_grade"));
        }
        System.out.println(set);
    }
}
