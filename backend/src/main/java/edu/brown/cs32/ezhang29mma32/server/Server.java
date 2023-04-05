package edu.brown.cs32.ezhang29mma32.server;
import static spark.Spark.after;

import java.io.IOException;
import spark.Spark;

/**
 * Class for the server that opens up ability for the user to pass in and receive API calls.
 * API endpoints for both CSV & Weather, and structuring of the cached weather caller are also implemented here.
 */
public final class Server {
  private final static String REDLINING_DATA_PATH = "src/main/geodata/historical_redlining.json";

  public static void main(String[] args) throws IOException {
    RedliningData redliningData = new RedliningData();

    Spark.port(3232);
    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "*");
    });

    Spark.get("/loadGeoJson", new LoadGeoJsonHandler(redliningData));
    Spark.get("/filterWithinBounds", new FilterWithinBoundsHandler(redliningData));
    Spark.get("/searchByDescription", new SearchByDescriptionHandler(redliningData));
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}
