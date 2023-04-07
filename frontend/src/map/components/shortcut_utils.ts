export const hasFocus = (htmlElement: HTMLElement): boolean => {
  const activeElt: Element | null = document.activeElement;
  if (activeElt === null) {
    return false;
  }
  if (!(activeElt instanceof HTMLElement)) {
    return false;
  }
  return htmlElement === activeElt;
};

export const removeDocFocus = () => {
  const activeElt: Element | null = document.activeElement;
  if (activeElt instanceof HTMLElement) {
    activeElt.blur()
  }
}