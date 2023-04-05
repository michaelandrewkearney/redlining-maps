package edu.brown.cs32.ezhang29mma32.server;

import edu.brown.cs32.ezhang29mma32.Adapters;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.FeatureCollection;
import edu.brown.cs32.ezhang29mma32.server.SearchByDescriptionHandler.SuccessResponse.Parameters;
import spark.Request;
import spark.Response;
import spark.Route;

final class SearchByDescriptionHandler implements Route {
  private final RedliningData redliningData;

  SearchByDescriptionHandler(RedliningData redliningData) {
    this.redliningData = redliningData;
  }

  public record SuccessResponse(String result, FeatureCollection matches, Parameters parameters) {
    public record Parameters(String query) {};
    public SuccessResponse(FeatureCollection featureCollection, Parameters parameters) {
      this("success", featureCollection, parameters);
    }
  }
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String query = request.queryParams("query");
      FeatureCollection resultFeatureCollection = this.redliningData.searchByDescription(query);
      SuccessResponse successResp = new SuccessResponse(resultFeatureCollection, new Parameters(query));
      return Adapters.ofClass(SuccessResponse.class).indent("    ").toJson(successResp);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
