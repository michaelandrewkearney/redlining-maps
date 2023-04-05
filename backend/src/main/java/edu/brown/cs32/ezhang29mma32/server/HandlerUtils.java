package edu.brown.cs32.ezhang29mma32.server;

import java.util.Collections;
import java.util.Set;
import spark.Request;

final class HandlerUtils {
  private HandlerUtils(){}

   /**
   * Takes in a user's query and checks to see if its parameters match the program's needed specificities
   * @param request a Request object representing the API request
    * @param expectedParams a Set of Strings representing the expected parameters that a user's input should have
   */
  protected static boolean checkParams(Request request, Set<String> expectedParams) {
    Set<String> actualParams = Collections.unmodifiableSet(request.queryParams());
    int numExpectedParams = expectedParams.size();
    int numActualParams = actualParams.size();
    if (expectedParams.size() != actualParams.size()) {
      throw new IllegalArgumentException(
          "wrong number of parameters: expected " + numExpectedParams
              + " but got " + numActualParams + " instead"
      );
    }

    for (String expectedParam : expectedParams) {
      if (!actualParams.contains(expectedParam)) {
        throw new IllegalArgumentException("missing required '" + expectedParam + "' parameter");
      }
    }
    return true;
  }
}
