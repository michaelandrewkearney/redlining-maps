import { RefObject, useEffect, useState } from "react";
import "../styles/SearchBox.css";
import SearchIcon from "../../resources/icons/search.svg";
import ClearIcon from "../../resources/icons/clear.svg";
import { removeDocFocus } from "./shortcut_utils";
import React from "react";

interface SearchBoxProps {
  disabled: boolean;
  handleSearch: (input: string) => void;
  handleSearchClear: () => void;
}

/**
 * InputBox contains the user input elements. It passes information out to logic handlers. It handles cycling through history with up/down arrows.
 * @param props
 * @returns InputBox with text input field and button
 */
export default function SearchBox(props: SearchBoxProps) {
  const [textbox, setTextbox] = useState<string>("");
  const searchBoxRef: RefObject<HTMLInputElement> = React.createRef();

  /**
   * Handles the submit button being clicked or the enter key being pressed!
   */
  function handleSubmit() {
    props.handleSearch(textbox);
  }

  function handleClear() {
    props.handleSearchClear();
    setTextbox("");
  }

  useEffect(() => {
    const handleFocusSearchShortcut = (event: KeyboardEvent) => {
      if (event.metaKey && event.shiftKey && event.key === "s") {
        const inputElt: HTMLInputElement | null = searchBoxRef.current;
        if (inputElt instanceof HTMLInputElement) {
          removeDocFocus();
          inputElt.focus();
        }
      }
    };

    const handleClearSearchShortcut = (event: KeyboardEvent) => {
      if (event.metaKey && event.key === "Backspace") {
        handleClear();
      }
    };

    window.addEventListener("keydown", handleFocusSearchShortcut);
    window.addEventListener("keydown", handleClearSearchShortcut);
  });

  // includes key handlers
  // include multiple key names for backwards browser compatability
  return (
    <div className="inputbox-wrapper">
      <input
        type="text"
        className="search-box"
        ref={searchBoxRef}
        onChange={(e) => setTextbox(e.target.value)}
        onKeyUp={(e) => {
          if (e.key === "Enter") {
            handleSubmit();
          }
        }}
        disabled={props.disabled}
        value={textbox}
      />
      <button className="clear" onClick={handleClear}>
        <img src={ClearIcon} alt="Clear logo" />
      </button>
    </div>
  );
}
