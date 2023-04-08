package edu.brown.cs32.ezhang29mkearne1.server.handler;

import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.server.ServerState;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.BadRequestException;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.ErrorResponse;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.ServerResponseException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public final class LoadGeoJsonHandler implements Route {
  private final ServerState state;

  public LoadGeoJsonHandler(ServerState state) {
    this.state = state;
  }

  public record LoadResponse(String result, String endpoint, String filepath) {
    public LoadResponse(String filepath) {
      this("success", "loadGeoJSON", filepath);
    }
  }

    @Override
    public Object handle(Request request, Response response) {
      String filepath = request.queryParams("filepath");
      if (filepath == null) {
        return new ErrorResponse(new BadRequestException("Need parameter 'filepath'", Map.copyOf(request.params())));
      }
      try {
        System.out.println("loading file");
        state.load(filepath);
        System.out.println("loaded file");
        LoadResponse resp = new LoadResponse(filepath);
        return Adapters.ofClass(LoadResponse.class).toJson(resp);
      } catch (ServerResponseException e) {
        return new ErrorResponse(e).serialize();
      }
    }
  }
