package edu.brown.cs32.ezhang29mkearne1.server;

import edu.brown.cs32.ezhang29mkearne1.geoData.BoundingBox;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.Feature;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.BadRequestException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ErrorResponse;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.CachedSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.ExpensiveSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher;
import edu.brown.cs32.ezhang29mkearne1.server.response.ServerResponses;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Map;

final class SearchGeoJSONHandler implements Route {
  private final State state;
  private Searcher<FeatureCollection, Feature> searcher;
  private final Searcher<FeatureCollection, Feature> cachedSearcher;
  private FeatureCollection stashedSearch = new FeatureCollection("FeatureCollection", new ArrayList<>());

  SearchGeoJSONHandler(State state) {
    this.state = state;
    searcher = new ExpensiveSearcher<>(stashedSearch);
    cachedSearcher = new CachedSearcher<>(new ExpensiveSearcher<>(state.getRedliningLayer().getFeatureCollection()), 1000, 360);
  }

  public record SuccessResponse(String result, FeatureCollection matches, Parameters parameters) {
    public record Parameters(String query) {};
    public SuccessResponse(FeatureCollection featureCollection, Parameters parameters) {
      this("success", featureCollection, parameters);
    }
  }
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String query = request.queryParams("query");
    if (query != null) {
      FeatureCollection results = cachedSearcher.search((Feature f) -> {
        f.props().containsKey("area_description_data");
        for (String s : ((Map<String, String>) f.props().get("area_description_data")).values()) {
          if (s.contains(query)) {
            return true;
          }
        }
        return false;
      });
      stashedSearch = results;
      searcher = new ExpensiveSearcher<>(stashedSearch);
    }
    try {
      double minLon = Double.parseDouble(request.queryParams("minLon"));
      double minLat = Double.parseDouble(request.queryParams("minLat"));
      double maxLon = Double.parseDouble(request.queryParams("maxLon"));
      double maxLat = Double.parseDouble(request.queryParams("maxLat"));
      BoundingBox box = new BoundingBox(minLon, minLat, maxLon, maxLat);
      FeatureCollection results = searcher.search((GeoJSON.Feature f) -> f.overlaps(box));
      return new ServerResponses.FeatureCollectionResponse("searchGeoJSON", results).serialize();
    } catch (NumberFormatException e) {
      return new ErrorResponse(new BadRequestException("BoundingBox params required.", Map.copyOf(request.params()))).serialize();
    }
  }
}
