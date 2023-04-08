# Redlining Maps
## Team Members
Elizabeth Zhang (ezhang29)
Michael Kearney (mkearne1)
<a href=https://github.com/cs0320-s2023/sprint-5-ezhang29-mkearne1>Repository</a>

## Design Choices
### GeoJSON
We modelled the GeoJSON spec in Java and used Moshi's polymorphic factories to parse GeoJSON data.
### Searching and Caching
Our searcher and cached searcher are generic and rely on a Filterable interface to ensuring filtering capability between parametized types. The Filterable interface ensures that a parametized FilterFunction can interface between the containing class and the items to be filtered. Our cache matches against these functions, wrapped with their query.

## Errors / Bugs
There are no known errors or bugs.

## Tests
### Backend
On the backend, we used random testing to check for the validity of BoundingBoxes and our containment functions. We tested that our cached searcher returns hits instead of always loading. We checked our endpoint handlers and GeoJSON parsing.
### Frontend
On the front end, we use mocking to test our handler functions, which were designed for data injection.


## How To
To run the program, run the Java server in 'backend'. Use Vite to run the 'index.html' in 'frontend'. To run tests, build the project with Maven.

## Reflection: Whose Labor?
We relied on the labor of people who have developed programming languages, development environments, software packages, and hardware.
Languages: HTML, CSS, React, TypeScript, Java
Software: IntelliJ, VisualStudioCode, GitHub, Shell, Zoom, Email, Google Docs
Packages: Moshi, JSON, GeoJSON, Spark, MapBox, JUnit, Maven, Vite
Hardware: Apple laptops (including developers, product assemblers, and resource extractors).
This project also uses the non-computer-oriented labor, including the data prepared by researchers at Mapping Inequality, and an <a href="https://www.svgrepo.com/svg/12848/x-symbol">SVG icon</a> made by someone at SVGRepo.