import { ErrorResponse, RequestJsonFunction, FeatureCollectionResponse, isErrorResponse, isSuccessResponse, API_URL, BBox } from "./command_utils";

interface SearchResponse extends FeatureCollectionResponse {}

const isSearchResponse = (json: any): json is SearchResponse => {
  if (!isSuccessResponse(json)) {
    return false;
  }
  if (!(json.endpoint === "searchGeoJSON")) {
    return false;
  }
  return true;
}

export function buildSearchGeoJSON(requestJson: RequestJsonFunction): (bbox: BBox, query?: String) => Promise<GeoJSON.FeatureCollection> {
  async function searchGeoJSON(bbox: BBox, query?: String): Promise<GeoJSON.FeatureCollection> {
    const requestURL: URL = new URL(API_URL + "searchGeoJSON")
    requestURL.searchParams.append("minLon", `${bbox.minLon}`)
    requestURL.searchParams.append("minLat", `${bbox.minLat}`)
    requestURL.searchParams.append("maxLon", `${bbox.maxLon}`)
    requestURL.searchParams.append("maxLat", `${bbox.maxLat}`)
    if (query) {
      requestURL.searchParams.append("query", `${query}`);
    }
    const json: Promise<any> = await requestJson(requestURL);

    if (isSearchResponse(json)) {
      const filterResp: SearchResponse = json;
      return filterResp.featureCollection;
    }

    if (isErrorResponse(json)) {
      const errorResp: ErrorResponse = json;
      throw new Error(errorResp.msg)
    }

    throw new Error(`Could not search redlining data by description ${query}.`)
  }
  return searchGeoJSON
}