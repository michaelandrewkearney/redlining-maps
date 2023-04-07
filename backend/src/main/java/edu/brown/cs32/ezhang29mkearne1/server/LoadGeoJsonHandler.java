package edu.brown.cs32.ezhang29mkearne1.server;

import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.server.LoadGeoJsonHandler.LoadResponse.LoadParameters;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.BadRequestException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ErrorResponse;
import edu.brown.cs32.ezhang29mkearne1.server.response.ServerResponses.FeatureCollectionResponse;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public final class LoadGeoJsonHandler implements Route {
  private final State state;

  public LoadGeoJsonHandler(State state) {
    this.state = state;
  }

  public record LoadResponse(String result, String filepath, LoadParameters parameters) {
    public LoadResponse(String filepath, LoadParameters parameters) {
      this("success", filepath, parameters);
    }
    public record LoadParameters(String filepath) {}
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filepath = request.queryParams("filepath");
    if (!state.isLayerAvailable(filepath)) {
      return new ErrorResponse(new BadRequestException(String.format("%s is not valid filepath.", filepath), Map.copyOf(request.params()))).serialize();
    }
    if (state.isLayerLoaded(filepath) || state.loadLayer(filepath)) {
      return new FeatureCollectionResponse("loadGeoJSON", state.getLoadedLayer(filepath).getFeatureCollection());
    }
    return new ErrorResponse(new DatasourceException(String.format("Unable to load file '%s'", filepath), Map.copyOf(request.params())));
    }
  }
