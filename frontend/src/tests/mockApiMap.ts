export const mockApiMap: Map<string, object> = new Map([
  [
    "http://localhost:3232/loadGeoJSON?filepath=src%2Ftest%2Fresources%2FmockRedliningData.json", 
    {
      endpoint: "loadGeoJSON", 
      result: "success",
      filepath: "src/test/resources/mockRedliningData.json"
    }
  ],
  [
    "http://localhost:3232/filterGeoJSON?minLon=0&minLat=0&maxLon=4&maxLat=4",
    {
      endpoint: "filterGeoJSON",
      result: "success",
      featureCollection: {
          "type": "FeatureCollection",
          "features": [
            {
              "geometry": {
                "type": "MultiPolygon",
                "coordinates": [
                  [
                    [
                      [
                        0,
                        0
                      ],
                      [
                        2,
                        4
                      ],
                      [
                        3,
                        0
                      ]
                    ]
                  ]
                ]
              },
              "properties": {
                "state": "AL",
                "city": "Birmingham",
                "name": "Triangle within bounding box (0, 0, 4, 4)"
              }
            }
          ]
        }
      }
  ]
])