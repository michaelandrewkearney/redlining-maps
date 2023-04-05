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
import { ACCESS_TOKEN } from "./private/keys";
import "./App.css";
import { geoLayer, overlayData } from "./overlays";
import { RequestJsonFunction } from "./repl/command_utils";
import { buildLoadGeoJSON } from "./repl/loadGeoJSON";
import { BBox, buildFilterGeoJSON } from "./repl/filterGeoJSON";

interface LatLon {
  readonly lat: number;
  readonly lon: number;
}

interface AppProps {
  requestJson: RequestJsonFunction;
}

function App({ requestJson }: AppProps) {
  const ProvidenceLatLon: LatLon = {
    lat: 41.825226,
    lon: -71.418884,
  };
  const initialZoom = 12;
  // const initialBBox: BBox = {
  //   minLon: -71.450772,
  //   minLat: 41.797150,
  //   maxLon: -71.450772,
  //   maxLat:  41.843145
  // }

  const [viewState, setViewState] = useState({
    longitude: ProvidenceLatLon.lon,
    latitude: ProvidenceLatLon.lat,
    zoom: initialZoom,
  });

  const [mapBbox, setMapBbox] = useState<BBox>();

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  // build commands
  const loadGeoJson: (filepath: string) => Promise<boolean> =
    buildLoadGeoJSON(requestJson);

  const filterGeoJson: (bbox: BBox) => Promise<GeoJSON.FeatureCollection> =
    buildFilterGeoJSON(requestJson);

  // load full redlining geodata within backend API
  useEffect(() => {
    loadGeoJson("src/main/geodata/historical_redlining.json");
  }, []);

  useEffect(() => {
    if (mapBbox === undefined) {
      setOverlay(undefined);
      return;
    }
    filterGeoJson(mapBbox).then((overlayData: GeoJSON.FeatureCollection) => {
      console.log(JSON.stringify(overlayData));
      setOverlay(overlayData);
    });
  }, [mapBbox]);

  const handleMapClick = (e: MapLayerMouseEvent) => {
    console.log(e.lngLat.lat);
    console.log(e.lngLat.lng);
  };

  const mapRef = React.useRef<MapRef>(null);

  const handleMapLoad = () => {
    updateMapBBox();
  };

  const handleMapMove = (e: ViewStateChangeEvent) => {
    setViewState(e.viewState);
    updateMapBBox();
  };

  const updateMapBBox = () => {
    const map = mapRef.current;
    if (map == null) {
      throw Error("No Map component present");
    }
    const bounds: LngLatBounds = map.getBounds();
    const bbox: BBox = {
      minLon: bounds.getWest(),
      minLat: bounds.getSouth(),
      maxLon: bounds.getEast(),
      maxLat: bounds.getNorth(),
    };
    setMapBbox(bbox);
  };

  return (
    <div className="App">
      <Map
        ref={mapRef}
        mapboxAccessToken={ACCESS_TOKEN}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom}
        onMove={handleMapMove}
        onClick={handleMapClick}
        onLoad={handleMapLoad}
        style={{ width: window.innerWidth, height: window.innerHeight }}
        mapStyle={"mapbox://styles/mapbox/dark-v10"}
      >
        <Source id="geo_data" type="geojson" data={overlay}>
          <Layer id={geoLayer.id} type={geoLayer.type} paint={geoLayer.paint} />
        </Source>
      </Map>
    </div>
  );
}

export default App;
