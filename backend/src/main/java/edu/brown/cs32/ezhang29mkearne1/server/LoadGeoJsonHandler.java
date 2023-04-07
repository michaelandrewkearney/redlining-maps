package edu.brown.cs32.ezhang29mkearne1.server;

import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.BadRequestException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ErrorResponse;
import edu.brown.cs32.ezhang29mkearne1.server.errorResponses.ServerResponseException;
import edu.brown.cs32.ezhang29mkearne1.server.response.ServerResponses.FeatureCollectionResponse;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
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
        //return error response
      }
      //filepath = filepath.replace("%2F", '/');
      System.out.println(filepath + " is file: " + new File(filepath).isFile());
      try {
        state.load(filepath);
        LoadResponse resp = new LoadResponse(filepath);
        return Adapters.ofClass(LoadResponse.class).toJson(resp);
      } catch (ServerResponseException e) {
        return new ErrorResponse(e).serialize();
      }
    }
  }
