package edu.brown.cs32.ezhang29mma32.server;

import edu.brown.cs32.ezhang29mma32.Adapters;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.FeatureCollection;
import edu.brown.cs32.ezhang29mma32.server.LoadGeoJsonHandler.LoadSuccessResponse.LoadParameters;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadGeoJsonHandler implements Route {
  private final RedliningData redliningData;

  public LoadGeoJsonHandler(RedliningData redliningData) {
    this.redliningData = redliningData;
  }

  public record LoadSuccessResponse(String result, FeatureCollection resultFeatureCollection, LoadParameters parameters) {
    public LoadSuccessResponse(FeatureCollection featureCollection, LoadParameters parameters) {
      this("success", featureCollection, parameters);
    }
    public record LoadParameters(String filepath) {}
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String filepath = request.queryParams("filepath");
      redliningData.loadFromGeoJSON(filepath);
      LoadSuccessResponse successResponse = new LoadSuccessResponse(
          redliningData.getFeatureCollection(),
          new LoadParameters(filepath)
      );
      return Adapters.ofClass(LoadSuccessResponse.class).indent("    ").toJson(successResponse);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
