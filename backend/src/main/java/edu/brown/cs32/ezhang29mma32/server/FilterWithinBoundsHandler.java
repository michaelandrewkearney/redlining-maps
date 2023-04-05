package edu.brown.cs32.ezhang29mma32.server;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs32.ezhang29mma32.Adapters;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.BoundingBox;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.FeatureCollection;
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterWithinBoundsHandler implements Route {
  private final RedliningData redliningData;

  public FilterWithinBoundsHandler(RedliningData redliningData) {
    this.redliningData = redliningData;
  }


  public record SuccessResponse(String result, FeatureCollection resultFeatureCollection, Parameters parameters) {
    public record Parameters(Double minLon, Double minLat, Double maxLon, Double maxLat) {};
    public SuccessResponse(FeatureCollection featureCollection, Parameters parameters) {
      this("success", featureCollection, parameters);
    }
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      Double minLon = Double.parseDouble(request.queryParams("minLon"));
      Double minLat = Double.parseDouble(request.queryParams("minLat"));
      Double maxLon = Double.parseDouble(request.queryParams("maxLon"));
      Double maxLat = Double.parseDouble(request.queryParams("maxLat"));

      BoundingBox boundingBox = new BoundingBox(minLon, minLat, maxLon, maxLat);
      FeatureCollection matchingFeatureCollection = redliningData.filterByBounds(boundingBox);
      SuccessResponse.Parameters params = new SuccessResponse.Parameters(minLon, minLat, maxLon, maxLat);
      SuccessResponse successResponse =
          new SuccessResponse(matchingFeatureCollection, params);

      return Adapters.ofClass(SuccessResponse.class).indent("   ").toJson(successResponse);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
