package edu.brown.cs32.ezhangmma32;

import com.squareup.moshi.JsonAdapter;
import edu.brown.cs32.ezhang29mma32.Adapters;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

final class TestingUtility {
  private TestingUtility(){}

  static Map<String, Object> convertErrResponseToMap(String errorResponseJson) throws IOException {
    JsonAdapter<ErrorResponseAsMapRecord> adapter = Adapters.ofClass(ErrorResponseAsMapRecord.class).nonNull();
    ErrorResponseAsMapRecord errRecord = Objects.requireNonNull(adapter.fromJson(errorResponseJson));
    return Map.of(
        "result", errRecord.result(),
        "message", errRecord.message(),
        "parameters", errRecord.parameters()
    );
  }

  public record ErrorResponseAsMapRecord(String result, String message, Map<String, Object> parameters){}
}
