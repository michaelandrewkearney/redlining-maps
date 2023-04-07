package edu.brown.cs32.ezhang29mkearne1.server;

import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.server.LoadGeoJsonHandler.LoadResponse.LoadParameters;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.BadRequestException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ErrorResponse;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ServerResponseException;
import edu.brown.cs32.ezhang29mkearne1.server.response.ServerResponses.FeatureCollectionResponse;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public final class LoadGeoJsonHandler implements Route {
  private final ServerState state;

  public LoadGeoJsonHandler(ServerState state) {
    this.state = state;
  }

  public record LoadResponse(String result, String filepath, LoadParameters parameters) {
    public LoadResponse(String filepath, LoadParameters parameters) {
      this("success", filepath, parameters);
    }
    public record LoadParameters(String filepath) {}
  }

    @Override
    public Object handle(Request request, Response response) throws ServerResponseException {
      String filepath = request.queryParams("filepath");
      try {
        state.load(filepath);
        LoadResponse resp = new LoadResponse(filepath, new LoadParameters(filepath));
        return Adapters.ofClass(LoadResponse.class).toJson(resp);
      } catch (ServerResponseException e) {
        return new ErrorResponse(e).serialize();
      }
    }
  }
