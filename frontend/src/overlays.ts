import { FeatureCollection } from "geojson"
import { FillLayer, LineLayer } from "react-map-gl"

const propertyName = "holc_grade";

export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#db5a0f",
      "E",
      "#cf0808",
      "#7a7a7a"
    ],
    "fill-opacity": 0.3
  }
}

export const searchFillLayer: FillLayer = {
  id: "search_fill_layer",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#db5a0f",
      "E",
      "#cf0808",
      "#7a7a7a"
    ],
    "fill-opacity": 0.5,
  }
}

export const searchLineLayer: LineLayer = {
  id: "search_line_layer",
  type: "line",
  paint: {
    "line-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#db5a0f",
      "E",
      "#cf0808",
      "#7a7a7a"
    ],
    "line-width": 4,
    "line-opacity": 1,
  }
}

export const searchThinLineLayer: LineLayer = {
  id: "search_thin_line_layer",
  type: "line",
  paint: {
    "line-color": "#111111",
    "line-width": 2,
    "line-opacity": 1,
  }
}
