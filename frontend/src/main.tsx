import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import "./index.css";
import { fetchJson } from "./repl/command_utils";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App requestJson={fetchJson} />
  </React.StrictMode>
);
