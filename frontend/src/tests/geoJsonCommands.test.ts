import "@testing-library/jest-dom";
import { BBox, RequestJsonFunction } from "../map/command_utils";
import { mockApiMap } from "./mockApiMap";
import { buildLoadGeoJSON } from "../map/loadGeoJSON";
import { buildFilterGeoJSON } from "../map/filterGeoJSON";
import { buildSearchGeoJSON } from "../map/searchGeoJSON";

// mock function for retrieving mock API data
let mockRequestJson: RequestJsonFunction;

let load: (filepath: string) => Promise<boolean>;
let filter: (bbox: BBox) => Promise<GeoJSON.FeatureCollection>;
let search: (bbox: BBox, query?: String) => Promise<GeoJSON.FeatureCollection>;

beforeAll(() => {
  mockRequestJson = (url: URL) => {
    console.log("running mock")
    console.log(url.href)
    const json = mockApiMap.get(url.href);
    console.log(json)
    return Promise.resolve(json);
  }

  load = buildLoadGeoJSON(mockRequestJson)
  filter = buildFilterGeoJSON(mockRequestJson)
  search = buildSearchGeoJSON(mockRequestJson)
})

test("load geoJSON", async () => {
  load("src/test/resources/mockRedliningData.json").then((output: boolean) =>
    expect(output).toBe(true)
  )
});

test("filter GeoJSON", async () => {
  filter({minLon: 0, minLat: 0, maxLon: 4, maxLat: 4}).then((output: GeoJSON.FeatureCollection) => {
    expect(output.features[0].properties).toStrictEqual(
      {
        state: 'AL',
        city: 'Birmingham',
        name: 'Triangle within bounding box (0, 0, 4, 4)'
      }
    )
  })
});