package edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses;


import java.util.Map;

public class IllegalFilepathException extends ServerResponseException {
  public IllegalFilepathException(String message, Map<String, Object> parameters) {
    super(message, ErrorCode.ILLEGALPATH, parameters);
  }
}
