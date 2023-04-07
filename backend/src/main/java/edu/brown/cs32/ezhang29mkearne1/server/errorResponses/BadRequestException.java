package edu.brown.cs32.ezhang29mkearne1.server.errorResponses;

import java.util.Map;

public class BadRequestException extends ServerResponseException {
  public BadRequestException(String message, Map<String, Object> parameters) {
    super(message, ErrorCode.BADREQUEST, parameters);
  }
}
