package edu.brown.cs32.ezhang29mma32.server;

import edu.brown.cs32.ezhang29mma32.Adapters;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.FeatureCollection;
import edu.brown.cs32.ezhang29mma32.server.LoadGeoJsonHandler.LoadResponse.LoadParameters;
import spark.Request;
import spark.Response;
import spark.Route;

final class LoadGeoJsonHandler implements Route {
  private final RedliningData redliningData;

  public LoadGeoJsonHandler(RedliningData redliningData) {
    this.redliningData = redliningData;
  }

  public record LoadResponse(String result, String filepath, LoadParameters parameters) {
    public LoadResponse(String filepath, LoadParameters parameters) {
      this("success", filepath, parameters);
    }
    public record LoadParameters(String filepath) {}
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String filepath = request.queryParams("filepath");
      redliningData.loadFromGeoJSON(filepath);
      LoadResponse successResponse = new LoadResponse(
          filepath,
          new LoadParameters(filepath)
      );
      return Adapters.ofClass(LoadResponse.class).indent("    ").toJson(successResponse);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
