package edu.brown.cs32.ezhangmma32.server.layer.search;

import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.server.MultiLayerState;
import edu.brown.cs32.ezhang29mkearne1.server.ServerState;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.CachedSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.ExpensiveSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestCachedSearcher {

    private GeoJSON.FeatureCollection fc;

    @BeforeEach
    public void beforeEach() {
        MultiLayerState state = new MultiLayerState();
        state.loadLayer("maplayers/redlining/test.json");
        fc = state.getLoadedLayer("maplayers/redlining/test.json").getFeatureCollection();
    }
    @Test
    public void testCachedSearcher() {
        CachedSearcher<GeoJSON.FeatureCollection, GeoJSON.Feature> cs = new CachedSearcher<>(new ExpensiveSearcher<>(fc));
        int i = 0;
        while (i < 10) {
            cs.search(new ServerState.StringFeatureFilterFunction((f) -> (f.type() == "Feature"), "Feature"));
            i++;
        }
        assertNotEquals(0, cs.getStats().hitCount());
    }
}
