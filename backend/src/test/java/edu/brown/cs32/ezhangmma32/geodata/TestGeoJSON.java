package edu.brown.cs32.ezhangmma32.geodata;

import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.server.State;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TestGeoJSON {
    private static GeoJSON.FeatureCollection fc;
    @BeforeAll
    public static void setupBeforeAll() {
        State state = new State();
        try {
            state.init();
            fc = state.getRedliningLayer().getFeatureCollection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void holcGradeSet() {
        Set<String> set = new HashSet<>();
        for (GeoJSON.Feature f: fc.features()) {
            set.add((String) f.props().get("holc_grade"));
        }
        System.out.println(set);
    }
}
