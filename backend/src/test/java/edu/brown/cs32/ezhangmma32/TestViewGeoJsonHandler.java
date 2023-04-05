package edu.brown.cs32.ezhangmma32;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.squareup.moshi.JsonReader;
import edu.brown.cs32.ezhang29mma32.server.LoadGeoJsonHandler;
import edu.brown.cs32.ezhang29mma32.server.RedliningData;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestViewGeoJsonHandler {


  @BeforeAll
  public static void setupBeforeEverything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  final RedliningData loadedGeoData = new RedliningData();

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    loadedGeoData.clear();

    // In fact, restart the entire Spark server for every test!
    Spark.get("/loadGeoJson", new LoadGeoJsonHandler(loadedGeoData));

    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadGeoJson");
    Spark.unmap("/viewGeoJson");
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
      reader.setLenient(true);
      return reader.nextSource().readUtf8();
    }
  }

  @Test
  public void testLoad() throws IOException {
    HttpURLConnection conn = tryRequest("/loadGeoJson?filepath=src/test/data/mockRedliningData.json");

    String jsonResponse = readResponse(conn);
    System.out.println(jsonResponse);
  }
}