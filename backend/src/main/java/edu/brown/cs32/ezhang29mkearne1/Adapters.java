package edu.brown.cs32.ezhang29mkearne1;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JSON adapter class that provides stubs for various JSON adapter types and Moshi building to avoid
 * code redundancy in classes that use JSON adapters
 */
public final class Adapters {
  private static final Builder moshiBuilder = new Moshi.Builder();
  private static Builder getNewBuilder() {
    return new Moshi.Builder();
  }
  public static <T> JsonAdapter<T> ofClass(Class<T> type) {
    return getNewBuilder()
        .build()
        .adapter(type);
  }
  public static <T> JsonAdapter<T> ofClass(Class<T> type, JsonAdapter.Factory factory) {
    return getNewBuilder().add(factory)
            .build()
            .adapter(type);
  }
  public static <T> JsonAdapter<T> ofClass(Class<T> type, List<JsonAdapter.Factory> factories) {
    Builder builder = getNewBuilder();
    for (JsonAdapter.Factory factory: factories) {
      builder.add(factory);
    }
    return builder
            .build()
            .adapter(type);
  }

  private static <T> JsonAdapter<T> ofType(Type type) {
    return getNewBuilder()
        .build()
        .adapter(type);
  }

  public static JsonAdapter<Map<String,Object>> ofMap() {
    return Adapters.ofType(
        Types.newParameterizedType(Map.class, String.class, Object.class)
    );
  }
}
