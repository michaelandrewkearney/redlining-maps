import React, { useEffect, useState } from "react";
import Map, {
  Layer,
  MapLayerMouseEvent,
  Source,
  ViewStateChangeEvent,
} from "react-map-gl";
import "mapbox-gl/dist/mapbox-gl.css";
import { ACCESS_TOKEN } from "./private/keys";
import "./App.css";
import { geoLayer, overlayData } from "./overlays";

// REMEMBER TO PUT YOUR API KEY IN A FOLDER THAT IS GITIGNORED!!
// (for instance, /src/private/api_key.tsx)
// import {API_KEY} from "./private/api_key"

interface LatLon {
  readonly lat: number;
  readonly lon: number;
}

function App() {
  const ProvidenceLatLon: LatLon = {
    lat: 41.825226,
    lon: -71.418884,
  };
  const initialZoom = 12;

  const [viewState, setViewState] = useState({
    longitude: ProvidenceLatLon.lon,
    latitude: ProvidenceLatLon.lat,
    zoom: initialZoom,
  });
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  useEffect(() => {
    setOverlay(overlayData());
  });

  const handleMapClick = (e: MapLayerMouseEvent) => {
    console.log(e.lngLat.lat);
    console.log(e.lngLat.lng);
  };

  return (
    <div className="App">
      <Map
        mapboxAccessToken={ACCESS_TOKEN}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom}
        onMove={(e: ViewStateChangeEvent) => setViewState(e.viewState)}
        onClick={handleMapClick}
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
