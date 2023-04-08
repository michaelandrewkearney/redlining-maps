package edu.brown.cs32.ezhang29mkearne1.server.response;

import com.squareup.moshi.*;
import edu.brown.cs32.ezhang29mkearne1.Adapters;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON;
import edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.FeatureCollection;
import org.jetbrains.annotations.NotNull;
import spark.Request;

import java.io.IOException;
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
        @FromJson
        public FeatureCollectionResponse fromJson(String json) {
            try {
                return GeoJSON.RawGeometry.getBuilder()
                        .add(FeatureCollection.class, GeoJSON.FeatureCollectionLike.getAdapter())
                        .build()
                        .adapter(FeatureCollectionResponse.class)
                        .fromJson(json);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        @ToJson
        public String serialize() {
            return GeoJSON.RawGeometry.getBuilder()
                    .add(FeatureCollection.class, GeoJSON.FeatureCollectionLike.getAdapter())
                    .build()
                    .adapter(FeatureCollectionResponse.class)
                    .toJson(this);
        }
    }

}
