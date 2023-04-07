package edu.brown.cs32.ezhangmma32;


import static edu.brown.cs32.ezhang29mkearne1.server.Server.start;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.squareup.moshi.JsonReader;
import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.server.FilterWithinBoundsHandler;
import edu.brown.cs32.ezhang29mkearne1.server.LoadGeoJsonHandler;
import edu.brown.cs32.ezhang29mkearne1.server.LoadGeoJsonHandler.LoadResponse;
import edu.brown.cs32.ezhang29mkearne1.server.MapLayer;
import edu.brown.cs32.ezhang29mkearne1.server.SearchGeoJSONHandler;
import edu.brown.cs32.ezhang29mkearne1.server.ServerState;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestGeoJSONHandlers {
  private final ServerState serverState = new ServerState("src/test/resources");

  @BeforeAll
  public static void setupBeforeEverything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    serverState.clear();

    // In fact, restart the entire Spark server for every test!
    Spark.get("/loadGeoJSON", new LoadGeoJsonHandler(serverState));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.stop();
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific, singular API endpoint/param
   *
   * @param apiCall the call string, including endpoint
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  static private String readResponse(HttpURLConnection clientConnection) throws IOException {
    try (Buffer buffer = new Buffer()) {
      JsonReader reader = JsonReader.of(buffer.readFrom(clientConnection.getInputStream()));
      return reader.nextSource().readUtf8();
    }
  }

  @Test
  public void testLoad() throws IOException {
    String mockFilepath = "src/test/resources/mockRedliningData.json";
    HttpURLConnection conn = tryRequest("/loadGeoJSON?filepath=src/test/resources/mockRedliningData.json");
    String rawResponse = readResponse(conn);
    LoadResponse successResponse = Adapters.ofClass(LoadResponse.class).fromJson(rawResponse);
    assertEquals(successResponse.result(), "success");
    assertEquals(successResponse.filepath(), mockFilepath);
  }
}