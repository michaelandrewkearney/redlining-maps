package edu.brown.cs32.ezhang29mkearne1.server.errorResponses;

import java.util.Map;

public class BadJsonException extends ServerResponseException {
  public BadJsonException(String message, String json) {
    super(message, ErrorCode.BADJSON, Map.of());
  }
}
