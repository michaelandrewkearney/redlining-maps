package edu.brown.cs32.ezhang29mkearne1.server.response;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import org.jetbrains.annotations.NotNull;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

public class ServerResponses {
    public interface ServerResponse {
        String result();
        String endpoint();
        String serialize();
    }

    public record FeatureCollectionResponse (
            String result,
            String endpoint,
            FeatureCollection featureCollection
    ) implements ServerResponse {
        public FeatureCollectionResponse(String endpoint, FeatureCollection featureCollection) {
            this("success", endpoint, featureCollection);
        }
        @Override
        public String serialize() {
            return new Moshi.Builder()
                    .add(FeatureCollection.class, FeatureCollection.getAdapter())
                    .build()
                    .adapter(FeatureCollectionResponse.class)
                    .toJson(this);
        }
    }

}
