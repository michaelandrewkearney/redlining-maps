# Server
## Project Details, Logistics
- Lizzy Zhang (ezhang29)
  - Implementation: Implemented the NWSAPIRequester class and appropriate record/"helper" class implementations,
the Polygon class for grid point / area radius storage, extensive error handling, Loaded CSV, LoadCSVHandler, HandlerUtils, and Weather Handler.
  - Testing: Created mocked data for weather API requests, tested functionality for loadCSV, and tested
functionality for various NWS API calls.
  - Other: Led conceptual and prototyping discussions, refactored CSV implementation to be better adjusted
for the server, conceptualized appropriate record classes to store data.

- Michael Ma (mma32)
  - Implementation: Created Cache class for Weather, LoadedCSV, SearchCSVHandler, and ViewCSVHandler.
  - Testing: Effects of caching data for weather, testing functionality for viewCSV and searchCSV, and
found appropriate example points coordinates, grid points, and other data for NWS API testing.
  - Other: Wrote extensive javadocs for all methods/interfaces/classes

Total time taken to complete project: 45-50 hours (combined)

Link to [Server Repo](https://github.com/cs0320-s2023/sprint-3-ezhang29-mma32)
Link to [CSV Repo](https://github.com/cs0320-s2023/csv-ezhangy)

## Design Choices
There are 3 packages included in the project:
- _api_ deals with all weather-related API and general API responses.
- _csv_ includes all CSV parsing, searching, and structuring code from sprint 1.
- _server_ includes server handling code.
  - _csv_ and _weather_ endpoint handlers for various features/functionalities (CSV viewing, loading,
searching, and weather querying/caching).

In general, there were many areas in which code repetition and/or redundancy occurred due to the nature
of having several conditions in the user stories that all rely on similar behaviors like calling the API.
To account for this code repetition, we created many exception classes for detailed error handling, 
record classes to safely store data, as well as classes like HandlerUtils which saves on redundancy
regarding commonly used computational logic.

## Errors/Bugs
A wide variety of errors are returned by the API to handle various errors in user input, and so discussing
the final, propagated error response & classes of error return is most conducive.

- **error_bad_json**: if the request itself was ill-formed (i.e: gibberish passed in as input).
- **error_bad_request** if the request was missing a needed field, or the field was ill-formed (i.e:
for the NWS API, passing in points coordinates with a valid structure but the specific coordinates
don't exist in the API).                                         
- **error_datasource** if the given data wasn't accessible - for example if the file queried didn't exist,
or the NWS API returned null fields for the grid point despite the user passing in valid coordinate points.

## Tests
_Note:_ For the most part, we tested the "main" method for appropriate classes to check implementation,
functionality, and edge cases. That is, we did not explicitly test for every single helper method because many
of the helper methods have a private field for defensive programming purposes, but the functionality of
these helper methods are intrinsically tested since the main methods have to rely on the helpers working 
in order for the main method itself to work and interact with data in the way that we intended.

Unit testing was conducted on weather-related code through mocked datasets representing NWS API calls.
Integration testing was conducted on weather-related code throuhg real NWS API calls, and checking that
the behavior matched what we were looking for through caching, endpoint responses, and the weather handler.

Testing was conducted on server and CSV related code through calls to our server and API, and checking
that the behavior matched what we were looking for -- primarily through the testing of the three
handlers of search, view, and load.