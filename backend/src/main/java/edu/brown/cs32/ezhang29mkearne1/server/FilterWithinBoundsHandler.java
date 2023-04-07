package edu.brown.cs32.ezhang29mkearne1.server;

import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.geoData.BoundingBox;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.Feature;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.BadRequestException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ErrorResponse;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.ExpensiveSearcher;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher;
import edu.brown.cs32.ezhang29mkearne1.server.response.ServerResponses;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public final class FilterWithinBoundsHandler implements Route {
  private final ServerState state;

  public FilterWithinBoundsHandler(ServerState state) {
    this.state = state;
  }

  public record SuccessResponse(String result, String endpoint, FeatureCollection filteredGeoJSON, Parameters parameters) {
    public record Parameters(Double minLon, Double minLat, Double maxLon, Double maxLat) {};
    public SuccessResponse(FeatureCollection filteredGeoJSON, Parameters parameters) {
      this("success", "filterWithinBounds", filteredGeoJSON, parameters);
    }
  }

  @Override
  public Object handle(Request request, Response response) {
    try {
      Searcher<FeatureCollection, Feature> searcher = new ExpensiveSearcher<>(this.state.getFeatureCollection());
      double minLon = Double.parseDouble(request.queryParams("minLon"));
      double minLat = Double.parseDouble(request.queryParams("minLat"));
      double maxLon = Double.parseDouble(request.queryParams("maxLon"));
      double maxLat = Double.parseDouble(request.queryParams("maxLat"));

      BoundingBox boundingBox = new BoundingBox(minLon, minLat, maxLon, maxLat);
      FeatureCollection results = searcher.search((GeoJSON.Feature feat) -> boundingBox.contains(feat));
      return new ServerResponses.FeatureCollectionResponse("filterGeoJSON", results).serialize();
    } catch (NumberFormatException e) {
      return new ErrorResponse(new BadRequestException("Must pass four parseable doubles.", Map.copyOf(request.params())));
    } catch (DatasourceException e) {
      return new ErrorResponse(e).serialize();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
