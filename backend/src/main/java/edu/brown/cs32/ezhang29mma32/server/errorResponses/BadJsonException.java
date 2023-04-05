package edu.brown.cs32.ezhang29mma32.server.errorResponses;

import java.util.Map;

public class BadJsonException extends ServerResponseException {
  public BadJsonException(String message, String json) {
    super(message, ErrorCode.BADJSON, Map.of());
  }
}
