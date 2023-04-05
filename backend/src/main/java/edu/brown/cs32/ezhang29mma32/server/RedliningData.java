package edu.brown.cs32.ezhang29mma32.server;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs32.ezhang29mma32.Adapters;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.BoundingBox;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.FeatureCollection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * LoadRedliningData is a final class which stores the historical redlining data used for the
 * API endpoints
 */
public final class RedliningData {
  private FeatureCollection featureCollection;

  /**
   * Creates a new empty RedliningData object.
   */
  public RedliningData() {
    this.featureCollection = null;
  }

  /**
   * Creates a new RedliningData object.
   *
   * @param geoJsonPath - the path to the redlining data GeoJson
   * @throws IOException if encountered while reading the GeoJson file
   */
  public RedliningData(String geoJsonPath) throws IOException {
    loadFromGeoJSON(geoJsonPath);
  }

  public void loadFromGeoJSON(String geoJsonPath) throws IOException {
    JsonAdapter<FeatureCollection> geoJsonadapter =
        Adapters.ofClass(FeatureCollection.class);
    BufferedReader br = new BufferedReader(new FileReader(geoJsonPath));
    String geoJson = br.lines().collect(Collectors.joining());
    this.featureCollection = geoJsonadapter.fromJson(geoJson);
  }

  public void clear() {
    this.featureCollection = null;
  }

  public FeatureCollection getFeatureCollection() {
    if (this.featureCollection == null) {
      throw new IllegalStateException("no GeoJSON data has been loaded");
    }
    return this.featureCollection.copy();
  }

  public FeatureCollection filterByBounds(BoundingBox boundingBox) {
    if (this.featureCollection == null) {
      throw new IllegalStateException("no GeoJSON data has been loaded");
    }
    return this.featureCollection.withinBoundingBox(boundingBox);
  }

  public FeatureCollection searchByDescription(String query) {
    if (this.featureCollection == null) {
      throw new IllegalStateException("no GeoJSON data has been loaded");
    }
    return this.featureCollection.searchByDescription(query);
  }
}
