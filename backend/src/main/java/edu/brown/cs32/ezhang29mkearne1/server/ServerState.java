package edu.brown.cs32.ezhang29mkearne1.server;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.IllegalFilepathException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerState {
  private FeatureCollection featureCollection;

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
    } catch (IOException e) {
      throw new DatasourceException("msg", Map.of());
    }
  }
  public void clear() {
    this.featureCollection = null;
    this.filepath = null;
  }

  public FeatureCollection getFeatureCollection() throws DatasourceException {
    if (this.featureCollection == null) {
      throw new DatasourceException("No GeoJSON data has been loaded", Map.of());
    }
    return this.featureCollection.copy();
  }
}
