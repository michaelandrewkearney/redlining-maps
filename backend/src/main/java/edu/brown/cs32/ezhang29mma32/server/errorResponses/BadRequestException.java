package edu.brown.cs32.ezhang29mma32.server.errorResponses;

import java.util.Map;

public class BadRequestException extends ServerResponseException {
  public BadRequestException(String message, Map<String, Object> parameters) {
    super(message, ErrorCode.BADREQUEST, parameters);
  }
}
