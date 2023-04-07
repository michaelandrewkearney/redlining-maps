package edu.brown.cs32.ezhang29mkearne1.server.errorResponses;

import java.util.Map;

public class DatasourceException extends ServerResponseException {
  public DatasourceException(String message, Map<String, Object> parameters) {
    super(message, ErrorCode.DATASOURCE, parameters);
  }
}
