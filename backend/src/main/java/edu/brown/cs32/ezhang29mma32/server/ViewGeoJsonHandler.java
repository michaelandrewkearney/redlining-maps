package edu.brown.cs32.ezhang29mma32.server;

import edu.brown.cs32.ezhang29mma32.Adapters;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.FeatureCollection;
import spark.Request;
import spark.Response;
import spark.Route;

 /**
  *  Handler class for boundingBox endpoint.
  *
  * <p>
  *   The endpoints returns filtered historical redlining data based on a bounding box provided
  *   in the user query.
  * </p>
  */
public class ViewGeoJsonHandler implements Route {
  private final RedliningData redliningData;

   public ViewGeoJsonHandler(RedliningData redliningData) {
     this.redliningData = redliningData;
   }

   public record ViewParameters() {}

   public record ViewSuccessResponse(String result, FeatureCollection resultFeatureCollection) {
     public ViewSuccessResponse(FeatureCollection featureCollection) {
       this("success", featureCollection);
     }
   }

   @Override
  public Object handle(Request request, Response response) throws Exception {
     try {
       ViewSuccessResponse successResponse =
           new ViewSuccessResponse(redliningData.getFeatureCollection());
       return Adapters.ofClass(ViewSuccessResponse.class).toJson(successResponse);
     } catch (Exception e) {
       e.printStackTrace();
     }
     return "";
  }
}
