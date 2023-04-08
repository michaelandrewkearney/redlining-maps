package edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses;

/**
 * An enum representing the type of error message that has occurred when the handler is not able to
 * successfully handle the user input
 */
public enum ErrorCode {
  DATASOURCE("error_datasource"),
  BADREQUEST("error_bad_request"),
  BADJSON("error_bad_json"),
  ILLEGALPATH("error_illegal_filepath");

  private final String code;
  ErrorCode(String code){
    this.code = code;
  }

  // Getter method that prevents users from changing the body of the original code itself
  public String toStringCode() {
    return code;
  }
}
