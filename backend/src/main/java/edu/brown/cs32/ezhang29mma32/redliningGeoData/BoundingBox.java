package edu.brown.cs32.ezhang29mma32.redliningGeoData;
import edu.brown.cs32.ezhang29mma32.redliningGeoData.FeatureCollection.Feature;
import java.util.List;

/**
 * BoundingBox is a record that represents a bounding box for latitude-longitude coordinates.
 *
 * @param minLon - the bounding box's minimum longitude
 * @param minLat - the bounding box's minimum latitude
 * @param maxLon - the bounding box's maximum longitude
 * @param maxLat - the bounding box's minimum longitude
 */
public record BoundingBox(Double minLon, Double minLat, Double maxLon, Double maxLat) {
  private boolean contains(List<Double> lonLatPoint) {
    // point should only ever have two elements
    assert lonLatPoint.size() == 2;
    double lon = lonLatPoint.get(0);
    double lat = lonLatPoint.get(1);

    boolean containsLon =  minLon <= lon && maxLon >= lon;
    boolean containsLat =  minLat <= lat && maxLat >= lat;
    return containsLon && containsLat;
  }


  /**
   * Returns true if the Feature is contained within the bounding box. A lon-lat coordinate is
   * considered contained if it lies inside or along the bounding box's edges.
   *
   * </p>
   * <p>Returns false if the Feature's geometry property is null.</p>
   *
   * @param feature the GeoJSON <code>Feature</code> to test
   * @return true if the <code>Feature</code> is contained within the bounding box.
   */
  public boolean contains(Feature feature) {
    if (feature.geometry() == null) {
      return false;
    }
    for (List<Double> lonLatPoint: feature.points()) {
      if (!this.contains(lonLatPoint)) {
        return false;
      }
    }
    return true;
  }
}
