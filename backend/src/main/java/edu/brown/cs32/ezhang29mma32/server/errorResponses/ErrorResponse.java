package edu.brown.cs32.ezhang29mma32.server.errorResponses;

;
import edu.brown.cs32.ezhang29mma32.Adapters;
import java.util.Map;

/**
 * @param
 */
public record ErrorResponse(ServerResponseException exception) {

  public String serialize() {
    Map<String, Object> errorMap = Map.of(
        "result", exception.getErrorCode().toStringCode(),
        "message", exception.getMessage(),
        "parameters", exception.getParameters()
    );
    return Adapters.ofMap().indent("    ").toJson(errorMap);
  }
}
