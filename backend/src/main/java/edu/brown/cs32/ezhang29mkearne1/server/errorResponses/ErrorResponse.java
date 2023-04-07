package edu.brown.cs32.ezhang29mkearne1.server.errorResponses;

;
import com.squareup.moshi.Moshi;
import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.server.response.ServerResponses;

import java.util.Map;

/**
 * @param
 */
public record ErrorResponse(String result, String msg, String exceptionType) {
  public ErrorResponse(ServerResponseException e) {
    this("error", e.getMessage(), e.getClass().toString());
  }
  public String serialize() {
    return new Moshi.Builder()
            .build()
            .adapter(ErrorResponse.class)
            .toJson(this);
  }
}
