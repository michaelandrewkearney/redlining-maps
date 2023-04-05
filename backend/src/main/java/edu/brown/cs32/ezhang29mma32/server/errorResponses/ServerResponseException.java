package edu.brown.cs32.ezhang29mma32.server.errorResponses;

import edu.brown.cs32.ezhang29mma32.server.errorResponses.ErrorCode;
import java.util.Collections;
import java.util.Map;

public abstract class ServerResponseException extends Exception {
  private final ErrorCode errorCode;
  private final Map<String, Object> parameters;
  public ServerResponseException(String message, ErrorCode errorCode, Map<String, Object> parameters) {
    super(message);
    this.parameters = Collections.unmodifiableMap(parameters);
    this.errorCode = errorCode;
  }

  public Map<String, Object> getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }
}
