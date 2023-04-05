import { ErrorResponse, RequestJsonFunction, SuccessResponse, isErrorResponse, isSuccessResponse } from "./command_utils";

export interface BBox {
  minLon: number, 
  minLat: number, 
  maxLon: number, 
  maxLat: number, 
}

interface FilterResponse extends SuccessResponse {
  endpoint: string
  filteredGeoJSON: GeoJSON.FeatureCollection
}

const isFilterResponse = (json: any): json is FilterResponse => {
  if (!isSuccessResponse(json)) {
    return false
  }
  if (!("endpoint" in json && json.endpoint === "filterWithinBounds")) {
    return false
  }
  if (!("filteredGeoJSON" in json)) {
    return false
  }
  return true;
}

export function buildFilterGeoJSON(requestJson: RequestJsonFunction): (bbox: BBox) => Promise<GeoJSON.FeatureCollection> {
  async function filterGeoJSON(bbox: BBox): Promise<GeoJSON.FeatureCollection> {
    const requestURL = new URL("http://localhost:3232/filterWithinBounds")
    requestURL.searchParams.append("minLon", `${bbox.minLon}`)
    requestURL.searchParams.append("minLat", `${bbox.minLat}`)
    requestURL.searchParams.append("maxLon", `${bbox.maxLon}`)
    requestURL.searchParams.append("maxLat", `${bbox.maxLat}`)
    
    const json: Promise<any> = await requestJson(requestURL);

    if (isErrorResponse(json)) {
      const errorResp: ErrorResponse = json;
      throw new Error(errorResp.msg)
    }

    if (isFilterResponse(json)) {
      const filterResp: FilterResponse = json;
      return filterResp.filteredGeoJSON
    }

    throw new Error(`could not fetch redlining data within bounding box ${JSON.stringify(bbox)}
    from server`)
  }
  return filterGeoJSON
}