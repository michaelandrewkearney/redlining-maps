package edu.brown.cs32.ezhang29mkearne1.server;
import static spark.Spark.after;

import java.io.File;
import java.net.URL;

import edu.brown.cs32.ezhang29mkearne1.server.handler.FilterGeoJSON;
import edu.brown.cs32.ezhang29mkearne1.server.handler.LoadGeoJsonHandler;
import edu.brown.cs32.ezhang29mkearne1.server.handler.SearchGeoJSONHandler;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.DatasourceException;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.IllegalFilepathException;
import spark.Spark;

/**
 * Class for the server that opens up ability for the user to pass in and receive API calls.
 * API endpoints for both CSV & Weather, and structuring of the cached weather caller are also implemented here.
 */
public final class Server {
  private final static String REDLINING_DATA_PATH = "maplayers/redlining/usa.json";
  private final static String PROVIDENCE_DATA_PATH = "maplayers/redlining/providenceri.json";

  public static void main(String[] args)
      throws DatasourceException, IllegalFilepathException {
    Spark.port(3232);
    after((request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
              response.header("Access-Control-Allow-Headers", "*");
            });
    start();
  }
  public static void start() throws DatasourceException, IllegalFilepathException {
    start(REDLINING_DATA_PATH);
  }

  public static void start(String filepath) throws DatasourceException, IllegalFilepathException {
    System.out.println("Starting server...");
    System.out.println("Loading USA redlining data...");
    ServerState state = new ServerState("");
    state.load(filepath);

    Spark.get("/loadGeoJSON", new LoadGeoJsonHandler(state));
    Spark.get("/filterGeoJSON", new FilterGeoJSON(state));
    Spark.get("/searchGeoJSON", new SearchGeoJSONHandler(state));
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
