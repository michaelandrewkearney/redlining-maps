import { ErrorResponse, RequestJsonFunction, SuccessResponse, isErrorResponse, isSuccessResponse } from "./command_utils";

interface LoadResponse extends SuccessResponse {
  type: string,
  filepath: string, 
}

const isLoadResponse = (json: any): json is LoadResponse => {
  if (!isSuccessResponse(json)) {
    return false;
  }
  if (!("filepath" in json) || typeof json.filepath !== "string"){
    return false;
  }
  return true;
}

export function buildLoadGeoJSON(requestJson: RequestJsonFunction): (filepath: string) => Promise<boolean> {
  async function loadGeoJSON(filepath: string): Promise<boolean> {
    const requestURL = new URL("http://localhost:3232/loadGeoJson")
    requestURL.searchParams.append("filepath", filepath)
    
    const json: Promise<any> = await requestJson(requestURL);

    if (isErrorResponse(json)) {
      const errorResp: ErrorResponse = json;
      throw new Error(errorResp.msg)
    }
    if (isLoadResponse(json)) {
      return true
    }
    throw new Error(`unexpected server response when trying to load 
    historicaly redlining data: got ${JSON.stringify(json)}`)
  }

  return loadGeoJSON;
}
