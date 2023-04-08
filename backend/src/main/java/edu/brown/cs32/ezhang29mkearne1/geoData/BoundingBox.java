package edu.brown.cs32.ezhang29mkearne1.geoData;

import static edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.isValidLat;
import static edu.brown.cs32.ezhang29mkearne1.geoData.GeoJSON.isValidLon;

/**
 * BoundingBox represents a bounded geographic area. BoundingBoxes are aligned with the Lng/Lat grid. BoundingBox math is boundary-inclusive: a point on the boundary of a BoundingBox is contained within it.
 *
 * @param minLon - the bounding box's minimum longitude
 * @param minLat - the bounding box's minimum latitude
 * @param maxLon - the bounding box's maximum longitude
 * @param maxLat - the bounding box's minimum longitude
 * @param wraps - if the bounding box wraps the international date line
 */
public record BoundingBox(double minLon, double minLat, double maxLon, double maxLat, boolean wraps) {
    /**
     * Compact constructor verifies coordinates are within global bounds and are valid
     * @param minLon
     * @param minLat
     * @param maxLon
     * @param maxLat
     */
    public BoundingBox {
        assert(isValidLon(minLon));
        assert(isValidLat(minLat));
        assert(isValidLon(maxLon));
        assert(isValidLat(maxLat));

        //A BoundingBox can wrap over the date lane, so don't check that minLon <= maxLon
        // todo test wrapping
//        assert(wraps == (maxLon < minLon));
//        assert(minLat <= maxLat);
    }

    /**
     * Non canonical constructor for general use
     * @param minLon
     * @param minLat
     * @param maxLon
     * @param maxLat
     */
    public BoundingBox(double minLon, double minLat, double maxLon, double maxLat) {
        this(minLon, minLat, maxLon, maxLat, maxLon < minLon);
    }
    /**
     * Non-canonical constructor for point-equivalent BoundingBox, i.e. one where the minimum and maximum values are the same.
     * @param lon
     * @param lat
     */
    public BoundingBox(double lon, double lat) {
        this(lon, lat, lon, lat);
    }
    /**
     * Constructs the smallest BoundingBox that bounds both this and that. If this or that does not completely contain the other, the new BoundingBox will include areas not bounded by this or that individually, but which are between this and that.
     * @param that the BoundingBox to combine with this
     * @return the smallest BoundingBox containing this and that
     */
    public BoundingBox getCombinedBounds(BoundingBox that) {
        return new BoundingBox(
                Math.min(minLon, that.minLon),
                Math.min(minLat, that.minLat),
                Math.max(maxLon, that.maxLon),
                Math.max(maxLat, that.maxLat));
    }

    /**
     * Checks whether this fully contains that. This method is commutative iff this equals that.
     * @param that the BoundingBox to be checked for containment
     * @return if this fully contains that
     */
    public boolean contains(BoundingBox that) {
        return that.minLat() >= this.minLat
                && that.maxLat() <= this.maxLat
                && that.minLon() >= this.minLon
                && that.maxLon() <= this.maxLon;
    }

    /**
     * Checks whether a BoundingBox fully contains a GeoJSON feature.
     * @param feature the GeoJSON Feature to be checked for containment
     * @return if this fully contains feature
     */
    public boolean contains(GeoJSON.Feature feature) {
        return (feature.geometry() == null) ? false : feature.geometry().isContained(this);
    }
    /**
     * Checks whether a BoundingBox fully contains a GeoJSON RawGeometry object.
     * @param geometry the GeoJSON RawGeometry object to be checked for containment
     * @return if this fully contains geometry
     */
    public boolean contains(GeoJSON.RawGeometry geometry) {
        return (geometry == null) ? false : geometry.isContained(this);
    }
    /**
     * Checks whether this overlaps with that. This method is always commutative.
     * @param that the BoundingBox to be checked for containment
     * @return if this fully contains that
     */
    public boolean overlaps(BoundingBox that) {
        boolean containsLon = this.maxLon() >= that.minLon() && this.minLon() <= that.maxLon();
        boolean containsLat = this.maxLat() >= that.minLat() && this.minLat() <= that.maxLat();
        return containsLon && containsLat;
    }
    /**
     * Checks whether a BoundingBox overlaps a GeoJSON feature.
     * @param feature the GeoJSON Feature to be checked for containment
     * @return if this fully contains feature
     */
    public boolean overlaps(GeoJSON.Feature feature) {
        return feature.geometry().overlaps(this);
    }

    public boolean overlaps(GeoJSON.RawGeometry geometry) {
        return geometry.overlaps(this);
    }

    public static BoundingBox getBounds(GeoJSON.Position[] list) {
        double minLon = GeoJSON.MAX_LON;
        double minLat = GeoJSON.MAX_LAT;
        double maxLon = GeoJSON.MIN_LON;
        double maxLat = GeoJSON.MIN_LAT;
        for (GeoJSON.Position position : list) {
            minLon = Math.min(minLon, position.lon());
            minLat = Math.min(minLat, position.lat());
            maxLon = Math.max(maxLon, position.lon());
            maxLat = Math.max(maxLat, position.lat());
        }
        return new BoundingBox(minLon, minLat, maxLon, maxLat);
    }
}
