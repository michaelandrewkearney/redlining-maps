package edu.brown.cs32.ezhang29mkearne1.server;
import static spark.Spark.after;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import spark.Spark;

/**
 * Class for the server that opens up ability for the user to pass in and receive API calls.
 * API endpoints for both CSV & Weather, and structuring of the cached weather caller are also implemented here.
 */
public final class Server {
  private final static String REDLINING_DATA_PATH = "src/main/geodata/usa.json";

  public static void main(String[] args) throws IOException {

    Spark.port(3232);
    after((request, response) -> {
              response.header("Access-Control-Allow-Origin", "*");
              response.header("Access-Control-Allow-Methods", "*");
              response.header("Access-Control-Allow-Headers", "*");
            });
    start();
  }

  public static void start() {
    ServerState state = new ServerState(List.of());
    Spark.get("/loadGeoJSON", new LoadGeoJsonHandler(state));
    Spark.get("/filterGeoJSON", new FilterWithinBoundsHandler(state));
    Spark.get("/searchGeoJSON", new SearchGeoJSONHandler(state));
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
