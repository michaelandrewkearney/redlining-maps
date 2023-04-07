package edu.brown.cs32.ezhang29mkearne1.server;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.IllegalFilepathException;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.CachedSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.ExpensiveSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerState {
  private FeatureCollection featureCollection;
  private Searcher<FeatureCollection, GeoJSON.Feature> filterer;
  private Searcher<FeatureCollection, GeoJSON.Feature> searcher;

 // TODO: protect file access
  private final List<String> allowedDirs;
  private String filepath;

  public ServerState(List<String> allowedDirs) {
    this.featureCollection = null;
    this.allowedDirs = allowedDirs;
    this.filepath = null;
  }

  public void load(String filepath)
      throws IllegalFilepathException, DatasourceException {
    if (false) {
      throw new IllegalFilepathException("msg", Map.of());
    }
    try {
      JsonAdapter<FeatureCollection> geoJsonadapter =
          Adapters.ofClass(FeatureCollection.class);
      BufferedReader br = new BufferedReader(new FileReader(filepath));
      String geoJson = br.lines().collect(Collectors.joining());
      this.featureCollection = geoJsonadapter.fromJson(geoJson);
      this.filepath = filepath;
      filterer = new ExpensiveSearcher<>(this.featureCollection);
      searcher = new CachedSearcher<>(new ExpensiveSearcher<>(this.featureCollection));
    } catch (IOException e) {
      throw new DatasourceException("msg", Map.of());
    }
  }
  public void clear() {
    this.featureCollection = null;
    this.filepath = null;
    this.searcher = null;
    this.filterer = null;
  }

  public FeatureCollection getFeatureCollection() throws DatasourceException {
    if (this.featureCollection == null) {
      throw new DatasourceException("No GeoJSON data has been loaded", Map.of());
    }
    return this.featureCollection.copy();
  }

    public Searcher<FeatureCollection, GeoJSON.Feature> getFilterer() throws DatasourceException {
        if (this.filterer == null) {
            throw new DatasourceException("No filterer has been constructed.", Map.of());
        }
        return this.filterer;
    }

    public Searcher<FeatureCollection, GeoJSON.Feature> getSearcher() throws DatasourceException {
        if (this.searcher == null) {
            throw new DatasourceException("No searcher has been constructed.", Map.of());
        }
        return this.searcher;
    }
}
