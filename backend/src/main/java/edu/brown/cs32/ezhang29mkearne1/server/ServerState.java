package edu.brown.cs32.ezhang29mkearne1.server;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.IllegalFilepathException;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.CachedSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.ExpensiveSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerState {
  private FeatureCollection featureCollection;
  private Searcher<GeoJSON.FeatureCollection, GeoJSON.Feature> filterer;
  private Searcher<GeoJSON.FeatureCollection, GeoJSON.Feature> searcher;

 // TODO: protect file access
  private final String allowedDir;

    public record StringFeatureFilterFunction(Searcher.FilterFunction<GeoJSON.Feature> function, String query) implements Searcher.FilterFunction<GeoJSON.Feature> {
        @Override
        public boolean run(GeoJSON.Feature filtered) {
            return function.run(filtered);
        }
        @Override
        public boolean equals(Object that) {
            if (that.getClass() != this.getClass()) {
                return false;
            }
            return ((StringFeatureFilterFunction) that).query().equals(query());
        }
        @Override
        public int hashCode() {
            return query.hashCode();
        }
    }

  public ServerState(String allowedDir) {
    this.featureCollection = null;
    this.allowedDir = allowedDir;
  }

  public void load(String filepath)
      throws IllegalFilepathException, DatasourceException {
    try {
      JsonAdapter<FeatureCollection> adapter = GeoJSON.FeatureCollectionLike.getAdapter();
      BufferedReader br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(filepath).getPath()));
      String geoJson = br.lines().collect(Collectors.joining());
      br.close();
      this.featureCollection = adapter.fromJson(geoJson);
      filterer = new ExpensiveSearcher<>(this.featureCollection);
      searcher = new CachedSearcher<>(new ExpensiveSearcher<>(this.featureCollection));
    } catch (IOException e) {
      throw new DatasourceException("Unable to open " + filepath, Map.of());
    }
  }

  public void clear() {
    this.featureCollection = null;
    this.searcher = null;
    this.filterer = null;
  }

  public FeatureCollection getFeatureCollection() throws DatasourceException {
    if (this.featureCollection == null) {
      throw new DatasourceException("No GeoJSON data has been loaded", Map.of());
    }
    return this.featureCollection;
  }

    public Searcher<GeoJSON.FeatureCollection, GeoJSON.Feature> getFilterer() throws DatasourceException {
        if (this.filterer == null) {
            throw new DatasourceException("No filterer has been constructed.", Map.of());
        }
        return this.filterer;
    }

    public Searcher<GeoJSON.FeatureCollection, GeoJSON.Feature> getSearcher() throws DatasourceException {
        if (this.searcher == null) {
            throw new DatasourceException("No searcher has been constructed.", Map.of());
        }
        return this.searcher;
    }

    public FeatureCollection search(String query, Searcher.FilterFunction<GeoJSON.Feature> function) {
        FeatureCollection results = searcher.search(new StringFeatureFilterFunction(function, query));
        return results;
    }
}
