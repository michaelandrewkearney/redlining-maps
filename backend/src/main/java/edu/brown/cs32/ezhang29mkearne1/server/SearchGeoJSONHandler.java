package edu.brown.cs32.ezhang29mkearne1.server;

import edu.brown.cs32.ezhang29mkearne1.geoData.BoundingBox;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.Feature;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.BadJsonException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.BadRequestException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ErrorResponse;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.ExpensiveSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher;
import edu.brown.cs32.ezhang29mkearne1.server.response.ServerResponses;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public final class SearchGeoJSONHandler implements Route {
  private final ServerState state;
  private Searcher<FeatureCollection, Feature> filterer;
  public SearchGeoJSONHandler(ServerState state) {
    this.state = state;
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
      FeatureCollection results = state.getSearcher().search((Feature f) -> {
        f.props().containsKey("area_description_data");
        for (String s : ((Map<String, String>) f.props().get("area_description_data")).values()) {
          if (s.contains(query)) {
            return true;
          }
        }
        return false;
      });
      filterer = new ExpensiveSearcher<>(results);
    }
    try {
      double minLon = Double.parseDouble(request.queryParams("minLon"));
      double minLat = Double.parseDouble(request.queryParams("minLat"));
      double maxLon = Double.parseDouble(request.queryParams("maxLon"));
      double maxLat = Double.parseDouble(request.queryParams("maxLat"));
      BoundingBox box = new BoundingBox(minLon, minLat, maxLon, maxLat);
      FeatureCollection results = filterer.search((GeoJSON.Feature feat) -> box.contains(feat));
      return new ServerResponses.FeatureCollectionResponse("searchGeoJSON", results).serialize();
    } catch (NumberFormatException e) {
      return new ErrorResponse(new BadRequestException("BoundingBox params required.", Map.copyOf(request.params()))).serialize();
    }
  }
}
