package edu.brown.cs32.ezhang29mma32.server.errorResponses;

import java.util.Map;

public class DatasourceException extends ServerResponseException {
  public DatasourceException(String message, Map<String, Object> parameters) {
    super(message, ErrorCode.DATASOURCE, parameters);
  }
}
