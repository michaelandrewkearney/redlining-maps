package edu.brown.cs32.ezhang29mkearne1.server;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.Feature;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import edu.brown.cs32.ezhang29mkearne1.server.search.CachedSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.search.ExpensiveSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.search.Searcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * LoadRedliningData is a final class which stores the historical redlining data used for the
 * API endpoints
 */
public final class MapLayer {
  private final String filepath;
  private final FeatureCollection featureCollection;

  /**
   * Creates a new empty RedliningData object.
   */
  public MapLayer(String filepath) throws IOException {
    try(FileReader fr = new FileReader(MapLayer.class.getClassLoader().getResource(filepath).getPath());
            BufferedReader br = new BufferedReader(fr)) {
      this.filepath = filepath;
      JsonAdapter<FeatureCollection> geoJsonadapter =
              GeoJSON.FeatureCollectionLike.getAdapter();
      String geoJson = br.lines().collect(Collectors.joining());
      this.featureCollection = geoJsonadapter.fromJson(geoJson);
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public Searcher<FeatureCollection, Feature> generateSearcher() {
    return new ExpensiveSearcher<>(featureCollection);
  }

  public Searcher<FeatureCollection, Feature> generateSearcher(int maxSize, int expireAfterWriteInMins) {
    return new CachedSearcher<>(this.generateSearcher(), maxSize, expireAfterWriteInMins);
  }

  public FeatureCollection getFeatureCollection() {
    if (this.featureCollection == null) {
      throw new IllegalStateException("No GeoJSON data has been loaded");
    }
    return this.featureCollection.copy();
  }
}
