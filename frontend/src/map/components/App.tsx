import React, { useEffect, useState } from "react";
import Map, {
  Layer,
  LngLatBounds,
  LngLatBoundsLike,
  MapLayerMouseEvent,
  MapRef,
  Source,
  ViewStateChangeEvent,
} from "react-map-gl";
import "mapbox-gl/dist/mapbox-gl.css";
import { ACCESS_TOKEN } from "../../private/keys";
import "../styles/App.css";
import {
  geoLayer,
  searchFillLayer,
  searchLineLayer,
  searchThinLineLayer,
} from "../../overlays";
import { RequestJsonFunction, BBox } from "../command_utils";
import { buildLoadGeoJSON } from "../loadGeoJSON";
import { buildFilterGeoJSON } from "../filterGeoJSON";
import Header from "./Header";
import TextBox from "./TextBox";
import { buildSearchGeoJSON } from "../searchGeoJSON";
import SearchBox from "./SearchBox";

const emptyFC: GeoJSON.FeatureCollection = {
  type: "FeatureCollection",
  features: [],
};

interface LonLat {
  readonly lon: number;
  readonly lat: number;
}

interface AppProps {
  requestJson: RequestJsonFunction;
  dataPath?: string;
}
const ProvidenceLonLat: LonLat = {
  lon: -71.418884,
  lat: 41.825226,
};
const initialZoom = 12;

function App({ requestJson, dataPath }: AppProps) {
  // build commands
  const loadGeoJSON: (filepath: string) => Promise<boolean> =
    buildLoadGeoJSON(requestJson);

  const filterGeoJSON: (bbox: BBox) => Promise<GeoJSON.FeatureCollection> =
    buildFilterGeoJSON(requestJson);

  const searchGeoJSON: (
    bbox: BBox,
    query?: string
  ) => Promise<GeoJSON.FeatureCollection> = buildSearchGeoJSON(requestJson);

  const mapRef = React.useRef<MapRef>(null);

  const [viewState, setViewState] = useState({
    longitude: ProvidenceLonLat.lon,
    latitude: ProvidenceLonLat.lat,
    zoom: initialZoom,
  });

  const [mapBbox, setMapBbox] = useState<BBox>();

  function getMapBounds(): BBox {
    let map = mapRef.current;
    if (map == null) {
      throw Error("No Map component present.");
    }
    const bounds: LngLatBounds = map.getBounds();
    return {
      minLon: bounds.getWest(),
      minLat: bounds.getSouth(),
      maxLon: bounds.getEast(),
      maxLat: bounds.getNorth(),
    };
  }

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection>(emptyFC);

  const [searchOverlay, setSearchOverlay] =
    useState<GeoJSON.FeatureCollection>(emptyFC);

  const [disabled, setDisabled] = useState<boolean>(false);

  const [selectedHeader, setSelectedHeader] = useState<String>("");
  const [selectedContent, setSelectedContent] = useState<String[]>([]);

  useEffect(() => {
    if (dataPath) {
      loadGeoJSON(dataPath);
    }
  }, []);

  useEffect(() => {
    if (mapBbox === undefined) {
      return;
    }
    filterGeoJSON(mapBbox).then((data: GeoJSON.FeatureCollection) => {
      setOverlay(data);
    });
    if (disabled) {
      searchGeoJSON(mapBbox).then((data: GeoJSON.FeatureCollection) => {
        setSearchOverlay(data);
      });
    }
  }, [mapBbox]);

  const handleMapClick = (e: MapLayerMouseEvent) => {
    const map = mapRef.current;
    if (map !== null) {
      const features: GeoJSON.Feature[] = map.queryRenderedFeatures(
        [
          [e.point.x, e.point.y],
          [e.point.x, e.point.y],
        ],
        { layers: ["geo_data"] }
      );
      const f: GeoJSON.Feature = features[0];
      if (f && f.properties) {
        setSelectedHeader(
          (f.properties.holc_id
            ? "#" + f.properties.holc_id.toString()
            : "Area") +
            " in " +
            (f.properties.city && f.properties.state)
            ? f.properties.city.toString() +
                ", " +
                f.properties.state.toString()
            : "Unknown City"
        ),
          setSelectedContent([
            "Name: " + (f.properties.name ? f.properties.name : "Unknown"),
            "HOLC Grade: " +
              (f.properties.holc_grade ? f.properties.holc_grade : "Unknown"),
            "Description: " +
              (f.properties.area_description_data
                ? f.properties.area_description_data.toString()
                : "Unknown"),
          ]);
      } else {
        setSelectedHeader("");
        setSelectedContent([]);
      }
    }
  };

  const handleMapMove = (e: ViewStateChangeEvent) => {
    setViewState(e.viewState);
  };

  const handleMapLoad = () => {
    updateMapBBox();
  };

  const handleMoveEnd = () => {
    updateMapBBox();
  };

  const handleZoomEnd = () => {
    updateMapBBox();
  };

  const updateMapBBox = () => {
    if (mapRef.current !== null) {
      setMapBbox(getMapBounds);
    }
  };

  const handleSearch = (input: string) => {
    searchGeoJSON(getMapBounds(), input).then(
      (data: GeoJSON.FeatureCollection) => {
        setSearchOverlay(data);
      }
    );
    setDisabled(true);
  };

  const handleSearchClear = () => {
    setSearchOverlay(emptyFC);
    setDisabled(false);
  };

  return (
    <div className="App">
      <Map
        ref={mapRef}
        mapboxAccessToken={ACCESS_TOKEN}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom}
        onZoom={handleMapMove}
        onMove={handleMapMove}
        onMoveEnd={handleMoveEnd}
        onZoomEnd={handleZoomEnd}
        onClick={handleMapClick}
        onLoad={handleMapLoad}
        mapStyle={"mapbox://styles/mapbox/dark-v10"}
      >
        <Source id="geo_data" type="geojson" data={overlay}>
          <Layer id={geoLayer.id} type={geoLayer.type} paint={geoLayer.paint} />
        </Source>
        <Source id="search_data" type="geojson" data={searchOverlay}>
          <Layer
            id={searchFillLayer.id}
            type={searchFillLayer.type}
            paint={searchFillLayer.paint}
          />
          <Layer
            id={searchLineLayer.id}
            type={searchLineLayer.type}
            paint={searchLineLayer.paint}
          />
          <Layer
            id={searchThinLineLayer.id}
            type={searchThinLineLayer.type}
            paint={searchThinLineLayer.paint}
          />
        </Source>
      </Map>
      <div className="infobox">
        <div className="header-wrapper">
          <Header />
        </div>
        <hr />
        <div className="input-wrapper">
          <div>Search neighborhoods by description.</div>
          <SearchBox
            disabled={disabled}
            handleSearch={handleSearch}
            handleSearchClear={handleSearchClear}
          />
        </div>
        <hr />
        <div className="output-wrapper">
          <TextBox header={selectedHeader} content={selectedContent} />
        </div>
      </div>
    </div>
  );
}

export default App;
