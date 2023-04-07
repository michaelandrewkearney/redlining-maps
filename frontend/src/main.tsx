import React from "react";
import ReactDOM from "react-dom/client";
import App from "./map/components/App";
import "./index.css";
import { fetchJson } from "./map/command_utils";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App
      requestJson={fetchJson}
      dataPath={"src/main/resources/maplayers/redlining/usa.json"}
    />
  </React.StrictMode>
);
