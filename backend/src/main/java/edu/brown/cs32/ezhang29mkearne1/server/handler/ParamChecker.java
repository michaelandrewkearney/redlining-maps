package edu.brown.cs32.ezhang29mkearne1.server.handler;

import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.BadJsonException;
import edu.brown.cs32.ezhang29mkearne1.server.response.errorResponses.BadRequestException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import spark.Request;

final class ParamChecker {
  private ParamChecker(){}

  static Map<String, Object> makeQueryMap(Request request) {
    Set<String> actualParams = Collections.unmodifiableSet(request.queryParams());
    Map<String, String> actualParamMap = new HashMap<>();
    for (String paramName: actualParams) {
      actualParamMap.put(paramName, request.queryParams(paramName));
    }
    return Map.copyOf(actualParamMap);
  }
   /**
   * Takes in a user's query and checks to see if its parameters match the program's needed specificities
   * @param request a Request object representing the API request
   */
  static boolean checkAllExist(Request request, Set<String> expectedParams)
      throws BadJsonException, BadRequestException {
    Set<String> actualParams = Collections.unmodifiableSet(request.queryParams());
    Map<String, Object> actualParamMap = makeQueryMap(request);

    int numExpectedParams = expectedParams.size();
    int numActualParams = actualParams.size();

    if (actualParams.size() == 0 && expectedParams.size() != 0) {
      throw new BadJsonException(
          "expected the following parameters but none were passed in:" + expectedParams,
          request.body()
      );
    }

    for (String actualParam : actualParams) {
      if (!expectedParams.contains(actualParam)) {
        throw new BadJsonException(
            String.format("could not parse parameter '%s', expected parameters are %s",
                actualParam, expectedParams),
            request.body()
        );
      }
    }

    for (String expectedParam : expectedParams) {
      if (!actualParams.contains(expectedParam)) {
        throw new BadRequestException(
            String.format("missing required '%s' parameter %n expected parameters:%s",
               expectedParam, expectedParams),
            Map.copyOf(actualParamMap)
        );
      }
    }



    if (expectedParams.size() != actualParams.size()) {
      throw new BadRequestException(
          "wrong number of parameters: expected " + numExpectedParams
              + " parameters but instead got " + numActualParams,
          Map.copyOf(actualParamMap)
      );
    }
    return true;
  }
}
