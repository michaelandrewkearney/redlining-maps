import { ErrorResponse, RequestJsonFunction, SuccessResponse, isErrorResponse, isSuccessResponse, API_URL} from "./command_utils";

interface LoadResponse extends SuccessResponse {
  filepath: string
}

const isLoadResponse = (json: any): json is LoadResponse => {
  if (!isSuccessResponse(json)) {
    console.log("not success repspone")
    return false;
  }
  if (!(json.endpoint == "loadGeoJSON")) {
    console.log("no enpoint matching")
    return false;
  }
  if (!("filepath" in json) || typeof json.filepath !== "string"){
    console.log("no filepath matching")
    return false;
  }
  return true;
}

export function buildLoadGeoJSON(requestJson: RequestJsonFunction): (filepath: string) => Promise<boolean> {
  async function loadGeoJSON(filepath: string): Promise<boolean> {
    const requestURL = new URL(API_URL + "loadGeoJSON")
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
