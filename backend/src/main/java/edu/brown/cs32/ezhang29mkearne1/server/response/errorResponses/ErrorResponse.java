package edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses;

;
import com.squareup.moshi.Moshi;

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
