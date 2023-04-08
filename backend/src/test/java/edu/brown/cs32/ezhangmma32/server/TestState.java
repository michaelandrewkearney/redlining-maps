package edu.brown.cs32.ezhangmma32.server;

import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.server.MultiLayerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestState {

    private MultiLayerState state;

    @BeforeEach
    public void setupBeforeEach() {
        state = new MultiLayerState();
    }

    @Test
    public void testMapLayerList() {
        assertNotEquals(0, state.getAvailableLayerCount());
        assertTrue(state.getAvailableLayers().contains("maplayers/redlining/test.json"));
    }

    @Test
    public void testLoadMapLayer() {
        assertTrue(state.loadLayer("maplayers/redlining/test.json"));
        GeoJSON.FeatureCollection c = state.getLoadedLayer("maplayers/redlining/test.json").getFeatureCollection();
        System.out.println(c);
        System.out.println(GeoJSON.FeatureCollectionLike.getAdapter().toJson(c));
    }

}
