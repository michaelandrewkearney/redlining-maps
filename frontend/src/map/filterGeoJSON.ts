import { ErrorResponse, RequestJsonFunction, FeatureCollectionResponse, isErrorResponse, isServerResponse, isSuccessResponse, API_URL, BBox} from "./command_utils";

interface FilterResponse extends FeatureCollectionResponse {}

const isFilterResponse = (json: any): json is FilterResponse => {
  if (!isSuccessResponse(json)) {
    return false;
  }
  if (!(json.endpoint === "filterGeoJSON")) {
    return false;
  }
  return true;
}

export function buildFilterGeoJSON(requestJson: RequestJsonFunction): (bbox: BBox) => Promise<GeoJSON.FeatureCollection> {
  async function filterGeoJSON(bbox: BBox): Promise<GeoJSON.FeatureCollection> {
    const requestURL: URL = new URL(API_URL + "filterGeoJSON")
    requestURL.searchParams.append("minLon", `${bbox.minLon}`)
    requestURL.searchParams.append("minLat", `${bbox.minLat}`)
    requestURL.searchParams.append("maxLon", `${bbox.maxLon}`)
    requestURL.searchParams.append("maxLat", `${bbox.maxLat}`)
    
    const json: Promise<any> = await requestJson(requestURL);

    if (isFilterResponse(json)) {
      const filterResp: FilterResponse = json;
      return filterResp.featureCollection;
    }

    if (isErrorResponse(json)) {
      const errorResp: ErrorResponse = json;
      throw new Error(errorResp.msg)
    }

    throw new Error(`could not fetch redlining data within bounding box ${JSON.stringify(bbox)}
    from server`)
  }
  return filterGeoJSON
}