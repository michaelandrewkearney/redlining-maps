export const API_URL = "http://localhost:3232/";

export interface BBox {
  minLon: number, 
  minLat: number, 
  maxLon: number, 
  maxLat: number, 
}

export interface RequestJsonFunction {
  (url: URL): Promise<any>;
}

export interface ServerResponse {
  result: string;
}

export interface ErrorResponse extends ServerResponse {
  msg: string;
}

export interface SuccessResponse extends ServerResponse {
  endpoint: String,
}

export interface FeatureCollectionResponse extends SuccessResponse {
  featureCollection: GeoJSON.FeatureCollection
}

export const isServerResponse = (json: any): json is ServerResponse => {
  if (!("result" in json)) {
    return false;
  }
  return true;
}

export const isErrorResponse = (json: any): json is ErrorResponse => {
  if (!isServerResponse(json)) {
    return false;
  }
  if (!json.result.includes("error")) {
    return false;
  }
  return true;
};

export const isSuccessResponse = (json: any): json is SuccessResponse => {
  if (!isServerResponse(json)) {
    return false;
  }
  if (!(json.result.includes("success"))) {
    return false;
  }
  if (!("endpoint" in json) || typeof json.endpoint !== "string") {
    return false;
  }
  return true;
};

export const isFeatureCollectionResponse = (json: any): json is FeatureCollectionResponse => {
  if (!isSuccessResponse(json)) {
    return false;
  }
  if (!("featureCollection" in json) || !isFeatureCollection(json.featureCollection)) {
    return false;
  }
  return true;
}

export const isFeatureCollection = (json: any): json is GeoJSON.FeatureCollection => {
  return json.type === "FeatureCollection"
}

export async function fetchJson(url: URL): Promise<any> {
  const json = await fetch(url)
    .then((response: Response) => response.json())
    .then((json: any) => json)
    .catch((problem) => {
      return {
        result: "error_connection_refused",
        msg: `Unable to connect to server. Try again later.`,
      };
    });
  return json;
}