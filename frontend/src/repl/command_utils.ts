export interface RequestJsonFunction {
  (url: URL): Promise<any>;
}

export interface ErrorResponse {
  result: string;
  msg: string;
}

export interface SuccessResponse {
  result: string;
}

export const isErrorResponse = (json: any): json is ErrorResponse => {
  if (!("result" in json)) {
    return false;
  }
  const maybeErrorResponse: ErrorResponse = json;
  if (!maybeErrorResponse.result.includes("error")) {
    return false;
  }
  return true;
};

export const isSuccessResponse = (json: any): json is SuccessResponse => {
  if (!("result" in json)) {
    return false;
  }
  if (!(json.result === "success")) {
    return false;
  }
  return true;
};

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