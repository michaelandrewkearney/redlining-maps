package edu.brown.cs32.ezhang29mma32.redliningGeoData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record FeatureCollection(String type, List<Feature> features) {
  public FeatureCollection(List<Feature> features) {
    this("FeatureCollection", features);
  }
  public FeatureCollection copy() {
    List<Feature> featuresCopy = this.features.stream()
        .map(Feature::copy)
        .toList();
    return new FeatureCollection(featuresCopy);
  }

  /**
   * Returns a FeatureCollection of Features with area descriptions containing a keyword.
   *
   * <p>
   *  If no matching Features are found, returns a FeatureCollection whose features
   *  field is an empty list
   * </p>
   * <p>
   *   Matches are case-insensitive
   * </p>
   *
   * @param query String to search for
   * @return Returns a FeatureCollection of Features with area descriptions containing a keyword.
   * */
  public FeatureCollection searchByDescription(String query) {
    List<Feature> matches = new LinkedList<>();
    for (Feature feature: this.features()) {
      if (feature.containsDescription(query)) {
        matches.add(feature.copy());
      }
    }
    return new FeatureCollection(matches);
  }

//   * Remove null <code>Feature</code> objects with null geometries from the <code>FeatureCollection</code>
//   *
//   * @return a new <code>FeatureCollection</code> object that only contains <code>Features</code>
//   * with nonnull <code>geometry</code> fields
//   *
//   */
//  public FeatureCollection removeNullFeatures() {
//    List<Feature> filteredFeatures = this.features.stream()
//        .filter(feature -> feature.geometry != null)
//        .map(feature -> feature.copy()) // defensive copy
//        .toList();
//
//    return new FeatureCollection(filteredFeatures);
//  }

  public FeatureCollection withinBoundingBox(BoundingBox boundingBox) {
    List<Feature> containedFeatures = this.features.stream()
        .filter(feature -> boundingBox.contains(feature))
        .toList();
    return new FeatureCollection(containedFeatures);
  }

  public record Feature(String type, MultiPolygon geometry, RedliningFeatureProperties properties) {
    public Feature(MultiPolygon geometry, RedliningFeatureProperties properties) {
      this("Feature", geometry, properties);
    }
    public Feature copy() {
      if (geometry == null) {
        return new Feature(null, this.properties.copy());
      }
      return new Feature(this.geometry.copy(), this.properties.copy());
    }

    /**
     * Return a Map representing area descriptions that containing a keyword query.
     *
     * <p>Returns an empty Map if no matching descriptions are found.</p>
     *
     * @param query String to search for within area descriptions
     * @return Map of String to Strings mapping area description codes to their corresponding
     * descriptions.
     **/
    public boolean containsDescription(String query) {
      return this.properties.containsDescription(query);
    }

    /**
     * Returns a list of coordinates. The longitude value comes first in each coordinate.
     *
     * @return a List containing a lon-lat coordinate represented as a List of Doubles
     */
    public List<List<Double>> points() {
      return this.geometry.points();
    }

    public record MultiPolygon(String type, List<List<List<List<Double>>>> coordinates) {
      public MultiPolygon(List<List<List<List<Double>>>> coordinates) {
        this("MultiPolygon", coordinates);
      }

      /**
       * Returns an unmodifiable List containing the elements within coordinates
       *m
       * @return
       */
      public MultiPolygon copy() {
        return new MultiPolygon(List.of(List.of(this.points())));
      }

      private List<List<Double>> points() {
        assert this.coordinates.size() == 1;
        assert this.coordinates.get(0).size() == 1;

        List<List<Double>> pointsCopy = this.coordinates.get(0).get(0).stream()
            .map((lonLatList) -> {
              // assert that each List<Double> only contains two coordinate points (lon, lat)
              assert lonLatList.size() == 2;
              return List.copyOf(lonLatList);
            })
            .toList();
        return pointsCopy;
      }
    }



    /**
     * TODO: add javadoc
     */
    public record RedliningFeatureProperties(String state, String city, String name, String holc_grade, Map<String, String> area_description_data) {

      /**
       * Returns a deep copy of the `RedliningFeatureProperties` object
       *
       * @return a `RedliningFeatureProperties` object
       */
      public RedliningFeatureProperties copy() {
        return new RedliningFeatureProperties(this.state, this.city, this.name, this.holc_grade, Map.copyOf(this.area_description_data));
      }

      /**
       * Return true if one or more area descriptions contains a keyword query.
       *
       * <p>Returns an empty Map if no matching descriptions are found.</p>
       * <p>Matches are not case-sensitive</p>
       *
       * @param query String to search for within area descriptions
       * @return Boolean
       */
      public boolean containsDescription(String query) {
        for (String areaCode : area_description_data.keySet()) {
          String descr = area_description_data.get(areaCode);
          String lowercaseQuery = query.toLowerCase();
          if (descr.toLowerCase().contains(lowercaseQuery)) {
            return true;
          }
        }
        return false;
      }
    }
  }
}

