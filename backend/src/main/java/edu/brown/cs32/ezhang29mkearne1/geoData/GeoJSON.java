package edu.brown.cs32.ezhang29mkearne1.geoData;

import com.squareup.moshi.Json;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import edu.brown.cs32.ezhang29mkearne1.server.layer.search.Searcher;

import java.util.*;

public class GeoJSON {
    public final static double MIN_LON = -180.0;
    public final static double MIN_LAT = -90.0;
    public final static double MAX_LON = 180.0;
    public final static double MAX_LAT = 90.0;
    public static boolean isValidLon(double lon) {
        return lon >= MIN_LON && lon <= MAX_LON;
    }
    public static boolean isValidLat(double lat) {
        return lat >= MIN_LAT && lat <= MAX_LAT;
    }
    public interface RawGeometry {

        boolean overlaps(BoundingBox box);
        boolean isContained(BoundingBox box);
        BoundingBox getBounds();
        Position[] toFlatList();
        RawGeometry copy();
        static Moshi.Builder getBuilder() {
            return new Moshi.Builder()
                    .add(Geometry.getFactory())
                    .add(new Position.Adapter())
                    .add(new MultiPoint.RawMultiPoint.Adapter())
                    .add(new LineString.RawLineString.Adapter())
                    .add(new MultiLineString.RawMultiLineString.Adapter())
                    .add(new Polygon.RawPolygon.Adapter())
                    .add(new MultiPolygon.RawMultiPolygon.Adapter());
        }
    }
    public interface Geometry extends RawGeometry {
        RawGeometry coordinates();
        @Override
        default boolean overlaps(BoundingBox box) {return (coordinates() == null) ? false : coordinates().overlaps(box);}
        @Override
        default boolean isContained(BoundingBox box) {return (coordinates() == null) ? false : coordinates().isContained(box);}
        @Override
        default BoundingBox getBounds() {return (coordinates() == null) ? new BoundingBox(MAX_LON,MAX_LAT) : coordinates().getBounds();}
        @Override
        default Position[] toFlatList() {return (coordinates() == null) ? new Position[]{} : coordinates().toFlatList();}
        Geometry copy();
        static PolymorphicJsonAdapterFactory<Geometry> getFactory() {
            return PolymorphicJsonAdapterFactory.of(Geometry.class, "type")
                        .withSubtype(Point.class, "Point")
                        .withSubtype(MultiPoint.class, "MultiPoint")
                        .withSubtype(LineString.class, "LineString")
                        .withSubtype(MultiLineString.class, "MultiLineString")
                        .withSubtype(Polygon.class, "Polygon")
                        .withSubtype(MultiPolygon.class, "MultiPolygon");
        }
    }
    public record Point (
            @Json(name = "coordinates") Position coordinates)
            implements Geometry {
        @Override
        public Point copy() {
            return new Point(coordinates.copy());
        }
    }
    public record Position (double lon, double lat, double alt) implements RawGeometry {
        @Override
        public BoundingBox getBounds() {
            return new BoundingBox(lon, lat, lon, lat);
        }
        @Override
        public boolean overlaps(BoundingBox box) {
            return lon >= box.minLon() &&
                    lon <= box.maxLon() &&
                    lat >= box.minLat() &&
                    lat <= box.maxLat();
        }
        @Override
        public boolean isContained(BoundingBox box) {
            return overlaps(box);
        }
        @Override
        public Position[] toFlatList() {return new Position[]{copy()};}
        @Override
        public Position copy() {return new Position(lon, lat, alt);}
        public static class Adapter {
            @ToJson
            public double[] toJson(Position position) {
                if (position.alt() == 0.0) {
                    return new double[]{position.lon(), position.lat()};
                }
                return new double[]{position.lon(), position.lat(), position.alt()};
            }
            @FromJson
            public Position fromJson(double[] array) throws UnsupportedOperationException {
                if (array.length == 2) {
                    return new Position(array[0], array[1], 0.0);
                } else if (array.length != 3) {
                    return new Position(array[0], array[1], array[2]);
                }
                throw new UnsupportedOperationException("Expected array of 2 or 3 elements but was " +
                        Arrays.toString(array));
            }
        }
    }
    public record MultiPoint (
            @Json(name = "coordinates") RawMultiPoint coordinates)
            implements Geometry {
        @Override
        public MultiPoint copy() {
            return new MultiPoint(coordinates().copy());
        }
        public record RawMultiPoint (Position[] list) implements RawGeometry {
            @Override
            public BoundingBox getBounds() {
                return BoundingBox.getBounds(list);
            }
            @Override
            public boolean overlaps(BoundingBox box) {
                for (Position e : list) {
                    if (e.overlaps(box)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean isContained(BoundingBox box) {
                for (Position e: list) {
                    if (!e.isContained(box)) {
                        return false;
                    }
                }
                return true;
            }
            @Override
            public Position[] toFlatList() {
                return Arrays.stream(list).flatMap((e) -> Arrays.stream(e.toFlatList())).toArray(Position[]::new);
            }
            @Override
            public RawMultiPoint copy() {
                return new RawMultiPoint(Arrays.stream(list()).map((e) -> e.copy()).toArray(Position[]::new));
            }
            public static class Adapter {
                @ToJson
                public Position[] toJson(RawMultiPoint r) {
                    return r.list();
                }
                @FromJson
                public RawMultiPoint fromJson(Position[] array) {
                    return new RawMultiPoint(array);
                }
            }
        }
    }
    public record LineString (
            @Json(name = "coordinates") RawLineString coordinates)
            implements Geometry {
        public LineString copy() {
            return new LineString(coordinates().copy());
        }
        public record RawLineString (Position[] list) implements RawGeometry {
            @Override
            public BoundingBox getBounds() {
                return BoundingBox.getBounds(list);
            }
            @Override
            public boolean overlaps(BoundingBox box) {
                for (Position e : list) {
                    if (e.overlaps(box)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean isContained(BoundingBox box) {
                for (Position e: list) {
                    if (!e.isContained(box)) {
                        return false;
                    }
                }
                return true;
            }
            @Override
            public Position[] toFlatList() {
                return Arrays.stream(list).flatMap((e) -> Arrays.stream(e.toFlatList())).toArray(Position[]::new);
            }
            @Override
            public RawLineString copy() {
                return new RawLineString(Arrays.stream(list()).map((e) -> e.copy()).toArray(Position[]::new));
            }
            public static class Adapter {
                @ToJson
                public Position[] toJson(RawLineString r) {
                    return r.list();
                }
                @FromJson
                public RawLineString fromJson(Position[] array) {
                    return new RawLineString(array);
                }
            }
        }
    }
    public record MultiLineString (
            @Json(name = "coordinates") RawMultiLineString coordinates)
            implements Geometry {
        @Override
        public MultiLineString copy() {return new MultiLineString(coordinates.copy());}
        public record RawMultiLineString (MultiPoint.RawMultiPoint[] list) implements RawGeometry {
            @Override
            public BoundingBox getBounds() {
                BoundingBox box = new BoundingBox(MAX_LON, MAX_LAT, MIN_LON, MIN_LAT);
                for (MultiPoint.RawMultiPoint e: list) {
                    box = box.getCombinedBounds(e.getBounds());
                }
                return box;
            }
            @Override
            public boolean overlaps(BoundingBox box) {
                for (MultiPoint.RawMultiPoint e : list) {
                    if (e.overlaps(box)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean isContained(BoundingBox box) {
                for (MultiPoint.RawMultiPoint e : list) {
                    if (!e.isContained(box)) {
                        return false;
                    }
                }
                return true;
            }
            @Override
            public Position[] toFlatList() {
                return Arrays.stream(list).flatMap((e) -> Arrays.stream(e.toFlatList())).toArray(Position[]::new);
            }
            @Override
            public RawMultiLineString copy() {
                return new RawMultiLineString(Arrays.stream(list()).map((e) -> e.copy()).toArray(MultiPoint.RawMultiPoint[]::new));
            }
            public static class Adapter {
                @ToJson
                public MultiPoint.RawMultiPoint[] toJson(RawMultiLineString r) {
                    return r.list();
                }
                @FromJson
                public RawMultiLineString fromJson(MultiPoint.RawMultiPoint[] array) {
                    return new RawMultiLineString(array);
                }
            }
        }

    }
    public record Polygon (
            @Json(name = "coordinates") MultiLineString.RawMultiLineString coordinates)
            implements Geometry {
        @Override
        public Polygon copy() {
            return new Polygon(coordinates.copy());
        }
        public record RawPolygon (MultiPoint.RawMultiPoint[] list) implements RawGeometry {
            @Override
            public BoundingBox getBounds() {
                BoundingBox box = new BoundingBox(MAX_LON, MAX_LAT, MIN_LON, MIN_LAT);
                for (MultiPoint.RawMultiPoint e: list) {
                    box = box.getCombinedBounds(e.getBounds());
                }
                return box;
            }
            @Override
            public boolean overlaps(BoundingBox box) {
                for (MultiPoint.RawMultiPoint e : list) {
                    if (e.overlaps(box)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean isContained(BoundingBox box) {
                for (MultiPoint.RawMultiPoint e : list) {
                    if (!e.isContained(box)) {
                        return false;
                    }
                }
                return true;
            }
            @Override
            public Position[] toFlatList() {
                return Arrays.stream(list).flatMap((e) -> Arrays.stream(e.toFlatList())).toArray(Position[]::new);
            }
            @Override
            public RawPolygon copy() {
                return new RawPolygon(Arrays.stream(list()).map((e) -> e.copy()).toArray(MultiPoint.RawMultiPoint[]::new));
            }
            public static class Adapter {
                @ToJson
                public MultiPoint.RawMultiPoint[] toJson(RawPolygon r) {
                    return r.list();
                }
                @FromJson
                public RawPolygon fromJson(MultiPoint.RawMultiPoint[] array) {
                    return new RawPolygon(array);
                }
            }
        }
    }
    public record MultiPolygon (
            @Json(name = "coordinates") RawMultiPolygon coordinates)
            implements Geometry {
        public record RawMultiPolygon(Polygon.RawPolygon[] list) implements RawGeometry {
            @Override
            public BoundingBox getBounds() {
                BoundingBox box = new BoundingBox(MAX_LON, MAX_LAT, MIN_LON, MIN_LAT);
                for (Polygon.RawPolygon e: list) {
                    box = box.getCombinedBounds(e.getBounds());
                }
                return box;
            }
            @Override
            public boolean overlaps(BoundingBox box) {
                for (Polygon.RawPolygon e : list) {
                    if (e.overlaps(box)) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public boolean isContained(BoundingBox box) {
                for (Polygon.RawPolygon e : list) {
                    if (!e.isContained(box)) {
                        return false;
                    }
                }
                return true;
            }
            @Override
            public Position[] toFlatList() {
                return Arrays.stream(list).flatMap((e) -> Arrays.stream(e.toFlatList())).toArray(Position[]::new);
            }
            @Override
            public RawMultiPolygon copy() {
                return new RawMultiPolygon(Arrays.stream(list()).map((e) -> e.copy()).toArray(Polygon.RawPolygon[]::new));
            }
            public static class Adapter {
                @ToJson
                public Polygon.RawPolygon[] toJson(RawMultiPolygon r) {return r.list();}
                @FromJson
                public RawMultiPolygon fromJson(Polygon.RawPolygon[] array) {
                    return new RawMultiPolygon(array);
                }
            }
        }
        @Override
        public boolean overlaps(BoundingBox box) {
            return coordinates.overlaps(box);
        }

        public Position[] toFlatList() {
            return coordinates().toFlatList();
        }

        public MultiPolygon copy() {
            return new MultiPolygon(coordinates().copy());
        }
    }
    public record Feature (
            @Json(name = "type") String type,
            @Json(name = "geometry") Geometry geometry,
            @Json(name = "properties") Map<String, Object> props) {
        public Feature copy() {
            return new Feature(
                    this.type,
                    (geometry == null) ? null : geometry.copy(),
                    // todo: copy props
                    (props == null) ? null : props);
        }
        public boolean overlaps(BoundingBox box) {
            return geometry != null && geometry.overlaps(box);
        }
        public static JsonAdapter<Feature> getAdapter() {
            return RawGeometry.getBuilder()
                    .build()
                    .adapter(Feature.class);
        }
    }
    public record FeatureCollection (
            @Json(name = "type") String type,
            @Json(name = "features") List<Feature> features)
             implements Searcher.Filterable<Feature> {
        public static JsonAdapter<FeatureCollection> getAdapter() {
            return RawGeometry.getBuilder()
                    .build()
                    .adapter(FeatureCollection.class);
        }
        public FeatureCollection copy() {
            List<Feature> featuresCopy = this.features.stream()
                    .map(Feature::copy)
                    .toList();
            return new FeatureCollection(this.type, featuresCopy);
        }

        @Override
        public Searcher.Filterable<Feature> filter(Searcher.FilterFunction<Feature> function) {
            List<Feature> results = new ArrayList<>();
            features.forEach((f) -> {if (function.run(f)) {results.add(f);}});
            return new FeatureCollection(this.type, results);
        }
    }
}



